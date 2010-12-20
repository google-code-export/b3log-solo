<#if 1 != users?size>
<div class="header-user">
    <div class="content">
        <#list users as user>
        <div>
            <a class="star-icon" href="/author-articles.do?oId=${user.oId}">
                ${user.userName}
            </a>
        </div>
        </#list>
        <div class="moon-current-icon"></div>
        <div class="clear"></div>
    </div>
</div>
</#if>
<div class="header-navi">
    <div class="header-navi-main content">
        <div class="left">
            <a href="/" class="header-title">
                ${blogTitle}
            </a>
            <span class="sub-title">${blogSubtitle}</span>
        </div>
        <div class="right">
            <ul class="tabs">
                <li class="tab">
                    <a href="/">${homeLabel}</a>
                </li>
                <li class="tab">
                    <a href="/tags.html">${allTagsLabel}</a>
                </li>
                <#if 0 != pageNavigations?size>
                <li class="tab" id="header-pages">
                    <a href="/">
                        <span class="left">
                            ${pageLabel}
                        </span>
                        <span class="arrow-dowm-icon"></span>
                        <span class="clear"></span>
                    </a>
                    <ul class="sub-tabs none">
                        <#list pageNavigations as page>
                        <li class="sub-tab">
                            <a href="${page.pagePermalink}">${page.pageTitle}</a>
                        </li>
                        </#list>
                    </ul>
                </li>
                </#if>
                <li class="tab">
                    <a href="/blog-articles-feed.do">
                        <span class="left">${atomLabel}</span>
                        <span class="atom-icon"></span>
                        <span class="clear"></span>
                    </a>
                </li>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
</div>
<div class="left none">
    ${noticeBoard}
</div>
<div class="right none">
    <span>${viewCount1Label}
        <span class='error-msg'>
            ${statistic.statisticBlogViewCount}
        </span>
        &nbsp;&nbsp;
    </span>
    <span>
        ${articleCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogArticleCount}
        </span>
        &nbsp;&nbsp;
    </span>
    <span>
        ${commentCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogCommentCount}
        </span>
    </span>
</div>
<div class="clear"></div>
<script type="text/javascript">
    var isAuthorArticle = false;
    $(".header-user a").each(function () {
        var it = this;
        if (window.location.search === it.search) {
            it.className = "star-current-icon";
            isAuthorArticle = true;
        }
    });
    if (isAuthorArticle) {
        $(".moon-current-icon").removeClass().addClass("moon-icon");
    }
</script>