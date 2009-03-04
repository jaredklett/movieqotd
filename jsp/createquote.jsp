<%@ page language="java" contentType="text/html" %>
<%@ page import="ly.ious.obv.movieqotd.Constants" %>
<%@ page import="ly.ious.obv.movieqotd.Controller"%>

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
    <input type="submit" value="Submit">
</form>

</body>

</html>