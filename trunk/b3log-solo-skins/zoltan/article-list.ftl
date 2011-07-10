<#list articles as article>
<div class="article-item">
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
        ${article.articleAbstract}
    </div>
    <div>
        ${tags1Label}<#list article.articleTags?split(",") as articleTag><span><a href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></span></#list>
        &nbsp;&nbsp;${viewCount1Label}
        <a href="${article.articlePermalink}">
            ${article.articleViewCount}
        </a>
    </div>
</div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if 1 != paginationPageNums?first>
    <a href="${path}/1">${firstPageLabel}</a>
    <a href="${path}/${paginationPreviousPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <a href="${path}/${paginationPageNum}" class="f-bold">${paginationPageNum}</a>
    <#else>
    <a href="${path}/${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a href="${path}/${paginationNextPageNum}">${nextPagePabel}</a>
    <a href="${path}/${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>