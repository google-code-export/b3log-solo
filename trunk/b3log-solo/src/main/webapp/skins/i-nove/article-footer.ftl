<div class="left copyright">
    <span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
    Powered by
    <a href="http://b3log-solo.googlecode.com" target="_blank">
        <span style="color: orange;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
    ver ${version}&nbsp;&nbsp;
    Theme by <a href="http://www.neoease.com" target="_blank">NeoEase</a> & <a href="http://b3log-vanessa.appspot.com" target="_blank">Vanessa</a>.
</div>
<div class="right goTop">
    <span onclick="goTop();">${goTopLabel}</span>
</div>
<script type="text/javascript">
    var goTop = function () {
        window.scrollTo(0, 0);
    }
    
    var initIndex = function () {
        // common-top.ftl use state
        jsonRpc.adminService.isAdminLoggedIn(function (result, error) {
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

        if ($("#sideNavi").length > 0) {
            // article-side.ftl selected style
            if (window.location.search === "") {
                localStorage.setItem("sideNaviId", "");
            }

            $("#sideNavi a").click(function () {
                localStorage.setItem("sideNaviId", $(this).attr("name"));
            });

            $("#sideNavi a").each(function () {
                var $it = $(this);
                $it.removeClass("selected");
                if ($it.attr("name") && $it.attr("name") === localStorage.getItem("sideNaviId")) {
                    $it.addClass("selected");
                }
            });

            // TODO: Vanessa, using template in article-side.ftl instead
            // article-side.ftl comments
            jsonRpc.commentService.getRecentComments(function (result, error) {
                if (!result || error) {
                    return;
                }
                var recentCommentsHTML = "<ul>";

                for (var i = 0; i < result.recentComments.length; i++) {
                    var comment = result.recentComments[i];
                    var itemHTML = "<li><img class='left' title='" + comment.commentName
                        + "' alt='" + comment.commentName
                        + "' src='" + comment.commentThumbnailURL + "'/><div class='left'><div><a href=" + comment.commentURL + ">"
                        + comment.commentName + "</a></div><div><a class='side-comment' href=" + comment.commentSharpURL + ">"
                        + comment.commentContent + "</a></div></div><div class='clear'></div></li>";
                    recentCommentsHTML += itemHTML;
                }
                $("#recentComments").after(recentCommentsHTML + "</ul>");
            });
        }

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

        jsonRpc.statisticService.incBlogViewCount(function (result, error) {});
    }
    initIndex();
    
    var clearCache = function () {
        var locationString = window.location.toString();
        var indexOfSharp = locationString.indexOf("#");
        var url = locationString.substring(locationString.lastIndexOf("/"),
        (-1 == indexOfSharp)? locationString.length : indexOfSharp);
        jsonRpc.adminService.clearPageCache(url);
        window.location.reload();
    }

    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }
</script>