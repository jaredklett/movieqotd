/*
 * @(#)TemplateLoader.java
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

import java.util.Scanner;

/**
 * All this class does is load template files.
 *
 * @author Jared Klett
 * @version $Id: TemplateLoader.java,v 1.1 2009/02/14 19:07:48 jklett Exp $
 */

public class TemplateLoader {

    public static String loadTemplate(String templateName) {
        String template;
        Scanner scanner = new Scanner(ClassLoader.getSystemResourceAsStream(templateName));
        template = scanner.useDelimiter("\\A").next();
        scanner.close();
        return template;
    }

} // class TemplateLoader
