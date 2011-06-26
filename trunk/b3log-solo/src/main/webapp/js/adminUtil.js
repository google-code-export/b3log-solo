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
    this.PAGE_SIZE = 1;
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
                case "draft-list":
                    adminUtil.getArticleList(1);
                    break;
                default:
                    break;
            }
        }
        window.location.hash = "#" + id;
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
                            case "draft-list":
                                adminUtil.getArticleList(1);
                                break;
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
    updateArticle: function (id, isArticle) {
        var tip = this.tip,
        that = this;
        $("#loadMsg").text(tip.loadingLabel);
        
        // 当前 tab 更新为发布文章
        $("#tabs").tabs("select", "article");
        window.location.hash = "#article";
        if ($("#tabs_article").html().replace(/\s/g, "") === "") {
            $("#tabs_article" ).load("admin-article.do", function () {
                that.getArticle(id, isArticle, tip, that);
            });
        } else {
            that.getArticle(id, isArticle, tip, that);
        }
    },
    
    // 获取文章并把值塞入发布文章页面
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
    
    getArticleList: function (pageNum, fromId) {
        $("#loadMsg").text(this.tip.loadingLabel);
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE,
            "articleIsPublished": true
        };
        if (fromId === "draft") {
            requestJSONObject.articleIsPublished = false;
        }
        jsonRpc.articleService.getArticles(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_ARTICLES_SUCC":
                        var articles = result.articles,
                        articleData = [];

                        for (var i = 0; i < articles.length; i++) {
                            articleData[i] = {};
                            articleData[i].tags = "<div title='" + articles[i].articleTags + "'>" + articles[i].articleTags + "</div>";
                            articleData[i].title = "<a href='" + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                            + articles[i].articleTitle + "</a>";
                            articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                            articleData[i].comments = articles[i].articleCommentCount;
                            articleData[i].articleViewCount = articles[i].articleViewCount;
                            articleData[i].id = articles[i].oId;
                            articleData[i].author = articles[i].authorName;
                            
                            var topClass = articles[i].articlePutTop ? adminUtil.tip.cancelPutTopLabel : adminUtil.tip.putTopLabel;
                            articleData[i].expendRow = "<a target='_blank' href='" + articles[i].articlePermalink + "'>" + adminUtil.tip.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"adminUtil.updateArticle('" + articles[i].oId + "', true);\">" + adminUtil.tip.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"removeArticle('" + articles[i].oId + "')\">" + adminUtil.tip.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"popTop(this, '" + articles[i].oId + "')\">" + topClass + "</a>  \
                                <a href='javascript:void(0)' onclick=\"adminUtil.popComment('" + articles[i].oId + "', '" + fromId + "')\">" + adminUtil.tip.commentLabel + "</a>";
                            
                            if (fromId === "draft") {
                                articleData[i].title = articles[i].articleTitle;
                                articleData[i].expendRow = "<a href='javascript:void(0)' onclick=\"adminUtil.updateArticle('" + articles[i].oId + "', true);\">" + adminUtil.tip.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"removeArticle('" + articles[i].oId + "')\">" + adminUtil.tip.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"adminUtil.popComment('" + articles[i].oId + "', '" + fromId + "')\">" + adminUtil.tip.commentLabel + "</a>";
                            }
                        };
                        $("#" + fromId + "List").table("update",{
                            data: [{
                                groupName: "all",
                                groupData: articleData
                            }]
                        });

                        if (0 === result.pagination.paginationPageCount) {
                            result.pagination.paginationPageCount = 1;
                        }

                        $("#" + fromId + "Pagination").paginate("update", {
                            pageCount: result.pagination.paginationPageCount,
                            currentPage: pageNum
                        });
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
            }
        }, requestJSONObject);
    },
    
    removeArticle: function (id, fromId) {
        var isDelete = confirm(this.tip.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(this.tip.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": id
            };
            jsonRpc.articleService.removeArticle(function (result, error) {
                try {
                    switch (result.status.code) {
                        case "REMOVE_ARTICLE_SUCC":
                            var events = result.status.events,
                            msg = adminUtil.tip.removeSuccLabel;
                            if (events) {
                                if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                    msg +=  ", " + adminUtil.tip.syncCSDNBlogFailLabel
                                    + events.blogSyncCSDNBlog.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                    msg += ", " + adminUtil.tip.syncCnBlogsFailLabel
                                    + events.blogSyncCnBlogs.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                    msg += ", " + adminUtil.tip.syncBlogJavaFailLabel 
                                    + events.blogSyncBlogJava.msg;
                                }
                            }
                            $("#tipMsg").text(msg);
                            adminUtil.getArticleList(1, fromId);
                            break;
                        case "REMOVE_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text(adminUtil.tip.forbiddenLabel);
                            break;
                        case "REMOVE_ARTICLE_FAIL_":
                            $("#tipMsg").text(adminUtil.tip.removeFailLabel);
                            break;
                        default:
                            $("#tipMsg").text("");
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                }
            }, requestJSONObject);
        }
    },
    
    popComment: function (id, fromId) {
        $("#" + fromId + "ListComments").data("oId", id);
        adminUtil.buildComments(fromId);
        $("#" + fromId + "ListComments").dialog("open");
    },
    
    buildComments: function (fromId) {
        $("#loadMsg").text(this.tip.loadingLabel);
        $("#" + fromId + "ListComments").html("");
        jsonRpc.commentService.getCommentsOfArticle(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_COMMENTS_SUCC":
                        var comments = result.comments,
                        commentsHTML = '';
                        for (var i = 0; i < comments.length; i++) {
                            var hrefHTML = "<a target='_blank' href='" + comments[i].commentURL + "'>",
                            content = comments[i].commentContent;
                            var ems = content.split("[em");
                            var contentHTML = ems[0];
                            for (var j = 1; j < ems.length; j++) {
                                var key = ems[j].substr(0, 2),
                                emImgHTML = "<img src='/skins/classic/emotions/em" + key
                                + ".png'/>";
                                contentHTML += emImgHTML + ems[j].slice(3);
                            }
                        
                            if (comments[i].commentURL === "http://") {
                                hrefHTML = "<a target='_blank'>";
                            }

                            commentsHTML += "<div class='comment-title'><span class='left'>"
                            + hrefHTML + comments[i].commentName + "</a>";

                            if (comments[i].commentOriginalCommentName) {
                                commentsHTML += "@" + comments[i].commentOriginalCommentName;
                            }
                            commentsHTML += "</span><span title='" + adminUtil.tip.removeLabel + "' class='right deleteIcon' onclick=\"adminUtil.deleteComment('"
                            + comments[i].oId + "', '" + fromId + "')\"></span><span class='right'><a href='mailto:"
                            + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                            + $.bowknot.getDate(comments[i].commentDate.time, 1)
                            + "&nbsp;</span><div class='clear'></div></div><div class='comment-body'>"
                            + contentHTML + "</div>";
                        }
                        if ("" === commentsHTML) {
                            commentsHTML = adminUtil.tip.noCommentLabel;
                        }
                        $("#" + fromId + "ListComments").html(commentsHTML);
                        break;
                    default:
                        break;
                };
                $("#loadMsg").text("");
            } catch (e) {}
        }, {
            "oId": $("#" + fromId + "ListComments").data("oId")
        });
    },
    
    deleteComment: function (id, fromId) {
        var isDelete = confirm(this.tip.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(this.tip.loadingLabel);
            jsonRpc.commentService.removeCommentOfArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_COMMENT_FAIL_FORBIDDEN":
                            $("#tipMsg").text(adminUtil.tip.forbiddenLabel);
                            break;
                        case "REMOVE_COMMENT_SUCC":
                            adminUtil.buildComments(fromId);
                            $("#tipMsg").text(adminUtil.tip.removeSuccLabel);
                            break;
                        default:
                            $("#tipMsg").text("");
                            $("#loadMsg").text("");
                            break;
                    }
                } catch (e) {}
            }, {
                "oId": id
            });
        }
    }
});