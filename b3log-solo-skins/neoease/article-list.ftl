<#list articles as article>
<div class="article">
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
        <span class="expand-ico" onclick="getArticle(this, '${article.oId}');"></span>
    </h2>
    <div class="left article-element">
        <span class="date-ico" title="${dateLabel}">  
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            <#else>
            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
            </#if>
        </span>
        <span class="user-ico" title="${authorLabel}">
            <a href="/authors/${article.authorId}">${article.authorName}</a>
        </span>
    </div>
    <div class="right article-element">
        <a href="${article.articlePermalink}#comments">
            ${article.articleCommentCount}&nbsp;&nbsp;${commentLabel}
        </a>&nbsp;&nbsp;
        <a href="${article.articlePermalink}">
            ${article.articleViewCount}&nbsp;&nbsp;${viewLabel}
        </a>
    </div>
    <div class="clear"></div>
    <div class="article-body">
        <div id="abstract${article.oId}">
            ${article.articleAbstract}
        </div>
        <div id="content${article.oId}" class="none"></div>
    </div>
    <div class="article-element">
        <span class="tag-ico" title="${tagsLabel}">
            <#list article.articleTags?split(",") as articleTag>
            <a href="/tags/${articleTag?url('UTF-8')}">
                ${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
        </span>
    </div>
</div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if 1 != paginationPageNums?first>
    <a href="${path}/1" title="${firstPageLabel}"><</a>
    <a href="${path}/${paginationPreviousPageNum}" title="${previousPageLabel}"><<</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <#if paginationPageNum == paginationCurrentPageNum>
    <a href="${path}/${paginationPageNum}" class="current">${paginationPageNum}</a>
    <#else>
    <a href="${path}/${paginationPageNum}">${paginationPageNum}</a>
    </#if>
    </#list>
    <#if paginationPageNums?last != paginationPageCount>
    <a href="${path}/${paginationNextPageNum}" title="${nextPagePabel}">></a>
    <a href="${path}/${paginationPageCount}" title="${lastPageLabel}">>></a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>