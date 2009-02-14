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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class that runs as a thread.
 *
 * @author Jared Klett
 * @version $Id: Daemon.java,v 1.4 2009/02/14 20:28:11 jklett Exp $
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

// Instance variables /////////////////////////////////////////////////////////

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
                        movie = Movies.getMovie(slaveConnection, quote.getMid());
                        genre = Genres.getGenre(slaveConnection, movie.getGid());
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
                    delta = game.getStartTime().getTime() - System.currentTimeMillis();
                    state = State.FIRST_ROUND;
                    break;
                case FIRST_ROUND:
                    log.debug("State: FIRST ROUND");
                    // Get the first part of the quote
                    String quoteText = currentQuote.getQuoteText();
                    String[] words = quoteText.split(" ");
                    int wordCountPerPart = words.length / 3;
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < wordCountPerPart; i++) {
                        builder.append(words[i]);
                    }
                    log.debug("First part of the quote: " + builder.toString());
                    // Send the tweet
                    // TODO
                    // Sleep until it's time for the next round
                    delta = game.getTimeBetweenRounds();
                    state = State.SECOND_ROUND;
                    break;
                case SECOND_ROUND:
                    log.debug("State: SECOND ROUND");
                    // Did anyone get it?
                    // TODO
                    break;
                case THIRD_ROUND:
                    log.debug("State: THIRD ROUND");
                    break;
                case GET_REPLIES:
                    log.debug("State: GET REPLIES");
                    break;
                case ANNOUNCE_WINNER:
                    log.debug("State: ANNOUNCE WINNER");
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