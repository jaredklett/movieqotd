/*
 * @(#)Main.java
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

import org.mortbay.jetty.Server;
import org.apache.log4j.Logger;

/**
 * Main application class.
 *
 * @author Jared Klett
 * @version $Id: Main.java,v 1.2 2009/03/05 17:06:13 jklett Exp $
 */

public class Main implements Runnable {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.2 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Main.class);

// Instance variables /////////////////////////////////////////////////////////

    /** The internal thread checks this flag on each iteration to see if it should exit. */
    private boolean running;
    /** An internal thread to keep an eye on the web server object. */
    private Thread thread;
    /** A mutex object for the internal thread to wait on. */
    private final Object mutex = new Object();
    /** The Jetty web server. */
    private Server server;
    /** The Jetty web server. */
    private Daemon daemon;
    /** An initialized but not running thread to call our clean up method when the JVM exits. */
    private Thread shutdownHook = new Thread("Main.shutdownHook") {
        public void run() {
            // Halt our internal thread
            stopThread();
        }
    };

// Constructor ////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    public Main() {
        // Create the Jetty server
        try {
            server = new Server(ClassLoader.getSystemResource("jetty.xml"));
        } catch (Exception e) {
            log.error("Could not load Jetty configuration!", e);
        }
        // Create the game daemon
        daemon = new Daemon();
        // Register our shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

// Instance methods ///////////////////////////////////////////////////////////

    /**
     * Runnable run method.
     */
    public void run() {
        // 1. Start Jetty and the daemon
        try {
            server.start();
        } catch (Exception e) {
            log.error("Could not start the Jetty server!", e);
        }
        daemon.startThread();

        // 2. Block on our monitor
        while(running) {
            synchronized(mutex) {
                try {
                    mutex.wait();
                } catch(InterruptedException e) {
                    log.warn("Internal thread interrupted while waiting...", e);
                }
            }
        }

        // 3. We were notified that we're shutting down, so stop Jetty
        try {
            server.stop();
        } catch (Exception e) {
            log.error("Could not shut down the Jetty server!", e);
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
        synchronized(mutex) {
            mutex.notify();
        }
        try {
            thread.join(1000);
        } catch(InterruptedException e) {
            log.warn("Interrupted while waiting for internal thread to join...", e);
        }
        thread = null;
    }

    /**
     * Class main method. Creates a new instance and kicks off the thread.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.startThread();
    }

} // class Main