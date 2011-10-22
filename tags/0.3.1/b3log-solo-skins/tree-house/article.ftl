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
        <div class="wrapper">
            <div class="bg-bottom">
                <#include "top-nav.ftl">
                <div class="content">
                    <div class="header">
                        <#include "header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div class="article">
                                <div class="article-header">
                                    <h2 class="marginBottom12">
                                        <a class="no-underline" href="${article.articlePermalink}">
                                            ${article.articleTitle}
                                            <#if article.hasUpdated>
                                            <sup>
                                                ${updatedLabel}
                                            </sup>
                                            </#if>
                                            <#if article.articlePutTop>
                                            <sup>
                                                ${topArticleLabel}
                                            </sup>
                                            </#if>
                                            <span>
                                                <#if article.hasUpdated>
                                                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                                <#else>
                                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                                </#if>
                                            </span>
                                        </a>
                                    </h2>
                                    <div class="marginLeft12">
                                        <#list article.articleTags?split(",") as articleTag>
                                        <a class="article-tags" href="/tags/${articleTag?url('UTF-8')}">
                                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                                        </#list>
                                        <div class="clear"></div>
                                    </div>
                                </div>
                                <div class="article-body">
                                    ${article.articleContent}
                                    <#if "" != article.articleSign.signHTML?trim>
                                    <div class="marginTop12">
                                        ${article.articleSign.signHTML}
                                    </div>
                                    </#if>
                                </div>
                                <div class="article-details-footer">
                                    <div class="left">
                                        <#if nextArticlePermalink??>
                                        <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                        <br/>
                                        </#if>
                                        <#if previousArticlePermalink??>
                                        <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                        </#if>
                                    </div>
                                    <div class="right">
                                        <span class="article-create-date left">
                                            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                                        </span>
                                        <span class="left commentIcon" title="${commentLabel}"></span>
                                        <span class="left">
                                            &nbsp;${article.articleCommentCount}&nbsp;&nbsp;
                                        </span>
                                        <a href="${article.articlePermalink}" class="left">
                                            <span class="left browserIcon" title="${viewLabel}"></span>
                                            ${article.articleViewCount}
                                        </a>
                                    </div>
                                    <div class="clear"></div>
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
                                <div id="randomArticles" class="article-relative"></div>
                                <div id="externalRelevantArticles" class="article-relative"></div>
                            </div>
                            <div class="line right"></div>
                            <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
                        </div>
                        <div class="left side">
                            <#include "side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
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