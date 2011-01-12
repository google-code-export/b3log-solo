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
        <link type="text/css" rel="stylesheet" href="/skins/andrea/default-index.css"/>
        <link href='http://fonts.googleapis.com/css?family=Neucha' rel='stylesheet' type='text/css'/>
        <link href='http://fonts.googleapis.com/css?family=Reenie+Beanie' rel='stylesheet' type='text/css'/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <#include "side-tool.ftl">
        <div class="wrapper">
            <#include "article-header.ftl">
            <div>
                <div class="main">
                    <div class="main-content">
                        <div id="tagsPanel">
                        </div>
                    </div>
                    <div class="main-footer"></div>
                </div>
                <div class="side-navi">
                    <#include "article-side.ftl">
                </div>
                <div class="brush">
                    <div class="brush-icon"></div>
                    <div id="brush"></div>
                </div>
                <div class="clear"></div>
                <div class="footer">
                    <#include "article-footer.ftl">
                </div>
            </div>
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
