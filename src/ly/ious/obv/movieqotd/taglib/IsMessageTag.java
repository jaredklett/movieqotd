/*
 * @(#)IsMessageTag.java
 *
 * Copyright (c) 2004-2008 by Obviously, Inc.
 * 187 Lafayette St, 6th Floor
 * New York, NY 10013
 * All rights reserved.
 *
 * This software is the confidential and
 * proprietary information of Obviously, Inc.
 */

package ly.ious.obv.movieqotd.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Only evaluates the body if there is a non-null, non-empty message in
 * the session.
 * Note: It is legal for the same tag object to be reused
 *
 * @author Jared Klett
 * @version $Id: IsMessageTag.java,v 1.1 2009/03/04 01:39:06 jklett Exp $
 */

public class IsMessageTag extends TagSupport {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.1 $";

    /** Attribute: Change semantics to "IsNoMessage" */
    private boolean opposite = false;

    /**
     * Creates new Tag
     */
    public IsMessageTag() {
        super();
    }

// Tag processing /////////////////////////////////////////////////////////////

    /**
     * Evaluate the body ONLY if the message exists and is non empty, or do
     * exactly the opposite.
     *
     * @throws javax.servlet.jsp.JspException on system level error
     */
    public int doStartTag() throws JspException {
        Object msgObj = pageContext.getSession().getAttribute(MessageTag.VARIABLE_MESSAGE);
        boolean retval = msgObj != null && !msgObj.toString().equals("");
        if (opposite)
            retval = !retval;
        return retval ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    /**
     * At the end, we define our user from the ID value, after checking that
     * he is logged in, etc.
     */
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    /**
     * Getter for property opposite.
     *
     * @return Value of property opposite.
     */
    public boolean isOpposite() {
        return opposite;
    }

    /**
     * Setter for property opposite.
     *
     * @param opposite New value of property opposite.
     */
    public void setOpposite(boolean opposite) {
        this.opposite = opposite;
    }

} // class IsMessageTag