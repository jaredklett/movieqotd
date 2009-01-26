/*
 * @(#)Game.java
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
import org.javaforge.util.Config;

import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Encapsulates information about a game.
 *
 * @author Jared Klett
 * @version $Id: Game.java,v 1.1 2009/01/26 04:19:57 jklett Exp $
 */

public class Game {

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Game.class);

// Configuration //////////////////////////////////////////////////////////////

    public static final String CONFIG_NAME = "game";
    public static final String PROPERTY_ANNOUNCE_TIME = "announce.time";
    public static final String PROPERTY_START_TIME = "start.time";
    public static final String PROPERTY_TIME_BETWEEN_ROUNDS = "time.between.rounds";

    /** TODO */
    private static String announceTime;
    /** TODO */
    private static String startTime;
    /** TODO */
    private static long timeBetweenRounds;

    public static final String DEFAULT_ANNOUNCE_TIME = "12:00 EST";
    public static final String DEFAULT_START_TIME = "13:00 EST";
    public static final long DEFAULT_TIME_BETWEEN_ROUNDS = 60 * 60 * 1000L;

// Class initializer //////////////////////////////////////////////////////////

    static {
        loadConfiguration();
    }

// Static methods /////////////////////////////////////////////////////////////

    public static void loadConfiguration() {
        Config config = new Config(CONFIG_NAME);

        synchronized (Game.class) {
            announceTime = config.stringProperty(PROPERTY_ANNOUNCE_TIME, DEFAULT_ANNOUNCE_TIME);
            startTime = config.stringProperty(PROPERTY_START_TIME, DEFAULT_START_TIME);
            timeBetweenRounds = config.longProperty(PROPERTY_TIME_BETWEEN_ROUNDS, DEFAULT_TIME_BETWEEN_ROUNDS);
        }

        log.info("Loading configuration from file: " + CONFIG_NAME);
        log.info(PROPERTY_ANNOUNCE_TIME + " = " + announceTime);
        log.info(PROPERTY_START_TIME + " = " + startTime);
        log.info(PROPERTY_TIME_BETWEEN_ROUNDS + " = " + timeBetweenRounds);
    }

// Constructor ////////////////////////////////////////////////////////////////

    public Game() {

    }

// Accessors //////////////////////////////////////////////////////////////////

    public Date getAnnounceTime() {
        return getTimeAsDate(announceTime);
    }

    public Date getStartTime() {
        return getTimeAsDate(startTime);
    }

    public long getTimeBetweenRounds() {
        return timeBetweenRounds;
    }

    private Date getTimeAsDate(String time) {
        Calendar now = Calendar.getInstance();
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ZZZ");
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            log.error("Caught exception while parsing announce time, wrapping and re-throwing...", e);
            throw new IllegalArgumentException(e);
        }
        Calendar then = Calendar.getInstance();
        then.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, then.get(Calendar.HOUR_OF_DAY));
        now.set(Calendar.MINUTE, then.get(Calendar.MINUTE));
        return now.getTime();
    }


    public static void main(String[] args) {
        Game game = new Game();
        System.out.println(game.getAnnounceTime());
        System.out.println(game.getStartTime());
    }

} // class Game