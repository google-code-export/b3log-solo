<%-- 
    Document   : index.jsp
    Description: Redirects all request to index.do
    Created on : Aug 31, 2010, 9:39:56 PM
    Author     : Liang Ding
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Solo</title>
        <link rel="shortcut icon" href="favicon.ico" />
        <link rel="icon" type="image/gif" href="favicon.gif"/>
    </head>
    <body>
        <%response.sendRedirect("index.do");%>
    </body>
</html>
