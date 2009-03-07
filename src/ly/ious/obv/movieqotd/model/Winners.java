/*
 * @(#)Winners.java
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
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Winners.java,v 1.7 2009/03/07 20:53:37 jklett Exp $
 */

public class Winners {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.7 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Winners.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movie_winners", "mw");
    public static final Column WID = new Column("movie_win_id", TABLE);
    public static final Column PID = new Column("person_id", TABLE);
    public static final Column QID = new Column("quote_id", TABLE);
    public static final Column DATESTAMP = new Column("datestamp", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            WID, PID, QID, DATESTAMP
    };

    private static final Column[] INSERT_COLUMNS = {
            PID, QID, DATESTAMP
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_WINNER_BY_WID = {
            WID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

    private static final String SQL_GET_WINNER_BY_WID = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_WINNER_BY_WID
    );

    private static final String SQL_INSERT = SQLBuilder.buildInsert(TABLE, INSERT_COLUMNS, null);

// Instance variables /////////////////////////////////////////////////////////

    private int movieWinnerId;
    private int personId;
    private int quoteId;
    private Date datestamp;

// Constructor ////////////////////////////////////////////////////////////////

    private Winners() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static Winners getWinner(Connection connection, int wid) throws SQLException {
        Winners winner = null;
        Object[] values = { wid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_WINNER_BY_WID, values);
        if (rs.next())
            winner = setParams(rs);
        rs.close();
        return winner;
    }

    public static boolean create(Connection connection, int personId, int quoteId) throws SQLException {
        Object[] values = { personId, quoteId, new Date() };
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
    private static Winners setParams(ResultSet rs) throws SQLException {
        int i = 1;
        Winners winner = new Winners();
        winner.setMovieWinnerId(rs.getInt(i));
        winner.setPersonId(rs.getInt(++i));
        winner.setQuoteId(rs.getInt(++i));
        winner.setDatestamp(rs.getDate(++i));
        return winner;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getMovieWinnerId() {
        return movieWinnerId;
    }

    public int getPersonId() {
        return personId;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public Date getDatestamp() {
        return datestamp;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setMovieWinnerId(int movieWinnerId) {
        this.movieWinnerId = movieWinnerId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public void setDatestamp(Date datestamp) {
        this.datestamp = datestamp;
    }

} // class Winners
