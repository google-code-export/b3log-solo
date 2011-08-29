<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="${article.articleTags}" />
        <meta name="description" content="${article.articleAbstract?html}" />
        </@head>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css" charset="utf-8" />
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css" charset="utf-8" />
    </head>
    <body>
        <#include "top-nav.ftl">
        <#include "side-tool.ftl">
        <div class="wrapper">
            <#include "header.ftl">
            <div>
                <div class="main">
                    <div class="main-content">
                        <div class="article">
                            <div class="date">
                                <div class="month">${article.articleCreateDate?string("MM")}</div>
                                <div class="day">${article.articleCreateDate?string("dd")}</div>
                            </div>
                            <div class="left">
                                <h2 class="article-title">
                                    <a class="no-underline" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                                <div class="article-date">
                                    <#if article.hasUpdated>
                                    ${article.articleUpdateDate?string("yyyy HH:mm:ss")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy HH:mm:ss")}
                                    </#if>
                                    by
                                    <a title="${article.authorName}" href="/authors/${article.authorId}">
                                        ${article.authorName}</a> |
                                    <a href="${article.articlePermalink}#comments">
                                        ${article.articleCommentCount}${commentLabel}
                                    </a>
                                </div>
                            </div>
                            <div class="clear"></div>
                            <div class="article-body">
                                ${article.articleContent}
                                <#if "" != article.articleSign.signHTML?trim>
                                <div class="marginTop12">
                                    ${article.articleSign.signHTML}
                                </div>
                                </#if>
                            </div>
                            <div class="right">
                                ${tag1Label}
                                <#list article.articleTags?split(",") as articleTag>
                                <span>
                                    <a href="/tags/${articleTag?url('UTF-8')}">
                                        ${articleTag}</a><#if articleTag_has_next>,</#if>
                                </span>
                                </#list>
                                &nbsp;&nbsp;${viewCount1Label}
                                <a href="${article.articlePermalink}">
                                    ${article.articleViewCount}
                                </a>
                            </div>
                            <div class="clear"></div>
                            <div class="article-relative">
                                <#if nextArticlePermalink??>
                                <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                &nbsp;&nbsp;&nbsp;
                                </#if>
                                <#if previousArticlePermalink??>
                                <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                </#if>
                            </div>
                            <#if 0 != relevantArticles?size>
                            <div class="article-relative left relevantArticles">
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
                            <div id="randomArticles"  class="article-relative left"></div>
                            <div class="clear"></div>
                            <div id="externalRelevantArticles"></div>
                        </div>
                        <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
                    </div>
                    <div class="main-footer"></div>
                </div>
                <div class="side-navi">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
                <div class="brush">
                    <div class="brush-icon"></div>
                    <div id="brush"></div>
                </div>
                <div class="footer">
                    <#include "footer.ftl">
                </div>
            </div>
        </div>
        <@comment_script oId=article.oId>
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
        page.loadRandomArticles();
        <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
        </@comment_script>    
    </body>
</html>
