<div id="top">
    <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
        <span style="color: orange;">B</span>
        <span style="color: blue;">3</span>
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
    var clearCache = function () {
        var locationString = window.location.toString();
        var indexOfSharp = locationString.indexOf("#");
        var cachedPageKey = locationString.substring(locationString.lastIndexOf("/"), indexOfSharp);
        jsonRpc.adminService.clearPageCache(cachedPageKey);
        window.location.reload();
    }

    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }

    var adminLogin = function () {
        var loginURL = jsonRpc.adminService.getLoginURL();
        window.location.href = loginURL;
    }

    var adminLogout = function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    }
</script>