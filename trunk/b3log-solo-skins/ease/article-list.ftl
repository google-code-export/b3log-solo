<ul>
    <#list articles as article>
    <li class="article">
        <div class="article-title">
            <h2>
                <a class="ft-gray" href="${servePath}${article.articlePermalink}">
                    ${article.articleTitle}
                </a>
                <#if article.hasUpdated>
                <sup class="ft-gray">
                    ${updatedLabel}
                </sup>
                </#if>
                <#if article.articlePutTop>
                <sup class="ft-gray">
                    ${topArticleLabel}
                </sup>
                </#if>
            </h2>
            <span onclick="getArticle(this, '${article.oId}');">${contentLabel}</span>
            <div class="right">
                <a class="ft-gray" href="${servePath}${article.articlePermalink}#comments">
                    ${article.articleCommentCount}&nbsp;&nbsp;${commentLabel}
                </a>&nbsp;&nbsp;
                <a class="ft-gray" href="${servePath}${article.articlePermalink}">
                    ${article.articleViewCount}&nbsp;&nbsp;${viewLabel}
                </a>
            </div>
            <div class="clear"></div>
        </div>
        <div class="article-body">
            <div id="abstract${article.oId}">
                ${article.articleAbstract}
            </div>
            <div id="content${article.oId}" class="none"></div>
        </div>
        <div class="article-info">
            <div class="right ft-gray">
                <#if article.hasUpdated>
                ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                <#else>
                ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                </#if>
                <a href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
            </div>
            <div class="left ft-gray">
                ${tag1Label}
                <#list article.articleTags?split(",") as articleTag>
                <a href="${servePath}/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>, </#if>
                </#list>
            </div>
            <div class="clear"></div>
        </div>
    </li>
    </#list>
</ul>
<#if paginationCurrentPageNum != paginationPageCount>
<div class="article-next ft-gray" onclick="getNextPage()" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
</#if>