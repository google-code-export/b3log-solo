<#list articles as article>
<div class="article">
    <div class="article-header">
        <div class="article-date">
            <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
            ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
            <#else>
            ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
            </#if>
        </div>
        <div class="article-title">
            <h2>
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
            <div class="article-tags">
                ${tags1Label}
                <#list article.articleTags as articleTag>
                <span>
                    <a href="/tags/${articleTag.tagTitle}">
                        ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                </span>
                </#list>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="article-body">
        <div class="article-abstract">
            ${article.articleAbstract}
        </div>
    </div>
    <div class="article-footer">
        <div class="right">
            <span class="article-create-date left">
                &nbsp;${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp
            </span>
            <a href="${article.articlePermalink}#comments" class="left">
                <span class="left commentIcon" title="${commentLabel}"></span>
                ${article.articleCommentCount}
            </a>
            <span class="left">&nbsp;&nbsp;</span>
            <a href="${article.articlePermalink}" class="left">
                <span class="left browserIcon" title="${viewLabel}"></span>
                ${article.articleViewCount}
            </a>
        </div>
        <div class="clear"></div>
    </div>
</div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if paginationPageNums?first != 1>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1">${firstPageLabel}</a>
    <a id="previousPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationFirstPageNum}">${previousPageLabel}</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a>
    </#list>
    <#if paginationPageNums?last!=paginationPageCount>
    <a id="nextPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationLastPageNum}">${nextPagePabel}</a>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}">${lastPageLabel}</a>
    </#if>
    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
</div>
</#if>
<script type="text/javascript">
    (function () {
        var local = window.location.search.substring(1);
        if (local === "") {
            localStorage.setItem("currentPage", 1);
        } else {
            var paramURL = local.split("&");
            for (var i = 0; i < paramURL.length; i++) {
                if (paramURL[i].split("=")[0] === "paginationCurrentPageNum") {
                    localStorage.setItem("currentPage", paramURL[i].split("=")[1]);
                }
            }
        }

        $(".pagination a").each(function () {
            var $it = $(this);
            $it.removeClass("selected");
            if ($it.text() === localStorage.getItem("currentPage")) {
                $it.addClass("selected");
            }
        });

        if ($("#nextPage").length > 0) {
            $("#nextPage").attr("href", $("#nextPage").attr("href").replace("{paginationLastPageNum}", parseInt(localStorage.getItem("currentPage")) + 1));
        }
        if ($("#previousPage").length > 0) {
            $("#previousPage").attr("href", $("#previousPage").attr("href").replace("{paginationFirstPageNum}", parseInt(localStorage.getItem("currentPage")) - 1));
        }
    })();
    
</script>