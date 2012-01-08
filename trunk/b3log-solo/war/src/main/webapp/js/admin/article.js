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
 * @version 1.0.1.6, Dec 22, 2011
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
        
        $.ajax({
            url: "/console/article/" + admin.article.status.id,
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    $("#tipMsg").text(result.msg);
                    
                    return;
                }
                
                // set default value for article.
                $("#title").val(result.article.articleTitle);
                admin.article.status.articleHadBeenPublished =  result.article.articleHadBeenPublished;
                try {
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
                } catch (e) {
                    $("#articleContent").val(result.article.articleContent);
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
                
                $("#loadMsg").text("");
            }
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
            
            $.ajax({
                url: "/console/article/" + id,
                type: "DELETE",
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    admin[fromId + "List"].getList(1);
                }
            });
        }
    },
    
    /*
     * 添加文章
     * @articleIsPublished 文章是否发布过
     */
    add: function (articleIsPublished) {
        if (admin.article.validate()) {
            var that = this;
            that._addDisabled();
            
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });

            var articleContent = "",
            articleAbstract = "";
            
            try {
                articleContent = tinyMCE.get('articleContent').getContent();
                articleAbstract =  tinyMCE.get('abstract').getContent();
            } catch (e) {
                articleContent = $("#articleContent").val();
                articleAbstract =  $("#abstract").val();
            }
            
            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": articleContent,
                    "articleAbstract": articleAbstract,
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId,
                    "postToCommunity": $("#postToCommunity").prop("checked")
                }
            };
            
            $.ajax({
                url: "/console/article/",
                type: "POST",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    
                    that._removeDisabled();
                      
                    if (!result.sc) {
                        return;
                    }
                    
                    if (articleIsPublished) {
                        admin.article.status.id = undefined;
                        admin.selectTab("article/article-list");
                    } else {
                        admin.selectTab("article/draft-list");
                    }
                    
                    admin.article.isConfirm = false;
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 更新文章
     * @articleIsPublished 文章是否发布过 
     */
    update: function (articleIsPublished) {
        if (admin.article.validate()) {
            var that = this;
            that._addDisabled();
            
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });
            
            var articleContent = "",
            articleAbstract = "";
            
            try {
                articleContent = tinyMCE.get('articleContent').getContent();
                articleAbstract =  tinyMCE.get('abstract').getContent();
            } catch (e) {
                articleContent = $("#articleContent").val();
                articleAbstract =  $("#abstract").val();
            }
            
            var requestJSONObject = {
                "article": {
                    "oId": this.status.id,
                    "articleTitle": $("#title").val(),
                    "articleContent": articleContent,
                    "articleAbstract": articleAbstract,
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId
                }
            };
            
            $.ajax({
                url: "/console/article/",
                type: "PUT",
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    that._removeDisabled();
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    if (articleIsPublished){
                        admin.selectTab("article/article-list");
                    } else {
                        admin.selectTab("article/draft-list");
                    }
                            
                    $("#tipMsg").text(Label.updateSuccLabel);
                    // reset article form
                    try {
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
                    } catch (e) {
                        $("#articleContent").val("");
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
                    
                    $("#loadMsg").text("");
                }
            });
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
        try {
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
        } catch (e) {
            $("#articleContent").val("");
            $("#abstract").val("");
        }
        // reset tag
        $("#tag").val("");
        $("#tagCheckboxPanel").hide().find("span").removeClass("selected");
        
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
                
                $("#loadMsg").text("");
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
        try {
            tinyMCE.init({
                // General options
                language: language,
                mode : "exact",
                elements : "articleContent",
                theme : "advanced",
                plugins : "spellchecker,autosave,style,advhr,advimage,advlink,preview,inlinepopups,media,paste,fullscreen,syntaxhl,wordcount",

                // Theme options
                theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,bullist,numlist,blockquote,|,justifyleft,justifycenter,justifyright,justifyfull,|,link,unlink,advhr,spellchecker,fullscreen,syntaxhl",
                theme_advanced_buttons2 : "formatselect,forecolor,|,pastetext,pasteword,cleanup,charmap,|,outdent,indent,undo,redo,|,image,iespell,media,code,preview,",
                theme_advanced_buttons3 : "",
                theme_advanced_toolbar_location : "top",
                theme_advanced_toolbar_align : "left",
                theme_advanced_resizing : true,
                theme_advanced_statusbar_location : "bottom",
                
                extended_valid_elements: "pre[name|class],iframe[src|width|height|name|align]",

                valid_children : "+body[style]",
                relative_urls: false,
                remove_script_host: false,
                oninit : function () {
                    if (typeof(fun) === "function") {
                        fun();
                    }
                }
            });
            
            tinyMCE.init({
                // General options
                language: language,
                mode : "exact",
                elements : "abstract",
                theme : "advanced",

                // Theme options
                theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,undo,redo,|,bullist,numlist",
                theme_advanced_buttons2 : "",
                theme_advanced_buttons3 : "",
                theme_advanced_toolbar_location : "bottom",
                theme_advanced_toolbar_align : "center"
            });
            
        } catch (e) {
            $("#tipMsg").text("TinyMCE load fail");
        }
    },
    
    /*
     * 验证发布文章字段的合法性
     */
    validate: function () {
        var articleContent = "",
        articleAbstract = "";
            
        try {
            articleContent = tinyMCE.get('articleContent').getContent();
            articleAbstract =  tinyMCE.get('abstract').getContent();
        } catch (e) {
            articleContent = $("#articleContent").val();
            articleAbstract =  $("#abstract").val();
        }
        
        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#title").focus().val("");
        } else if (articleContent.replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.contentEmptyLabel);
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.tagsEmptyLabel);
            $("#tag").focus().val("");
        } else if(articleAbstract.replace(/\s/g, "") === "") {
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
        var that = this;
        that._addDisabled();
        
        $.ajax({
            url: "/console/article/unpublish/" + admin.article.status.id,
            type: "PUT",
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                
                that._removeDisabled();
                     
                if (!result.sc) {
                    return;
                }
                    
                admin.selectTab("article/draft-list");
                admin.article.status.id = undefined;
                admin.article.isConfirm = false;
            }
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
        $("#loadMsg").text(Label.loadingLabel);
        var articleContent = "";
            
        try {
            articleContent = tinyMCE.get('articleContent').getContent();
        } catch (e) {
            articleContent = $("#articleContent").val();
        }
        
        if (window.location.hash === "#article/article" && 
            articleContent.replace(/\s/g, '') !== "") {
            if (confirm(Label.editorPostLabel)) {
                admin.article.clear();
            }
        }
        
        $("#loadMsg").text("");
    },
    
    /*
     * @description: 仿重复提交，点击一次后，按钮设置为 disabled
     */
    _addDisabled: function () {
        $("#unSubmitArticle").attr("disabled", "disabled");
        $("#saveArticle").attr("disabled", "disabled");
        $("#submitArticle").attr("disabled", "disabled");
    },
    
    /*
     * @description: 仿重复提交，当后台有数据返回后，按钮移除 disabled 状态
     */
    _removeDisabled: function () {
        $("#unSubmitArticle").removeAttr("disabled");
        $("#saveArticle").removeAttr("disabled");
        $("#submitArticle").removeAttr("disabled");
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