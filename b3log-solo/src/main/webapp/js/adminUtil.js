/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
  
    beforeInitArticle: function () {
        articleStatus = $("#title").data("articleStatus");
        // set button status
        if (articleStatus) {
            if (articleStatus.isArticle) { 
                $("#unSubmitArticle").show();
                $("#submitArticle").hide();
            } else {
                $("#submitArticle").show();
                $("#unSubmitArticle").hide();
            }
            if (articleStatus.articleHadBeenPublished) {
                $("#postToCommunityTR").hide();
            } else {
                $("#postToCommunityTR").show();
            }
        } else {
            $("#submitArticle").show();
            $("#unSubmitArticle").hide();
            $("#postToCommunityTR").show();
        }

        $("#postToCommunity").attr("checked", "checked");
    },
    
    clearArticle: function () {
        $("#title").removeData("articleStatus").val("");
        this.beforeInitArticle();
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
        $(".signs button").each(function (i) {
            if (i === $(".signs button").length - 1) {
                this.className = "selected";
            } else {
                this.className = "";
            }
        });
    },

    init: function () {
        // 不支持 IE 6
        Util.killIE();      
        
        var tip = this.tip;
        // 构建 tabs 及其点击事件
        $("#tabs").tabs({
            "bind":[{
                "type": "click",
                "action": function (event, data) {
                    if ($("#tabs_" + data.id).html().replace(/\s/g, "") === "") {
                        $("#loadMsg").text(tip.loadingLabel);
                        $("#tabs_" + data.id).load("admin-" + data.id + ".do");
                    } else {
                        switch (data.id) {
                            case "article":
                                adminUtil.clearArticle();
                                break;
                            case "page-list":
                                getPageList(1);
                                break;
                            case "article-list":
                                getArticleList(1);
                                break;
                            case "draft-list":
                                getDraftList(1);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }]
        });
        
        // 根据 hash 设置当前 tab，如果 hash 为空时，默认为发布文章
        var hash = window.location.hash;
        if (hash !== "") {
            var tabId = hash.substr(1, hash.length - 1);
            $("#tabs_" + tabId).load("admin-" + tabId + ".do");
            $("#tabs").tabs("select", tabId);
        } else {
            $("#tabs_article").load("admin-article.do");
            window.location.hash = "#article";
        }
        
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
    },

    // others
    removeUnusedTags: function () {
        var tip = this.tip;
        $("#tipMsg").text("");
        jsonRpc.tagService.removeUnusedTags(function (result, error) {
            try {
                if (result.sc === "REMOVE_UNUSED_TAGS_SUCC") {
                    $("#tipMsg").text(tip.removeSuccLabel);
                } else {
                    $("#tipMsg").text(tip.removeFailLabel);
                }
            } catch (e) {
            }
        });
    },

    // article list and draft list
    updateArticle: function (data, isArticle) {
        var tip = this.tip,
        that = this;
        $("#loadMsg").text(tip.loadingLabel);
        
        // 当前 tab 更新为发布文章
        $("#tabs").tabs("select", "article");
        window.location.hash = "#article";
        if ($("#tabs_article").html().replace(/\s/g, "") === "") {
            $("#tabs_article" ).load("admin-article.do", function () {
                that.getArticle(data.id, isArticle, tip, that);
            });
        } else {
            that.getArticle(data.id, isArticle, tip, that);
        }
    },
    
    getArticle: function (id, isArticle, tip, that) {
        var requestJSONObject = {
            "oId": id
        };
        jsonRpc.articleService.getArticle(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_ARTICLE_SUCC":
                        // set default value for article.
                        $("#title").val(result.article.articleTitle).data("articleStatus", {
                            "isArticle": isArticle,
                            'oId': id,
                            "articleHadBeenPublished": result.article.articleHadBeenPublished
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
                        var signs = result.article.signs;
                        $(".signs button").each(function (i) {
                            if (parseInt(result.article.articleSign_oId) === parseInt(signs[i].oId)) {
                                $("#articleSign" + signs[i].oId).addClass("selected");
                            } else {
                                $("#articleSign" + signs[i].oId).removeClass("selected");
                            }
                        });

                        that.beforeInitArticle();
                        $("#tipMsg").text(tip.getSuccLabel);
                        break;
                    case "GET_ARTICLE_FAIL_":
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
            }
        }, requestJSONObject);
    },
    
    selectTab: function (id) {
        $("#tabs").tabs("select", id);
        if ($("#tabs_" + id).html().replace(/\s/g, "") === "") {
            $("#tabs_" + id).load("admin-" + id + ".do");
        } else {
            switch (id) {
                case "article":
                    adminUtil.clearArticle();
                    break;
                case "page-list":
                    getPageList(1);
                    break;
                case "article-list":
                    getArticleList(1);
                    break;
                case "draft-list":
                    getDraftList(1);
                    break;
                default:
                    break;
            }
        }
        window.location.hash = "#" + id;
    }
});