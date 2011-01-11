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
    
    // set selected navi
    $("#header-navi li").each(function (i) {
        if (i < $("#header-navi li").length - 1) {
            var $it = $(this),
            locationURL = window.location.pathname + window.location.search;
            if (i === 0 && (locationURL.indexOf("/index.do") > -1 || locationURL === "/")) {
                $it.addClass("selected");
                return;
            }
            if (locationURL.indexOf($it.find("a").attr("href")) > -1 && i !== 0) {
                $it.addClass("selected");
            }
        }
    });
</script>