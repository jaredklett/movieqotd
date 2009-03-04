/*
 * @(#)Controller.java
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

import com.blipnetworks.sql.DataSourceManager;
import ly.ious.obv.movieqotd.model.Genres;
import ly.ious.obv.movieqotd.model.Movies;
import ly.ious.obv.movieqotd.model.Quotes;
import ly.ious.obv.movieqotd.taglib.MessageTag;
import org.apache.log4j.Logger;
import org.javaforge.util.FailException;
import org.javaforge.util.ServletUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controls the functions of the admin webapp.
 *
 * @author Jared Klett
 * @version $Id: Controller.java,v 1.1 2009/03/04 01:58:11 jklett Exp $
 */

public class Controller extends HttpServlet {

// CVS info ///////////////////////////////////////////////////////////////////

    public static final String CVS_REV = "$Revision: 1.1 $";

// Static variables ///////////////////////////////////////////////////////////

    /** Our logging facility. */
    private static Logger log = Logger.getLogger(Controller.class);

    public static final String COMMAND_CREATE_QUOTE = "log_in";

    public static final String PARAMETER_QUOTE_TEXT = "quote_text";
    public static final String PARAMETER_GENRE_ID = "genre_id";
    public static final String PARAMETER_MOVIE_TITLE = "movie_title";

// Servlet methods ////////////////////////////////////////////////////////////

    /**
     * Servlet init method. Doesn't do much except be a good citizen.
     *
     * @param config The servlet configuration from the app server.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Common parameters
        String command = request.getParameter(Constants.PARAMETER_COMMAND);
        String okayPage = request.getParameter(Constants.PARAMETER_OKAY);
        String errorPage = request.getParameter(Constants.PARAMETER_ERROR);
        try {
            if (command.equals(COMMAND_CREATE_QUOTE))
                createQuote(request, response);
            else
                throw new FailException("Unknown command: " + command);
        } catch (FailException e) {
            log.error("Command failed: " + e.getMessage());
            MessageTag.setMessage(request.getSession(), "Command failed: " + e.getMessage());

            if (errorPage != null) {
                // TODO: put the error message as a parameter to the error page
                response.sendRedirect(errorPage);
                return;
            }
        }

        // We're okay, redirect to the proper page
        if (okayPage != null) {
            response.sendRedirect(okayPage);
            return;
        }

        response.getWriter().println("Could not execute command: " + command);
    }

    protected void createQuote(HttpServletRequest request, HttpServletResponse response) throws FailException {
        String quoteText = request.getParameter(PARAMETER_QUOTE_TEXT);
        String movieTitle = request.getParameter(PARAMETER_MOVIE_TITLE);
        int genreId = ServletUtils.intParam(request, PARAMETER_GENRE_ID, -1);
        Connection masterConnection = null;
        Connection slaveConnection = null;
        try {
            masterConnection = DataSourceManager.getMasterConnection("obviously");
            slaveConnection = DataSourceManager.getSlaveConnection("obviously");
            Genres genre = Genres.getGenre(slaveConnection, genreId);
            Movies movie = Movies.getMovieByTitle(slaveConnection, movieTitle);
            if (movie == null) {
                boolean movieCreated = Movies.create(masterConnection, movieTitle, genre.getGenreId());
                if (!movieCreated)
                    throw new FailException("Could not create movie, title: " + movieTitle);
                movie = Movies.getMovieByTitle(slaveConnection, movieTitle);
            }
            boolean quoteCreated = Quotes.create(masterConnection, movie.getMovieId(), quoteText);
            if (!quoteCreated)
                throw new FailException("Could not create quote: " + quoteText);
        } catch (SQLException e) {
            throw new FailException(e.getMessage());
        } finally {
            try {
                if (masterConnection != null) masterConnection.close();
                if (slaveConnection != null) slaveConnection.close();
            } catch (Exception e) { /* ignore */ }
        }
    }

} // class Controller
