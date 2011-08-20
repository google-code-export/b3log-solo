<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="<#list article.articleTags?split(',') as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>"/>
        <meta name="description" content="${article.articleAbstract}"/>
        </@head>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
    </head>
    <body>
        <#include "top-nav.ftl">
        <div class="wrapper">
            <div class="wrap header">
                <#include "header.ftl">
            </div>
            <div class="wrap">
                <div class="left main">
                    <div class="article-header">
                        <h2>
                            <a href="${article.articlePermalink}">
                                ${article.articleTitle}
                            </a>
                            <#if article.hasUpdated>
                            <sup class="red">
                                ${updatedLabel}
                            </sup>
                            </#if>
                            <#if article.articlePutTop>
                            <sup class="red">
                                ${topArticleLabel}
                            </sup>
                            </#if>
                        </h2>
                        <div>
                            ${tags1Label}<#list article.articleTags?split(",") as articleTag><span><a href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></span></#list>
                            &nbsp;&nbsp;${viewCount1Label}
                            <a href="${article.articlePermalink}">
                                ${article.articleViewCount}  
                            </a>
                        </div>
                        <div>
                            ${createDateLabel}:
                            <a href="${article.articlePermalink}">
                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                            </a>
                            by
                            <a href="/authors/${article.authorId}">
                                ${article.authorName}
                            </a>
                            -
                            <a href="${article.articlePermalink}#comments">
                                ${article.articleCommentCount} ${commentLabel}
                            </a>
                        </div>
                    </div>
                    <div class="article-body">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12 right">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <div class="marginTop12 marginBottom12">
                        <#if nextArticlePermalink??>
                        <b>${nextArticle1Label}</b><a href="${nextArticlePermalink}">${nextArticleTitle}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </#if>
                        <#if previousArticlePermalink??>
                        <b>${previousArticle1Label}</b><a href="${previousArticlePermalink}">${previousArticleTitle}</a>
                        </#if>
                    </div>
                    <#if 0 != relevantArticles?size>
                    <div class="article-relative">
                        <h4>${relevantArticles1Label}</h4>
                        <ul class="marginLeft12">
                            <#list relevantArticles as relevantArticle>
                            <li>
                                <a href="${relevantArticle.articlePermalink}">
                                    ${relevantArticle.articleTitle}
                                </a>
                            </li>
                            </#list>
                        </ul>
                    </div>
                    </#if>
                    <div id="randomArticles" class="marginTop12 article-relative"></div>
                    <div id="externalRelevantArticles" class="marginTop12 article-relative"></div>
                    <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
                </div>
                <div class="right side">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "footer.ftl">
            </div>
        </div>  <@comment_script oId=article.oId>
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
        page.loadRandomArticles();
        <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
        </@comment_script>
    </body>
</html>