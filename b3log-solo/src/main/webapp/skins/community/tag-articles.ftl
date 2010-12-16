<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${tag.tagTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/community/default-index.css"/>
        <link href="tag-articles-feed.do?oId=${oId}" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="header">
            <#include "article-header.ftl">
        </div>
        <div class="content">
            <h2>${tag1Label}
                <span id="tagArticlesTag">
                    ${tag.tagTitle}
                </span>(${tag.tagPublishedRefCount})
            </h2>
            <#include "common-articles.ftl">
        </div>
        <div>
            <#include "article-side.ftl">
        </div>
        <div class="footer">
            <#include "article-footer.ftl">
        </div>
    </body>
</html>
