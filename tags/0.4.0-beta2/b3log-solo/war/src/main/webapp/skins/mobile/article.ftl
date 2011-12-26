<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="${article.articleTags}" />
        <meta name="description" content="${article.articleAbstract?html}" />
        </@head>
    </head>
    <body class="classic-wptouch-bg">
        <#include "header.ftl">
        <div class="content single">
            <div class="post">
                <a class="sh2" href="${article.articlePermalink}" rel="bookmark">${article.articleTitle}</a>
                <div class="single-post-meta-top">
                    <#if article.hasUpdated>
                    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                    <#else>
                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                    </#if>
                    &rsaquo; ${article.authorName}<br />
                    <a href="#comments">${skipToComment}</a>
                </div>
            </div>
            <div class="clearer"></div>
            <div class="post article-body" id="post-${article.oId}">
                <div id="singlentry" class="left-justified">
                    ${article.articleContent}
                    <#if "" != article.articleSign.signHTML?trim>
                    <div class=""><!--TODO sign class-->
                        ${article.articleSign.signHTML}
                    </div>
                    </#if>
                </div>
                <!-- Categories and Tags post footer -->
                <div class="single-post-meta-bottom">
                    ${tags1Label}
                    <#list article.articleTags?split(",") as articleTag>
                    <a href="/tags/${articleTag?url('UTF-8')}" rel="tag">${articleTag}</a><#if articleTag_has_next>,</#if>
                    </#list>
                </div>   
                <ul id="post-options">
                    <#if nextArticlePermalink??>
                    <li><a href="${nextArticlePermalink}" id="oprev"></a></li>
                    </#if>
                    <li><a href="mailto:?subject=${article.authorName} - ${article.articleTitle}&body=Check out this post: http://${blogHost}${article.articlePermalink}" id="omail"></a></li>
                    <li><a href="javascript:void(0)" onclick="window.open('http://service.weibo.com/share/share.php?url=http://${blogHost}${article.articlePermalink}&title=B3LOG%20-%20${article.articleTitle}', '_blank');" id="otweet"></a></li>		
                    <li><a href="javascript:void(0)" id="obook"></a></li>
                    <#if previousArticlePermalink??>
                    <li><a href="${previousArticlePermalink}" id="onext"></a></li>
                    </#if>
                </ul>
            </div>
            <div id="bookmark-box" style="display:none">
                <ul>
                    <li><a  href="http://del.icio.us/post?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/delicious.jpg" alt="" /> Del.icio.us</a></li>
                    <li><a href="http://digg.com/submit?phase=2&url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/digg.jpg" alt="" /> Digg</a></li>
                    <li><a href="http://technorati.com/faves?add=http://localhost/blog/?p=12" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/technorati.jpg" alt="" /> Technorati</a></li>
                    <li><a href="http://ma.gnolia.com/bookmarklet/add?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/magnolia.jpg" alt="" /> Magnolia</a></li>
                    <li><a href="http://www.newsvine.com/_wine/save?popoff=0&u=http://localhost/blog/?p=12&h=${article.articleTitle}" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/newsvine.jpg" target="_blank"> Newsvine</a></li>
                    <li class="noborder"><a href="http://reddit.com/submit?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="/skins/${skinDirName}/themes/core/core-images/bookmarks/reddit.jpg" alt="" /> Reddit</a></li>
                </ul>
            </div>
            <@comments commentList=articleComments permalink=article.articlePermalink></@comments>
        </div>
        <#include "footer.ftl">    
        <@comment_script oId=article.oId>
        page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
        <#if 0 != randomArticlesDisplayCount>
        page.loadRandomArticles();
        </#if>
        <#if 0 != relevantArticlesDisplayCount>
        page.loadRelevantArticles('${article.oId}', '${relevantArticles1Label}');
        </#if>
        <#if 0 != externalRelevantArticlesDisplayCount>
        page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
        </#if>
        </@comment_script>    
    </body>
</html>