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
                        <ul id="tags">
                            <#list tags as tag>
                            <li>
                                <a data-count="${tag.tagPublishedRefCount}"
                                   href="/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">
                                    <span>${tag.tagTitle}</span>
                                    (<b>${tag.tagPublishedRefCount}</b>)
                                </a>
                            </li>
                            </#list>
                        </ul>
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
        <script type="text/javascript">
            common.buildTags();
        </script>
    </body>
</html>
