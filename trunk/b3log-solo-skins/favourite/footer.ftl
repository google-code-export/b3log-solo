<div class="info">
    <div class="copyright">
        <span style="color:white;">&copy; ${year}</span> - <a style="color:white;" href="http://${blogHost}">${blogTitle}</a><br/>
        Powered by
        <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
            ${b3logLabel}&nbsp;
            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
        ver ${version}&nbsp;&nbsp;
        Theme by <a style="color:white;" href="http://www.iprimidieci.com/" target="_blank">Primi</a> & <a style="color:white;" href="http://lamb.b3log.org" target="_blank">Lamb</a>.
    </div>
    <div class="right goTop">
        <span onclick="common.goTop();">${goTopLabel}</span>
    </div>
</div>
<#if "" == miniPostfix>
<script type="text/javascript" src="/js/lib/jquery/jquery-1.7.min.js" charset="utf-8"></script>
<#else>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js" charset="utf-8"></script>
</#if>
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
    common.replaceSideEm($(".side-navi .navi-comments .side-comment"));
</script>