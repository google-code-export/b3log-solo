<div id="main">
    <div id="main-aux">
        <div id="patch"></div>
        <div id="main-aux1">
            <#if tag??>
                <h2>${tag1Label}
                    <span id="tagArticlesTag">
                        ${tag.tagTitle}
                    </span>(${tag.tagPublishedRefCount})
                </h2>
            </#if>
            <#list articles as article>
            <div class="post-cont">
                   <div class="headline">
                    <h2>
                        <a href="${article.articlePermalink}">${article.articleTitle}</a>
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
                    <p class="postmetadata">
                        <span class="calendar">
                           <span class="month">${article.articleCreateDate?string("MM")}</span>
                           <span class="day">${article.articleCreateDate?string("dd")}</span>
                        </span>
                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                        
                        <a href="${article.articlePermalink}#comments">
                            ${commentLabel}:&nbsp;
                            ${article.articleCommentCount}
                        </a>
                        &nbsp;&nbsp;
                        <a href="${article.articlePermalink}">
                            ${viewLabel}:&nbsp;
                            ${article.articleViewCount}
                        </a>
                    </div>
                    <div class="article-tags">
                        ${tags1Label}
                        <#list article.articleTags as articleTag>
                        <span>
                            <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                                ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                        </span>
                        </#list>
                    </div>
                    <div class="post-excerpt article-body">
                        <p>${article.articleAbstract}</p>
                    </div>
                </div>
            </#list>
            <#if 0 != paginationPageCount>
                <div class="wp-pagenavi">
                    <#if paginationPageNums?first != 1>
                        <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=1" class="page">${firstPageLabel}</a>
                        <a id="previousPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationFirstPageNum}" class="page">${previousPageLabel}</a>
                    </#if>
                    <#list paginationPageNums as paginationPageNum>
                        <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}" class="page">${paginationPageNum}</a>
                    </#list>
                    <#if paginationPageNums?last!=paginationPageCount>
                        <a id="nextPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={paginationLastPageNum}" class="page">${nextPagePabel}</a>
                        <a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageCount}" class="page">${lastPageLabel}</a>
                    </#if>
                    &nbsp;&nbsp;${sumLabel} ${paginationPageCount} ${pageLabel}
<!-- <a href="http://www.madeincima.eu/page/2/" class="nextpostslink">&raquo;</a> -->
                </div>
            </#if>


        </div>
    </div>
</div>
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

        $(".wp-pagenavi a").each(function () {
            var $it = $(this);
            $it.removeClass("current");
            if ($it.text() === currentPage) {
                $it.addClass("current");
                $it.after("<span class=\"current\">" + currentPage + "</span>");
                $it.remove();
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