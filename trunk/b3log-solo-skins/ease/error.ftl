<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${notFoundLabel} - ${blogTitle}">
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="robots" content="noindex, follow"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="body">
            <div class="wrapper">
                <div class="error">
                    <h2 >${sorryLabel} ${notFoundLabel}</h2>
                    <a href="${servePath}">${returnTo1Label}${blogTitle}</a>
                </div>
                <#include "side.ftl">
                <div class="clear"></div>
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>