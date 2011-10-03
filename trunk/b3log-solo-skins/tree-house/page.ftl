<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${page.pageTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${page.pageTitle}" />
        <meta name="description" content="${metaDescription}" />
        </@head>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css" charset="utf-8" />
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css" charset="utf-8" />
    </head>
    <body>
        <div class="wrapper">
            <div class="bg-bottom">
                ${topBarReplacement}
                <div class="content">
                    <div class="header">
                        <#include "header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div class="article">
                                <div class="article-body">
                                    ${page.pageContent}
                                </div>
                            </div>
                            <div class="line right"></div>
                            <@comments commentList=pageComments permalink=page.pagePermalink></@comments>
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
        <@comment_script oId=page.oId></@comment_script>
    </body>
</html>
