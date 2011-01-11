<dl>
    <#list articles as article>
    <dd class="article">
        <span class="date">
            <span class="month">${article.articleCreateDate?string("MM")}</span>
            <span class="day">${article.articleCreateDate?string("dd")}</span>
        </span>
        <h2 class="article-title">
            <a class="no-underline" href="${article.articlePermalink}">
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
        <div class="margin5">
            <div class="article-date left">
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
        <div class="article-abstract">
            ${article.articleAbstract}
        </div>
        <div class="article-footer">
            <a href="${article.articlePermalink}" class="left">
                <span class="left article-browserIcon" title="${viewLabel}"></span>
                ${article.articleViewCount}
            </a>
            <div class="left">
                <span class="tagsIcon" title="${tagLabel}"></span>
                <#list article.articleTags?split(",") as articleTag>
                <span>
                    <a href="/tags/${articleTag?url('UTF-8')}">
                        ${articleTag}</a><#if articleTag_has_next>,</#if>
                </span>
                </#list>
            </div>
            <div class="clear"></div>
        </div>
    </dd>
    </#list>
</dl>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if paginationPageNums?first != 1>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1">${firstPageLabel}</a>
    <a id="previousPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationFirstPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a>
    </#list>
    <#if paginationPageNums?last!=paginationPageCount>
    <a id="nextPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationLastPageNum}">${nextPagePabel}</a>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>