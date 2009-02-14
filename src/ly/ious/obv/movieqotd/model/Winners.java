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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Winners.java,v 1.4 2009/02/14 18:00:35 jklett Exp $
 */

public class Winners {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.4 $";

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

// Instance variables /////////////////////////////////////////////////////////

    private int wid;
    private int pid;
    private int qid;
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
        winner.setWid(rs.getInt(i));
        winner.setPid(rs.getInt(++i));
        winner.setQid(rs.getInt(++i));
        winner.setDatestamp(rs.getDate(++i));
        return winner;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getWid() {
        return wid;
    }

    public int getPid() {
        return pid;
    }

    public int getQid() {
        return qid;
    }

    public Date getDatestamp() {
        return datestamp;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setWid(int wid) {
        this.wid = wid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public void setDatestamp(Date datestamp) {
        this.datestamp = datestamp;
    }

} // class Winners
