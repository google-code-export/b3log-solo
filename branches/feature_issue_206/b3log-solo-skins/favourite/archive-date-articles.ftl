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
        <meta name="revised" content="B3log,${archiveDate.archiveDateMonth}/${archiveDate.archiveDateYear}"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="roundtop"></div>
                <div class="body">
                    <div class="left main">
                        <div class="kind-title">
                            ${archive1Label}
                        </div>
                        <div class="kind-panel">
                            <#if "en" == localeString?substring(0, 2)>
                            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
                            <#else>
                            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                            </#if>
                        </div>
                        <#include "common-articles.ftl">
                    </div>
                    <div class="right">
                        <#include "article-side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="roundbottom"></div>
            </div>
        </div>
        <div class="footer">
            <div class="footer-icon"><#include "statistic.ftl"></div>
            <#include "article-footer.ftl">
        </div>
    </body>
</html>
