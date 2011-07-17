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
        <div class="wrapper">
            <div class="content">
                <#include "header.ftl">
                <div class="body">
                    <div class="left main">
                        <#list tags as tag>
                        <a href="/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">${tag.tagTitle}(${tag.tagPublishedRefCount})</a>
                        &nbsp;&nbsp;
                        </#list>
                    </div>
                    <div class="right">
                        <#include "side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="footer">
                    <#include "footer.ftl">
                </div>
            </div>
        </div>
    </body>
</html>
