<div class="copyright">
    © 2010 - <a href="http://${blogHost}">${blogTitle}</a><br/>
    Powered by
    <a class="b3log" href="http://b3log-solo.googlecode.com" target="_blank">
        <span style="color: orange;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}<br/>
    Theme by <a href="http://www.madeincima.eu/" target="_blank">Andrea</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
</div>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript">
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}"
    });
    util.init();
    util.replaceCommentsEm(".side-navi .navi-comments .side-comment", "i-nove");
    
    // init brush
    var buildBrush = function () {
        if ($.browser.msie) {
            $("#brush").height(document.documentElement.scrollHeight - 550).css("background-position",
            parseInt((document.documentElement.scrollWidth - 910) / 2 - 51) + "px -150px");
        } else {
            $("#brush").height(document.documentElement.scrollHeight - 600).css("background-position",
            parseInt((document.documentElement.scrollWidth - 910) / 2 - 51) + "px -150px");
        }
    }
    buildBrush();
    $(window).resize(function () {
        buildBrush();
    });

</script>