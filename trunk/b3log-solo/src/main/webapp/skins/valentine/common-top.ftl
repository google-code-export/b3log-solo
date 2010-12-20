<div id="top">
    <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
        <span style="color: orange;margin-left:0px;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span>
    </a>
    <span class="right">
        <span id="admin"></span>
    </span>
    <div class="clear"></div>
</div>
<script type="text/javascript">
     var initIndex = function () {
        // common-top.ftl use state
        jsonRpc.adminService.isLoggedIn(function (result, error) {
            if (result && !error) {
                var loginHTML = "<span class='left' onclick='clearAllCache();'>${clearAllCacheLabel}&nbsp;|&nbsp;</span>"
                    + "<span class='left' onclick='clearCache();'>${clearCacheLabel}&nbsp;|&nbsp;</span>"
                    + "<div class='left adminIcon' onclick=\"window.location='/admin-index.do';\" title='${adminLabel}'></div>"
                    + "<div class='left'>&nbsp;|&nbsp;</div>"
                    + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
                $("#admin").append(loginHTML);
            } else {
                $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
            }
        });
    }
    initIndex();
    var adminLogin = function () {
        var loginURL = jsonRpc.adminService.getLoginURL("/admin-index.do");
        window.location.href = loginURL;
    }
    var adminLogout = function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    }
    var clearCache = function () {
        jsonRpc.adminService.clearPageCache(window.location.pathname);
        window.location.reload();
    }
    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }
</script>