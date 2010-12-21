<#list articles as article>
<div class="marginBottom40">
    <div class="article-header">
        <div class="article-date">
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm")}
            <#else>
            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm")}
            </#if>
        </div>
        <div class="arrow-right"></div>
        <div class="clear"></div>
        <ul>
            <li>
                <span class="left">
                    by&nbsp;
                </span>
                <a class="left" title="${article.authorName}" href="/author-articles.do?oId=${article.authorId}">
                    ${article.authorName}
                </a>
                <span class="clear"></span>
            </li>
            <li>
                <a href="${article.articlePermalink}" title="${viewLabel}">
                    ${viewLabel} (${article.articleViewCount})
                </a>
            </li>
            <li>
                <a title="${commentLabel}" href="${article.articlePermalink}#comments">
                    ${commentLabel} (${article.articleCommentCount})
                </a>
            </li>
        </ul>
    </div>
    <div class="article-main">
        <h2 class="title">
            <a class="noUnderline" href="${article.articlePermalink}">
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
        <div class="article-body">
            ${article.articleAbstract}
        </div>
        <div class="read-more">
            <a href="${article.articlePermalink}">
                <span class="left">${readmore2Label}</span>
                <span class="read-more-icon"></span>
                <span class="clear"></span>
            </a>
            <div class="clear"></div>
        </div>
    </div>
    <div class="article-footer">
        <h3>${tagsLabel}</h3>
        <ul>
            <#list article.articleTags as articleTag>
            <li>
                <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                    ${articleTag.tagTitle}
                </a>
            </li>
            </#list>
            <li>
                ${createDateLabel}:
                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm")}
            </li>
        </ul>
    </div>
    <div class="clear"></div>
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
    ${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>