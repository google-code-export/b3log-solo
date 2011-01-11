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
                <a title="${article.authorName}" href="/author-articles.do?oId=${article.authorId}">
                    ${article.authorName}</a> |
                <a href="${article.articlePermalink}#comments">
                    ${article.articleCommentCount}${commentLabel}
                </a>
            </div>
        </div>
        <div class="clear"></div>
        <div class="article-abstract">
            ${article.articleAbstract}
            <br/>
            <a class="right" href="${article.articlePermalink}">
                ${readmore2Label}...
            </a>
            <span class="clear"></span>
        </div>
    </dd>
    </#list>
</dl>
<#if 0 != paginationPageCount>
<div class="pagination right">
    <#if paginationPageNums?first != 1>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1" title="${firstPageLabel}"><<</a>
    <a id="previousPage"
       href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationFirstPageNum}"
       title="${previousPageLabel}"><</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a>
    </#list>
    <#if paginationPageNums?last!=paginationPageCount>
    <a id="nextPage"
       href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationLastPageNum}"
       title="${nextPagePabel}">></a>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}"
       title="${lastPageLabel}">>></a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
<div class="clear"></div>
</#if>