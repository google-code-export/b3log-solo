<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="b3log, b3log solo, solo, GAE blog, 88250, vanessa"/>
        <meta name="description" content="b3log,a open sources blog base GAE.一个基于 GAE 的开源博客程序。"/>
        <meta http-equiv="pragma" CONTENT="no-cache"/>
        <meta name="author" content="b3log-solo.googlecode.com"/>
        <meta name="revised" content="b3log, 9/10/10"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta HTTP-EQUIV="Window-target" CONTENT="_top"/>
        <title>${blogTitle}</title>
        <script type="text/javascript" src="js/lib/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="skins/${skinDirName}/default-index.css"/>
        <link href="tag-articles-feed.do?oId=${oId}" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="content">
            <div class="header">
                <#include "article-header.ftl">
            </div>
            <div class="body">
                <div class="left side">
                    <#include "article-side.ftl">
                </div>
                <div class="right main">
                    <h2>${tag1Label}
                        <span id="tagArticlesTag">
                            ${tag.tagTitle}
                        </span>(${tag.tagReferenceCount})
                    </h2>
                    <#include "common-articles.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
        </div>
    </body>
</html>
