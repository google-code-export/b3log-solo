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
        <div class="wrapper">
            <div class="bg-bottom">
                <#include "top-nav.ftl">
                <div class="content">
                    <div class="header">
                        <#include "header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div id="tagsPanel">
                                &nbsp;
                            </div>
                        </div>
                        <div class="left side">
                            <#include "side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="footer">
                        <#include "footer.ftl">
                    </div>
                </div>
            </div>
        </div>
        <div class='goTopIcon' onclick='util.goTop();'></div>
        <div class='goBottomIcon' onclick='util.goBottom();'></div>
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
