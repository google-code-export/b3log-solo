Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank">
    <span style="color: orange;">B</span>
    <span style="color: blue;">3</span>
    <span style="color: green;">L</span>
    <span style="color: red;">O</span>
    <span style="color: blue;">G</span>&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span>
</a>
, ver ${version}
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
                var loginHTML = "<a class='noUnderline' href='admin-index.do'>${adminLabel}</a>&nbsp;|&nbsp;"
                    + "<span onclick='clearAllCache();'>${clearAllCacheLabel}</span>&nbsp;|&nbsp;"
                    + "<span onclick='clearCache();'>${clearCacheLabel}</span>&nbsp;|&nbsp;"
                    + "<span onclick='adminLogout();'>${logoutLabel}</span>";
                $("#admin").append(loginHTML);
            } else {
                $("#admin").append("<span class='noUnderline' onclick='adminLogin();'>${loginLabel}</span>");
            }
        });
        
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
                var commentContent = comment.commentContent.length > 20 ?
                    comment.commentContent.substring(0, 20) : comment.commentContent;
                itemHTML += commentContent + "</li>";

                recentCommentsHTML += itemHTML;
            }

            recentCommentsHTML += "</ul>";
            $("#recentComments").after(recentCommentsHTML);
        });

        // article-side.ftl blogStatistic
        jsonRpc.statisticService.getBlogStatistic(function (result, error) {
            if (!error && result) {
                var statisticHTML = "<li>${viewCount1Label} " + result.statisticBlogViewCount + "</li>"
                    + "<li>${articleCount1Label} " + result.statisticBlogArticleCount + "</li>"
                    + "<li>${commentCount1Label} "+ result.statisticBlogCommentCount + "</li>";
                $("#statistic").html(statisticHTML);
            }
        });

        // article-side.ftl ellipsis
        var sideEllipsis = function () {
            var sideLength = parseInt(($("#sideNavi").width() - 24) / 7);
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

    initArticle();

    jsonRpc.statisticService.incBlogViewCount();

    //article-side.ftl
    function handleResponse (response) {
        var userIntroHTML = "<li><img src='" + response.data.thumbnailUrl + "'/></li>"
            + "<li>" + response.data.displayName + "</li>"
            + "<li class='aboutMe'>" + response.data.aboutMe + "</li>";

        $("#userIntro").html(userIntroHTML);
    }
</script>
<script type="text/javascript" src="http://www.googleapis.com/buzz/v1/people/${userEmail}/@self?alt=json&callback=handleResponse"></script>