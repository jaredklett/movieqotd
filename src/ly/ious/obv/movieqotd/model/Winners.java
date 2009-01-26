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

import org.javaforge.sql.Table;
import org.javaforge.sql.Column;
import com.blipnetworks.sql.SQLBuilder;

/**
 * TODO
 *
 * @author Jared Klett
 * @version $Id: Winners.java,v 1.2 2009/01/26 04:29:49 jklett Exp $
 */

public class Winners {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.2 $";

// Table structure ////////////////////////////////////////////////////////////

    public static final Table TABLE = new Table("winners", "w");
    public static final Column WID = new Column("wid", TABLE);
    public static final Column PID = new Column("pid", TABLE);
    public static final Column QID = new Column("qid", TABLE);
    public static final Column DATESTAMP = new Column("datestamp", TABLE);

// Table column groupings /////////////////////////////////////////////////////

    private static final Column[] ALL_COLUMNS = {
            WID, PID, QID, DATESTAMP
    };

// Pre-made SQL queries ///////////////////////////////////////////////////////

    /** Generated SQL which loads all the rows in the table. */
    public static final String SQL_SELECT = SQLBuilder.buildSelect(
            new Table[] {TABLE},
            ALL_COLUMNS,
            null
    );

} // class Winners