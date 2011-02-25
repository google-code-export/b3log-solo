<div class="article-relative">
    <#if nextArticlePermalink??>
    <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
    </#if>
    <br/>
    <#if previousArticlePermalink??>
    <br/>
    <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
    </#if>
</div>
<#if 0 != relevantArticles?size>
<div class="article-relative">
    <h5>${relevantArticles1Label}</h5>
    <ul class="marginLeft12">
        <#list relevantArticles as relevantArticle>
        <li>
            <a href="${relevantArticle.articlePermalink}">
                ${relevantArticle.articleTitle}
            </a>
        </li>
        </#list>
    </ul>
</div>
</#if>
<div id="randomArticles"></div>
<div id="externalRelevantArticles"></div>
