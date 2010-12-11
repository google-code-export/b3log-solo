<div class="header-user">
    <div id="statistic">
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
</div>
<div class="header-navi">
    <div class="header-navi-main">
        <div class="left">
            <h1 class="title">
                <a href="/" id="logoTitle" >
                    ${blogTitle}
                </a>
            </h1>
            <span class="sub-title">${blogSubtitle}</span>
        </div>
        <div class="right header-right">
            <div class="left marginLeft12">
                <#list pageNavigations as page>
                <span>
                    <a href="${page.pagePermalink}">${page.pageTitle}</a>&nbsp;&nbsp;
                </span>
                </#list>
                <a href="/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
                <a href="/blog-articles-feed.do">${atomLabel}</a><a href="/blog-articles-feed.do"><img src="/images/feed.png" alt="Atom"/></a>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</div>
<script type="text/javascript">
    var replaceCommentsEm = function (selector) {
        var $commentContents = $(selector);
        for (var i = 0; i < $commentContents.length; i++) {
            var str = $commentContents[i].innerHTML;
            var ems = str.split("[em");
            var content = ems[0];
            for (var j = 1; j < ems.length; j++) {
                var key = ems[j].substr(0, 2),
                emImgHTML = "<img src='/skins/classic/emotions/em" + key
                    + ".png'/>";
                content += emImgHTML + ems[j].slice(3);
            }
            $commentContents[i].innerHTML = content;
        }
    }
</script>
