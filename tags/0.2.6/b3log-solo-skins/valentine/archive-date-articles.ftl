<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>
            <#if "en" == localeString?substring(0, 2)>
            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
            <#else>
            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
            </#if>
            - ${blogTitle}
        </title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, ${year}"/>
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
                    <div class="kind-title">
                        ${archive1Label}
                        <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
                        <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                        </#if>
                    </div>
                    <div class="kind-panel"> 
                    </div>
                    <#include "common-articles-blue.ftl">
                </div>
                <div id="left-sidebar">
                    <div id="about-tag"><!--about tag --></div>
                    <#include "sidebar.ftl">
                </div>
            </div>
            <div id="prefooter-page">
                <div id="rss-page"><!-- RSS Icon--></div>
                <div id="rss-lefttext"><a href="/blog-articles-feed.do">Subscribe to RSS</a></div>
            </div>
            <!-- start footer -->
            <#include "footer.ftl">
        </div>
    </body>
</html>