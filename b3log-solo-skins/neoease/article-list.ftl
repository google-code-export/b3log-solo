<div class="main">
    <#list articles as article>
    <div id="article${article.oId}" class="article">
        <h2>
            <a class="article-title" href="${article.articlePermalink}">
                ${article.articleTitle}
            </a>
            <#if article.hasUpdated>
            <sup class="tip">
                ${updatedLabel}
            </sup>
            </#if>
            <#if article.articlePutTop>
            <sup class="tip">
                ${topArticleLabel}
            </sup>
            </#if>
        </h2>
        <div class="left article-element">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            <#else>
            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
            </#if>
            <a href="/authors/${article.authorId}">${article.authorName}</a>
        </div>
        <div class="right article-element">
            ${article.articleCommentCount}
            ${article.articleViewCount}
        </div>
        <div class="clear"></div>
        <div class="article-body">
            ${article.articleAbstract}
        </div>
        <div class="article-element">
            <#list article.articleTags?split(",") as articleTag>
            <a href="/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
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
        <a href="${path}/${paginationPageNum}" class="selected">${paginationPageNum}</a>
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
</div>