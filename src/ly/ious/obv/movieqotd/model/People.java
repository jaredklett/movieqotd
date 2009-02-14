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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: People.java,v 1.4 2009/02/14 18:00:35 jklett Exp $
 */

public class People {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.4 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("people", "p");
    public static final Column PID = new Column("person_id", TABLE);
    public static final Column TWITTER_NAME = new Column("twitter_name", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            PID, TWITTER_NAME
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_PEOPLE_BY_PID = {
            PID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
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

// Instance variables /////////////////////////////////////////////////////////

    private int pid;
    private String twitterName;

// Constructor ////////////////////////////////////////////////////////////////

    private People() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static People getPeople(Connection connection, int pid) throws SQLException {
        People people = null;
        Object[] values = { pid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_PEOPLE_BY_PID, values);
        if (rs.next())
            people = setParams(rs);
        rs.close();
        return people;
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
