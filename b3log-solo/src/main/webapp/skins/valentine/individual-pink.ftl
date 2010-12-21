<#if articles_r?size<=0>Pink is Null</#if>
<#list articles_r as article>
<div class="post-individual">
    <div class="posttime-pink">
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
        <h2 class="pink"><a href="${article.articlePermalink}" rel="bookmark" title="">${article.articleTitle}</a>
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
    <p class="postdetails-pink">Posted by: ${article.authorName}<br />
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
    <div class="readmore"><a class="text-link2" href="${article.articlePermalink}" title="Read more">Read more&raquo;</a></div>
    <div class="cmtballoon-pink">
        <a class="text-link" href="${article.articlePermalink}#comments" title="View Comments">${article.articleCommentCount}</a></div>
    <div class="cmt-tag">comment(s)</div>

    <div class="hori-line"><!-- HR --></div>
</div>
</#list>