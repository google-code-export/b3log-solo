<dl>
    <#list articles as article>
    <dd class="article">
        <div class="date">
            <div class="month">${article.articleCreateDate?string("MM")}</div>
            <div class="day">${article.articleCreateDate?string("dd")}</div>
        </div>
        <div class="left">
            <h2>
                <a href="${article.articlePermalink}" title="${tags1Label}${article.articleTags}">
                    ${article.articleTitle}
                </a>
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
            </h2>
            <div class="article-date">
                <#if article.hasUpdated>
                ${article.articleUpdateDate?string("yyyy HH:mm:ss")}
                <#else>
                ${article.articleCreateDate?string("yyyy HH:mm:ss")}
                </#if>
                by
                <a class="underline" title="${article.authorName}" href="/authors/${article.authorId}">
                    ${article.authorName}</a> |
                <a class="underline" href="${article.articlePermalink}#comments">
                    ${article.articleCommentCount}${commentLabel}
                </a>
            </div>
        </div>
        <div class="clear"></div>
        <div class="article-abstract article-body">
            ${article.articleAbstract}
            <div class="clear"></div>
            <a class="right underline" href="${article.articlePermalink}">
                ${readmore2Label}...
            </a>
            <span class="clear"></span>
        </div>
    </dd>
    </#list>
</dl>
<#if 0 != paginationPageCount>
<div class="pagination right">
    <#if 1 != paginationPageNums?first>
    <a href="${path}/1"><<}</a>
    <a id="previousPage" href="${path}/${paginationPreviousPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <a href="${path}/${paginationPageNum}" class="selected">${paginationPageNum}</a>
    <#else>
    <a href="${path}/${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a id="nextPage" href="${path}/${paginationNextPageNum}">${nextPagePabel}</a>
    <a href="${path}/${paginationPageCount}">>></a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
<div class="clear"></div>
</#if>