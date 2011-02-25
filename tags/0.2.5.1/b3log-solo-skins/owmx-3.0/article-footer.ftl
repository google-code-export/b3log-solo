<footer>
	<a href="http://code.google.com/appengine" target="_blank"><img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" alt="Powered by Google App Engine" /></a>&nbsp;&nbsp;
    <span>© 2011</span> - <a href="http://${blogHost}">${blogTitle}</a>
    Powered by
    <a href="http://b3log-solo.googlecode.com" target="_blank">
        <span style="color: orange;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}&nbsp;&nbsp;
    Theme by <a href="http://lightdian.b3log.org/" target="_blank">Dongxu Wang</a> & <a href="http://www.jabz.info/contact/jonas-jared-jacek/" title="Profile of Jonas Jacek">Jonas Jacek</a>.
	<div onclick="util.goTop();">${goTopLabel}</div>
</footer>
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
    util.replaceCommentsEm(".side-comment");
    
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