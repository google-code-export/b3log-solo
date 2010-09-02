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
                <a class="noUnderline" href="article-detail.do?oId=${article.oId}">
                    ${article.articleTitle}
                </a>
                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                <sup class="error-msg" style="font-size: 12px">
                    ${updatedLabel}
                </sup>
                </#if>
            </h2>
            <div class="article-tags">
                ${tag1Label}
                <#list article.articleTags as articleTag>
                <span>
                    <a href="tag-articles.do?oId=${articleTag.oId}">
                        ${articleTag.tagTitle}
                    </a>
                    <#if articleTag_has_next>,</#if>
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
            <span class="article-create-date">
                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
            </span>
            &nbsp;
            ${commentLabel}(${article.articleCommentCount})
            &nbsp;
            ${viewLabel}(${article.articleViewCount})
        </div>
        <div class="clear"></div>
    </div>
</div>
</#list>
<#if 0 != paginationPageCount>
<div class="pagination">
    <#if paginationFirstPageNum!=1>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1"><<</a>
    </#if>
    <#list paginationPageNums as paginationPageNum>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a>
    </#list>
    <#if paginationLastPageNum!=paginationPageCount>
    <a href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}">>></a>
    </#if>
    ${sumLabel} ${paginationPageCount} ${pageLabel}
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
    })();
    
</script>