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
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <div class="left main">
                    <div>
                        <div class="article">
                            <div class="article-header">
                                <div class="article-date">
                                    <#if article.hasUpdated>
                                    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    </#if>
                                </div>
                                <div class="article-title">
                                    <h2>
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
                                    <div class="article-tags">
                                        ${tags1Label}
                                        <#list article.articleTags?split(",") as articleTag>
                                        <span>
                                            <a href="/tags/${articleTag?url('UTF-8')}">
                                                ${articleTag}</a><#if articleTag_has_next>,</#if>
                                        </span>
                                        </#list>&nbsp;&nbsp;&nbsp;
                                        ${author1Label}<a href="/authors/${article.authorId}">
                                            ${article.authorName}
                                        </a>
                                    </div>
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
                            <div class="article-details-footer">
                                <div class="left">
                                    <#if nextArticlePermalink??>
                                    <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a><br/>
                                    </#if>
                                    <#if previousArticlePermalink??>
                                    <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                    </#if>
                                </div>
                                <div class="right">
                                    <span class="article-create-date left">
                                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                                    </span>
                                    <a href="${article.articlePermalink}#comments" class="left">
                                        <span class="left commentIcon" title="${commentLabel}"></span>
                                        <span class="left">${article.articleCommentCount}</span>&nbsp;&nbsp;
                                    </a>
                                    <a href="${article.articlePermalink}" class="left">
                                        <span class="left browserIcon" title="${viewLabel}"></span>
                                        <span id="articleViewCount">${article.articleViewCount}</span>
                                    </a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <#if 0 != relevantArticles?size>
                            <div class="article-relative left" style="width: 50%;">
                                <h5>${relevantArticles1Label}</h5>
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
                            <div id="randomArticles" class="left article-relative"></div>
                            <div class="clear"></div>
                            <div id="externalRelevantArticles" class="article-relative"></div>
                        </div>
                        <div class="clear"></div>
                        <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
                    </div>
                </div>
                <div class="right side">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "footer.ftl">
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
