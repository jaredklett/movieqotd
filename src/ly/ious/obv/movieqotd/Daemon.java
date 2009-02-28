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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A class that runs as a thread.
 *
 * @author Jared Klett
 * @version $Id: Daemon.java,v 1.10 2009/02/28 23:02:16 jklett Exp $
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

    private String username = "movieqotd";
    private String password = "dmitri1";
    /** TODO */
    private boolean testmode;
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
        state = State.NO_GAME;
        testmode = true;
        // Register our shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void run() {
        long delta = 5000L;
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
                    if (testmode)
                        delta = 1000L;
                    else
                        delta = game.getAnnounceTime().getTime() - System.currentTimeMillis();
                    state = State.ANNOUNCE_GAME;
                    break;
                case ANNOUNCE_GAME:
                    log.debug("State: ANNOUNCE GAME");
                    // Form the tweet
                    // TODO: externalize
                    String announceTemplate = TemplateLoader.loadTemplate("trivia_announce.tmpl");
                    log.debug("Loaded template: " + announceTemplate);
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("$GENRENAME$", game.getGenre().getGenreName());
                    String tweet = StringUtils.mapReplace(StringUtils.mapSplit(announceTemplate, map), map, "$");
                    // Send the tweet
                    if (testmode)
                        log.debug("Announce tweet: " + tweet);
                    else {
                        try {
                            new Twitter(username, password).update(tweet);
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }
                    // Sleep until it's time for the first round
                    if (testmode)
                        delta = 3000L;
                    else
                        delta = game.getStartTime().getTime() - System.currentTimeMillis();
                    state = State.FIRST_ROUND;
                    break;
                case FIRST_ROUND:
                    log.debug("State: FIRST ROUND");
                    // Get the first part of the quote
                    if (testmode)
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
                    if (testmode)
                        delta = 3000L;
                    else
                        delta = game.getTimeBetweenRounds();
                    state = State.SECOND_ROUND;
                    break;
                case SECOND_ROUND:
                    log.debug("State: SECOND ROUND");

                    // TODO: Did anyone get it?

                    if (testmode)
                        log.debug("Second part of the quote: " + game.getQuote().getSecondPart());
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(game.getQuote().getSecondPart());
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }
                    if (testmode)
                        delta = 3000L;
                    else
                        delta = game.getTimeBetweenRounds();
                    state = State.THIRD_ROUND;
                    break;
                case THIRD_ROUND:
                    log.debug("State: THIRD ROUND");

                    // TODO: Did anyone get it?

                    if (testmode)
                        log.debug("Third part of the quote: " + game.getQuote().getThirdPart());
                    else {
                        // Send the tweet
                        try {
                            new Twitter(username, password).update(game.getQuote().getThirdPart());
                        } catch (TwitterException e) {
                            log.error("Caught exception while sending tweet!", e);
                        }
                    }
                    if (testmode)
                        delta = 3000L;
                    else
                        delta = game.getTimeBetweenRounds();
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
                            if (text.equalsIgnoreCase(game.getMovie().getMovieTitle())) {
                                // possible WIN
                                game.addToWinnerList(reply);
                            }
                        }
                    }
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
                    break;
                case ANNOUNCE_WINNER:
                    log.debug("State: ANNOUNCE WINNER");
                    boolean noWinner = game.getWinnerList().size() == 0;
                    if (!noWinner) {
                        game.sortWinnerList();
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
                    awMap.put("$MOVIETITLE$", game.getMovie().getMovieTitle());
                    if (!noWinner)
                        awMap.put("$SCREENNAME$", game.getWinnerList().get(0).getUser().getScreenName());
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