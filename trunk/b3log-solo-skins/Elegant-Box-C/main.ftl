<#list articles as article>
<div class="post">
    <div class="title">
        <h2><a href="${article.articlePermalink}" rel="bookmark">${article.articleTitle}</a></h2>
        <div class="fixed"></div>
    </div>
    <div class="info">
        <span>
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("${Elegant_Box_C_articleTimeFormatLabel}")}
            <#else>
            ${article.articleCreateDate?string("${Elegant_Box_C_articleTimeFormatLabel}")}
            </#if>
        </span>
        <span>
            | ${tags1Label}
            <#list article.articleTags?split(",") as articleTag>
            <a rel="tag" href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
        </span>
        <div class="fixed"></div>
    </div>
    <div class="content">
        <p>${article.articleAbstract}</p>
        <div class="fixed"></div>
    </div>
    <div class="comments">
        <#if article.articleCommentCount==0>
        <a href="${article.articlePermalink}#comments">${Elegant_Box_C_noCommentLabel}</a>
        <#else>
        <a href="${article.articlePermalink}#comments">${article.articleCommentCount} ${Elegant_Box_C_numOfCommentsLabel}</a>
        </#if>
    </div>
</div>
</#list>