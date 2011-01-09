<#list articles as article>
<div class="article">
    <div class="article-header">
        <h2>
            <a class="noUnderline" href="${article.articlePermalink}">
                ${article.articleTitle}
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
            </a>
        </h2>
    </div>
    <div class="left article-info">
        <div class="article-date">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            <#else>
            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
            </#if>
        </div>
        <div class="article-comment">
            <a href="${article.articlePermalink}#comments">
                ${commentLabel}（${article.articleCommentCount}）
            </a>
        </div>
    </div>
    <div class="right article-main">
        <em class="article-tags">
            <#list article.articleTags?split(",") as articleTag>
            <a href="/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
        </em>
        <div class="article-abstract">
            ${article.articleAbstract}
        </div>
    </div>
    <div class="clear"></div>
</div>
<div class="line right"></div>
<div class="clear"></div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if paginationPageNums?first != 1>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1">${firstPageLabel}</a>
    <a id="previousPage" href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationFirstPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a>
    </#list>
    <#if paginationPageNums?last!=paginationPageCount>
    <a id="nextPage" href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationLastPageNum}">${nextPagePabel}</a>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
<#else>
&nbsp;
</#if>