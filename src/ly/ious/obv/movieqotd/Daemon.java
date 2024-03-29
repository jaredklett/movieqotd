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

import org.apache.log4j.Logger;
import org.javaforge.util.StringUtils;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ly.ious.obv.movieqotd.model.People;
import ly.ious.obv.movieqotd.model.Winners;
import ly.ious.obv.movieqotd.model.Guesses;
import com.blipnetworks.sql.DataSourceManager;

/**
 * A class that runs as a thread.
 *
 * @author Jared Klett
 * @version $Id: Daemon.java,v 1.25 2009/03/16 19:42:46 jklett Exp $
 */

public class Daemon implements Runnable {

    private static final long TEST_DELTA = 20 * 1000L;
    //private static final long TEST_DELTA = 2 * 60 * 1000L;
    private static final String NO_WINNER_TMPL = "no_winner.tmpl";
    private static final String WINNER_ANNOUNCE_TMPL = "winner_announce.tmpl";
    private static final String TRIVIA_ANNOUNCE_TMPL = "trivia_announce.tmpl";

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

// Instance variables /////////////////////////////////////////////////////////

    // TODO: do something with this
    private String username = "movieqotd";
    private String password = "dmitri1";

    private boolean timetestmode;
    private boolean tweettestmode;
    private boolean running;
    private Thread thread;
    private State state;
    /** An initialized but not running thread to call our clean up method when the JVM exits. */
    private Thread shutdownHook = new Thread(UUID.randomUUID().toString() + "-shutdownhook") {
        public void run() {
            stopThread(); // Halt our internal thread
        }
    };

    public Daemon() {
        state = State.NO_GAME;
        // TODO: make configurable
        timetestmode = false;
        tweettestmode = false;
        // Register our shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void run() {
        long sleepTime = 5000L;
        Game game = null;

        while (running) {

            switch (state) {

                case NO_GAME:

                    log.debug("State: NO GAME");
                    // Create a new game
                    try {
                        game = new Game();
                    } catch (SQLException e) {
                        log.error("Caught exception while trying to create game!", e);
                    }
                    // Sleep until it's announce time
                    if (timetestmode)
                        sleepTime = TEST_DELTA;
                    else
                        sleepTime = game.getAnnounceTime().getTime() - System.currentTimeMillis();

                    state = State.ANNOUNCE_GAME;

                    break;

                case ANNOUNCE_GAME:

                    log.debug("State: ANNOUNCE GAME");
                    // Form the tweet
                    String announceTemplate = TemplateLoader.loadTemplate(TRIVIA_ANNOUNCE_TMPL);
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("$GENRENAME$", game.getGenre().getGenreName());
                    String tweet = StringUtils.mapReplace(StringUtils.mapSplit(announceTemplate, map), map, "$");

                    // Send the tweet
                    if (tweettestmode)
                        log.debug("Announce tweet: " + tweet);
                    else {
                        try {
                            new Twitter(username, password).update(tweet);
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }

                    // Sleep until it's time for the first round
                    if (timetestmode)
                        sleepTime = TEST_DELTA;
                    else
                        sleepTime = game.getStartTime().getTime() - System.currentTimeMillis();

                    state = State.FIRST_ROUND;

                    break;

                case FIRST_ROUND:

                    log.debug("State: FIRST ROUND");

                    // Get the first part of the quote
                    if (tweettestmode)
                        log.debug("First part of the quote: " + game.getQuote().getFirstPart());
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(game.getQuote().getFirstPart());
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }

                    // Sleep until it's time for the next round
                    if (timetestmode)
                        sleepTime = TEST_DELTA;
                    else
                        sleepTime = game.getTimeBetweenRounds();

                    state = State.SECOND_ROUND;

                    break;

                case SECOND_ROUND:

                    log.debug("State: SECOND ROUND");

                    gatherReplies(game);

                    if (game.getWinnerList().size() > 0) {
                        // we have a winner!
                        state = State.ANNOUNCE_WINNER;
                        sleepTime = game.getTimeBeforeWinner();
                        break;
                    }

                    if (tweettestmode)
                        log.debug("Second part of the quote: " + game.getQuote().getSecondPart());
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(game.getQuote().getSecondPart());
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }
                    if (timetestmode)
                        sleepTime = TEST_DELTA;
                    else
                        sleepTime = game.getTimeBetweenRounds();

                    state = State.THIRD_ROUND;

                    break;

                case THIRD_ROUND:

                    log.debug("State: THIRD ROUND");

                    gatherReplies(game);

                    if (game.getWinnerList().size() > 0) {
                        // we have a winner!
                        state = State.ANNOUNCE_WINNER;
                        sleepTime = game.getTimeBeforeWinner();
                        break;
                    }

                    if (tweettestmode)
                        log.debug("Third part of the quote: " + game.getQuote().getThirdPart());
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(game.getQuote().getThirdPart());
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }

                    if (timetestmode)
                        sleepTime = TEST_DELTA;
                    else
                        sleepTime = game.getTimeBetweenRounds();

                    state = State.GET_REPLIES;

                    break;

                case GET_REPLIES:

                    log.debug("State: GET REPLIES");

                    gatherReplies(game);

                    if (game.getWinnerList().size() == 0) {
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

                    sleepTime = game.getTimeBeforeWinner();

                    break;

                case ANNOUNCE_WINNER:

                    log.debug("State: ANNOUNCE WINNER");

                    boolean noWinner = game.getWinnerList().size() == 0;

                    if (!noWinner) {
                        game.sortWinnerList();
                    }

                    // Form the tweet
                    String winnerTemplate;
                    if (noWinner)
                        winnerTemplate = TemplateLoader.loadTemplate(NO_WINNER_TMPL);
                    else
                        winnerTemplate = TemplateLoader.loadTemplate(WINNER_ANNOUNCE_TMPL);

                    log.debug("Loaded template: " + winnerTemplate);

                    Map<String,String> awMap = new HashMap<String,String>();
                    awMap.put("$MOVIETITLE$", game.getMovie().getMovieTitle());

                    if (!noWinner) {
                        User winnerUser = game.getWinnerList().get(0).getUser();
                        // Create the person if necessary and add them to the winners table
                        Connection masterConnection = null;
                        Connection slaveConnection = null;
                        try {
                            masterConnection = DataSourceManager.getMasterConnection("obviously");
                            slaveConnection = DataSourceManager.getSlaveConnection("obviously");
                            People winnerPerson = People.getPeopleByName(slaveConnection, winnerUser.getScreenName());
                            if (winnerPerson == null) {
                                People.create(masterConnection, winnerUser.getScreenName());
                                winnerPerson = People.getPeopleByName(slaveConnection, winnerUser.getScreenName());
                            }
                            Winners.create(masterConnection, winnerPerson.getPid(), game.getQuote().getQuoteId());
                        } catch (SQLException e) {
                            log.error("Caught exception while trying to update winners!", e);
                        } finally {
                            try { if (masterConnection != null) masterConnection.close(); } catch (SQLException e) { /* ignored */ }
                            try { if (slaveConnection != null) slaveConnection.close(); } catch (SQLException e) { /* ignored */ }
                        }
                        // Add their name to the template substitution map
                        awMap.put("$SCREENNAME$", winnerUser.getScreenName());
                    }

                    String awTweet = StringUtils.mapReplace(StringUtils.mapSplit(winnerTemplate, awMap), awMap, "$");

                    if (tweettestmode)
                        log.debug("Announce winner tweet: " + awTweet);
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(awTweet);
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }

                    state = State.NO_GAME;
                    // Wait 5 minutes and set up the next game
                    sleepTime = 5 * 60 * 1000L;

                    break;

                default:

                    log.debug("State: UNKNOWN");

                    break;
            }
            try { Thread.sleep(sleepTime); } catch (InterruptedException e) { /* ignored */ }
        }
    }

// Instance methods ///////////////////////////////////////////////////////////

    private void gatherReplies(Game game) {
        Twitter twitter = new Twitter(username, password);
        // Start pulling pages of replies from Twitter
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            List<Status> replies = new ArrayList<Status>();
            try {
                replies = twitter.getRepliesByPage(i);
            } catch (TwitterException e) {
                log.error("Caught exception while retrieving replies!", e);
            }
            if (replies.size() == 0) {
                break;
            }
            for (Status reply : replies) {
                // If this reply is older than the start time of the current game, ignore it
                if (reply.getCreatedAt().before(game.getStartTime()))
                    continue;
                // Record the guess
                Connection masterConnection = null;
                Connection slaveConnection = null;
                try {
                    masterConnection = DataSourceManager.getMasterConnection("obviously");
                    slaveConnection = DataSourceManager.getSlaveConnection("obviously");
                    People player = People.getPeopleByName(slaveConnection, reply.getUser().getScreenName());
                    if (player == null) {
                        People.create(masterConnection, reply.getUser().getScreenName());
                        player = People.getPeopleByName(slaveConnection, reply.getUser().getScreenName());
                    }
                    Guesses.create(masterConnection, game.getQuote().getQuoteId(), player.getPid(), reply.getText(), reply.getCreatedAt());
                } catch (SQLException e) {
                    log.error("Caught exception while trying to update winners!", e);
                } finally {
                    try { if (masterConnection != null) masterConnection.close(); } catch (SQLException e) { /* ignored */ }
                    try { if (slaveConnection != null) slaveConnection.close(); } catch (SQLException e) { /* ignored */ }
                }
                // Now see if the guess is correct
                String title = game.getMovie().getMovieTitle();
                // be forgiving, i.e. "A Fish Called Wanda" / "Fish Called Wanda"
                if (title.startsWith("A") || title.startsWith("An") || title.startsWith("The")) {
                    title = title.substring(title.indexOf(" ") + 1, title.length());
                }
                String text = reply.getText();
                Pattern pattern = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                boolean isMatch = matcher.find();
                if (isMatch) {
                    // possible WIN
                    log.debug("Reply text: " + text);
                    game.addToWinnerList(reply);
                }
            }
        }
    }

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