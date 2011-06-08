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
	<p>Date: 
	    <#if article.hasUpdated>
	    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
	    <#else>
	    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
	    </#if> | Browsers: <a href="${article.articlePermalink}">
            <span class="left article-browserIcon" title="${viewLabel}"></span>
            ${article.articleViewCount}
        </a> | Comments: 
	    <a href="${article.articlePermalink}#comments">
	        <span class="left articles-commentIcon" title="${commentLabel}"></span>
	        ${article.articleCommentCount}
	    </a></p>
	<p>Tags: 
	    <#list article.articleTags?split(",") as articleTag>
	    <span>
	        <a href="/tags/${articleTag?url('UTF-8')}">
	            ${articleTag}</a><#if articleTag_has_next>,</#if>
	    </span>
	    </#list></p>
	</section>
	<p>
</#list>
<#if 0 != paginationPageCount>
<div>
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