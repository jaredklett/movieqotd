<%@ page language="java" contentType="text/html" %>
<%@ page import="com.blipnetworks.sql.DataSourceManager" %>
<%@ page import="ly.ious.obv.movieqotd.Constants"%>
<%@ page import="ly.ious.obv.movieqotd.Controller"%>
<%@ page import="ly.ious.obv.movieqotd.model.Genres"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.SQLException"%>

<%@ taglib uri="obviously.tld" prefix="obviously" %>

<%! String okayPage = "/createquote.jsp"; %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd">
<html lang="en">
<head>
    <title>Enter a quote</title>
</head>
<body>

<obviously:hasMessage>
    <p><font color="red"><obviously:message /></font></p>
</obviously:hasMessage>

<h3>Enter a quote:</h3>

<form method="post" action="/controller">
    <input type="hidden" name="<%= Constants.PARAMETER_COMMAND %>" value="<%= Controller.COMMAND_CREATE_QUOTE %>">
    <input type="hidden" name="<%= Constants.PARAMETER_OKAY %>" value="<%= okayPage %>">
    <p>Movie title: <input type="text" name="<%= Controller.PARAMETER_MOVIE_TITLE %>" size="20" maxlength="50" value=""></p>
    <p>Quote text: <input type="text" name="<%= Controller.PARAMETER_QUOTE_TEXT %>" size="20" maxlength="50" value=""></p>
    <select name="<%= Controller.PARAMETER_GENRE_ID %>">
<%
    Connection slaveConnection = null;
    Genres[] genres = new Genres[0];
    try {
        slaveConnection = DataSourceManager.getSlaveConnection("obviously");
        genres = Genres.getAll(slaveConnection);
    } catch (SQLException e) {
        // do nothing
    } finally {
        if (slaveConnection != null) try { slaveConnection.close(); } catch (SQLException e) { /* ignored */ }
    }
    for (Genres genre : genres) {
%>
        <option value="<%= genre.getGenreId() %>"><%= genre.getGenreName() %></option>
<%  } %>
    </select>
    <input type="submit" value="Submit">
</form>

</body>

</html>