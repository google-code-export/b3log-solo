<div id="footer">

    <div id="twitter">
        <h2 class="alt"></h2>
        <p></p>
        <p id="follow"></p>
    </div><hr />
        <div  style="padding-top:400px;text-align: center;color: #FFFFFF;">
           <span>© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
            Powered by
            <a href="http://b3log-solo.googlecode.com" target="_blank">
                <span style="color: orange;">B</span>
                <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                <span style="color: green;">L</span>
                <span style="color: red;">O</span>
                <span style="color: blue;">G</span>&nbsp;
                <span style="color: orangered; font-weight: bold;">Solo</span></a>,
            ver ${version}
        </div>
</div>


<ul id="service-menu">
     <li id="contact-me"><a href="http://www.madeincima.eu/contact/">Contact</a></li>
     <li id="about-me"><a href="http://www.madeincima.eu/about/">About</a></li>
 </ul>

<script type="text/javascript">
    var initIndex = function () {
        // side comment
        replaceCommentsEm("#recentComments li .side-comment");
        
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
    
    var clearCache = function () {
        jsonRpc.adminService.clearPageCache(window.location.pathname);
        window.location.reload();
    }

    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }
</script>
