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
                    <ul id="tags">
                        <#list tags as tag>
                        <li>
                            <a href="/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">
                                <span>${tag.tagTitle}</span>
                                (<b>${tag.tagPublishedRefCount}</b>)
                            </a>
                        </li>
                        </#list>
                    </ul>
                </article>
                <#include "side.ftl">
                <div class="clear"></div>
            </div>
            <#include "footer.ftl">
        </div>
        <script type="text/javascript">
            common.buildTags();
        </script>
    </body>
</html>