<#include "macro.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount}) - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "top-nav.ftl">
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <div class="left main">
                    <div>
                        <h2 class="marginLeft12">${archive1Label}
                            <#if "en" == localeString?substring(0, 2)>
                            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
                            <#else>
                            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                            </#if>
                        </h2>
                    </div>
                    <#include "article-list.ftl">
                </div>
                <div class="right side">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "footer.ftl">
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
    </body>
</html>
