/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
 * @fileoverview Page util, load heighlight and process comment.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.7, May 4, 2012
 */
var Page = function (tips) {
    this.currentCommentId = "";
    this.tips = tips;
};

$.extend(Page.prototype, {   
    /*
     * @description 评论时点击表情，在评论内容中插入相关代码
     * @param {String} name 用于区别回复评论还是对文章的评论
     */
    insertEmotions:  function (name) {
        if (name === undefined) {
            name = "";
        }
        
        $("#emotions" + name + " span").click(function () {
            var $comment = $("#comment" + name);
            var endPosition = Util.getCursorEndPosition($comment[0]);
            var key = "[" + this.className + "]",
            textValue  = $comment[0].value;
            textValue = textValue.substring(0, endPosition) + key + textValue.substring(endPosition, textValue.length);
            $("#comment" + name).val(textValue);

            if ($.browser.msie) {
                endPosition -= textValue.split('\n').length - 1;
                var oR = $comment[0].createTextRange();
                oR.collapse(true);
                oR.moveStart('character', endPosition + 6);
                oR.select();
            } else {
                $comment[0].setSelectionRange(endPosition + 6, endPosition + 6);
            }
        });
    },

    /*
     * @description 评论校验
     * @param {String} state 用于区别回复评论还是对文章的评论
     */
    validateComment: function (state) {
        var commentName = $("#commentName" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
        commenterContent = $("#comment" + state).val().replace(/(^\s*)|(\s*$)/g, "");
        if (2 > commentName.length || commentName.length > 20) {
            $("#commentErrorTip" + state).html(this.tips.nameTooLongLabel);
            $("#commentName" + state).focus();
        } else if ($("#commentEmail" + state).val().replace(/\s/g, "") === "") {
            $("#commentErrorTip" + state).html(this.tips.mailCannotEmptyLabel);
            $("#commentEmail" + state).focus();
        } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#commentEmail" + state).val())) {
            $("#commentErrorTip" + state).html(this.tips.mailInvalidLabel);
            $("#commentEmail" + state).focus();
        } else if (2 > commenterContent.length || commenterContent.length > 500) {
            $("#commentErrorTip" + state).html(this.tips.commentContentCannotEmptyLabel);
            $("#comment" + state).focus();
        } else if ($("#commentValidate" + state).val().replace(/\s/g, "") === "") {
            $("#commentErrorTip" + state).html(this.tips.captchaCannotEmptyLabel);
            $("#commentValidate" + state).focus();
        } else {
            return true;
        }
        $("#commentErrorTip" + state).show();
        return false;
    },
    
    /*
     * @description 把评论中的标识替换为图片
     * @param {Dom} selector
     */
    replaceCommentsEm: function (selector) {
        var $commentContents = $(selector);
        for (var i = 0; i < $commentContents.length; i++) {
            var str = $commentContents[i].innerHTML;
            $commentContents[i].innerHTML =  Util.replaceEmString(str);
        }
    },
    
    /*
     * @description 初始化 SyantaxHighlighter
     * @param {Array} languages 需要加载的语言 
     */
    _initSyntaxHighlighter: function (languages) {
        // load brush js
        for (var i = 0; i < languages.length; i++) {
            switch (languages[i]) {
                case "groovy":
                    languages[i] =  'groovy				/js/lib/SyntaxHighlighter/scripts/shBrushGroovy.js';
                    break;
                case "java":
                    languages[i] =  'java				/js/lib/SyntaxHighlighter/scripts/shBrushJava.js';
                    break;
                case "php":
                    languages[i] =  'php				/js/lib/SyntaxHighlighter/scripts/shBrushPhp.js';
                    break;
                case "scala":
                    languages[i] =  'scala				/js/lib/SyntaxHighlighter/scripts/shBrushScala.js';
                    break;
                case "sql":
                    languages[i] =  'sql				/js/lib/SyntaxHighlighter/scripts/shBrushSql.js';
                    break;
                case "applescript":
                    languages[i] =  'applescript			/js/lib/SyntaxHighlighter/scripts/shBrushAppleScript.js';
                    break;
                case "as3": 
                case "actionscript3":
                    languages[i] =  'actionscript3 as3                  /js/lib/SyntaxHighlighter/scripts/shBrushAS3.js';
                    break;
                case "bash":
                case "shell":
                    languages[i] =  'bash shell                         /js/lib/SyntaxHighlighter/scripts/shBrushBash.js';
                    break;
                case "coldfusion":
                case "cf":
                    languages[i] =  'coldfusion cf			/js/lib/SyntaxHighlighter/scripts/shBrushColdFusion.js';
                    break;
                case "c#":
                case "c-sharp":
                case "csharp":
                    languages[i] =  'c# c-sharp csharp                  /js/lib/SyntaxHighlighter/scripts/shBrushCSharp.js';
                    break;
                case "cpp":
                case "c":
                    languages[i] =  'cpp c				/js/lib/SyntaxHighlighter/scripts/shBrushCpp.js';
                    break;	
                case "css":
                    languages[i] =  "css				/js/lib/SyntaxHighlighter/scripts/shBrushCss.js";
                    break;
                case "delphi":
                case "pascal":
                    languages[i] =  'delphi pascal			/js/lib/SyntaxHighlighter/scripts/shBrushDelphi.js';
                    break;			
                case "diff":
                case "patch":
                case "pas":
                    languages[i] =  'diff patch pas			/js/lib/SyntaxHighlighter/scripts/shBrushDiff.js';
                    break;			
                case "erl":
                case "erlang":
                    languages[i] =  'erl erlang                         /js/lib/SyntaxHighlighter/scripts/shBrushErlang.js';
                    break;			
                case "js":
                case "jscript":
                case "javascript":
                    languages[i] =  'js jscript javascript              /js/lib/SyntaxHighlighter/scripts/shBrushJScript.js';
                    break;			
                case "jfx":
                case "javafx":
                    languages[i] =  'jfx javafx                 	/js/lib/SyntaxHighlighter/scripts/shBrushJavaFX.js';
                    break;			
                case "perl":
                case "pl":
                    languages[i] =  'perl pl                    	/js/lib/SyntaxHighlighter/scripts/shBrushPerl.js';
                    break;			
                case "plain":
                case "text":
                    languages[i] =  'text plain                 	/js/lib/SyntaxHighlighter/scripts/shBrushPlain.js';
                    break;			
                case "ps":
                case "powershell":
                    languages[i] =  'ps powershell                      /js/lib/SyntaxHighlighter/scripts/shBrushPowerShell.js';
                    break;			
                case "py":
                case "python":
                    languages[i] =  'py python                          /js/lib/SyntaxHighlighter/scripts/shBrushPython.js';
                    break;			
                case "rails":
                case "ror":
                case "ruby":
                case "rb":
                    languages[i] =  'ruby rails ror rb          	/js/lib/SyntaxHighlighter/scripts/shBrushRuby.js';
                    break;	
                case "sass":
                case "scss":
                    languages[i] =  'sass scss                  	/js/lib/SyntaxHighlighter/scripts/shBrushSass.js';
                    break;
                case "vb":
                case "vbnet":
                    languages[i] =  'vb vbnet                   	/js/lib/SyntaxHighlighter/scripts/shBrushVb.js';
                    break;			
                case "xml":
                case "xhtml":
                case "xslt": 
                case "html":
                    languages[i] =  'xml xhtml xslt html                /js/lib/SyntaxHighlighter/scripts/shBrushXml.js';
                    break;	
                default:
                    break;
            }
        }
        
        // code high lighter
        SyntaxHighlighter.autoloader.apply(null, languages);
        SyntaxHighlighter.config.stripBrs = true;
        SyntaxHighlighter.all();  
    },
    
    /*
     * @description 加载 SyntaxHighlighter 
     * @param {String} SHTheme SyntaxHighLighter 样式
     */
    _loadSyntaxHighlighter: function (SHTheme) {
        var cssName = SHTheme ? SHTheme : "shCoreEclipse",
        that = this;
        // load css
        if (document.createStyleSheet) {
            document.createStyleSheet("/js/lib/SyntaxHighlighter/styles/" + cssName + ".css");
        } else {
            $("head").append($("<link rel='stylesheet' href='/js/lib/SyntaxHighlighter/styles/" 
                + cssName + ".css' type='text/css' charset='utf-8' />"));
        } 
        
        // load js
        $.ajax({
            url: latkeConfig.staticServePath + "/js/lib/SyntaxHighlighter/scripts/shCore.js",
            dataType: "script",
            cache: true,
            success: function() {
                // get brush settings
                var languages = [],
                isScrip = false;
                $(".article-body pre").each(function () {
                    var name = this.className.split(";")[0];
                    var language = name.substr(7, name.length - 1);
                    
                    if (this.className.indexOf("html-script: true") > -1 && 
                        (language !== "xml" && language !== "xhtml" && 
                            language !== "xslt" && language != "html")) {
                        isScrip = true;
                    }
                    
                    languages.push(language);
                });
                
                // when html-script is true, need shBrushXml.js
                if (isScrip) {
                    $.ajax({
                        url: latkeConfig.staticServer + latkeConfig.contextPath + "/js/lib/SyntaxHighlighter/scripts/shBrushXml.js",
                        dataType: "script",
                        cache: true,
                        success: function() {
                            that._initSyntaxHighlighter(languages);
                        }
                    });
                } else {
                    that._initSyntaxHighlighter(languages);
                }
            }
        });  
    },
    
    /*
     * @description 解析语法高亮
     * @param {Obj} obj 语法高亮配置参数
     * @param {Obj} obj.SHTheme 语法高亮 SyntaxHighLighter 样式
     */
    parseLanguage: function (obj) {
        var isPrettify = false,
        isSH = false;
        
        $(".article-body pre").each(function () {
            if (this.className.indexOf("brush") > -1) {
                isSH = true;
            } 
            
            if (this.className.indexOf("prettyprint") > -1) {
                isPrettify = true;
            }
        });
        
        
        if (isSH) {
            this._loadSyntaxHighlighter(obj ? (obj.SHTheme ? obj.SHTheme : undefined) : undefined);
        }
        
        if (isPrettify) {            
            // load css
            if (document.createStyleSheet) {
                document.createStyleSheet("/js/lib/google-code-prettify/prettify.css");
            } else {
                $("head").append($("<link rel='stylesheet' href='/js/lib/google-code-prettify/prettify.css' type='text/css' charset='utf-8' />"));
            } 
        
            // load js
            document.write("<script src=\"/js/lib/google-code-prettify/prettify.js\"><\/script>");
            
            // load function
            $(document).ready(function () {
                prettyPrint();
            });
        }
        
    },
    
    /*
     * @description 文章/自定义页面加载
     * @param {Obj} obj 配置设定
     * @param {Obj} obj.language 代码高亮配置
     */
    load: function (obj) {
        var that = this;
        // emotions
        that.insertEmotions();
        
        // language
        that.parseLanguage(obj ? (obj.language ? obj.language : undefined) : undefined);
        
        // submit comment
        $("#commentValidate").keypress(function (event) {
            if (event.keyCode === 13) {
                that.submitComment();
            }
        });
        
        // captcha
        $("#captcha").click(function () {
            $(this).attr("src", "/captcha.do?code=" + Math.random());
        });

        // cookie
        var $top = $("#top #admin");
        if ($top.length === 1) {
            if ($top.find("a").length > 2) {
                if (Cookie.readCookie("commentName") === "") {
                    Cookie.createCookie("commentName", $top.find("span").text(), 365); 
                }

                if (Cookie.readCookie("commentURL") === "") {
                    Cookie.createCookie("commentURL", window.location.host, 365);
                }
            }
        }
        
        $("#commentEmail").val(Cookie.readCookie("commentEmail"));
        $("#commentURL").val(Cookie.readCookie("commentURL"));
        $("#commentName").val(Cookie.readCookie("commentName"));
    },

    /*
     * @description 加载随机文章
     * @param {String} headTitle 随机文章标题
     */
    loadRandomArticles: function (headTitle) {
        var randomArticles1Label = this.tips.randomArticles1Label;
        // getRandomArticles
        $.ajax({
            url: latkeConfig.staticServer + latkeConfig.contextPath + "/get-random-articles.do",
            type: "POST",
            success: function(result, textStatus){
                var randomArticles = result.randomArticles;
                if (0 === randomArticles.length) {
                    return;
                }

                var listHtml = "";
                for (var i = 0; i < randomArticles.length; i++) {
                    var article = randomArticles[i];
                    var title = article.articleTitle;
                    var randomArticleLiHtml = "<li>" + "<a title='" + title + "' href='" + article.articlePermalink +"'>" +  title + "</a></li>";
                    listHtml += randomArticleLiHtml;
                }
                
                var titleHTML = headTitle ? headTitle : "<h4>" + randomArticles1Label + "</h4>";
                var randomArticleListHtml = titleHTML + "<ul class='marginLeft12'>" + listHtml + "</ul>";
                $("#randomArticles").append(randomArticleListHtml);
            }
        });
    },
    
    /*
     * @description 加载相关文章
     * @param {String} id 文章 id
     * @param {String} headTitle 相关文章标题
     */
    loadRelevantArticles: function (id, headTitle) {
        try {
            $.ajax({
                url: latkeConfig.staticServer + latkeConfig.contextPath + "/article/id/" + id + "/relevant/articles",
                type: "GET",
                success: function(data, textStatus){
                    var articles = data.relevantArticles;
                    if (0 === articles.length) {
                        $("#relevantArticles").remove();
                        return;
                    }
                    var listHtml = "";
                    for (var i = 0; i < articles.length; i++) {
                        var article = articles[i];
                        var title = article.articleTitle;
                        var articleLiHtml = "<li>"
                        + "<a title='" + title + "' href='" + article.articlePermalink + "'>"
                        +  title + "</a></li>"
                        listHtml += articleLiHtml
                    }
                
                    var relevantArticleListHtml = headTitle 
                    + "<ul class='marginLeft12'>"
                    + listHtml + "</ul>";
                    $("#relevantArticles").append(relevantArticleListHtml);
                }
            });
        } catch (e) {
        }
    },
    
    /*
     * @description 加载站外相关文章
     * @param {String} tags 文章 tags
     * @param {String} headTitle 站外相关文章标题
     */
    loadExternalRelevantArticles: function (tags, headTitle) {
        var tips = this.tips;
        try {
            $.ajax({
                url: "http://rhythm.b3log.org:80/get-articles-by-tags.do?tags=" + tags
                + "&blogHost=" + tips.blogHost + "&paginationPageSize=" + tips.externalRelevantArticlesDisplayCount,
                type: "GET",
                cache: true,
                dataType:"jsonp",
                error: function(){
                // alert("Error loading articles from Rhythm");
                },
                success: function(data, textStatus){
                    var articles = data.articles;
                    if (0 === articles.length) {
                        return;
                    }
                    var listHtml = "";
                    for (var i = 0; i < articles.length; i++) {
                        var article = articles[i];
                        var title = article.articleTitle;
                        var articleLiHtml = "<li>"
                        + "<a title='" + title + "' target='_blank' href='" + article.articlePermalink + "'>"
                        +  title + "</a></li>"
                        listHtml += articleLiHtml
                    }
                
                    var titleHTML = headTitle ? headTitle : "<h4>" + tips.externalRelevantArticles1Label + "</h4>";
                    var randomArticleListHtml = titleHTML
                    + "<ul class='marginLeft12'>"
                    + listHtml + "</ul>";
                    $("#externalRelevantArticles").append(randomArticleListHtml);
                }
            });
        } catch (e) {
        // 忽略相关文章加载异常：load script error
        }
    },
    
    /*
     * @description 提交评论
     * @param {String} commentId 回复评论时的评论 id
     * @param {String} state 区分回复文章还是回复评论的标识
     */
    submitComment: function (commentId, state) {
        if (!state) {
            state = '';
        }
        var that = this,
        tips = this.tips,
        type = "article";
        if (tips.externalRelevantArticlesDisplayCount === undefined) {
            type = "page";
        }
        
        if (this.validateComment(state)) {
            $("#submitCommentButton" + state).attr("disabled", "disabled");
            $("#commentErrorTip" + state).show().html(this.tips.loadingLabel);
            
            var requestJSONObject = {
                "oId": tips.oId,
                "commentContent": $("#comment" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
                "commentEmail": $("#commentEmail" + state).val(),
                "commentURL": Util.proessURL($("#commentURL" + state).val().replace(/(^\s*)|(\s*$)/g, "")),
                "commentName": $("#commentName" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
                "captcha": $("#commentValidate" + state).val()
            };

            if (state === "Reply") {
                requestJSONObject.commentOriginalCommentId = commentId;
            }
            $.ajax({
                type: "POST",
                url: latkeConfig.staticServer + latkeConfig.contextPath + "/add-" + type + "-comment.do",
                cache: false,
                contentType: "application/json",
                data: JSON.stringify(requestJSONObject),
                success: function(result){
                    if (!result.sc) {
                        $("#commentErrorTip" + state).html(result.msg);
                        $("#commentValidate" + state).val("").focus();
                        
                        $("#submitCommentButton" + state).removeAttr("disabled");
                        $("#captcha" + state).attr("src", "/captcha.do?code=" + Math.random());
                        
                        return;
                    } 
                    
                    result.replyNameHTML = "";
                    if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                        result.replyNameHTML = '<a>' + $("#commentName" + state).val() + '</a>';
                    } else {
                        result.replyNameHTML = '<a href="' + Util.proessURL($("#commentURL" + state).val()) + 
                        '" target="_blank">' + $("#commentName" + state).val() + '</a>';
                    }
                            
                    that.addCommentAjax(addComment(result, state), state);

                    $("#submitCommentButton" + state).removeAttr("disabled");
                    $("#captcha" + state).attr("src", "/captcha.do?code=" + Math.random());
                }
            });

            Cookie.createCookie("commentName", requestJSONObject.commentName, 365);
            Cookie.createCookie("commentEmail", requestJSONObject.commentEmail, 365);
            Cookie.createCookie("commentURL", $("#commentURL" + state).val().replace(/(^\s*)|(\s*$)/g, ""), 365);
        }
    },
    
    /*
     * @description 添加回复评论表单
     * @param {String} id 被回复的评论 id
     * @param {String} commentFormHTML 评论表单HTML
     * @param {String} endHTML 判断该表单使用 table 还是 div 标签，然后进行构造
     */
    addReplyForm: function (id, commentFormHTML, endHTML) {
        var that = this;
        if (id === this.currentCommentId) {
            if (Cookie.readCookie("commentName")  === "") {
                $("#commentNameReply").focus();
            } else {
                $("#commentReply").focus();
            }
            return;
        } else {
            $("#replyForm").remove();
            endHTML = endHTML ? endHTML : "";
            if (endHTML === "</div>") {
                $("#" + id).append(commentFormHTML  + $("#commentForm").html() + endHTML);
            } else {
                $("#" + id).append(commentFormHTML  + $("#commentForm").html() + "</table>" + endHTML);
            }

            // change id, bind event and set value
            $("#replyForm input, #replyForm textarea").each(function () {
                this.id = this.id + "Reply";
            });
            
            $("#commentNameReply").val(Cookie.readCookie("commentName"));
            
            $("#commentEmailReply").val(Cookie.readCookie("commentEmail"));
            
            var $label = $("#replyForm #commentURLLabel");
            if ($label.length === 1) {
                $label.attr("id", "commentURLLabelReply");
            }
            
            $("#commentURLReply").val(Cookie.readCookie("commentURL"));
            
            $("#replyForm #emotions").attr("id", "emotionsReply");
            
            this.insertEmotions("Reply");
            
            $("#commentValidateReply").unbind().keypress(function (event) {
                if (event.keyCode === 13) {
                    that.submitComment(id, 'Reply');
                    event.preventDefault();
                }
            });
            $("#replyForm #captcha").attr("id", "captchaReply").
            attr("src", "/captcha.do?" + new Date().getTime()).click(function () {
                $(this).attr("src", "/captcha.do?code=" + Math.random());
            });
        
            $("#replyForm #commentErrorTip").attr("id", "commentErrorTipReply").html("").hide();
            
            $("#replyForm #submitCommentButton").attr("id", "submitCommentButtonReply");
            $("#replyForm #submitCommentButtonReply").unbind("click").removeAttr("onclick").click(function () {
                that.submitComment(id, 'Reply');
            });
            
            if (Cookie.readCookie("commentName")  === "") {
                $("#commentNameReply").focus();
            } else {
                $("#commentReply").focus();
            }
        }
        this.currentCommentId = id;
    },

    /* 
     * @description 隐藏回复评论的浮出层
     * @parma {String} id 被回复的评论 id
     */
    hideComment: function (id) {
        $("#commentRef" + id).hide();
    },
    
    /* 
     * @description 显示回复评论的浮出层
     * @parma {Dom} it 触发事件的 dom
     * @param {string} id 被回复的评论 id
     * @param {Int} top it top 位置相对浮出层的高度
     */
    showComment: function (it, id, top) {
        if ( $("#commentRef" + id).length > 0) {
            // 此处重复设置 top 是由于评论为异步，原有回复评论的显示位置应往下移动
            $("#commentRef" + id).show().css("top", ($(it).position().top + top) + "px");
        } else {
            var $refComment = $("#" + id).clone();
            $refComment.addClass("comment-body-ref").attr("id", "commentRef" + id);
            $refComment.find("#replyForm").remove();
            $("#comments").append($refComment);
            $("#commentRef" + id).css("top", ($(it).position().top + top) + "px");
        }
    },

    /* 
     * @description 回复不刷新，将回复内容异步添加到评论列表中
     * @parma {String} commentHTML 回复内容 HTML
     * @param {String} state 用于区分评论文章还是回复评论
     */
    addCommentAjax: function (commentHTML, state) {
        if ($("#comments").children().length > 0) {
            $($("#comments").children()[0]).before(commentHTML);
        } else {
            $("#comments").html(commentHTML);
        }

        if (state === "") {
            $("#commentErrorTip").html("").hide();
            $("#comment").val("");
            $("#commentValidate").val("");
            $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
        } else {
            $("#replyForm").remove();
        }
        window.location.hash = "#comments";
    }
});