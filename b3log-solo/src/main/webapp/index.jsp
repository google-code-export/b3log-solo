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
        <meta name="keywords" content="b3log, b3log solo, solo, GAE blog, 88250, vanessa"/>
        <meta name="description" content="b3log,a open sources blog base GAE.一个基于 GAE 的开源博客程序。"/>
        <meta http-equiv="pragma" CONTENT="no-cache"/>
        <meta name="author" content="b3log-solo.googlecode.com"/>
        <meta name="revised" content="b3log, 9/10/10"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta HTTP-EQUIV="Window-target" CONTENT="_top"/>
        <title>Solo</title>
        <link rel="icon" type="image/png" href="favicon.png"/>
    </head>
    <body>
        <%response.sendRedirect("index.do");%>
    </body>
</html>
