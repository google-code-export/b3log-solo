<div id="top">
    <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
        <span style="color: orange;">B</span>
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
    var adminLogin = function () {
        var loginURL = jsonRpc.adminService.getLoginURL("/admin-index.do");
        window.location.href = loginURL;
    }

    var adminLogout = function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    }
</script>