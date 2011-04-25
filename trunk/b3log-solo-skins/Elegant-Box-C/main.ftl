<#list articles as article>
<div class="post">
    <div class="title">
        <h2><a href="${article.articlePermalink}" rel="bookmark">${article.articleTitle}</a></h2>
        <div class="fixed"></div>
    </div>
    <div class="info">
        <span>
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy年MM月dd日")}
            <#else>
            ${article.articleCreateDate?string("yyyy年MM月dd日")}
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
        <a href="${article.articlePermalink}#comments" title="${article.articleTitle} 上的评论">没有评论</a>
        <#else>
        <a href="${article.articlePermalink}#comments" title="${article.articleTitle} 上的评论">${article.articleCommentCount}条评论</a>
        </#if>
    </div>
</div>
</#list>