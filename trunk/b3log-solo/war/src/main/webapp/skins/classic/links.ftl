<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${linkLabel} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${linkLabel} - ${blogTitle}"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <#list links as link>
                <li>
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        <img alt="${link.linkTitle}"
                             src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" /></a>
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">${link.linkTitle}
                    </a>
                    <#-- ${link.linkDescription} -->
                </li>
                </#list>
            </div>
            <div class="footer">
                <#include "footer.ftl">
            </div>
        </div>
    </body>
</html>
