<div id="footer">
    <div class="left copyright">
        <span style="color:white;">© 2010</span> - <a style="color:white;" href="http://${blogHost}">${blogTitle}</a><br/>
        Powered by
        <a href="http://b3log-solo.googlecode.com" target="_blank">
            <span style="color: orange;">B</span>
            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
            <span style="color: green;">L</span>
            <span style="color: red;">O</span>
            <span style="color: blue;">G</span>&nbsp;
            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
        ver ${version}&nbsp;&nbsp;
        Theme by <a style="color:white;" href="http://www.iprimidieci.com/" target="_blank">Primi</a> & <a style="color:white;" href="http://lamb.b3log.org" target="_blank">Lamb</a>.
    </div>
    <div class="right nowrap statistic">
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
</div>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript">
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}",
        "skinDirName": "${skinDirName}"
    });
    util.init();
    util.replaceCommentsEm(".side-navi .navi-comments .side-comment");
</script>
