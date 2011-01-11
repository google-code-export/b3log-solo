<div>
    <div class="header-title">
        <h1 class="title">
            <a href="/" id="logoTitle" >
                ${blogTitle}
            </a>
        </h1>
        <span class="sub-title">${blogSubtitle}</span>
    </div>
    <div class="right">
        <ul>
            <li>
                <a class="home" href="/"></a>
            </li>
            <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}">
                    ${page.pageTitle}
                </a>
            </li>
            </#list>
            <li>
                <a href="/tags.html">${allTagsLabel}</a>
            </li>
            <li>
                <a href="/blog-articles-feed.do">
                    ${atomLabel}
                    <img src="/images/feed.png" alt="Atom"/>
                </a>
            </li>
            <li>
                <a class="lastNavi" href="javascript:void(0);"></a>
            </li>
        </ul>
    </div>
    <div class="clear"></div>
</div>