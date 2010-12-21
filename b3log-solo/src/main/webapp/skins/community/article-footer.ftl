<div class="content">
    <span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a>
    <br/>
    <div class="left">
        Powered by
        <a href="http://b3log-solo.googlecode.com" target="_blank" style="text-decoration: none;">
            <span style="color: orange;">B</span>
            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
            <span style="color: green;">L</span>
            <span style="color: red;">O</span>
            <span style="color: blue;">G</span>&nbsp;
            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
        ver ${version}&nbsp;&nbsp;
        Theme by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a> & <a href="http://demo.woothemes.com/skeptical/" target="_blank">Skeptical</a>.
    </div>
    <div class="right nowrap">
        ${viewCount1Label}
        <span class='error-msg'>
            ${statistic.statisticBlogViewCount}
        </span>
        &nbsp;&nbsp;
        ${articleCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogArticleCount}
        </span>
        &nbsp;&nbsp;
        ${commentCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogCommentCount}
        </span>
    </div>
    <div class="clear"></div>
    <div class="goTop" onclick="util.goTop();">${goTopLabel}</div>
</div>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
<script type="text/javascript">
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}"
    });

    var init = function () {
        // article header: user list.
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

        util.init();
    }

    init();
</script>