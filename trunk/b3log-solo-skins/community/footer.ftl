<div class="content paddingTop12 paddingBottom12">
    <div class="left">
        <div>
            <span style="color: gray;">&copy; ${year}</span> - <a href="http://${blogHost}">${blogTitle}</a>
        </div>
        Powered by
        <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
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
        <div class="goTop right" onclick="common.goTop();">${goTopLabel}</div>
        <br/>
        <div class="right">
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
    </div>
    <div class="clear"></div>
</div>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript">
    var common = new Common({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}",
        "skinDirName": "${skinDirName}",
        "loginLabel": "${loginLabel}",
        "loginLabel": "${loginLabel}",
        "em00Label": "${em00Label}",
        "em01Label": "${em01Label}",
        "em02Label": "${em02Label}",
        "em03Label": "${em03Label}",
        "em04Label": "${em04Label}",
        "em05Label": "${em05Label}",
        "em06Label": "${em06Label}",
        "em07Label": "${em07Label}",
        "em08Label": "${em08Label}",
        "em09Label": "${em09Label}",
        "em10Label": "${em10Label}",
        "em11Label": "${em11Label}",
        "em12Label": "${em12Label}",
        "em13Label": "${em13Label}",
        "em14Label": "${em14Label}"
    }),
    maxLength = parseInt("${mostCommentArticles?size}");

    (function () {
        // article header: user list.
        var isAuthorArticle = false;
        $(".header-user a").each(function () {
            var it = this;
            if (window.location.pathname === it.pathname) {
                it.className = "star-current-icon";
                isAuthorArticle = true;
            }
        });
        if (isAuthorArticle) {
            $(".moon-current-icon").removeClass().addClass("moon-icon");
        }

        common.init();
        common.replaceSideEm($(".recent-comments div"));
        
        $(".footer-block").each(function (num) {
            var $lis = $(this).find("li");
            if ($lis.length > maxLength) {
                for (var i = maxLength; i < $lis.length; i++) {
                    $lis.get(i).style.display = "none";
                }
                $(this).find("h4").append("<span class='down-icon' onmouseover=\"showFooterBlock(this, " + num + ");\"></span>");
            }
        });
    })();

    var showFooterBlock = function (it, num) {
        var $li = $($(".footer-block").get(num)).find("li");
        for (var i = maxLength; i < $li.length; i++) {
            if (it.className === "down-icon") {
                $($li.get(i)).slideDown("normal");
            } else {
                $($li.get(i)).slideUp("normal");
            }
        }
        if (it.className === "down-icon") {
            it.className = "up-icon";
        } else {
            it.className = "down-icon";
        }
    }
</script>