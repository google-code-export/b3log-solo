<div class="info">
    <div class="left copyright">
        <span style="color:white;">&copy; ${year}</span> - <a style="color:white;" href="http://${blogHost}">${blogTitle}</a><br/>
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
    <div class="right goTop">
        <span onclick="util.goTop();">${goTopLabel}</span>
    </div>
</div>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript">
    var util = new Common({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}",
        "skinDirName": "${skinDirName}",
        "loginLabel": "${loginLabel}"
    });
    util.init();
    util.replaceCommentsEm(".side-navi .navi-comments .side-comment");
</script>