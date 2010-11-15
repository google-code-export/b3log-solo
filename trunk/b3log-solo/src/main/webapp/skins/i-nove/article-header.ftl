<div class="header">
    <h1 class="title">
        <a href="/" id="logoTitle" >
            ${blogTitle}
        </a>
    </h1>
    <span class="sub-title">${blogSubtitle}</span>
</div>
<div id="header-navi">
    <div class="left">
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
                <a href="/tags.do">${allTagsLabel}</a>
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
    <div class="right" id="statistic">
        <span>${viewCount1Label}<span class='error-msg'>
                ${statistic.statisticBlogViewCount}</span>&nbsp;&nbsp;</span>
        <span>${articleCount1Label}<span class='error-msg'>
                ${statistic.statisticBlogArticleCount}</span>&nbsp;&nbsp;</span>
        <span>${commentCount1Label}<span class='error-msg'>
                ${statistic.statisticBlogCommentCount}</span></span>
    </div>
    <div class="clear"></div>
</div>
