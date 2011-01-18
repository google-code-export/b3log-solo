
<#include "common-top.ftl">
<div id="wrapper-sub">
    <div id="header-wendyside"><!-- header --></div>
    <div id="content-wendyside">
        <div id="wendy-pagecontents">
            <div class="post-individual">
                <div class="posttime-pink">
                    <div class="posttime-MY"><#if article.hasUpdated>
                        ${article.articleUpdateDate?string("MMM yyyy")}
                        <#else>
                        ${article.articleCreateDate?string("MMM yyyy")}
                        </#if></div>
                    <div class="posttime-D"><#if article.hasUpdated>
                        ${article.articleUpdateDate?string("dd")}
                        <#else>
                        ${article.articleCreateDate?string("dd")}
                        </#if></div>
                </div>
                <div class="posttitle-page">
                    <h1 class="pink sIFR-replaced">
                        <span class="sIFR-alternate">${article.articleTitle}</span></h1>
                </div>
                <p class="postdetails-pink">Posted by:${article.authorName}<br/>
                    Tags:
                    <#list article.articleTags?split(",") as articleTag>
                    <a href="/tags/${articleTag?url('UTF-8')}">
                        ${articleTag}</a><#if articleTag_has_next>,</#if>
                    </#list>
                </p>
                <p>&nbsp;</p>
                <!-- zomg Loop loop -->
                <div class="homeentry">
                    ${article.articleContent}
                    <#if "" != article.articleSign.signHTML?trim>
                    <div class="marginTop12">
                        ${article.articleSign.signHTML}
                    </div>
                    </#if>
                </div>
                <p>&nbsp;</p>
                <#include "relative.ftl">
                <p>&nbsp;</p>
                <#include "comment.ftl">
                <p>&nbsp;</p>
            </div>
            <!-- End loop --></div>
        <div id="right-sidebar">
            <div id="author-tag"><!--author tag --></div>
            <#include "sidebar.ftl">
        </div>
    </div>
    <div id="prefooter-wendy">
        <div id="rss-right"><!-- RSS Icon--></div>
        <div id="rss-righttext"><a href="http://lambsand.appspot.com/feed">Subscribe to RSS</a></div>
    </div>
    <#include "footer.ftl">
</div>
