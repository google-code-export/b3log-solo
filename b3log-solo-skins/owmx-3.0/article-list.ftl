<#list articles as article>
<h1>
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
</h1>
<p>${article.articleAbstract}</p>
<section class="meta">
    <p>
        ${author1Label}<a href="/authors/${article.authorId}">${article.authorName}</a> |
        <#if article.hasUpdated>
        ${updateDateLabel}:
	    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm")}
        <#else>
        ${createDateLabel}:
	    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm")}
        </#if> | ${viewCount1Label} <a href="${article.articlePermalink}">
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
            <a href="/tags/${articleTag?url('UTF-8')}">
	            ${articleTag}
            </a><#if articleTag_has_next>,</#if>
        </span>
        </#list>
    </p>
</section>
</#list>
<#if 0 != paginationPageCount>
<div>
    <#if 1 != paginationPageNums?first>
    <a href="${path}/1">${firstPageLabel}</a>
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
    <a href="${path}/${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>