<div class="top">
    <div id="navigation">
        <a href="/" class="home">${homeLabel}</a>
        <a href="/tags.html" class="about">${allTagsLabel}</a>
        <#list pageNavigations as page>
        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" class="${page.pageTitle}">${page.pageTitle}</a>
        </#list>
        <a href="/blog-articles-feed.do" class="classifiche">${atomLabel}</a>
    </div>
    <div class="thinks"></div>
</div>