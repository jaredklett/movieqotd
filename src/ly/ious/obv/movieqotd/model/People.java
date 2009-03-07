/*
 * @(#)People.java
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

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: People.java,v 1.5 2009/03/07 20:45:21 jklett Exp $
 */

public class People {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.5 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(People.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("people", "p");
    public static final Column PID = new Column("person_id", TABLE);
    public static final Column TWITTER_NAME = new Column("twitter_name", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            PID, TWITTER_NAME
    };

    private static final Column[] INSERT_COLUMNS = {
            TWITTER_NAME
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_PEOPLE_BY_PID = {
            PID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    private static final Object[] WHERE_GET_PEOPLE_BY_NAME = {
            TWITTER_NAME, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

    private static final String SQL_GET_PEOPLE_BY_PID = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_PEOPLE_BY_PID
    );

    private static final String SQL_GET_PEOPLE_BY_NAME = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_PEOPLE_BY_NAME
    );

    private static final String SQL_INSERT = SQLBuilder.buildInsert(TABLE, INSERT_COLUMNS, null);

// Instance variables /////////////////////////////////////////////////////////

    private int pid;
    private String twitterName;

// Constructor ////////////////////////////////////////////////////////////////

    private People() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static People getPeopleById(Connection connection, int pid) throws SQLException {
        People people = null;
        Object[] values = { pid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_PEOPLE_BY_PID, values);
        if (rs.next())
            people = setParams(rs);
        rs.close();
        return people;
    }

    public static People getPeopleByName(Connection connection, String twitterName) throws SQLException {
        People people = null;
        Object[] values = { twitterName };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_PEOPLE_BY_NAME, values);
        if (rs.next())
            people = setParams(rs);
        rs.close();
        return people;
    }

    public static boolean create(Connection connection, String twitterName) throws SQLException {
        Object[] values = { twitterName };
        int rowsInserted = SQLExec.doUpdate(connection, SQL_INSERT, values);
        boolean success = rowsInserted > 0;
        if (!success)
            log.warn("Attempted to create record; " + rowsInserted + " returned from insert!");
        return success;
    }

    /**
     * Convenience method to create a new <code>Conversion</code> object and
     * populate it with the fields from the passed result set.
     *
     * @param rs The <code>ResultSet</code> to get the values from.
     * @return A newly populated <code>Conversion</code> object.
     * @throws SQLException If a database access error occurs.
     */
    private static People setParams(ResultSet rs) throws SQLException {
        int i = 1;
        People people = new People();
        people.setPid(rs.getInt(i));
        people.setTwitterName(rs.getString(++i));
        return people;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getPid() {
        return pid;
    }

    public String getTwitterName() {
        return twitterName;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setTwitterName(String twitterName) {
        this.twitterName = twitterName;
    }

} // class People
