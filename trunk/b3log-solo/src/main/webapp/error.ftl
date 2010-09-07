<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>${blogTitle}</title>
        <script type="text/javascript" src="js/lib/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/json-rpc.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="shortcut icon" href="favicon.ico" />
        <link rel="icon" type="image/gif" href="favicon.gif"/>
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="content">
            <div class="header">
                <#include "article-header.ftl">
            </div>
            <div class="body">
                <h1 class="error-title">${sorryLabel}</h1>
                <div class="error-panel">
                    <h1>${notFoundLabel}</h1>
                    ${returnTo1Label}<a href="${blogHost}">${blogTitle}</a>
                </div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
        </div>
    </body>
</html>
