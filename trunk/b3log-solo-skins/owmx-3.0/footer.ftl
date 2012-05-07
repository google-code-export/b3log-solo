<footer>
    <a href="http://code.google.com/appengine" target="_blank"><img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" alt="Powered by Google App Engine" /></a>&nbsp;&nbsp;
    <span>&copy; ${year}</span> - <a href="${servePath}">${blogTitle}</a>
    Powered by
    <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
        ${b3logLabel}&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}&nbsp;&nbsp;
    Theme by <a href="http://dx.b3log.org/" target="_blank">DX</a> & <a href="http://www.jabz.info/contact/jonas-jared-jacek/" title="Profile of Jonas Jacek">Jonas Jacek</a>.
    <div onclick="common.goTop();">${goTopLabel}</div>
</footer>
<script type="text/javascript" src="/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var common = new Common({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}",
        "skinDirName": "${skinDirName}",
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
    });
    common.init();
    common.replaceSideEm($(".side-comment").parent());
</script>
${plugins}
