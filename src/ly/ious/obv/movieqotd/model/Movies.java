/*
 * @(#)Movies.java
 *
 * Copyright (c) 2004-2008 by Obviously, Inc.
 * 187 Lafayette St, 6th Floor
 * New York, NY 10013
 * All rights reserved.
 *
 * This software is the confidential and
 * proprietary information of Obviously, Inc.
 */

package ly.ious.obv.movieqotd.model;

import com.blipnetworks.sql.SQLBuilder;
import com.blipnetworks.sql.SQLConstants;
import com.blipnetworks.sql.SQLExec;
import org.apache.log4j.Logger;
import org.javaforge.sql.Column;
import org.javaforge.sql.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Movies.java,v 1.7 2009/03/04 01:58:11 jklett Exp $
 */

public class Movies {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.7 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Movies.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movies", "m");
    public static final Column MID = new Column("movie_id", TABLE);
    public static final Column MOVIE_TITLE = new Column("movie_title", TABLE);
    public static final Column GID = new Column("genre_id", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            MID, MOVIE_TITLE, GID
    };

    private static final Column[] INSERT_COLUMNS = {
            MOVIE_TITLE, GID
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_MOVIE_BY_MID = {
            MID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    private static final Object[] WHERE_GET_MOVIE_BY_TITLE = {
            MOVIE_TITLE, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

    private static final String SQL_GET_MOVIE_BY_MID = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_MOVIE_BY_MID
    );

    private static final String SQL_GET_MOVIE_BY_TITLE = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_MOVIE_BY_TITLE
    );

    private static final String SQL_INSERT = SQLBuilder.buildInsert(TABLE, INSERT_COLUMNS, null);

// Instance variables /////////////////////////////////////////////////////////

    private int movieId;
    private String movieTitle;
    private int genreId;

// Constructor ////////////////////////////////////////////////////////////////

    private Movies() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static Movies getMovieById(Connection connection, int mid) throws SQLException {
        Movies movie = null;
        Object[] values = { mid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_MOVIE_BY_MID, values);
        if (rs.next())
            movie = setParams(rs);
        rs.close();
        return movie;
    }

    public static Movies getMovieByTitle(Connection connection, String movieTitle) throws SQLException {
        Movies movie = null;
        Object[] values = { movieTitle };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_MOVIE_BY_TITLE, values);
        if (rs.next())
            movie = setParams(rs);
        rs.close();
        return movie;
    }

    public static boolean create(Connection connection, String movieTitle, int genreId) throws SQLException {
        Object[] values = { movieTitle, genreId };
        int rowsInserted = SQLExec.doUpdate(connection, SQL_INSERT, values);
        boolean success = rowsInserted > 0;
        if (!success)
            log.warn("Attempted to create record; " + rowsInserted + " returned from insert!");
        return success;
    }

    /**
     * Convenience method to create a new <code>Quotes</code> object and
     * populate it with the fields from the passed result set.
     *
     * @param rs The <code>ResultSet</code> to get the values from.
     * @return A newly populated <code>Quotes</code> object.
     * @throws SQLException If a database access error occurs.
     */
    private static Movies setParams(ResultSet rs) throws SQLException {
        int i = 1;
        Movies movie = new Movies();
        movie.setMovieId(rs.getInt(i));
        movie.setMovieTitle(rs.getString(++i));
        movie.setGenreId(rs.getInt(++i));
        return movie;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public int getGenreId() {
        return genreId;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

} // class Movies
