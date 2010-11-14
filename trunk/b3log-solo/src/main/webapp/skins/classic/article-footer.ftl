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
    var goingTop = false;
    var goingBottom = false;

    var goTop = function (acceleration, time) {
        if (goingBottom) {
            return;
        }
        
        goingTop = true;
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

        var x = Math.max(x1, Math.max(x2, x3));
        var y = Math.max(y1, Math.max(y2, y3));
        var speed = 1 + acceleration;
        window.scrollTo(Math.floor(x / speed), Math.floor(y / speed));
        
        if(x > 0 || y > 0) {
            var invokeFunction = "goTop(" + acceleration + ", " + time + ")";
            window.setTimeout(invokeFunction, time);
        } else {
            goingTop = false;
        }
    }
    
    var goBottom = function (acceleration, time) {
        if (goingTop) {
            return;
        }
        
        goingBottom = true;
        acceleration = acceleration || 0.1;
        acceleration = acceleration > 1 ? 1 : acceleration;
        time = time || 16;

        var x1 = 0;
        var x2 = 0;
        var x3 = 0;
        var y1 = 0;
        var y2 = 0;
        var y3 = 0;
        var clientHeight = 0;
        var scrollHeight = 0;

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

        var x = Math.max(x1, Math.max(x2, x3));
        var y = Math.max(y1, Math.max(y2, y3));

        if(document.body.clientHeight && document.documentElement.clientHeight) {
            clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        } else {
            clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        }

        scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
        var speed = acceleration;
        window.scrollTo(0, y + Math.ceil(((scrollHeight - y - clientHeight) * speed)));

        if (clientHeight + y < scrollHeight) {
            var invokeFunction = "goBottom(" + acceleration + ", " + time + ")";
            window.setTimeout(invokeFunction, time);
        } else {
            goingBottom = false;
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

            // article-side.ftl ellipsis
            sideEllipsis();
            
            $(window).resize(function () {
                if ($("#sideNavi").width() > 195) {
                    sideEllipsis();
                }
            });
        }
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