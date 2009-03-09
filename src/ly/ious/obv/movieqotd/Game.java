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

import com.blipnetworks.sql.DataSourceManager;
import ly.ious.obv.movieqotd.model.Genres;
import ly.ious.obv.movieqotd.model.Movies;
import ly.ious.obv.movieqotd.model.Quotes;
import org.apache.log4j.Logger;
import org.javaforge.util.Config;
import twitter4j.Status;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Encapsulates information about a game.
 *
 * @author Jared Klett
 * @version $Id: Game.java,v 1.8 2009/03/09 03:10:07 jklett Exp $
 */

public class Game {

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Game.class);

    private static Comparator<Status> ascendingDateStatusComparator = new Comparator<Status>() {
        public int compare(Status s1, Status s2) {
            if (s1.getCreatedAt().before(s2.getCreatedAt()))
                return -1;
            if (s1.getCreatedAt().after(s2.getCreatedAt()))
                return 1;
            return 0;
        }
    };

// Configuration //////////////////////////////////////////////////////////////

    public static final String CONFIG_NAME = "game";
    public static final String PROPERTY_ANNOUNCE_TIME = "announce.time";
    public static final String PROPERTY_START_TIME = "start.time";
    public static final String PROPERTY_TIME_BETWEEN_ROUNDS = "time.between.rounds";
    public static final String PROPERTY_TIME_BEFORE_WINNER = "time.before.winner";
    public static final String PROPERTY_SITE = "site";

    /** TODO */
    private static String announceTime;
    /** TODO */
    private static String startTime;
    /** TODO */
    private static long timeBetweenRounds;
    /** TODO */
    private static long timeBeforeWinner;
    /** TODO */
    private static String site;

    public static final String DEFAULT_ANNOUNCE_TIME = "13:00 EST";
    public static final String DEFAULT_START_TIME = "14:00 EST";
    public static final long DEFAULT_TIME_BETWEEN_ROUNDS = 60 * 60 * 1000L;
    public static final long DEFAULT_TIME_BEFORE_WINNER = 60 * 1000L;
    public static final String DEFAULT_SITE = "obviously";

// Instance initializer ///////////////////////////////////////////////////////

    private List<Status> winnerList;
    private Quotes quote;
    private Movies movie;
    private Genres genre;

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
            timeBeforeWinner = config.longProperty(PROPERTY_TIME_BEFORE_WINNER, DEFAULT_TIME_BEFORE_WINNER);
            site = config.stringProperty(PROPERTY_SITE, DEFAULT_SITE);
        }

        log.info("Loading configuration from file: " + CONFIG_NAME);
        log.info(PROPERTY_ANNOUNCE_TIME + " = " + announceTime);
        log.info(PROPERTY_START_TIME + " = " + startTime);
        log.info(PROPERTY_TIME_BETWEEN_ROUNDS + " = " + timeBetweenRounds);
        log.info(PROPERTY_TIME_BEFORE_WINNER + " = " + timeBeforeWinner);
        log.info(PROPERTY_SITE + " = " + site);
    }

// Constructor ////////////////////////////////////////////////////////////////

    public Game() throws SQLException {
        winnerList = new ArrayList<Status>();
        // Pick a quote
        Connection masterConnection;
        Connection slaveConnection;
        masterConnection = DataSourceManager.getMasterConnection(site);
        slaveConnection = DataSourceManager.getSlaveConnection(site);
        log.debug("Opened database connections...");
        quote = Quotes.getRandomQuote(slaveConnection);
        movie = Movies.getMovieById(slaveConnection, quote.getMovieId());
        genre = Genres.getGenre(slaveConnection, movie.getGenreId());
        // Mark that it's been used
        quote.setUsed(true);
        quote.setUsedDatestamp(new Date());
        boolean success = quote.updateUsed(masterConnection);
        if (!success) {
            // TODO: what to do? rollback!
            log.warn("What the heck do I do now?");
        }
        // Clean up database connections
        try { masterConnection.close(); } catch (SQLException e) { /* ignored */ }
        try { slaveConnection.close(); } catch (SQLException e) { /* ignored */ }
    }

// Instance methods ///////////////////////////////////////////////////////////

    public void sortWinnerList() {
        Collections.sort(winnerList, ascendingDateStatusComparator);
    }

    public void addToWinnerList(Status status) {
        winnerList.add(status);
    }

// Accessors //////////////////////////////////////////////////////////////////

    public Date getAnnounceTime() {
        return getTimeAsDate(announceTime, true);
    }

    public Date getStartTime() {
        return getTimeAsDate(startTime, false);
    }

    public long getTimeBetweenRounds() {
        return timeBetweenRounds;
    }

    public long getTimeBeforeWinner() {
        return timeBeforeWinner;
    }

    private Date getTimeAsDate(String time, boolean tomorrow) {
        Calendar now = Calendar.getInstance();
        int todayOfYear = now.get(Calendar.DAY_OF_YEAR);
        int year = now.get(Calendar.YEAR);
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
        then.set(Calendar.DAY_OF_YEAR, tomorrow ? todayOfYear + 1 : todayOfYear);
        then.set(Calendar.YEAR, year);
        return then.getTime();
    }

    public Quotes getQuote() {
        return quote;
    }

    public Movies getMovie() {
        return movie;
    }

    public Genres getGenre() {
        return genre;
    }

    public List<Status> getWinnerList() {
        return winnerList;
    }

// Main method ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        try {
            Game game = new Game();
            System.out.println(game.getAnnounceTime());
            System.out.println(game.getStartTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

} // class Game