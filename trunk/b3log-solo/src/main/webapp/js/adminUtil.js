/*
 * Copyright (c) 2009, 2010, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var AdminUtil = function (tip) {
    this.PAGE_SIZE = 18;
    this.WINDOW_SIZE = 10;
    this.tip = tip;
};

$.extend(AdminUtil.prototype, {
    adminUtil: {
        version:"0.0.0.2",
        author: "lly219@gmail.com"
    },
    
    adminLogout: function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    },

    changeList: function (it) {
        var tabs = ['article', 'article-list', 'draft-list', 'link-list', 'preference',
        'article-sync', 'page', 'file-list', 'others', 'user-list'];
        for (var i = 0; i < tabs.length; i++) {
            if (it.id === tabs[i] + "Tab") {
                if ($("#" + tabs[i] + "Panel").html().replace(/\s/g, "") === "") {
                    $("#loadMsg").text(this.tip.loadingLabel);
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
    },

    clearAtricle: function () {
        $("#title").removeData("articleStatus").val("");
        beforeInitArticle();
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
    },

    init: function () {
        // Removes functions with the current user role
        if (this.tip.userRole !== "adminRole") {
            var unUsed = ['link-list', 'preference', 'file-list', 'article-sync', 'page', 'others', 'user-list'];
            for (var i = 0; i < unUsed.length; i++) {
                $("#" + unUsed[i] + "Tab").remove();
                $("#" + unUsed[i] + "Panel").remove();
            }
        }

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
        var leftWidth = $(".side").width() + parseInt($main.css("padding-left"))
        + parseInt($main.css("padding-right")) + 17;

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
       
        $("#articlePanel").load("admin-article.do",function () {
            $("#loadMsg").text("");
        });
    },

    // others
    removeUnusedTags: function () {
        var tip = this.tip;
        $("#tipMsg").text("");
        jsonRpc.tagService.removeUnusedTags(function (result, error) {
            if (result.sc === "REMOVE_UNUSED_TAGS_SUCC") {
                $("#tipMsg").text(tip.removeSuccLabel);
            } else {
                $("#tipMsg").text(tip.removeFailLabel);
            }
        });
    },

    // article list and draft list
    updateArticle: function (event, isArticle) {
        var tip = this.tip;
        $("#loadMsg").text(tip.loadingLabel);
        $("#articleTab").click();
        var requestJSONObject = {
            "oId": event.data.id[0]
        };
        jsonRpc.articleService.getArticle(function (result, error) {
            switch (result.sc) {
                case "GET_ARTICLE_SUCC":
                    // set default value for article.
                    $("#title").val(result.article.articleTitle).data("articleStatus", {
                        "isArticle": isArticle,
                        'oId': event.data.id[0]
                    });
                    if (tinyMCE.get('articleContent')) {
                        tinyMCE.get('articleContent').setContent(result.article.articleContent);
                    } else {
                        $("#articleContent").val(result.article.articleContent);
                    }
                    if (tinyMCE.get('abstract')) {
                        tinyMCE.get('abstract').setContent(result.article.articleAbstract);
                    } else {
                        $("#abstract").val(result.article.articleAbstract);
                    }

                    var tags = result.article.articleTags,
                    tagsString = '';
                    for (var i = 0; i < tags.length; i++) {
                        if (0 === i) {
                            tagsString = tags[i].tagTitle;
                        } else {
                            tagsString += "," + tags[i].tagTitle;
                        }
                    }
                    $("#tag").val(tagsString);
                    $("#permalink").val(result.article.articlePermalink);

                    // signs
                    var signs = result.article.signs,
                    signHTML = "";
                    for (var j = 0; j < signs.length; j++) {
                        if (result.article.articleSign.oId === signs[j].oId) {
                            signHTML = "<option value='" + signs[j].oId
                            + "'>" + signs[j].signHTML + "</option>" + signHTML;
                        } else {
                            signHTML += "<option value='" + signs[j].oId
                            + "'>" + signs[j].signHTML + "</option>";
                        }

                    }
                    $("#articleSigns").append(signHTML);

                    beforeInitArticle();
                    $("#tipMsg").text(tip.getSuccLabel);
                    break;
                case "GET_ARTICLE_FAIL_":
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }
});