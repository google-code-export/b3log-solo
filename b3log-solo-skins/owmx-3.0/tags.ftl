<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${allTagsLabel} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${allTagsLabel}"/>
        <meta name="description" content="<#list tags as tag>${tag.tagTitle}<#if tag_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "top-nav.ftl">
        <div id="a">
            <#include "header.ftl">
            <div id="b">
                <article>
                    <#list tags as tag>
                    <a href="/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">${tag.tagTitle}(${tag.tagPublishedRefCount})</a>
                    &nbsp;&nbsp;
                    </#list>
                </article>
                <#include "side.ftl">
                <div class="clear"></div>
            </div>
            <#include "footer.ftl">
        </div>
    </body>
</html>
