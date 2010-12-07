<div class="info">
<div class="left copyright">
    <span style="color:white;">© 2010</span> - <a style="color:white;" href="http://${blogHost}">${blogTitle}</a><br/>
    Powered by
    <a href="http://b3log-solo.googlecode.com" target="_blank">
        <span style="color: orange;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}&nbsp;&nbsp;
    Theme by <a style="color:white;" href="http://www.iprimidieci.com/" target="_blank">Primi</a> & <a style="color:white;" href="http://lamb.b3log.org" target="_blank">Lamb</a>.
</div>
<div class="right goTop">
    <span onclick="goTop();">${goTopLabel}</span>
</div>
<div class="qq" id="qq2">
<a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=61357158&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:61357158:50" alt="点击这里给我发消息" title="点击这里给我发消息"></a>
</div>
</div>
<script type="text/javascript">
    var goTop = function () {
        window.scrollTo(0, 0);
    }
    
    var initIndex = function () {
        // side comment
        replaceCommentsEm(".side-navi .navi-comments .side-comment");
        
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

        // set selected navi
        $("#header-navi li").each(function (i) {
            if (i < $("#header-navi li").length - 1) {
                var $it = $(this),
                locationURL = window.location.pathname + window.location.search;
                if (i === 0 && (locationURL.indexOf("/index.do") > -1 || locationURL === "/")) {
                    $it.addClass("selected");
                    return;
                }
                if (locationURL.indexOf($it.find("a").attr("href")) > -1 && i !== 0) {
                    $it.addClass("selected");
                }
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
</script>