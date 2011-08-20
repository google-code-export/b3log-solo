<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${page.pageTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${page.pageTitle}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
    </head>
    <body>
        <#include "top-nav.ftl">
        <div id="a">
            <#include "header.ftl">
            <div id="b">
                <article>
                    <div class="single_page article-body">
			${page.pageContent}
                    </div>
                    <@comments commentList=pageComments permalink=page.pagePermalink></@comments>
                </article>
                <#include "side.ftl">
                <div class="clear"></div>
            </div>
            <#include "footer.ftl">
        </div>
        <@comment_script oId=page.oId></@comment_script>
    </body>
</html>
