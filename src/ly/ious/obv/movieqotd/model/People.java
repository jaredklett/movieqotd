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

import org.javaforge.sql.Table;
import org.javaforge.sql.Column;
import com.blipnetworks.sql.SQLBuilder;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: People.java,v 1.2 2009/01/26 04:29:49 jklett Exp $
 */

public class People {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.2 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("people", "p");
    public static final Column PID = new Column("qid", TABLE);
    public static final Column TWITTER_NAME = new Column("twitter_name", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            PID, TWITTER_NAME
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    /** Generated SQL which loads all the rows in the table. */
    public static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

} // class People