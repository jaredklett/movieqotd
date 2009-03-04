/*
 * @(#)Genres.java
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
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Genres.java,v 1.6 2009/03/04 00:55:07 jklett Exp $
 */

public class Genres {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.6 $";

// Constants //////////////////////////////////////////////////////////////////

    private static final Genres[] TYPE_ARRAY = new Genres[0];

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Genres.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movie_genres", "mg");
    public static final Column GID = new Column("genre_id", TABLE);
    public static final Column GENRE_NAME = new Column("genre_name", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            GID, GENRE_NAME
    };

    private static final Column[] INSERT_COLUMNS = {
            GENRE_NAME
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_GENRE_BY_GID = {
            GID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null,
            SQLConstants.SQL_ASC
    );

    private static final String SQL_GET_GENRE_BY_GID = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_GENRE_BY_GID
    );

    private static final String SQL_INSERT = SQLBuilder.buildInsert(TABLE, INSERT_COLUMNS, null);

// Instance variables /////////////////////////////////////////////////////////

    private int genreId;
    private String genreName;

// Constructor ////////////////////////////////////////////////////////////////

    private Genres() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static Genres[] getAll(Connection connection) throws SQLException {
        List<Genres> list = new ArrayList<Genres>();
        Object[] values = {
                2 // order by name
        };
        ResultSet rs = SQLExec.doQuery(connection, SQL_SELECT, values);
        while (rs.next()) {
            list.add(setParams(rs));
        }
        rs.close();
        return list.toArray(TYPE_ARRAY);
    }

    public static Genres getGenre(Connection connection, int gid) throws SQLException {
        Genres genre = null;
        Object[] values = { gid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_GENRE_BY_GID, values);
        if (rs.next())
            genre = setParams(rs);
        rs.close();
        return genre;
    }

    public static boolean create(Connection connection, String genreName) throws SQLException {
        Object[] values = { genreName };
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
    private static Genres setParams(ResultSet rs) throws SQLException {
        int i = 1;
        Genres genre = new Genres();
        genre.setGenreId(rs.getInt(i));
        genre.setGenreName(rs.getString(++i));
        return genre;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

} // class Genres
