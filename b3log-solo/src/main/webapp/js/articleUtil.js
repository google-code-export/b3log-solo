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

var ArticleUtil = function (tip) {
    this.currentCommentId = "";
    this.tip = tip;
};

$.extend(ArticleUtil.prototype, {
    articleUtil: {
        version:"0.0.0.2",
        author: "lly219@gmail.com"
    },
    
    insertEmotions:  function (name) {
        if (name === undefined) {
            name = "";
        }
        
        $("#emotions" + name + " img").click(function () {
            // TODO: should be insert it at the after of cursor
            var key = this.className;
            $("#comment" + name).val($("#comment" + name).val() + key).focus();
        });
    },

    validateComment: function (state) {
        var commentName = $("#commentName" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
        commenterContent = $("#comment" + state).val().replace(/(^\s*)|(\s*$)/g, "");
        if (2 > commentName.length || commentName.length > 20) {
            $("#commentErrorTip" + state).html(this.tip.nameTooLongLabel);
            $("#commentName" + state).focus();
        } else if ($("#commentEmail" + state).val().replace(/\s/g, "") === "") {
            $("#commentErrorTip" + state).html(this.tip.mailCannotEmptyLabel);
            $("#commentEmail" + state).focus();
        } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#commentEmail" + state).val())) {
            $("#commentErrorTip" + state).html(this.tip.mailInvalidLabel);
            $("#commentEmail" + state).focus();
        } else if (2 > commenterContent.length || commenterContent.length > 500) {
            $("#commentErrorTip" + state).html(this.tip.commentContentCannotEmptyLabel);
            $("#comment" + state).focus();
        } else if ($("#commentValidate" + state).val().replace(/\s/g, "") === "") {
            $("#commentErrorTip" + state).html(this.tip.captchaCannotEmptyLabel);
            $("#commentValidate" + state).focus();
        } else {
            return true;
        }
        return false;
    },
    
    hideComment: function (id) {
        $("#commentItemRef" + id).hide();
    },

    addCommentAjax: function (commentHTML, state) {
        if ($("#comments .comments-header").length > 0) {
            $("#comments .comments-header").after(commentHTML);
        } else if ($("#comments>div").first().length === 1) {
            $("#comments>div").first().before(commentHTML);
        } else {
            $("#comments").html(commentHTML);
        }

        if (state === "") {
            $("#commentErrorTip").html("");
            $("#comment").val("");
            $("#commentValidate").val("");
            $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
        } else {
            $("#replyForm").remove();
        }
    },

    replaceEmotions: function (commentContentHTML, skinName) {
        var commentContents = commentContentHTML.split("[em");
        commentContentHTML = commentContents[0];
        for (var j = 1; j < commentContents.length; j++) {
            var key = commentContents[j].substr(0, 2),
            emImgHTML = "<img src='/skins/" + skinName + "/emotions/em" + key + ".png'/>";
            commentContentHTML += emImgHTML + commentContents[j].slice(3);
        }
        return commentContentHTML;
    },
    
    getDate: function (time,type) {
        var c = new Date(time);
        var d=c.getFullYear(),month=c.getMonth()+1,day=c.getDate(),hours=c.getHours(),seconds=c.getSeconds(),minutes=c.getMinutes();
        if(month<10){
            month="0"+month.toString();
        }
        if(day<10){
            day="0"+day.toString();
        }
        if(hours<10){
            hours="0"+hours.toString();
        }
        if(minutes<10){
            minutes="0"+minutes.toString();
        }
        if(seconds<10){
            seconds="0"+seconds.toString();
        }
        switch(type){
            case undefined:
                return d + "-" + month + "-" + day;
            case "yyyy-mm-dd hh:mm:ss":
                return d + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
            default:
                return false;
        }
    },

    load: function () {
        // emotions
        this.insertEmotions();
        
        // code high lighter
        SyntaxHighlighter.autoloader(
            'js jscript javascript  /js/lib/SyntaxHighlighter/scripts/shBrushJScript.js',
            'java                   /js/lib/SyntaxHighlighter/scripts/shBrushJava.js',
            'xml                    /js/lib/SyntaxHighlighter/scripts/shBrushXml.js'
            );

        SyntaxHighlighter.config.tagName = "pre";
        SyntaxHighlighter.config.stripBrs = true;
        SyntaxHighlighter.defaults.toolbar = false;
        SyntaxHighlighter.all();

        // submit comment
        $("#commentValidate").keypress(function (event) {
            if (event.keyCode === 13) {
                articleUtil.submitComment();
            }
        });

        // cookie
        $("#commentEmail").val(Cookie.readCookie("commentEmail"));
        $("#commentURL").val(Cookie.readCookie("commentURL"));
        $("#commentName").val(Cookie.readCookie("commentName"));
    },

    loadRandomArticles: function () {
        var randomArticles1Label = this.tip.randomArticles1Label;
        // getRandomArticles
        jsonRpc.articleService.getRandomArticles(function (result, error) {
            if (result && !error) {
                var randomArticles = result.list;
                if (0 === randomArticles.length) {
                    return;
                }

                var listHtml = "";
                for (var i = 0; i < randomArticles.length; i++) {
                    var article = randomArticles[i];
                    var title = article.articleTitle;
                    var randomArticleLiHtml = "<li>" + "<a href='" + article.articlePermalink +"'>" +  title + "</a></li>";
                    listHtml += randomArticleLiHtml;
                }

                var randomArticleListHtml = "<h5>" + randomArticles1Label + "</h5>" + "<ul class='marginLeft12'>" + listHtml + "</ul>";
                $("#randomArticles").append(randomArticleListHtml);
            }
        });
    },

    loadExternalRelevantArticles: function (tags) {
        var tip = this.tip;
        $.ajax({
            url: "http://rhythm.b3log.org:80/get-articles-by-tags.do?tags=" + tags
            + "&blogHost=" + tip.blogHost + "&paginationPageSize=" + tip.externalRelevantArticlesDisplayCount,
            type: "GET",
            dataType:"jsonp",
            jsonp: "callback",
            error: function(){
                alert("Error loading articles from Rhythm");
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
                    + "<a target='_blank' href='" + article.articlePermalink + "'>"
                    +  title + "</a></li>"
                    listHtml += articleLiHtml
                }
                
                var randomArticleListHtml = "<h5>" + tip.externalRelevantArticles1Label + "</h5>"
                + "<ul class='marginLeft12'>"
                + listHtml + "</ul>";
                $("#externalRelevantArticles").append(randomArticleListHtml).addClass("article-relative");
            }
        });
    },
    
    loadTool: function () {
        // Stack initialize
        var openspeed = 300;
        var closespeed = 300;
        $('.stack>img').toggle(function(){
            var vertical = 0;
            var horizontal = 0;
            var $el=$(this);
            $el.next().children().each(function(){
                $(this).animate({
                    top: '-' + vertical + 'px',
                    left: horizontal + 'px'
                }, openspeed);
                vertical = vertical + 36;
                horizontal = (horizontal + 0.42) * 2;
            });
            $el.next().animate({
                top: '-21px',
                left: '-6px'
            }, openspeed).addClass('openStack')
            .find('li a>img').animate({
                width: '28px',
                marginLeft: '9px'
            }, openspeed);
            $el.animate({
                paddingTop: '0'
            });
        }, function(){
            //reverse above
            var $el=$(this);
            $el.next().removeClass('openStack').children('li').animate({
                top: '32px',
                left: '6px'
            }, closespeed);
            $el.next().find('li a>img').animate({
                width: '32px',
                marginLeft: '0'
            }, closespeed);
            $el.animate({
                paddingTop: '9px'
            });
        });

        // Stacks additional animation
        $('.stack li a').hover(function(){
            $("img",this).animate({
                width: '32px'
            }, 100);
            $("span",this).animate({
                marginRight: '12px'
            });
        },function(){
            $("img",this).animate({
                width: '28px'
            }, 100);
            $("span",this).animate({
                marginRight: '0'
            });
        });
    },

    submitComment: function (commentId, statue) {
        if (!statue) {
            statue = '';
        }
        var tip = this.tip, 
        type = "Article";
        if (tip.randomArticles1Label === undefined) {
            type = "Page";
        }
        if (this.validateComment(statue)) {
            $("#commentErrorTip" + statue).html(this.tip.loadingLabel);
            var requestJSONObject = {
                "oId": tip.oId,
                "commentContent": $("#comment" + statue).val().replace(/(^\s*)|(\s*$)/g, ""),
                "commentEmail": $("#commentEmail" + statue).val(),
                "commentURL": "http://" + $("#commentURL" + statue).val().replace(/(^\s*)|(\s*$)/g, ""),
                "commentName": $("#commentName" + statue).val().replace(/(^\s*)|(\s*$)/g, ""),
                "captcha": $("#commentValidate" + statue).val()
            };

            if (statue === "Reply") {
                requestJSONObject.commentOriginalCommentId = commentId;
            }
            
            jsonRpc.commentService["addCommentTo" + type](function (result, error) {
                if (result && !error) {
                    switch (result.sc) {
                        case "COMMENT_" + type.toUpperCase() + "_SUCC":
                            addComment(result, statue);
                            break;
                        case "CAPTCHA_ERROR":
                            $("#commentErrorTip" + statue).html(tip.captchaErrorLabel);
                            $("#captcha" + statue).attr("src", "/captcha.do?code=" + Math.random());
                            $("#commentValidate" + statue).val("").focus();
                            break;
                        default:
                            break;
                    }
                }
            }, requestJSONObject);

            Cookie.createCookie("commentName", requestJSONObject.commentName, 365);
            Cookie.createCookie("commentEmail", requestJSONObject.commentEmail, 365);
            Cookie.createCookie("commentURL", $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""), 365);
        }
    },

    addReplyForm: function (id, commentFormHTML) {
        if (id === this.currentCommentId) {
            $("#commentNameReply").focus();
            return;
        } else {
            $("#replyForm").remove();
            $("#commentItem" + id).append(commentFormHTML);
            $("#commentValidateReply").keypress(function (event) {
                if (event.keyCode === 13) {
                    articleUtil.submitComment(id, 'Reply');
                }
            });
            this.insertEmotions("Reply");
            if (Cookie.readCookie("commentName")  === "") {
                $("#commentNameReply").focus();
            } else {
                $("#commentReply").focus();
            }
        }
        this.currentCommentId = id;
    }
});

var Cookie = {
    readCookie: function (name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return "";
    },

    eraseCookie: function (name) {
        this.createCookie(name,"",-1);
    },

    createCookie: function (name,value,days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime()+(days*24*60*60*1000));
            expires = "; expires="+date.toGMTString();
        }
        document.cookie = name+"="+value+expires+"; path=/";
    }
};
