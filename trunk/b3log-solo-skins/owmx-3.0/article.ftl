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
        <div id="a">
            <#include "header.ftl">
            <div id="b">
                <article>
                    <h1>
                        ${article.articleTitle}
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
                    </h1>
                    <section class="meta">
                        <p> 
                            ${author1Label}<a href="/authors/${article.authorId}">${article.authorName}</a> |
                            <#if article.hasUpdated>
                            ${updateDateLabel}:${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                            <#else>
                            ${createDateLabel}:${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                            </#if>
                            ${viewCount1Label}<a href="${article.articlePermalink}">
                                <span class="left article-browserIcon" title="${viewLabel}"></span>
				            ${article.articleViewCount}
                            </a> | ${commentCount1Label}  
                            <a href="${article.articlePermalink}#comments">
                                <span class="left articles-commentIcon" title="${commentLabel}"></span>
					        ${article.articleCommentCount}
                            </a>
                        </p>
                        <p>
                            ${tags1Label} 
                            <#list article.articleTags?split(",") as articleTag>
                            <span>
                                <a href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if>
                            </span>
                            </#list>
                        </p>
                    </section>
                    <p>
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                    <div class="marginTop12">
                        ${article.articleSign.signHTML}
                    </div>
                    </#if>
                    </p>
                    <div class="marginBottom12">
                        <#if nextArticlePermalink??>
                        <a class="left" href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                        </#if>
                        <#if previousArticlePermalink??>
                        <a href="${previousArticlePermalink}" class="right">${previousArticle1Label}${previousArticleTitle}</a>
                        </#if>
                        <div class="clear"></div>
                    </div>
                    <#if 0 != relevantArticles?size>
                    <div class="article-relative">
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
                    <ol id="randomArticles"></ol>
                    <ol id="externalRelevantArticles"></ol>
                    <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
                </article>
                <#include "side.ftl">
                <div class="clear"></div>
            </div>
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
