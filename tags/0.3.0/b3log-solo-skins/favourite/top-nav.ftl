<div id="top">
    <a href="http://b3log-solo.googlecode.com" target="_blank" class="hover">
        B3log Solo
    </a>
    <span class="right" id="admin">
    </span>
    <div class="clear"></div>
</div>
<div class="top">
    <div id="navigation">
        <a href="/" class="home">${homeLabel}</a>
        <a href="/tags.html" class="about">${allTagsLabel}</a>
        <#list pageNavigations as page>
        <a href="${page.pagePermalink}" class="${page.pageTitle}">${page.pageTitle}</a>
        </#list>
        <a href="/blog-articles-feed.do" class="classifiche">${atomLabel}</a>
    </div>
    <div class="thinks"></div>
</div>