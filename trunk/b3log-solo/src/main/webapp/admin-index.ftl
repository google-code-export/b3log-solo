<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
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
        <div id="loadMsg"></div>
        <div id="tipMsg"></div>
        <div id="adminMain">
            <#include "common-top.ftl">
            <div id="allPanel">
                <div class="left side">
                    <ul id="sideNavi">
                        <li id="articleTab" onclick="changeList(this);clearAtricle();" class="selected">
                            <div class="left postIcon"></div>
                            <span>&nbsp;${postArticleLabel}</span>
                        </li>
                        <li id="article-listTab" onclick="changeList(this);">
                            <div class="left articlesIcon"></div>
                            <span>&nbsp;${articleListLabel}</span>
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
                            <div class="left blogSyncIcon"></div>
                            <span>&nbsp;{pageLabel}</span>
                        </li>
                        <!--li>
                            <div class="left fileIcon"></div>
                            <span>&nbsp;${fileListLabel}</span>
                        </li-->
                    </ul>
                </div>
                <div class="left" id="main">
                    <div class="content">
                        <div id="articlePanel">
                            <div class="form paddingTop12">
                                <div class='left label'>
                                    ${title1Label}
                                </div>
                                <div class="left input">
                                    <input id="title" type="text"/>
                                </div>
                                <div class="clear"></div>
                                <div class="label">
                                    ${content1Label}
                                </div>
                                <div class="marginBottom12 marginLeft12">
                                    <textarea rows="30" id="articleContent" name="articleContent" style="width: 99%;"></textarea>
                                </div>
                                <div class="left label">${tags1Label}</div>
                                <div class="left input">
                                    <input id="tag" type="text"/>
                                </div>
                                <div class="clear"></div>
                                <div class="left label">${abstract1Label}</div>
                                <div class="left input">
                                    <textarea id="abstract" rows="12" name="abstract"></textarea>
                                </div>
                                <div class="clear"></div>
                                <div class="right label">
                                    <button id="submitArticle">${postLabel}</button>
                                </div>
                                <div class="clear"></div>
                            </div>
                        </div>
                        <div id="article-listPanel" class="none">
                        </div>
                        <div id="link-listPanel" class="none">
                        </div>
                        <div id="preferencePanel" class="none">
                        </div>
                        <div id="article-syncPanel" class="none">
                        </div>
                        <div id="pagePanel" class="none">
                        </div>
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
        </div>
        <script type="text/javascript">
            var changeList = function (it) {
                var tabs = ['article', 'article-list', 'link-list', 'preference', 'article-sync', 'page'];
                for (var i = 0; i < tabs.length; i++) {
                    if (it.id === tabs[i] + "Tab") {
                        if ($("#" + tabs[i] + "Panel").html().replace(/\s/g, "") === "") {
                            $("#" + tabs[i] + "Panel").load("admin-" + tabs[i] + ".do");
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
            }

            var PAGE_SIZE = 18,
            WINDOW_SIZE = 10;
            var initAdmin = function () {
                $("#loadMsg").text("${loadingLabel}");
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
                    var loginHTML = "<div class='left homeIcon' onclick=\"window.location='index.do';\" title='${indexLabel}'></div>"
                        + "<div class='left'>&nbsp;|&nbsp;</div>"
                        + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
                }

                // submit action
                $("#submitArticle").click(function () {
                    if ($("#title").data("oId")) {
                        updateArticle();
                    } else {
                        addArticle();
                    }
                });

                // tag auto completed
                jsonRpc.tagService.getTags(function (result, error) {
                    if (result.length > 0) {
                        var tags = [];
                        for (var i = 0; i < result.length; i++) {
                            tags.push(result[i].tagTitle);
                        }
                        $("#tag").completed({
                            height: 160,
                            data: tags
                        });
                    }
                })

                // editor
                tinyMCE.init({
                    // General options
                    mode : "exact",
                    elements : "articleContent, abstract",
                    theme : "advanced",
                    plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen",

                    // Theme options
                    theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
                    theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen",
                    theme_advanced_buttons3 : "",
                    theme_advanced_toolbar_location : "top",
                    theme_advanced_toolbar_align : "left",
                    theme_advanced_resizing : true,

                    extended_valid_elements: "pre[name|class]",

                    relative_urls: false,
                    remove_script_host: false
                });
                clearAtricle();
                $("#loadMsg").text("");
            }
            initAdmin();

            var validateArticle = function () {
                if ($("#title").val().replace(/\s/g, "") === "") {
                    $("#tipMsg").text("${titleEmptyLabel}");
                    $("#title").focus().val("");
                } else if (tinyMCE.get('articleContent').getContent().replace(/\s/g, "") === "") {
                    $("#tipMsg").text("${contentEmptyLabel}");
                } else if ($("#tag").val().replace(/\s/g, "") === "") {
                    $("#tipMsg").text("${tagsEmptyLabel}");
                    $("#tag").focus().val("");
                } else if(tinyMCE.get('abstract').getContent().replace(/\s/g, "") === "") {
                    $("#tipMsg").text("${abstractEmptyLabel}");
                } else {
                    return true;
                }
                return false;
            }

            var addArticle = function () {
                if (validateArticle()) {
                    $("#loadMsg").text("${loadingLabel}");
                    var tagArray = $("#tag").val().split(",");
                    var requestJSONObject = {
                        "article": {
                            "articleTitle": $("#title").val(),
                            "articleContent": tinyMCE.get('articleContent').getContent(),
                            "articleAbstract": tinyMCE.get('abstract').getContent(),
                            "articleTags": $.bowknot.trimUnique(tagArray).toString()
                        }
                    };

                    jsonRpc.articleService.addArticle(function (result, error) {
                        switch (result.status.code) {
                            case "ADD_ARTICLE_SUCC":
                                var events = result.status.events;
                                if (events) {
                                    var msg = "${addSuccLabel}";
                                    if ("BLOG_SYNC_ADD_CSDN_BLOG_FAIL" === events.blogSyncCSDNBlog.code) {
                                        msg += ", ${syncCSDNBlogFailLabel}";
                                    }

                                    if ("BLOG_SYNC_ADD_CNBLOGS_FAIL" === events.blogSyncCnBlogs.code) {
                                        msg += ", ${syncCnBlogsFailLabel}";
                                    }

                                    if ("BLOG_SYNC_ADD_BLOGJAVA_FAIL" === events.blogSyncBlogJava.code) {
                                        msg += ", ${syncBlogJavaFailLabel}";
                                    }
                                    $("#article-listPanel").load("admin-article-list.do", function () {
                                        $("#tipMsg").text(msg);
                                        $("#article-listTab").click();
                                    });

                                    if ("BLOG_SYNC_ADD_CSDN_BLOG_SUCC" === events.blogSyncCSDNBlog.code
                                        && "BLOG_SYNC_ADD_CNBLOGS_SUCC" === events.blogSyncCnBlogs.code
                                        && "BLOG_SYNC_ADD_BLOGJAVA_SUCC" === events.blogSyncBlogJava.code) {
                                        $("#article-listPanel").load("admin-article-list.do", function () {
                                            $("#tipMsg").text("${addSuccLabel}");
                                            $("#article-listTab").click();
                                        });
                                    }
                                    return;
                                }
                                $("#article-listPanel").load("admin-article-list.do", function () {
                                    $("#tipMsg").text("${addSuccLabel}");
                                    $("#article-listTab").click();
                                });
                                break;
                            default:
                                $("#tipMsg").text("${addFailLabel}");
                                break;
                        }
                        $("loadMsg").text("");
                    }, requestJSONObject);
                }
            }

            var updateArticle = function () {
                if (validateArticle()) {
                    $("#loadMsg").text("${loadingLabel}");
                    var tagArray = $("#tag").val().split(",");

                    var requestJSONObject = {
                        "article": {
                            "oId": $("#title").data("oId"),
                            "articleTitle": $("#title").val(),
                            "articleContent": tinyMCE.get('articleContent').getContent(),
                            "articleAbstract": tinyMCE.get('abstract').getContent(),
                            "articleTags": $.bowknot.trimUnique(tagArray).toString()
                        }
                    };

                    jsonRpc.articleService.updateArticle(function (result, error) {
                        switch (result.status.code) {
                            case "UPDATE_ARTICLE_SUCC":
                                var events = result.status.events;
                                if (events) {
                                    var msg = "${updateSuccLabel}";
                                    if ("BLOG_SYNC_UPDATE_CSDN_BLOG_FAIL" === events.blogSyncCSDNBlog.code) {
                                        msg += ", ${syncCSDNBlogFailLabel}";
                                    }

                                    if ("BLOG_SYNC_UPDATE_CNBLOGS_FAIL" === events.blogSyncCnBlogs.code) {
                                        msg += ", ${syncCnBlogsFailLabel}";
                                    }

                                    if ("BLOG_SYNC_UPDATE_BLOGJAVA_FAIL" === events.blogSyncBlogJava.code) {
                                        msg += ", ${syncBlogJavaFailLabel}";
                                    }
                                    $("#article-listPanel").load("admin-article-list.do", function () {
                                        $("#tipMsg").text(msg);
                                        $("#article-listTab").click();
                                    });

                                    if ("BLOG_SYNC_UPDATE_CSDN_BLOG_SUCC" === events.blogSyncCSDNBlog.code
                                        && "BLOG_SYNC_UPDATE_CNBLOGS_SUCC" === events.blogSyncCnBlogs.code
                                        && "BLOG_SYNC_UPDATE_BLOGJAVA_SUCC" === events.blogSyncBlogJava.code) {
                                        $("#article-listPanel").load("admin-article-list.do", function () {
                                            $("#tipMsg").text("${updateSuccLabel}");
                                            $("#article-listTab").click();
                                        });
                                    }
                                    return;
                                }
                                $("#article-listPanel").load("admin-article-list.do", function () {
                                    $("#tipMsg").text("${updateSuccLabel}");
                                    $("#article-listTab").click();
                                });
                                break;
                            default:
                                $("#tipMsg").text("${updateFailLabel}");
                                break;
                        }
                        $("loadMsg").text("");
                    }, requestJSONObject);
                }
            }
        </script>
    </body>
</html>
