<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="${article.articleTags}" />
        <meta name="description" content="${article.articleAbstract?html}" />
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <div class="bg">
            <div class="wrapper">
                <div class="content">
                    <#include "header.ftl">
                    <div class="body">
                        <div class="left main">
                            <div class="article">
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
                                <div class="margin5">
                                    <div class="article-date left">
                                        <a class="left" title="${article.authorName}" href="/authors/${article.authorId}">
                                            <span class="authorIcon"></span>
                                            ${article.authorName}
                                        </a>
                                        <span class="dateIcon left"></span>
                                        <#if article.hasUpdated>
                                        ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                        <#else>
                                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                        </#if>
                                    </div>
                                    <div class="right">
                                        <a href="${article.articlePermalink}#comments" class="left">
                                            <span class="left articles-commentIcon" title="${commentLabel}"></span>
                                            ${article.articleCommentCount}
                                        </a>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                <div class="article-body">
                                    ${article.articleContent}
                                    <#if "" != article.articleSign.signHTML?trim>
                                    <div class="marginTop12">
                                        ${article.articleSign.signHTML}
                                    </div>
                                    </#if>
                                </div>
                                <div class="margin5 paddingTop12">
                                    <a class="left" href="${article.articlePermalink}">
                                        <span title="${viewLabel}" class="left article-browserIcon"></span>
                                        ${article.articleViewCount}
                                    </a>
                                    <div class="left">
                                        <span title="${tagLabel}" class="tagsIcon"></span>
                                        <#list article.articleTags?split(",") as articleTag>
                                        <span>
                                            <a href="/tags/${articleTag?url('UTF-8')}">
                                                ${articleTag}</a><#if articleTag_has_next>,</#if>
                                        </span>
                                        </#list>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                <div>
                                    <#if nextArticlePermalink??>
                                    <div class="right">
                                        <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                    </div>
                                    <div class="clear"></div>
                                    </#if>
                                    <#if previousArticlePermalink??>
                                    <div class="right">
                                        <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                    </div>
                                    </#if>
                                    <div class="clear"></div>
                                </div>
                                <div id="relevantArticles" class="article-relative"></div>
                                <div id="randomArticles" class="article-relative"></div>
                                <div id="externalRelevantArticles" class="article-relative"></div>
                            </div>
                            <@comments commentList=articleComments article=article></@comments>
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
            <@comment_script oId=article.oId>
            page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
            <#if 0 != randomArticlesDisplayCount>
            page.loadRandomArticles();
            </#if>
            <#if 0 != relevantArticlesDisplayCount>
            page.loadRelevantArticles('${article.oId}', '<h4>${relevantArticles1Label}</h4>');
            </#if>
            <#if 0 != externalRelevantArticlesDisplayCount>
            page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
            </#if>
            </@comment_script>    
        </div>
    </body>
</html>
