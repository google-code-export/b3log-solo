<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${allTagsLabel} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list tags as tag>${tag.tagTitle}<#if tag_has_next>,</#if></#list>"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/community/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="header">
            <#include "article-header.ftl">
        </div>
        <div class="content">
            <div id="tagsPanel" class="marginBottom40">
            </div>
        </div>
        <div>
            <#include "article-side.ftl">
        </div>
        <div class="footer">
            <#include "article-footer.ftl">
        </div>
        <script type="text/javascript">
            util.setTagsPanel([<#list tags as tag>{
                    tagNameURLEncoded: "${tag.tagTitle?url('UTF-8')}",
                    tagName: "${tag.tagTitle}",
                    tagCount: ${tag.tagPublishedRefCount},
                    tagId: ${tag.oId}
                }<#if tag_has_next>,</#if>
                    </#list>]);
        </script>
    </body>
</html>
