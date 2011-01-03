<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${page.pageTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div id="wrapper-sub">
            <div id="header-page"><!-- header --></div>
            <div id="content-page">
                <div id="single-pagecontents">
                ${page.pageContent}
                </div>
                <div id="left-sidebar">
                    <div id="about-tag"><!--about tag --></div>
                    <#include "sidebar.ftl">
                </div>
            </div>
            <div id="prefooter-page">
                <div id="rss-page"><!-- RSS Icon--></div>
                <div id="rss-lefttext"><a href="http://lambsand.appspot.com/feed">Subscribe to RSS</a></div>
            </div>
            <!-- start footer -->
            <#include "footer.ftl">
        </div>
    </body>
</html>
