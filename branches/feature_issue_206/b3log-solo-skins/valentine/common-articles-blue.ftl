<#list articles as article>
<div class="post-individual">
    <div class="posttime-blue">
        <div class="posttime-MY">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("MMM yyyy")}
            <#else>
            ${article.articleCreateDate?string("MMM yyyy")}
            </#if>
        </div>
        <div class="posttime-D">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("dd")}
            <#else>
            ${article.articleCreateDate?string("dd")}
            </#if>
        </div>
    </div>
    <div class="posttitle-home">
        <h2 class="blue">
            <a href="${article.articlePermalink}" rel="bookmark" title="">${article.articleTitle}</a>
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
    </div>
    <p class="postdetails-blue">Posted by:${article.authorName}<br/>
        Tags:
            <#list article.articleTags?split(",") as articleTag>
                <a href="/tags/${articleTag?url('UTF-8')}">
                    ${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
    </p>
    <div class="homeentry">
        <p>${article.articleAbstract}</p>
    </div>
    <p>&nbsp;</p>
    <div class="readmore">
        <a class="text-link2" href="${article.articlePermalink}" title="Read more">Read more&raquo;</a>
    </div>

    <div class="cmtballoon-blue">
        <a class="text-link" href="${article.articlePermalink}#comments" title="View Comments">${article.articleCommentCount}</a>
    </div>
    <div class="cmt-tag">comment(s)</div>
    <div class="hori-line"><!-- HR --></div>
</div>
</#list>
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