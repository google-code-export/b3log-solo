<#list articles as article>
<div class="article">
    <h2 class="article-title">
        <a class="noUnderline" href="${article.articlePermalink}">
            ${article.articleTitle}
        </a>
        <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
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
    <div class="posttime-blue">
         <div class="posttime-MY">
            <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
            ${article.articleUpdateDate?string("MMM yyyy")}
            <#else>
            ${article.articleCreateDate?string("MMM yyyy")}
            </#if>
         </div>
         <div class="posttime-D">
            <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
            ${article.articleUpdateDate?string("dd")}
            <#else>
            ${article.articleCreateDate?string("dd")}
            </#if>
         </div>
    </div>
    <div class="article-abstract">
        <div class="note">
            <div class="corner"></div>
            <div class="substance">
            ${article.articleAbstract}
            </div>
        </div>
    </div>
    <div class="margin25">
        <a href="${article.articlePermalink}" class="left">
            <span class="left article-browserIcon" title="${viewLabel}"></span>
            <span class="count">${article.articleViewCount}</span>
        </a>
        <div class="left">
            <span class="tagsIcon" title="${tagLabel}"></span>
            <#list article.articleTags as articleTag>
            <span class="count">
                <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                    ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
            </span>
            </#list>
        </div>
        <a href="${article.articlePermalink}#comments" class="left">
            <span class="left articles-commentIcon" title="${commentLabel}"></span>
            <span class="count">${article.articleCommentCount}</span>
        </a>
        <div class="right more">
        <a href="${article.articlePermalink}" class="right">
            ${readmoreLabel}
        </a>
        </div>
        <div class="clear"></div>
    </div>
    <div class="article-footer">
        <div class="clear"></div>
    </div>
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
<script type="text/javascript">
    (function () {
        var local = window.location.search.substring(1),
        currentPage = "";
        if (local === "") {
            currentPage = "1";
        } else {
            var paramURL = local.split("&");
            for (var i = 0; i < paramURL.length; i++) {
                if (paramURL[i].split("=")[0] === "paginationCurrentPageNum") {
                    currentPage = paramURL[i].split("=")[1];
                }
            }
        }

        $(".pagination a").each(function () {
            var $it = $(this);
            $it.removeClass("selected");
            if ($it.text() === currentPage) {
                $it.addClass("selected");
            }
        });

        if ($("#nextPage").length > 0) {
            $("#nextPage").attr("href", $("#nextPage").attr("href").replace("{paginationLastPageNum}", parseInt(currentPage) + 1));
        }
        if ($("#previousPage").length > 0) {
            $("#previousPage").attr("href", $("#previousPage").attr("href").replace("{paginationFirstPageNum}", parseInt(currentPage) - 1));
        }
    })();
</script>