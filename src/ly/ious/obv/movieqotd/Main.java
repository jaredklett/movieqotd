/*
 * @(#)Main.java
 *
 * Copyright (c) 2004-2008 by Obviously, Inc.
 * 187 Lafayette St, 6th Floor
 * New York, NY 10013
 * All rights reserved.
 *
 * This software is the confidential and
 * proprietary information of Obviously, Inc.
 */

package ly.ious.obv.movieqotd;

/**
 * Main application class.
 *
 * @author Jared Klett
 * @version $Id: Main.java,v 1.1 2009/01/26 04:19:57 jklett Exp $
 */

public class Main {

    public static void main(String[] args) {
        Daemon daemon = new Daemon();
        daemon.startThread();
    }

} // class Main