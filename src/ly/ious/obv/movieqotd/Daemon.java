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

import java.util.UUID;
import java.util.Date;

/**
 * A class that runs as a thread.
 *
 * @author Jared Klett
 * @version $Id: Daemon.java,v 1.1 2009/01/26 04:19:57 jklett Exp $
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
                    delta = game.getAnnounceTime().getTime() - System.currentTimeMillis();
                    state = State.ANNOUNCE_GAME;
                    break;
                case ANNOUNCE_GAME:
                    log.debug("State: ANNOUNCE GAME");
                    // Pick a quote
                    // TODO
                    // Form the tweet
                    // TODO
                    // Send the tweet
                    // TODO
                    // Sleep until it's time for the first round
                    delta = game.getStartTime().getTime() - System.currentTimeMillis();
                    state = State.FIRST_ROUND;
                    break;
                case FIRST_ROUND:
                    log.debug("State: FIRST ROUND");
                    // Get the first part of the quote
                    // TODO
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