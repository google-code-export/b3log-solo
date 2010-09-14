<span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank">
    <span style="color: orange;">B</span>
    <span style="color: blue;">3</span>
    <span style="color: green;">L</span>
    <span style="color: red;">O</span>
    <span style="color: blue;">G</span>&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span>,
</a>ver ${version}
<script type="text/javascript">
    var ellipsis = function (str, strLength) {
        var length = 0,
        strTrim = str.replace(/(^\s*)|(\s*$)/g, ""),
        strArray = strTrim.split(""),
        resultStr = "";
        for (var i = 0; i < strArray.length; i++) {
            if (length < strLength) {
                if(strArray[i]&& strArray[i].match(/[^u4E00-u9FA5]/)) {
                    length += 2;
                } else {
                    length++;
                }
                resultStr += strArray[i];
            }
        }
        if (strTrim !== resultStr) {
            resultStr += "...";
        }
        return resultStr;
    }
    
    var initArticle = function () {
        // common-top.ftl use state
        jsonRpc.adminService.isAdminLoggedIn(function (result, error) {
            if (result && !error) {
                var loginHTML = "<span class='left' onclick='clearAllCache();'>${clearAllCacheLabel}&nbsp;|&nbsp;</span>"
                    + "<span class='left' onclick='clearCache();'>${clearCacheLabel}&nbsp;|&nbsp;</span>"
                    + "<div class='left adminIcon' onclick=\"window.location='admin-index.do';\" title='${adminLabel}'></div>"
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
                localStorage.setItem("sideNaviName", "");
                localStorage.setItem("sideNaviId", "");
            }

            $("#sideNavi a").click(function () {
                localStorage.setItem("sideNaviName", $(this).text());
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
            var sideEllipsis = function () {
                var sideLength = parseInt(($("#sideNavi").width() - 24) / 7 - 5);
                $("#mostCommentArticles a").each(function () {
                    var str = ellipsis(this.title, sideLength);
                    $(this).text(str.toString());
                });
                $("#mostViewCountArticles a").each(function () {
                    var str = ellipsis(this.title, sideLength);
                    $(this).text(str.toString());
                });
                $("#sideLink a").each(function () {
                    var str = ellipsis(this.title, sideLength);
                    $(this).text(str.toString());
                });
            }
            sideEllipsis();
            
            $(window).resize(function () {
                if ($("#sideNavi").width() > 195) {
                    sideEllipsis();
                }
            });
        }

        jsonRpc.statisticService.incBlogViewCount();
    }
    initArticle();

    // article-side.ftl user introduction
    function handleResponse (response) {
        if ($("#sideNavi").length > 0) {
            var userInfo = {},
            userIntroHTML = "";
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

            if (userInfo.thumbnailUrl === null || userInfo.thumbnailUrl === undefined || userInfo.thumbnailUrl === "") {
                userIntroHTML = "";
            } else {
                userIntroHTML = "<li><img src='" + userInfo.thumbnailUrl + "'/></li>"
                    + "<li>" + userInfo.displayName + "</li>"
                    + "<li class='aboutMe'>" + userInfo.aboutMe + "</li>";
            }
            $("#userIntro").html(userIntroHTML);
        }
    }
</script>
<script type="text/javascript" src="http://www.googleapis.com/buzz/v1/people/${userEmail}/@self?alt=json&callback=handleResponse"></script>