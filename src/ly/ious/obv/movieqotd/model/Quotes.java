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

import org.javaforge.sql.Table;
import org.javaforge.sql.Column;
import com.blipnetworks.sql.SQLBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: jklett
 * Date: Jan 24, 2009
 * Time: 6:54:49 PM
 * To change this template use File | Settings | File Templates.
 */

public class Quotes {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.1 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("quotes", "q");
    public static final Column QID = new Column("qid", TABLE);
    public static final Column MID = new Column("mid", TABLE);
    public static final Column QUOTE_TEXT = new Column("quote_text", TABLE);
    public static final Column USED = new Column("used", TABLE);
    public static final Column USED_DATESTAMP = new Column("used_datestamp", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            QID, MID, QUOTE_TEXT, USED, USED_DATESTAMP
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    /** Generated SQL which loads all the rows in the table. */
    public static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

} // class Quotes