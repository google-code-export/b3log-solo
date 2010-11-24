<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>
            <#if "en" == localeString?substring(0, 2)>
            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDateArticleCount})
            <#else>
            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDateArticleCount})
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
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="body">
                    <div class="left main">
                        <div class="kind-title">
                            ${archive1Label}
                        </div>
                        <div class="kind-panel">
                            <#if "en" == localeString?substring(0, 2)>
                            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDateArticleCount})
                            <#else>
                            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDateArticleCount})
                            </#if>
                        </div>
                        <#include "common-articles.ftl">
                    </div>
                    <div class="right">
                        <#include "article-side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="footer">
                    <#include "article-footer.ftl">
                </div>
            </div>
        </div>
    </body>
</html>
