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
        <#include "side-tool.ftl">
        <div class="wrapper">
            <#include "header.ftl">
            <div>
                <div class="main">
                    <div class="main-content">
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
                        <div class="clear"></div>
                    </div>
                    <div class="main-footer"></div>
                </div>
                <div class="side-navi">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
                <div class="brush">
                    <div class="brush-icon"></div>
                    <div id="brush"></div>
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
