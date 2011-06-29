/*
 * Copyright (c) 2011, B3log Team
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

/**
 *  article for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.1, Jun 28, 2011
 */

admin.article = {
    status: undefined,
    
    /* 获取文章并把值塞入发布文章页面 */
    get: function (id, isArticle) {
        $("#loadMsg").text(Label.loadingLabel);
        admin.selectTab("article", function () {
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

                            admin.article.setStatus();
                            $("#tabs").tabs("select", "article");
                        
                            $("#tipMsg").text(Label.getSuccLabel);
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
        });
    },
    
    /*
     * 删除文章
     * @id 文章 id
     * @fromId 文章来自草稿夹(draft)/文件夹(article)
     */
    del: function (id, fromId) {
        var isDelete = confirm(Label.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": id
            };
            jsonRpc.articleService.removeArticle(function (result, error) {
                try {
                    switch (result.status.code) {
                        case "REMOVE_ARTICLE_SUCC":
                            var events = result.status.events,
                            msg = Label.removeSuccLabel;
                            if (events) {
                                if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                    msg +=  ", " + Label.syncCSDNBlogFailLabel
                                    + events.blogSyncCSDNBlog.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                    msg += ", " + Label.syncCnBlogsFailLabel
                                    + events.blogSyncCnBlogs.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                    msg += ", " + Label.syncBlogJavaFailLabel 
                                    + events.blogSyncBlogJava.msg;
                                }
                            }
                            $("#tipMsg").text(msg);
                            admin[fromId + "List"].getList(1);
                            break;
                        case "REMOVE_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
                            break;
                        case "REMOVE_ARTICLE_FAIL_":
                            $("#tipMsg").text(Label.removeFailLabel);
                            break;
                        default:
                            $("#tipMsg").text("");
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, requestJSONObject);
        }
    },
    
    /*
     * 添加文章
     * @articleIsPublished 文章是否发布过
     */
    add: function (articleIsPublished) {
        if (admin.article.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(","),
            signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });

            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": Util.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId,
                    "postToCommunity": $("#postToCommunity").attr("checked")
                }
            };

            jsonRpc.articleService.addArticle(function (result, error) {
                try {
                    switch (result.status.code) {
                        case "ADD_ARTICLE_FAIL_DUPLICATED_PERMALINK":
                            var msg = Label.addFailLabel + ", " + Label.duplicatedPermalinkLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "ADD_ARTICLE_SUCC":
                            var events = result.status.events;
                            if (events) {
                                var msg = Label.addSuccLabel;
                                if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                    msg += ", " + Label.syncCSDNBlogFailLabel + ": "
                                    + events.blogSyncCSDNBlog.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                    msg += ", " + Label.syncCnBlogsFailLabel + ": "
                                    + events.blogSyncCnBlogs.msg;
                                }

                                if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                    msg += ", " + Labe.syncBlogJavaFailLabel + ": "
                                    + events.blogSyncBlogJava.msg;
                                }

                                //                            if ("POST_TO_BUZZ_FAIL" === events.postToGoogleBuzz.code) {
                                //                                msg += ", ${postToBuzzFailLabel}";
                                //                            }
                                $("#tipMsg").text(msg);
                                admin.selectTab("article-list");
                            } else {
                                $("#tipMsg").text(Label.addSuccLabel);
                                admin.selectTab("draft-list");
                            }
                        
                            // reset article form
                            admin.article.clear();
                            break;
                        default:
                            $("#tipMsg").text(Label.addFailLabel);
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, requestJSONObject);
        }
    },
    
    /*
     * 更新文章
     * @articleIsPublished 文章是否发布过 
     */
    update: function (articleIsPublished) {
        if (admin.article.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(","),
            signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });
            
            var requestJSONObject = {
                "article": {
                    "oId": this.status.oId,
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": Util.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId
                }
            };

            jsonRpc.articleService.updateArticle(function (result, error) {
                try {
                    switch (result.status.code) {
                        case "UPDATE_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
                            break;
                        case "UPDATE_ARTICLE_FAIL_DUPLICATED_PERMALINK":
                            var msg = Label.addFailLabel + ", " + Label.duplicatedPermalinkLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "UPDATE_ARTICLE_SUCC":
                            if (articleIsPublished){
                                var events = result.status.events;
                                if (events) {
                                    var msg = Label.updateSuccLabel;
                                    if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                        msg += ", " + Label.syncCSDNBlogFailLabel + ": "
                                        + events.blogSyncCSDNBlog.msg;
                                    }

                                    if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                        msg += ", " + Label.syncCnBlogsFailLabel + ": "
                                        + events.blogSyncCnBlogs.msg;
                                    }

                                    if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                        msg += ", " + Label.syncBlogJavaFailLabel + ": "
                                        + events.blogSyncBlogJava.msg;
                                    }

                                    $("#tipMsg").text(msg);
                                    admin.selectTab("article-list");
                                }
                            } else {
                                $("#tipMsg").text(Label.updateSuccLabel);
                                admin.selectTab("draft-list");
                            }
                            // reset article form
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
                            break;
                        default:
                            $("#tipMsg").text(Label.updateFailLabel);
                            break;
                    }
                    $("loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },
    
    /*
     * 发布文章页面设置文章按钮、发布到社区等状态的显示
     */
    setStatus: function () {
        this.status = $("#title").data("articleStatus");
        // set button status
        if (this.status) {
            if (this.status.isArticle) { 
                $("#unSubmitArticle").show();
                $("#submitArticle").hide();
            } else {
                $("#submitArticle").show();
                $("#unSubmitArticle").hide();
            }
            if (this.status.articleHadBeenPublished) {
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
    
    /*
     * 清除发布文章页面的输入框的内容
     */
    clear: function () {
        $("#title").removeData("articleStatus").val("");
        this.setStatus();
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
    
    /*
     * 初始化发布文章页面
     */
    init: function () {
        admin.article.clear();
        
        // Inits Signs.
        jsonRpc.preferenceService.getSigns(function (result, error) {
            try {
                $(".signs button").each(function (i) {
                    // Sets signs.
                    if (i === result.length) {
                        $("#articleSign0").addClass("selected");
                    } else {
                        $("#articleSign" + result[i].oId).tip({
                            content: result[i].signHTML === "" ? "该签名档为空" : result[i].signHTML,
                            position: "top"
                        });
                    }
                    // Binds checkbox event.
                    $(this).click(function () {
                        if (this.className !== "selected") {
                            $(".signs button").each(function () {
                                this.className = "";
                            });
                            this.className = "selected";
                        }
                    });
                });
            } catch(e) {
                console.error(e);
            }
        });
        
        // tag auto completed
        jsonRpc.tagService.getTags(function (result, error) {
            try {
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
            } catch (e) {}
        });

        // submit action
        $("#submitArticle").click(function () {
            if (admin.article.status) {
                admin.article.update(true);
            } else {
                admin.article.add(true);
            }
        });
        
        $("#saveArticle").click(function () {
            if (admin.article.status) {
                admin.article.update(admin.article.status.isArticle);
            } else {
                admin.article.add(false);
            }
        });

        // editor
        var localeString = Label.localeString;
        var language = localeString.substring(0, 2);
        tinyMCE.init({
            // General options
            language: language,
            mode : "exact",
            elements : "articleContent, abstract",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen,syntaxhl",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen,syntaxhl",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class],iframe[src|width|height|name|align]",

            relative_urls: false,
            remove_script_host: false
        });

        $("#loadMsg").text("");
    },
    
    /*
     * 验证发布文章字段的合法性
     */
    validate: function () {
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
    },
    
    /*
     * 取消发布
     */
    unPublish: function () {
        jsonRpc.articleService.cancelPublishArticle(function (result, error) {
            try {
                if (result.sc === "CANCEL_PUBLISH_ARTICLE_SUCC") {
                    $("#tipMsg").text(Label.unPulbishSuccLabel);
                    admin.selectTab("draft-list");
                } else {
                    $("#tipMsg").text(Label.unPulbishFailLabel);
                }
            } catch (e) {}
        }, {
            oId: admin.article.status.oId
        });
    }
}

