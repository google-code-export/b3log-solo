<div class="copyright">
    &copy; ${year} - <a href="http://${blogHost}">${blogTitle}</a><br/>
    Powered by
    <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
        ${b3logLabel}&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}<br/>
    Theme by <a href="http://www.madeincima.eu/" target="_blank">Andrea</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
</div>
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

    // init brush
    var buildBrush = function () {
        $("#brush").height(document.documentElement.scrollHeight - document.documentElement.clientHeight).css("background-position",
        parseInt((document.documentElement.scrollWidth - 910) / 2 - 56) + "px -150px");
    };

    // init
    (function () {
        common.init();
        common.replaceSideEm($("#naviComments li"));
    
        // brush
        buildBrush();

        $(window).resize(function () {
            buildBrush();
        });

        // bg
        $("#changeBG a").click(function () {
            if (this.className !== 'selected') {
                switch (this.id) {
                    case "greyBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-grey.jpg)");
                        break;
                    case "blueBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-blue.jpg)");
                        break;
                    case "brownBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-brown.jpg)");
                        break;
                }

                $("#changeBG a").removeClass();
                this.className = "selected";
            }
        });

        // page navi
        $(".side-tool li li a").hover(function () {
            if (parseInt($(this).css("padding-left")) === 9) {
                $(this).animate({
                    "padding-left": "54px"
                }, 600 );
            }
        }, function () {
            $(this).animate({
                "padding-left": "9px"
            }, 600 );
        });
    })();
</script>