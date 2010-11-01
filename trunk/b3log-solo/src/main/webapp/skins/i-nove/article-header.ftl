<div class="header">
    <h1 class="title">
        <a href="/" id="logoTitle" >
            ${blogTitle}
        </a>
    </h1>
    <span class="sub-title">${blogSubtitle}</span>
</div>
<div class="header-navi">
    <div class="left">
        <#list pageNavigations as page>
        <span>
            <a href="/page.do?oId=${page.oId}">
                ${page.pageTitle}
            </a>&nbsp;&nbsp;
        </span>
        </#list>
        <a href="/tags.do">${allTagsLabel}</a>&nbsp;&nbsp;
        <a href="/blog-articles-feed.do">${atomLabel}</a><a href="/blog-articles-feed.do"><img src="/images/feed.png" alt="Atom"/></a>
    </div>
    <div class="right" id="statistic">
    </div>
    <div class="clear"></div>
</div>
