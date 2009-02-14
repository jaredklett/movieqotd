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
import org.javaforge.sql.Column;
import org.javaforge.sql.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Movies.java,v 1.4 2009/02/14 18:00:35 jklett Exp $
 */

public class Movies {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.4 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movies", "m");
    public static final Column MID = new Column("movie_id", TABLE);
    public static final Column MOVIE_TITLE = new Column("movie_title", TABLE);
    public static final Column GID = new Column("genre_id", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            MID, MOVIE_TITLE, GID
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_MOVIE_BY_MID = {
            MID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
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

// Instance variables /////////////////////////////////////////////////////////

    private int mid;
    private String movieTitle;
    private int gid;

// Constructor ////////////////////////////////////////////////////////////////

    private Movies() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static Movies getMovie(Connection connection, int mid) throws SQLException {
        Movies movie = null;
        Object[] values = { mid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_MOVIE_BY_MID, values);
        if (rs.next())
            movie = setParams(rs);
        rs.close();
        return movie;
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
        movie.setMid(rs.getInt(i));
        movie.setMovieTitle(rs.getString(++i));
        movie.setGid(rs.getInt(++i));
        return movie;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getMid() {
        return mid;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public int getGid() {
        return gid;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setMid(int mid) {
        this.mid = mid;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

} // class Movies
