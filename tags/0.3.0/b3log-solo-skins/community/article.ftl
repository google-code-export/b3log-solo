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
        <div class="header">
            <#include "header.ftl">
        </div>
        <div class="content">
            <div class="marginBottom40">
                <div class="article-header">
                    <div class="article-date">
                        <#if article.hasUpdated>
                        ${article.articleUpdateDate?string("yyyy-MM-dd HH")}
                        <#else>
                        ${article.articleCreateDate?string("yyyy-MM-dd HH")}
                        </#if>
                    </div>
                    <div class="arrow-right"></div>
                    <div class="clear"></div>
                    <ul>
                        <li>
                            <span class="left">
                                by&nbsp;
                            </span>
                            <a class="left" title="${article.authorName}" href="/authors/${article.authorId}}">
                                ${article.authorName}
                            </a>
                            <span class="clear"></span>
                        </li>
                        <li>
                            <a href="${article.articlePermalink}" title="${viewLabel}">
                                ${viewLabel} (${article.articleViewCount})
                            </a>
                        </li>
                        <li>
                            <a title="${commentLabel}" href="${article.articlePermalink}#comments">
                                ${commentLabel} (${article.articleCommentCount})
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="article-main article-detail-body">
                    <h2 class="title">
                        <a href="${article.articlePermalink}">${article.articleTitle}</a>
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
                    <div class="article-body">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <div class="tags">
                        <span class="tag-icon" title="${tagsLabel}"></span>
                        ${tags1Label}
                        <#list article.articleTags?split(",") as articleTag>
                        <a href="/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                        </#list>
                    </div>
                </div>
                <div class="clear"></div>
                <div class="article-detail-footer">
                    <#if nextArticlePermalink??>
                    <a href="${nextArticlePermalink}" class="left">${nextArticle1Label} ${nextArticleTitle}</a>
                    </#if>
                    <#if previousArticlePermalink??>
                    <a href="${previousArticlePermalink}" class="right">${previousArticle1Label} ${previousArticleTitle}</a>
                    </#if>
                    <div class="clear"></div>
                    <div id="randomArticles" class="left article-relative"></div>
                    <#if 0 != relevantArticles?size>
                    <div class="article-relative right" style="width: 48%;">
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
                    <div class="clear"></div>
                    <div id="externalRelevantArticles" class="article-relative"></div>
                </div>
            </div>
            <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
        </div>
        <div>
            <#include "side.ftl">
        </div>
        <div class="footer">
            <#include "footer.ftl">
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