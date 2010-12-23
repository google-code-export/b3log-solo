<#if articles_l?size<=0>Blue is Null</#if>
<#list articles_l as article>
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
            <#list article.articleTags as articleTag>
                <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                    ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
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
<#if 0 != paginationPageCount_l>
<div class="pagination">
    <#if paginationPageNums_l?first != 1>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum_l=1">${firstPageLabel}</a>
    <a id="previousPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum_l={paginationFirstPageNum_l}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums_l as paginationPageNum_l>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum_l=${paginationPageNum_l}">${paginationPageNum_l}</a>
    </#list>
    <#if paginationPageNums_l?last!=paginationPageCount_l>
    <a id="nextPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum_l={paginationLastPageNum_l}">${nextPagePabel}</a>
    <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum_l=${paginationPageCount_l}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount_l} ${pageLabel}
</div>
</#if>