<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>B3log</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link rel="icon" type="image/png" href="favicon.png"/>
    </head>
    <body>
        <div id="initPanel">
        </div>
        <script type="text/javascript">
            var init = function () {
                var isAdminLoggedIn = jsonRpc.adminService.isAdminLoggedIn();
                if (isAdminLoggedIn) {
                    jQuery("#initPanel").html("<button onclick='initSys();'>init</button>");
                } else {
                    jQuery("#initPanel").html("<button onclick='login();'>login</button>");
                }
            }
            init();

            var initSys = function () {
                var rslt = jsonRpc.adminService.init();
                if ("INIT_B3LOG_SOLO_SUCC" === rslt.sc) {
                    window.location.href = "/admin-index.do";
                } else {
                    alert("init error!");
                }
            }

            var login = function () {
                var loginURL = jsonRpc.adminService.getLoginURL("/init.do");
                window.location.href = loginURL;
            }
        </script>
    </body>
</html>
