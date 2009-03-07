/*
 * @(#)Guesses.java
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

import org.apache.log4j.Logger;
import org.javaforge.sql.Table;
import org.javaforge.sql.Column;
import com.blipnetworks.sql.SQLBuilder;
import com.blipnetworks.sql.SQLExec;

import java.util.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Guesses.java,v 1.1 2009/03/07 22:33:36 jklett Exp $
 */

public class Guesses {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.1 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(People.class);

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("movie_guesses", "mg");
    public static final Column MGID = new Column("movie_guess_id", TABLE);
    public static final Column QID = new Column("quote_id", TABLE);
    public static final Column PID = new Column("person_id", TABLE);
    public static final Column GUESS_TEXT = new Column("guess_text", TABLE);
    public static final Column DATESTAMP = new Column("datestamp", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            MGID, QID, PID, GUESS_TEXT, DATESTAMP
    };

    private static final Column[] INSERT_COLUMNS = {
            QID, PID, GUESS_TEXT, DATESTAMP
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    /** Generated SQL which loads all the rows in the table. */
    private static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

    private static final String SQL_INSERT = SQLBuilder.buildInsert(TABLE, INSERT_COLUMNS, null);

// Instance variables /////////////////////////////////////////////////////////

    private int movieGuessId;
    private int quoteId;
    private int personId;
    private String guessText;
    private Date datestamp;

// Constructor ////////////////////////////////////////////////////////////////

    private Guesses() {
        // should never be called outside
    }

// Class methods //////////////////////////////////////////////////////////////

    public static boolean create(Connection connection, int quoteId, int personId, String guessText, Date datestamp) throws SQLException {
        Object[] values = { quoteId, personId, guessText, datestamp };
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
    private static Guesses setParams(ResultSet rs) throws SQLException {
        int i = 1;
        Guesses guess = new Guesses();
        guess.setMovieGuessId(rs.getInt(i));
        guess.setQuoteId(rs.getInt(++i));
        guess.setPersonId(rs.getInt(++i));
        guess.setGuessText(rs.getString(++i));
        guess.setDatestamp(rs.getDate(++i));
        return guess;
    }

// Accessors //////////////////////////////////////////////////////////////////

    public int getMovieGuessId() {
        return movieGuessId;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public int getPersonId() {
        return personId;
    }

    public String getGuessText() {
        return guessText;
    }

    public Date getDatestamp() {
        return datestamp;
    }

// Mutators ///////////////////////////////////////////////////////////////////

    public void setMovieGuessId(int movieGuessId) {
        this.movieGuessId = movieGuessId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setGuessText(String guessText) {
        this.guessText = guessText;
    }

    public void setDatestamp(Date datestamp) {
        this.datestamp = datestamp;
    }

} // class Guesses
