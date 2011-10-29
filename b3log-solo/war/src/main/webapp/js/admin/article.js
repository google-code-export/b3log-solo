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

/**
 * @fileoverview article for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Oct 29, 2011
 */
admin.article = {
    // 当发文章，取消发布，更新文章时设置为 false。不需在离开编辑器时进行提示。
    isConfirm: true,
    status: {
        id: undefined,
        isArticle: undefined,
        articleHadBeenPublished: undefined
    },
    
    /* 
     * 获取文章并把值塞入发布文章页面 
     * @id 文章 id
     * @isArticle 文章或者草稿
     */
    get: function (id, isArticle) {
        this.status.id = id;
        this.status.isArticle = isArticle
        admin.selectTab("article/article");
    },
    
    getAndSet: function () {
        $("#loadMsg").text(Label.loadingLabel);
        var requestJSONObject = {
            "oId": admin.article.status.id
        };
                        
        jsonRpc.articleService.getArticle(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_ARTICLE_SUCC":
                        // set default value for article.
                        $("#title").val(result.article.articleTitle);
                        admin.article.status.articleHadBeenPublished =  result.article.articleHadBeenPublished;
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
                        $("#tipMsg").text(Label.getSuccLabel);
                        break;
                    case "GET_ARTICLE_FAIL_":
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        }, requestJSONObject);
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
                    switch (result.sc) {
                        case "REMOVE_ARTICLE_SUCC":
                            msg = Label.removeSuccLabel;
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
            var signId = "";
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
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId,
                    "postToCommunity": $("#postToCommunity").prop("checked")
                }
            };

            jsonRpc.articleService.addArticle(function (result, error) {
                try {
                    switch (result.status.code) {
                        case "ADD_ARTICLE_FAIL_DUPLICATED_PERMALINK":
                            var msg = Label.addFailLabel + ", " + Label.duplicatedPermalinkLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "ADD_ARTICLE_FAIL_INVALID_PERMALINK_FORMAT":
                            msg = Label.addFailLabel + ", " + Label.invalidPermalinkFormatLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "ADD_ARTICLE_SUCC":
                            if (articleIsPublished) {
                                admin.article.status.id = undefined;
                                admin.selectTab("article/article-list");
                            } else {
                                admin.selectTab("article/draft-list");
                            }
                            $("#tipMsg").text(Label.addSuccLabel);
                            admin.article.isConfirm = false;
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
            var signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });
            
            var requestJSONObject = {
                "article": {
                    "oId": this.status.id,
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
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
                                admin.selectTab("article/article-list");
                            } else {
                                admin.selectTab("article/draft-list");
                            }
                            
                            $("#tipMsg").text(Label.updateSuccLabel);
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
                            admin.article.status.id = undefined;
                            admin.article.isConfirm = false;
                            break;
                        default:
                            $("#tipMsg").text(Label.updateFailLabel);
                            break;
                    }
                    $("loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, requestJSONObject);
        }
    },
    
    /*
     * 发布文章页面设置文章按钮、发布到社区等状态的显示
     */
    setStatus: function () {
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
        this.status = {
            id: undefined,
            isArticle: undefined,
            articleHadBeenPublished: undefined
        };
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
        $("#title").val("");
        $("#permalink").val("");
        $(".signs button").each(function (i) {
            if (i === 0) {
                this.className = "selected";
            } else {
                this.className = "";
            }
        });
    },
    
    /*
     * 初始化发布文章页面
     */
    init: function (fun) {
        //admin.article.clear();
        // Inits Signs.
        
        $.ajax({
            url: "/console/signs/",
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    return;
                }
                    
                $(".signs button").each(function (i) {
                    // Sets signs.
                    if (i === result.signs.length) {
                        $("#articleSign1").addClass("selected");
                    } else {
                        $("#articleSign" + result.signs[i].oId).tip({
                            content: result.signs[i].signHTML === "" ? Label.signIsNullLabel : result.signs[i].signHTML.replace(/\n/g, "").replace(/<script.*<\/script>/ig, ""),
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
            }
        });
        
        // For tag auto-completion
        $.ajax({ // Gets all tags
            url: "/console/tags",
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    return;
                }
           
                if (0 >= result.tags.length) {
                    return;
                }
                
                var tags = [];
                for (var i = 0; i < result.tags.length; i++) {
                    tags.push(result.tags[i].tagTitle);
                }
                
                $("#tag").completed({
                    height: 160,
                    buttonText: Label.selectLabel,
                    data: tags
                });
                
                $("#loadMsg").text("");
            }
        });
    
        // submit action
        $("#submitArticle").click(function () {
            if (admin.article.status.id) {
                admin.article.update(true);
            } else {
                admin.article.add(true);
            }
        });
        
        $("#saveArticle").click(function () {
            if (admin.article.status.id) {
                admin.article.update(admin.article.status.isArticle);
            } else {
                admin.article.add(false);
            }
        });

        // editor
        var language = Label.localeString.substring(0, 2);
        if (language === "zh") {
            language = "zh-cn";
        }
        tinyMCE.init({
            // General options
            language: language,
            mode : "exact",
            elements : "articleContent, abstract",
            theme : "advanced",
            plugins : "autosave,style,advhr,advimage,advlink,preview,inlinepopups,media,paste,fullscreen,syntaxhl",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen,syntaxhl",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class],iframe[src|width|height|name|align]",

            relative_urls: false,
            remove_script_host: false,
            oninit : function () {
                if (typeof(fun) === "function") {
                    fun();
                }
            }
        });
    },
    
    /*
     * 验证发布文章字段的合法性
     */
    validate: function () {
        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#title").focus().val("");
        } else if (tinyMCE.get('articleContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.contentEmptyLabel);
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.tagsEmptyLabel);
            $("#tag").focus().val("");
        } else if(tinyMCE.get('abstract').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.abstractEmptyLabel);
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
                    admin.selectTab("article/draft-list");
                    admin.article.status.id = undefined;
                    admin.article.isConfirm = false;
                } else {
                    $("#tipMsg").text(Label.unPulbishFailLabel);
                }
            } catch (e) {}
        }, {
            oId: admin.article.status.id
        });
    },
    
    trimUniqueArray: function(str){
        str = str.toString();
        var arr = str.split(",");
        for(var i = 0; i < arr.length; i++) {
            arr[i] = arr[i].replace(/(^\s*)|(\s*$)/g,"");
            if( arr[i] === "" ){
                arr.splice(i, 1);
                i--
            }
        }
        var unique =  $.unique(arr);
        return unique.toString();
    },
    
    /*
     * 点击发文文章时的处理
     */
    prePost:function () {
        if (window.location.hash === "#article/article" && 
            tinyMCE.get('articleContent').getContent().replace(/\s/g, '') !== "") {
            if (confirm(Label.editorPostLabel)) {
                admin.article.clear();
            }
        }
    }
}

/*
 * 注册到 admin 进行管理 
 */
admin.register.article =  {
    "obj": admin.article,
    "init": admin.article.init,
    "refresh": function () {
        $("#loadMsg").text("");
    }
}