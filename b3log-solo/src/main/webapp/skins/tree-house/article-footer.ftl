<span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank">
    <span style="color: orange;">B</span>
    <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
    <span style="color: green;">L</span>
    <span style="color: red;">O</span>
    <span style="color: blue;">G</span>&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span></a>,
ver ${version}
<script type="text/javascript">
    var goTop = function () {
        window.scrollTo(0, 0);
    }

    var goBottom = function () {
        var clientHeight  = 0, scrollHeight = 0;
        if(document.body.clientHeight && document.documentElement.clientHeight) {
            clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        } else {
            clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        }
        scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
        window.scrollTo(0, scrollHeight - clientHeight - 350);
        
    }



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

        // article-header.ftl blogStatistic
        jsonRpc.statisticService.getBlogStatistic(function (result, error) {
            if (!error && result) {
                var statisticHTML = "<div>${viewCount1Label}&nbsp;&nbsp;<span class='error-msg'>"
                    + result.statisticBlogViewCount + "</span></div>"
                    + "<div>${articleCount1Label}&nbsp;&nbsp;<span class='error-msg'>"
                    + result.statisticBlogArticleCount + "</span></div>"
                    + "<div>${commentCount1Label}&nbsp;&nbsp;<span class='error-msg'>"
                    + result.statisticBlogCommentCount + "</span></div>";
                $("#statistic").html(statisticHTML);
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

            // article-side.ftl comments
            jsonRpc.commentService.getRecentComments(function (result, error) {
                if (!result || error) {
                    return;
                }
                var recentCommentsHTML = "<ul>";
                for (var i = 0; i < result.recentComments.length; i++) {
                    var comment = result.recentComments[i];
                    var itemHTML = "<li><a href=" + comment.commentSharpURL + ">"
                        + comment.commentName + ":<span>" + comment.commentContent + "</span></a></li>";
                    recentCommentsHTML += itemHTML;
                }
                recentCommentsHTML += "</ul><div class='clear'></div>";
                $("#recentComments").after(recentCommentsHTML);
            });
        }

        jsonRpc.statisticService.incBlogViewCount();
    }
    initIndex();
</script>