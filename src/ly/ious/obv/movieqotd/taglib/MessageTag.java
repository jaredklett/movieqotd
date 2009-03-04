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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * Message tag for JSP: Stores a single message in the session context and
 * prints it. The message is cleared after printing it (or optionally not).
 * This is intended to pass results from servlets back to JSP pages for display.
 * This is a body-less tag.
 *
 * @author Jared Klett
 * @version $Id: MessageTag.java,v 1.1 2009/03/04 01:39:06 jklett Exp $
 */

public final class MessageTag extends TagSupport {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.1 $";

// HTTP Session Variables /////////////////////////////////////////////////////

    /** Session variable: The default message session variable */
    public static final String VARIABLE_MESSAGE = "com.blipnetworks.taglib.MessageTag.message";

// Properties - actually attributes of the tag ////////////////////////////////

    /** Tag Property: Clear the value of the message after display? Default: yes */
    private boolean reset = true;

    /** Tag property: What to set the message to */
    private String set;

// Constructor ////////////////////////////////////////////////////////////////

    /**
     * Creates new AuthTag
     */
    public MessageTag() {
        super();
    }

// Tag processing /////////////////////////////////////////////////////////////

    /**
     * There should never be a body for this tag (it is ignored if it exists)
     *
     * @return SKIP_BODY; it is a bodyless tag
     */
    public int doStartTag() {
        return SKIP_BODY;
    }

    /**
     * At the end, we define our user from the ID value, after checking that
     * he is logged in, etc. *
     */
    public int doEndTag() throws JspException {
        // Which ID do we use: default or user specified
        String useId = id == null ? VARIABLE_MESSAGE : id;
        Object msgObj;
        String message;

        if (set != null) {
            // We have to set the message, not get it
            setMessage(pageContext.getSession(), set, useId);
            return EVAL_PAGE;
        }

        msgObj = pageContext.getSession().getAttribute(useId);
        if (msgObj != null) {
            message = msgObj.toString();
            try {
                pageContext.getOut().write(message);
            } catch (IOException ioe) {
                throw new JspException(ioe.toString());
            }
        }

        if (reset)
            pageContext.getSession().removeAttribute(useId);

        return EVAL_PAGE;
    }

// Message setting and retrieval //////////////////////////////////////////////

    /**
     * Sets the message
     *
     * @param session The session to set the message in
     * @param message The message to set
     */
    public static void setMessage(HttpSession session, String message) {
        if (session != null)
            session.setAttribute(VARIABLE_MESSAGE, message);
    }

    /**
     * Set the message using the specified session attribute ID
     *
     * @param session if null, no message is set
     */
    public static void setMessage(HttpSession session, String message, String idToUse) {
        // println("MessageTag: Setting message: " + message);
        if (session != null)
            session.setAttribute(idToUse, message);
    }

    /**
     * Gets the default message from the session as a String
     *
     * @return null if no message is set *
     */
    public static String getMessage(HttpSession session) {
        if (session == null)
            return null;
        Object retval = session.getAttribute(VARIABLE_MESSAGE);
        if (retval instanceof String)
            return (String) retval;
        return null;
    }

// Property/attribute accessors/mutators //////////////////////////////////////

    /**
     * Getter for property reset.
     *
     * @return Value of property reset.
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * Setter for property reset.
     *
     * @param reset New value of property reset.
     */
    public void setReset(boolean reset) {
        this.reset = reset;
    }

    /**
     * Getter for property set.
     *
     * @return Value of property set.
     */
    public String getSet() {
        return set;
    }

    /** Setter for property set.
     * @param set New value of property set.
     */
    public void setSet(String set) {
		this.set = set;
	}
}