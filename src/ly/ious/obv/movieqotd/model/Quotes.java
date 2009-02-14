/*
 * @(#)Quotes.java
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
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Quotes.java,v 1.6 2009/02/14 19:30:07 jklett Exp $
 */

public class Quotes {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.6 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Quotes.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movie_quotes", "mq");
    public static final Column QID = new Column("quote_id", TABLE);
    public static final Column MID = new Column("movie_id", TABLE);
    public static final Column QUOTE_TEXT = new Column("quote_text", TABLE);
    public static final Column USED = new Column("used", TABLE);
    public static final Column USED_DATESTAMP = new Column("used_timestamp", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            QID, MID, QUOTE_TEXT, USED, USED_DATESTAMP
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    private static final Object[] WHERE_GET_QUOTE_BY_QID = {
            QID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    private static final Object[] WHERE_UPDATE_USED = {
            QID, SQLConstants.SQL_EQ, SQLConstants.SQL_QUES
    };

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

    private static final String SQL_GET_QUOTE_BY_QID = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            WHERE_GET_QUOTE_BY_QID
    );

    private static final String SQL_UPDATE_USED = SQLBuilder.buildUpdate(
            new Table[] {TABLE},
            new Column[] { USED, USED_DATESTAMP },
            null,
            WHERE_UPDATE_USED
    );

// Instance variables /////////////////////////////////////////////////////////

    private int qid;
    private int mid;
    private String quoteText;
    private boolean used;
    private Date usedDatestamp;

// Constructor ////////////////////////////////////////////////////////////////

    private Quotes() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static Quotes getRandomQuote(Connection connection) throws SQLException {
        List<Quotes> list = new ArrayList<Quotes>();
        Object[] values = {
                0 // not used
        };
        ResultSet rs = SQLExec.doQuery(connection, SQL_SELECT, values);
        while (rs.next()) {
            Quotes quote = setParams(rs);
            list.add(quote);
        }
        rs.close();
        return list.get(new Random().nextInt(list.size()));
    }

    public static Quotes getQuote(Connection connection, int qid) throws SQLException {
        Quotes quote = null;
        Object[] values = { qid };
        ResultSet rs = SQLExec.doQuery(connection, SQL_GET_QUOTE_BY_QID, values);
        if (rs.next())
            quote = setParams(rs);
        rs.close();
        return quote;
    }

    /**
     * Convenience method to create a new <code>Quotes</code> object and
     * populate it with the fields from the passed result set.
     *
     * @param rs The <code>ResultSet</code> to get the values from.
     * @return A newly populated <code>Quotes</code> object.
     * @throws SQLException If a database access error occurs.
     */
    private static Quotes setParams(ResultSet rs) throws SQLException {
        int i = 1;
        Quotes quote = new Quotes();
        quote.setQid(rs.getInt(i));
        quote.setMid(rs.getInt(++i));
        quote.setQuoteText(rs.getString(++i));
        quote.setUsed(rs.getInt(++i) == 1);
        quote.setUsedDatestamp(rs.getDate(++i));
        return quote;
    }

    public boolean updateUsed(Connection connection) throws SQLException {
        Object[] values = { used, usedDatestamp, qid };
        int rowsUpdated = SQLExec.doUpdate(connection, SQL_UPDATE_USED, values);
        boolean updated = rowsUpdated > 0;
        if (!updated)
            log.warn("Updating quote failed; ID: " + qid + "; " + rowsUpdated + " rows returned!");
        return updated;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getQid() {
        return qid;
    }

    public int getMid() {
        return mid;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public boolean isUsed() {
        return used;
    }

    public Date getUsedDatestamp() {
        return usedDatestamp;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setQid(int qid) {
        this.qid = qid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setUsedDatestamp(Date usedDatestamp) {
        this.usedDatestamp = usedDatestamp;
    }

} // class Quotes
