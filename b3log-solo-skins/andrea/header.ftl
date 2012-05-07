<div class="header">
    <div class="left">
        <h1>
            <a href="${servePath}">
                ${blogTitle}
            </a>
        </h1>
        <span class="sub-title">${blogSubtitle}</span>
        <div>
            <span>
                ${viewCount1Label}
                <span class='error-msg'>
                    ${statistic.statisticBlogViewCount}
                </span>
            </span>
            <span>
                ${articleCount1Label}
                <span class='error-msg'>
                    ${statistic.statisticPublishedBlogArticleCount}
                </span>
            </span>
            <span>
                ${commentCount1Label}
                <span class='error-msg'>
                    ${statistic.statisticPublishedBlogCommentCount}
                </span>
            </span>
        </div>
        <span class="clear"></span>
    </div>
    <div class="right">
        <ul>
            <li>
                <a class="home" href="${servePath}">Blog</a>
            </li>
            <li>
                <a href="${servePath}/tags.html">Tags</a>
            </li>
            <li>
                <a href="${servePath}/blog-articles-feed.do">
                    Atom
                </a>
            </li>
        </ul>
    </div>
    <div class="clear"></div>
</div>