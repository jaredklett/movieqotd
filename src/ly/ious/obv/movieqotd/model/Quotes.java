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
 * @version $Id: Quotes.java,v 1.4 2009/02/04 03:25:42 jklett Exp $
 */

public class Quotes {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.4 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("quotes", "q");
    public static final Column QID = new Column("qid", TABLE);
    public static final Column MID = new Column("mid", TABLE);
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
