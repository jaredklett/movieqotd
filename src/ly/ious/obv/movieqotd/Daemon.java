/*
 * @(#)Daemon.java
 *
 * Copyright (c) 2004-2008 by Obviously, Inc.
 * 187 Lafayette St, 6th Floor
 * New York, NY 10013
 * All rights reserved.
 *
 * This software is the confidential and
 * proprietary information of Obviously, Inc.
 */

package ly.ious.obv.movieqotd;

import com.blipnetworks.sql.DataSourceManager;
import ly.ious.obv.movieqotd.model.Genres;
import ly.ious.obv.movieqotd.model.Movies;
import ly.ious.obv.movieqotd.model.Quotes;
import org.apache.log4j.Logger;
import org.javaforge.util.StringUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * A class that runs as a thread.
 *
 * @author Jared Klett
 * @version $Id: Daemon.java,v 1.8 2009/02/28 21:17:13 jklett Exp $
 */

public class Daemon implements Runnable {

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Daemon.class);

    public enum State {
        NO_GAME,
        ANNOUNCE_GAME,
        FIRST_ROUND,
        SECOND_ROUND,
        THIRD_ROUND,
        GET_REPLIES,
        ANNOUNCE_WINNER
    }

    private static Comparator<Status> ascendingDateStatusComparator = new Comparator<Status>() {
        public int compare(Status s1, Status s2) {
            if (s1.getCreatedAt().before(s2.getCreatedAt()))
                return -1;
            if (s1.getCreatedAt().after(s2.getCreatedAt()))
                return 1;
            return 0;
        }
    };

// Instance variables /////////////////////////////////////////////////////////

    private String username = "movieqotd";
    private String password = "dmitri1";
    private List<Status> winnerList;
    private Quotes currentQuote;
    private Movies currentMovie;
    private Genres currentGenre;
    /** TODO */
    private String site;
    /** TODO */
    private boolean running;
    /** TODO */
    private Thread thread;
    /** TODO */
    private State state;
    /** An initialized but not running thread to call our clean up method when the JVM exits. */
    private Thread shutdownHook = new Thread(UUID.randomUUID().toString() + "-shutdownhook") {
        public void run() {
            stopThread(); // Halt our internal thread
        }
    };

    public Daemon() {
        this("obviously");
    }

    public Daemon(String site) {
        this.site = site;
        state = State.NO_GAME;
        // Register our shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void run() {
        long delta = 5000L;
        Game game = new Game();
        winnerList = new ArrayList<Status>();
        while (running) {
            switch (state) {
                case NO_GAME:
                    log.debug("State: NO GAME");
                    // Create a new game
                    game = new Game();
                    // Sleep until it's announce time
                    //delta = game.getAnnounceTime().getTime() - System.currentTimeMillis();
                    // TODO: for testing
                    delta = 1000L;
                    state = State.ANNOUNCE_GAME;
                    break;
                case ANNOUNCE_GAME:
                    log.debug("State: ANNOUNCE GAME");
                    // Pick a quote
                    Connection masterConnection;
                    Connection slaveConnection;
                    try {
                        masterConnection = DataSourceManager.getMasterConnection(site);
                        slaveConnection = DataSourceManager.getSlaveConnection(site);
                    } catch (SQLException e) {
                        log.error("Caught exception while trying to get a database connection!", e);
                        log.warn("Can't continue, bailing out...");
                        break;
                    }
                    log.debug("Opened database connections...");
                    Quotes quote;
                    Movies movie;
                    Genres genre;
                    try {
                        quote = Quotes.getRandomQuote(slaveConnection);
                        movie = Movies.getMovie(slaveConnection, quote.getMovieId());
                        genre = Genres.getGenre(slaveConnection, movie.getGenreId());
                    } catch (Exception e) {
                        log.error("Caught exception while trying to fetch a random quote!", e);
                        break;
                    }
                    // Form the tweet
                    // TODO: externalize
                    String announceTemplate = TemplateLoader.loadTemplate("trivia_announce.tmpl");
                    log.debug("Loaded template: " + announceTemplate);
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("$GENRENAME$", genre.getGenreName());
                    String tweet = StringUtils.mapReplace(StringUtils.mapSplit(announceTemplate, map), map, "$");
                    // Send the tweet
                    // TODO
                    log.debug("Announce tweet: " + tweet);
                    // Set the quote in memory
                    currentQuote = quote;
                    currentMovie = movie;
                    currentGenre = genre;
                    // Mark that it's been used
                    quote.setUsed(true);
                    quote.setUsedDatestamp(new Date());
                    try {
                        boolean success = quote.updateUsed(masterConnection);
                        if (!success) {
                            // TODO: what to do? rollback!
                            log.warn("What the heck do I do now?");
                        }
                    } catch (SQLException e) {
                        log.error("Caught exception while trying to set a quote as used!", e);
                    }
                    // Clean up database connections
                    try { masterConnection.close(); } catch (SQLException e) { /* ignored */ }
                    try { slaveConnection.close(); } catch (SQLException e) { /* ignored */ }
                    // Sleep until it's time for the first round
                    //delta = game.getStartTime().getTime() - System.currentTimeMillis();
                    // TODO: for testing
                    delta = 3000L;
                    state = State.FIRST_ROUND;
                    break;
                case FIRST_ROUND:
                    log.debug("State: FIRST ROUND");
                    // Get the first part of the quote
                    log.debug("First part of the quote: " + currentQuote.getFirstPart());
                    // Send the tweet
                    // TODO
                    // Sleep until it's time for the next round
                    //delta = game.getTimeBetweenRounds();
                    // TODO: for testing
                    delta = 3000L;
                    state = State.SECOND_ROUND;
                    break;
                case SECOND_ROUND:
                    log.debug("State: SECOND ROUND");
                    // Did anyone get it?
                    // TODO
                    log.debug("Second part of the quote: " + currentQuote.getSecondPart());
                    // TODO: for testing
                    state = State.THIRD_ROUND;
                    break;
                case THIRD_ROUND:
                    log.debug("State: THIRD ROUND");
                    log.debug("Third part of the quote: " + currentQuote.getThirdPart());
                    state = State.GET_REPLIES;
                    break;
                case GET_REPLIES:
                    log.debug("State: GET REPLIES");
                    Twitter twitter = new Twitter(username, password);
                    for (int i = 1; i < Integer.MAX_VALUE; i++) {
                        List<Status> replies = null;
                        try {
                            replies = twitter.getRepliesByPage(i);
                        } catch (TwitterException e) {
                            log.error("Caught exception while retrieving replies!", e);
                        }
                        if (replies.size() == 0) {
                            break;
                        }
                        for (Status reply : replies) {
                            // TODO FIXME: be forgiving, i.e. "A Fish Called Wanda" / "Fish Called Wanda"
                            String text = reply.getText();
                            if (text.equalsIgnoreCase(movie.getMovieTitle())) {
                                // possible WIN
                                winnerList.add(reply);
                            }
                        }
                    }
                    if (winnerList.size() == 0) {
                        // no winners yet, go to the next round
                        if (state == State.FIRST_ROUND) {
                            state = State.SECOND_ROUND;
                        } else if (state == State.SECOND_ROUND) {
                            state = State.THIRD_ROUND;
                        } else {
                            state = State.ANNOUNCE_WINNER;
                        }
                    } else {
                        // we have one or more winners
                        state = State.ANNOUNCE_WINNER;
                    }
                    break;
                case ANNOUNCE_WINNER:
                    log.debug("State: ANNOUNCE WINNER");
                    boolean noWinner = winnerList.size() == 0;
                    if (!noWinner) {
                        Collections.sort(winnerList, ascendingDateStatusComparator);
                    }
                    // Form the tweet
                    String winnerTemplate;
                    if (noWinner) {
                        // TODO: externalize
                        winnerTemplate = TemplateLoader.loadTemplate("no_winner.tmpl");
                    } else {
                        // TODO: externalize
                        winnerTemplate = TemplateLoader.loadTemplate("winner_announce.tmpl");
                    }
                    log.debug("Loaded template: " + winnerTemplate);
                    Map<String,String> awMap = new HashMap<String,String>();
                    awMap.put("$MOVIETITLE$", genre.getGenreName());
                    if (!noWinner)
                        awMap.put("$SCREENNAME$", winnerList.get(0).getUser().getScreenName());
                    String awTweet = StringUtils.mapReplace(StringUtils.mapSplit(winnerTemplate, awMap), awMap, "$");
                    // Send the tweet
                    // TODO
                    log.debug("Announce winner tweet: " + awTweet);
                    break;
                default:
                    log.debug("State: UNKNOWN");
                    break;
            }
            try { Thread.sleep(delta); } catch (InterruptedException e) { /* ignored */ }
        }
    }

// Instance methods ///////////////////////////////////////////////////////////

    /**
     * Creates and starts a thread with this object.
     */
    public void startThread() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stops and destroys the thread running this object.
     */
    public void stopThread() {
        running = false;
        thread.interrupt();
        try {
            thread.join(15000);
        }
        catch(InterruptedException e) {
            log.warn("Interrupted while waiting for internal thread to join!", e);
        }
        thread = null;
    }

} // class Daemon