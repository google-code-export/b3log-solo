<div class="footer">
    <div class="wrapper">
        <div class="left">
            <span style="color: gray;">&copy; ${year}</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
            Powered by
            <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
                ${b3logLabel}&nbsp;
                <span style="color: orangered; font-weight: bold;">Solo</span></a>,
            ver ${version}&nbsp;&nbsp;
            Theme by <a href="http://www.neoease.com" target="_blank">NeoEase</a>
            & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
        </div>
        <a class="right" target="_blank" href="http://code.google.com/appengine/">
            <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif" />
        </a>
        <div class="clear"></div>
    </div>
</div>
<div id="goTop" onclick="goTop()">TOP</div>
<#if "" == miniPostfix>
<script type="text/javascript" src="/js/lib/jquery/jquery-1.7.min.js" charset="utf-8"></script>
<#else>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js" charset="utf-8"></script>
</#if>
<script type="text/javascript" src="/skins/${skinDirName}/js/${skinDirName}${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
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
    common.replaceSideEm($(".recent-comments-content"));
    common.buildTags("tagsSide");
    
    // recent comments
    $(".recent-comments .recent-comments-main").each(function () {
        if ($(this).find(".recent-comments-content>a").height() < 30) {
            $(this).find(".expand-ico").remove();
        } else {
            $(this).find(".expand-ico").parent().next().css({
                "white-space": "nowrap"
            });
        }
    });
</script>
