<span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank" style="text-decoration: none;">
    <span style="color: orange;">B</span>
    <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
    <span style="color: green;">L</span>
    <span style="color: red;">O</span>
    <span style="color: blue;">G</span>&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span></a>,
ver ${version}&nbsp;&nbsp;
Theme by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a> & <a href="http://demo.woothemes.com/skeptical/" target="_blank">Skeptical</a>.
<div onclick="goTop();">${goTopLabel}</div>
<script type="text/javascript">
    var goTop = function () {
        window.scrollTo(0, 0);
    }
  
    var initIndex = function () {        
        // common-top.ftl use state
        jsonRpc.adminService.isLoggedIn(function (result, error) {
            if (result && !error) {
                var loginHTML = "";
                    <#if isAdminLoggedIn>
                    loginHTML = "<span class='left' onclick='clearAllCache();'>${clearAllCacheLabel}&nbsp;|&nbsp;</span>"
                    + "<span class='left' onclick='clearCache();'>${clearCacheLabel}&nbsp;|&nbsp;</span>";
                    </#if>
                    loginHTML += "<div class='left adminIcon' onclick=\"window.location='/admin-index.do';\" title='${adminLabel}'></div>"
                    + "<div class='left'>&nbsp;|&nbsp;</div>"
                    + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
               
                $("#admin").append(loginHTML);
            } else {
                $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
            }
        });
    }
    initIndex();
    
    var clearCache = function () {
        jsonRpc.adminService.clearPageCache(window.location.pathname);
        window.location.reload();
    }

    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }
    
    var adminLogin = function () {
        var loginURL = jsonRpc.adminService.getLoginURL("/admin-index.do");
        window.location.href = loginURL;
    }

    var adminLogout = function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    }
</script>