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
    var goTop = function (acceleration, time) {
        acceleration = acceleration || 0.1;
        time = time || 16;

        var x1 = 0;
        var y1 = 0;
        var x2 = 0;
        var y2 = 0;
        var x3 = 0;
        var y3 = 0;

        if (document.documentElement) {
            x1 = document.documentElement.scrollLeft || 0;
            y1 = document.documentElement.scrollTop || 0;
        }

        if (document.body) {
            x2 = document.body.scrollLeft || 0;
            y2 = document.body.scrollTop || 0;
        }

        var x3 = window.scrollX || 0;
        var y3 = window.scrollY || 0;

        // 滚动条到页面顶部的水平距离
        var x = Math.max(x1, Math.max(x2, x3));
        // 滚动条到页面顶部的垂直距离
        var y = Math.max(y1, Math.max(y2, y3));

        // 滚动距离 = 目前距离 / 速度, 因为距离原来越小, 速度是大于 1 的数, 所以滚动距离会越来越小
        var speed = 1 + acceleration;
        window.scrollTo(Math.floor(x / speed), Math.floor(y / speed));

        // 如果距离不为零, 继续调用迭代本函数
        if(x > 0 || y > 0) {
            var invokeFunction = "goTop(" + acceleration + ", " + time + ")";
            window.setTimeout(invokeFunction, time);
        }
    }

    var strEllipsis = function (it, length) {
        var $it = $(it);
        if (it.offsetWidth > length) {
            var str = $it.text().replace(/(^\s*)|(\s*$)/g, "");
            $it.text(str.substr(0, str.length - 2));
            strEllipsis(it, length);
        } else {
            return {
                value:$it.text(),
                change: false
            };
        }
        return {
            value:$it.text(),
            change: true
        };
    }

    var sideEllipsis = function () {
        var sideLength = $("#sideNavi").width() - 50;
        $("#mostCommentArticles a").each(function () {
            var result = strEllipsis(this, sideLength);
            if (result.change) {
                $(this).text(result.value + "...");
            }
        });
        $("#mostViewCountArticles a").each(function () {
            var result = strEllipsis(this, sideLength);
            if (result.change) {
                $(this).text(result.value + "...");
            }
        });
        $("#sideLink a").each(function () {
            var result = strEllipsis(this, sideLength);
            if (result.change) {
                $(this).text(result.value + "...");
            }
        });
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
                var statisticHTML = "<span>${viewCount1Label}<span class='error-msg'>"
                    + result.statisticBlogViewCount + "</span>&nbsp;&nbsp;</span>"
                    + "<span>${articleCount1Label}<span class='error-msg'>"
                    + result.statisticBlogArticleCount + "</span>&nbsp;&nbsp;</span>"
                    + "<span>${commentCount1Label}<span class='error-msg'>"
                    + result.statisticBlogCommentCount + "</span></span>";
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
                        + comment.commentName + "</a>: ";

                    // The comment.commentContent may contain HTML entities, such as
                    // &#27979;&#35797;. So convert these entities to HTML string
                    // for calculating the length of the comment content
                    var commentContentSpan = document.createElement("span");
                    commentContentSpan.innerHTML = comment.commentContent;
                    var commentContent = commentContentSpan.innerHTML.length > 11 ?
                        commentContentSpan.innerHTML.substring(0, 11) + "...." : commentContentSpan.innerHTML;
                    itemHTML += commentContent + "</li>";

                    recentCommentsHTML += itemHTML;
                }

                recentCommentsHTML += "</ul>";
                $("#recentComments").after(recentCommentsHTML);
            });
        
            // article-side.ftl ellipsis
            sideEllipsis();
            
            $(window).resize(function () {
                if ($("#sideNavi").width() > 195) {
                    sideEllipsis();
                }
            });
        }

        if ($("#randomArticles").length < 1) {
            $("body").append("<div class='goTopIcon' onclick='goTop();'></div>");
        }
        jsonRpc.statisticService.incBlogViewCount(function (result, error) {});
    }
    initIndex();
    
    var clearCache = function () {
        // FIXME: issue 20
        var locationString = window.location.toString();
        var indexOfSharp = locationString.indexOf("#");
        var cachedPageKey = locationString.substring(locationString.lastIndexOf("/"),
        (-1 == indexOfSharp)? locationString.length : indexOfSharp);
        jsonRpc.adminService.clearPageCache(cachedPageKey);
        window.location.reload();
    }

    var clearAllCache = function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    }
    
    // article-side.ftl user introduction
    function handleResponse (response) {
        if ($("#sideNavi").length > 0) {
            var userInfo = {};
            var userIntroHTML = "";
            
            if (response.error) {
                userInfo.thumbnailUrl = localStorage.getItem("userInfoThumbnailUrl");
                userInfo.displayName = localStorage.getItem("userInfoDisplayName");
                userInfo.aboutMe = localStorage.getItem("userInfoAboutMe");
            } else {
                userInfo = response.data;
                localStorage.setItem("userInfoThumbnailUrl", response.data.thumbnailUrl);
                localStorage.setItem("userInfoDisplayName", response.data.displayName);
                localStorage.setItem("userInfoAboutMe", response.data.aboutMe);
            }

            if (null !== userInfo.thumbnailUrl
                &&  undefined !== userInfo.thumbnailUrl
                && "" !== userInfo.thumbnailUrl) {
                userIntroHTML = "<li><img src='" + userInfo.thumbnailUrl + "'/></li>"
                    + "<li>" + userInfo.displayName + "</li>"
                    + "<li class='aboutMe'>" + userInfo.aboutMe + "</li>";
            }
            
            $("#userIntro").html(userIntroHTML);
        }
    }
</script>
<script type="text/javascript" src="http://www.googleapis.com/buzz/v1/people/${userEmail}/@self?alt=json&callback=handleResponse"></script>