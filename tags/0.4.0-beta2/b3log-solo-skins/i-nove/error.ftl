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
        <div class="wrapper">
            <div class="content">
                <#include "header.ftl">
                <div class="body">
                    <div class="left main">
                        <div class="error-panel">
                            <h1 class="error-title">${notFoundLabel}</h1>
                            <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>
                        </div>
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
