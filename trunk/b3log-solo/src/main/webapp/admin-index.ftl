<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <script type="text/javascript" src="js/lib/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/json-rpc.js"></script>
        <script type="text/javascript" src="js/lib/jquery/jquery.bowknot.min.js"></script>
        <script type="text/javascript" src="js/lib/tiny_mce/tiny_mce.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-admin.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-bowknot.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link rel="shortcut icon" href="favicon.ico" />
        <link rel="icon" type="image/gif" href="favicon.gif"/>
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="topMsg">
            <span id="tipMsg"></span>
        </div>
        <div id="allPanel">
            <div class="left side">
                <ul id="navi">
                    <li>
                        <div class="left postIcon"></div>
                        <span>&nbsp;${postArticleLabel}</span>
                    </li>
                    <li>
                        <div class="left articlesIcon"></div>
                        <span>&nbsp;${articleListLabel}</span>
                    </li>
                    <li>
                        <div class="left linkIcon"></div>
                        <span>&nbsp;${linkManagementLabel}</span>
                    </li>
                    <li>
                        <div class="left preferenceIcon"></div>
                        <span>&nbsp;${preferenceLabel}</span>
                    </li>
                    <li>
                        <div class="left blogSyncIcon"></div>
                        <span>&nbsp;${blogSyncLabel}</span>
                    </li>
                </ul>
            </div>
            <div class="left" id="main">
                <div id="content">
                </div>
            </div>
            <div class="clear"></div>
            <div class="footer">
                Powered by
                <a href="http://b3log-solo.googlecode.com" target="_blank">
                    <span style="color: orange;">B</span>
                    <span style="color: blue;">3</span>
                    <span style="color: green;">L</span>
                    <span style="color: red;">O</span>
                    <span style="color: blue;">G</span>&nbsp;
                    <span style="color: orangered; font-weight: bold;">Solo</span>,
                </a>ver ${version}
            </div>
        </div>
        <script type="text/javascript">
            var PAGE_SIZE = 18,
            WINDOW_SIZE = 10;
            var initAdmin = function () {
                // tipMsg
                setInterval(function () {
                    if($("#tipMsg").text() !== "") {
                        setTimeout(function () {
                            $("#tipMsg").text("");
                        }, 5000);
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

                // navi action
                $("#navi li").each(function (i) {
                    var naviURL = ['article', 'article-list', 'link-list', 'preference', 'article-sync'],
                    $it = $(this);
                    $it.click(function () {
                        var loadURL = "admin-" + naviURL[i] + ".do";
                        if (!$it.hasClass("selected")){
                            $("#navi li").removeClass('selected');
                            $it.addClass('selected');
                        }
                        $("#content").load(loadURL);
                    });
                }).mouseover(function () {
                    $(this).addClass('hover');
                }).mouseout(function () {
                    $(this).removeClass('hover');
                });

                // login state
                var isAdminLoggedIn = jsonRpc.adminService.isAdminLoggedIn();
                if (isAdminLoggedIn) {
                    var loginHTML = "<div class='left homeIcon' onclick=\"window.location='index.do';\" title='${indexLabel}'></div>"
                        + "<div class='left'>&nbsp;|&nbsp;</div>"
                        + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
                }
            }
            initAdmin();

            // util for admin
            var setCurrentNaviStyle = function (i) {
                $("#navi li").removeClass('selected');
                $($("#navi li").get(i)).addClass("selected");
            }
        </script>
    </body>
</html>
