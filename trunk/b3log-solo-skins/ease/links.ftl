<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content=""/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="body">
            <#if 0 != links?size>
            <ul class="other-main links">
                <#list links as link>
                <li>
                    <a href="${link.linkAddress}" alt="${link.linkTitle}" target="_blank">
                        <img alt="${link.linkTitle}"
                             src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" /></a>
                    <a href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">${link.linkTitle}
                    </a>
                </li>
                </#list>
            </ul>
            </#if>
        </div>
        <#include "footer.ftl">
    </body>
</html>
