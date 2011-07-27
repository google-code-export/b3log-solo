<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${welcomeToSoloLabel}</title>
        <link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css"/>
        <link type="text/css" rel="stylesheet" href="/css/default-init${miniPostfix}.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        <img style="width:16px;height:16px;" class="introSign" alt="B3log" title="B3log" src="/favicon.png"/>
                    </a>
                    <div class="left introImg">
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img style="width: 153px;height:56px;" alt="B3log" title="B3log" src="/images/logo.png"/>
                        </a>
                    </div>
                    <div class="left introContent">
                        ${initIntroLabel}
                        <button onclick='initSys();'>${initLabel}</button>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year}
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
                        <span style="color: orange;">B</span>
                        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                        <span style="color: green;">L</span>
                        <span style="color: red;">O</span>
                        <span style="color: blue;">G</span>&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                    ver ${version}
                </div>
            </div>
        </div>
        <script type="text/javascript" src="js/common.js"></script>
        <script type="text/javascript">
            (function () {
                Util.killIE();
                $.ajax({
                    type: "POST",
                    url: "/check-login.do",
                    success: function(result){
                        if (!result.isLoggedIn) {
                            $(".introContent").html("<h1>" + $(".introContent h2").html() + "</h1>"
                                + "<img src='/images/arrow.png' title='B3log' alt='B3log'/><button onclick='login();'>${loginLabel}</button>")
                            .removeClass("introContent").addClass("introContentLogin");
                            $($(".introContentLogin h1 span")[0]).css("font-size", "36px");
                        }
                    }
                });
            })();

            var initSys = function () {
                if(confirm("${confirmInitLabel}")){
                    var rslt = jsonRpc.adminService.init();
                    if ("INIT_B3LOG_SOLO_SUCC" === rslt.sc) {
                        window.location.href = "/admin-index.do";
                    } else {
                        alert("init error!");
                    }
                }
            }

            var login = function () {
                var loginURL = jsonRpc.adminService.getLoginURL("/init.do");
                window.location.href = loginURL;
            }
        </script>
    </body>
</html>
