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
        <div class="header">
            <#include "header.ftl">
        </div>
        <div class="content">
            <h2>${archive1Label}
                <#if "en" == localeString?substring(0, 2)>
                ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount})
                <#else>
                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                </#if>
            </h2>
            <#include "article-list.ftl">
        </div>
        <div>
            <#include "side.ftl">
        </div>
        <div class="footer">
            <#include "footer.ftl">
        </div>
    </body>
</html>
