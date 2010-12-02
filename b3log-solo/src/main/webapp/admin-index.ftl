<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/lib/jquery/jquery.bowknot.min.js"></script>
        <script type="text/javascript" src="js/lib/tiny_mce/tiny_mce.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-admin.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-bowknot.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link rel="icon" type="image/png" href="favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <div id="loadMsg">${loadingLabel}</div>
        <div id="tipMsg"></div>
        <div id="adminMain">
            <div id="top">
                <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
                    <span style="color: orange;margin-left:0px;">B</span>
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
            <div id="allPanel">
                <div class="left side">
                    <ul id="sideNavi">
                        <li id="articleTab" onclick="changeList(this);clearAtricle();">
                            <div class="left postIcon"></div>
                            <span>&nbsp;${postArticleLabel}</span>
                        </li>
                        <li id="article-listTab" onclick="changeList(this);">
                            <div class="left articlesIcon"></div>
                            <span>&nbsp;${articleListLabel}</span>
                        </li>
                        <li id="draft-listTab" onclick="changeList(this);">
                            <div class="left draftsIcon"></div>
                            <span>&nbsp;${draftListLabel}</span>
                        </li>
                        <li id="link-listTab" onclick="changeList(this);">
                            <div class="left linkIcon"></div>
                            <span>&nbsp;${linkManagementLabel}</span>
                        </li>
                        <li id="preferenceTab" onclick="changeList(this);">
                            <div class="left preferenceIcon"></div>
                            <span>&nbsp;${preferenceLabel}</span>
                        </li>
                        <li id="article-syncTab" onclick="changeList(this);">
                            <div class="left blogSyncIcon"></div>
                            <span>&nbsp;${blogSyncLabel}</span>
                        </li>
                        <li id="pageTab" onclick="changeList(this);">
                            <div class="left pageIcon"></div>
                            <span>&nbsp;${pageMgmtLabel}</span>
                        </li>
                        <li id="file-listTab" onclick="changeList(this);">
                            <div class="left fileIcon"></div>
                            <span>&nbsp;${fileListLabel}</span>
                        </li>
                        <li id="othersTab" onclick="changeList(this);">
                            <div class="left othersIcon"></div>
                            <span>&nbsp;${othersLabel}</span>
                        </li>
                    </ul>
                </div>
                <div class="left" id="main">
                    <div class="content">
                        <div id="articlePanel" class="none">
                        </div>
                        <div id="article-listPanel" class="none">
                        </div>
                        <div id="draft-listPanel" class="none">
                        </div>
                        <div id="link-listPanel" class="none">
                        </div>
                        <div id="preferencePanel" class="none">
                        </div>
                        <div id="article-syncPanel" class="none">
                        </div>
                        <div id="pagePanel" class="none">
                        </div>
                        <div id="file-listPanel" class="none">
                        </div>
                        <div id="othersPanel" class="none">
                        </div>
                    </div>
                </div>
                <div class="clear"></div>
                <div class="footer">
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        <span style="color: orange;">B</span>
                        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                        <span style="color: green;">L</span>
                        <span style="color: red;">O</span>
                        <span style="color: blue;">G</span>&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span>,
                    </a>ver ${version}
                </div>
            </div>
        </div>
        <script type="text/javascript">
            var PAGE_SIZE = 18,
            WINDOW_SIZE = 10;

            var adminLogin = function () {
                var loginURL = jsonRpc.adminService.getLoginURL("/admin-index.do");
                window.location.href = loginURL;
            }

            var adminLogout = function () {
                var logoutURL = jsonRpc.adminService.getLogoutURL();
                window.location.href = logoutURL;
            }
            
            var changeList = function (it) {
                var tabs = ['article', 'article-list', 'draft-list', 'link-list', 'preference',
                    'article-sync', 'page', 'file-list', 'others'];
                for (var i = 0; i < tabs.length; i++) {
                    if (it.id === tabs[i] + "Tab") {
                        if ($("#" + tabs[i] + "Panel").html().replace(/\s/g, "") === "") {
                            $("#loadMsg").text("${loadingLabel}");
                            $("#" + tabs[i] + "Panel").load("admin-" + tabs[i] + ".do");
                        } else {
                            switch (tabs[i]) {
                                case "others":
                                    getCacheState();
                                    break;
                                case "article-list":
                                    getArticleList(1);
                                    break;
                                case "draft-list":
                                    getDraftList(1);
                                    break;
                                case "page":
                                    getPageList(1);
                                    break;
                                default:
                                    break;
                            }
                        }
                        $("#" + tabs[i] + "Panel").show();
                        $("#" + tabs[i] + "Tab").addClass("selected");
                    } else {
                        $("#" + tabs[i] + "Panel").hide();
                        $("#" + tabs[i] + "Tab").removeClass("selected");
                    }
                }
            }

            var clearAtricle = function () {
                $("#title").removeData("oId").val("");
                if (tinyMCE.get("articleContent")) {
                    tinyMCE.get('articleContent').setContent("");
                } else {
                    $("#articleContent").val("");
                }
                if (tinyMCE.get('abstract')) {
                    tinyMCE.get('abstract').setContent("");
                } else {
                    $("#abstract").val("");
                }
                $("#tag").val("");
                $("#permalink").val("");
            }

            var initAdmin = function () {
                // tipMsg
                setInterval(function () {
                    if($("#tipMsg").text() !== "") {
                        setTimeout(function () {
                            $("#tipMsg").text("");
                        }, 8000);
                    }
                }, 6000);

                // resize
                var $main = $("#main");
                var leftWidth = $(".side").width() + $.bowknot.strToInt($main.css("padding-left"))
                    + $.bowknot.strToInt($main.css("padding-right")) + 20;

                var windowWidth = document.documentElement.clientWidth - leftWidth;
                if (windowWidth < 700) {
                    windowWidth = 700;
                }
                $("#main").css("width", windowWidth);
                $(window).resize(function () {
                    var windowWidth = document.documentElement.clientWidth - leftWidth;
                    if (windowWidth < 700) {
                        windowWidth = 700;
                    }
                    $("#main").css("width", windowWidth);
                });

                // sideNavi action
                $("#sideNavi li").mouseover(function () {
                    $(this).addClass('hover');
                }).mouseout(function () {
                    $(this).removeClass('hover');
                });

                // login state
                var isAdminLoggedIn = jsonRpc.adminService.isAdminLoggedIn();
                if (isAdminLoggedIn) {
                    var loginHTML = "<div class='left homeIcon' onclick=\"window.location='/';\" title='${indexLabel}'></div>"
                        + "<div class='left'>&nbsp;|&nbsp;</div>"
                        + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
                }
                $("#articlePanel").load("admin-article.do",function () {
                    $("#loadMsg").text("");
                });
            }
            initAdmin();
        </script>
    </body>
</html>
