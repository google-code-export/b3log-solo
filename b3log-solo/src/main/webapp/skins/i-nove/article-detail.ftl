<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head> 
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="revised" content="${blogTitle}, ${article.articleCreateDate?string('MM/dd/yy')}"/>
        <meta name="generator" content="b3log"/>
        <meta name="author" content="${blogTitle}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${article.articleTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="body">
                    <div class="left main">
                        <div class="article">
                            <h2 class="article-title">
                                <a class="noUnderline" href="${article.articlePermalink}">${article.articleTitle}</a>
                                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                <sup class="red">
                                    ${updatedLabel}
                                </sup>
                                </#if>
                                <#if article.articlePutTop>
                                <sup class="red">
                                    ${topArticleLabel}
                                </sup>
                                </#if>
                            </h2>
                            <div class="margin5">
                                <div class="article-date left">
                                    <span class="dateIcon left"></span>
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    </#if>
                                </div>
                                <div class="right">
                                    <a href="/article-detail.do?oId=${article.oId}#comments" class="left">
                                        <span class="left articles-commentIcon" title="${commentLabel}"></span>
                                        ${article.articleCommentCount}
                                    </a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="article-body">
                                ${article.articleContent}
                            </div>
                            <div class="margin5 paddingTop12">
                                <a class="left" href="/article-detail.do?oId=${article.oId}">
                                    <span title="${viewLabel}" class="left article-browserIcon"></span>
                                    ${article.articleViewCount}
                                </a>
                                <div class="left">
                                    <span title="${tagLabel}" class="tagsIcon"></span>
                                    <#list articleTags as articleTag>
                                    <span>
                                        <a href="/tag-articles.do?oId=${articleTag.oId}">
                                            ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                                    </span>
                                    </#list>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="article-relative">
                                <#if nextArticleId??>
                                <a href="/article-detail.do?oId=${nextArticleId}">${nextArticle1Label}${nextArticleTitle}</a>
                                <br/>
                                </#if>
                                <#if previousArticleId??>
                                <a href="/article-detail.do?oId=${previousArticleId}">${previousArticle1Label}${previousArticleTitle}</a>
                                </#if>
                            </div>
                            <#if 0 != relevantArticles?size>
                            <div class="article-relative">
                                <h5>${relevantArticles1Label}</h5>
                                <ul class="marginLeft12">
                                    <#list relevantArticles as relevantArticle>
                                    <li>
                                        <a href="/article-detail.do?oId=${relevantArticle.oId}">
                                            ${relevantArticle.articleTitle}
                                        </a>
                                    </li>
                                    </#list>
                                </ul>
                            </div>
                            </#if>
                            <div id="randomArticles"></div>
                            <div id="externalRelevantArticles"></div>
                        </div>
                        <div class="comments" id="comments" name="comments">
                            <#list articleComments as comment>
                            <div id="commentItem${comment.oId}" class="comment-body">
                                <div class="comment-panel">
                                    <div class="left comment-author">
                                        <div>
                                            <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                        </div>
                                        <#if "http://" == comment.commentURL>
                                        <a name="${comment.oId}" class="left">${comment.commentName}</a>
                                        <#else>
                                        <a name="${comment.oId}" href="${comment.commentURL}"
                                           target="_blank">${comment.commentName}</a>
                                        </#if>
                                    </div>
                                    <div class="left comment-info">
                                        <div class="left">
                                            ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                            <#if comment.isReply>
                                            &nbsp;@&nbsp;<a
                                                href="http://${blogHost}/article-detail.do?oId=${article.oId}#${comment.commentOriginalCommentId}"
                                                onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                                onmouseout="hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                            </#if>
                                        </div>
                                        <div class="right">
                                            <a class="noUnderline"
                                               href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                        </div>
                                        <div class="clear">
                                        </div>
                                        <div class="comment-content">
                                            ${comment.commentContent}
                                        </div>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                            </div>
                            </#list>
                        </div>
                        <table class="comment-form">
                            <tbody>
                                <tr>
                                    <td width="208px">
                                        <input class="normalInput" id="commentName"/>
                                    </td>
                                    <td colspan="2" width="400px">
                                        ${commentNameLabel}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <input class="normalInput" id="commentEmail"/>
                                    </td>
                                    <td colspan="2">
                                        ${commentEmailLabel}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <input value="http://" id="commentURL"/>
                                    </td>
                                    <td colspan="2">
                                        ${commentURLLabel}
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="3">
                                        <textarea rows="10" cols="96" id="comment"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <input class="normalInput" id="commentValidate"/>
                                    </td>
                                    <td>
                                        <img id="captcha" alt="validate" src="/captcha.do"></img>
                                    </td>
                                    <th align="right">
                                        <span class="error-msg" id="commentErrorTip"/>
                                    </th>
                                </tr>
                                <tr>
                                    <td colspan="3" align="right">
                                        <button onclick="submitComment();">${submmitCommentLabel}</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="right">
                        <#include "article-side.ftl">
                    </div>
                    <div class="clear"></div>
                </div>
                <script type="text/javascript">
                    var currentCommentId = "";

                    var moveCursor = function(event) {
                        if ($.browser.msie) {
                            var e = event.srcElement;
                            var r = e.createTextRange();
                            r.moveStart('character', e.value.length);
                            r.collapse(true);
                            r.select();
                        } else {
                            var iCaretPos = event.target.value.length;
                            event.target.selectionStart = iCaretPos;
                            event.target.selectionEnd = iCaretPos;
                        }
                    }

                    var validateComment = function (state) {
                        if (state === undefined) {
                            state = '';
                        }
                        var commentName = $("#commentName" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
                        commenterContent = $("#comment" + state).val().replace(/(^\s*)|(\s*$)/g, "");
                        if (2 > commentName.length || commentName.length > 20) {
                            $("#commentErrorTip" + state).html("${nameTooLongLabel}");
                            $("#commentName" + state).focus();
                        } else if ($("#commentEmail" + state).val().replace(/\s/g, "") === "") {
                            $("#commentErrorTip" + state).html("${mailCannotEmptyLabel}");
                            $("#commentEmail" + state).focus();
                        } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#commentEmail" + state).val())) {
                            $("#commentErrorTip" + state).html("${mailInvalidLabel}");
                            $("#commentEmail" + state).focus();
                        }  else if (2 > commenterContent.length || commenterContent.length > 500) {
                            $("#commentErrorTip" + state).html("${commentContentCannotEmptyLabel}");
                            $("#comment" + state).focus();
                        } else if ($("#commentValidate" + state).val().replace(/\s/g, "") === "") {
                            $("#commentErrorTip" + state).html("${captchaCannotEmptyLabel}");
                            $("#commentValidate" + state).focus();
                        } else {
                            return true;
                        }
                        return false;
                    }

                    var replyTo = function (id) {
                        if (id === currentCommentId) {
                            $("#commentNameReply").focus();
                            return;
                        } else {
                            $("#replyForm").remove();

                            var commentFormHTML = "<table class='marginTop12 comment-form' id='replyForm'><tbody><tr>"
                                + "<td width='208px'><input class='normalInput' id='commentNameReply'/>"
                                + "</td><td colspan='2' width='400px'>${commentNameLabel}</td></tr><tr><td>"
                                + "<input class='normalInput' id='commentEmailReply'/></td><td colspan='2'>${commentEmailLabel}</td></tr><tr>"
                                + "<td><input value='http://' id='commentURLReply'/>"
                                + "</td><td colspan='2'>${commentURLLabel}</td></tr><tr><td colspan='3'>"
                                + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                                + "<td><input class='normalInput' id='commentValidateReply'/>"
                                + "</td><td><img id='captchaReply' alt='validate' src='/captcha.do?"
                                + new Date().getTime() + "'></img></td><th align='right'>"
                                + "<span class='error-msg' id='commentErrorTipReply'/>"
                                + "</th></tr><tr><td colspan='3' align='right'>"
                                + "<button onclick=\"submitCommentReply('" + id + "');\">${submmitCommentLabel}</button>"
                                + "</td></tr></tbody></table>";

                            $("#commentItem" + id).append(commentFormHTML);

                            $("#commentValidateReply").keypress(function (event) {
                                if (event.keyCode === 13) {
                                    submitCommentReply(id);
                                }
                            });

                            $("#commentURLReply").keyup(function (event) {
                                if (-1 === this.value.indexOf("http://")) {
                                    this.value = "http://";
                                }
                                moveCursor(event);
                            }).focus(function (event) {
                                moveCursor(event);
                            });

                            $("#commentNameReply").focus();
                        }
                        currentCommentId = id;
                    }

                    var submitCommentReply = function (id) {
                        if (validateComment("Reply")) {
                            $("#commentErrorTipReply").html("${loadingLabel}");
                            var requestJSONObject = {
                                "oId": "${article.oId}",
                                "commentContent": $("#commentReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "commentEmail": $("#commentEmailReply").val(),
                                "commentURL": $("#commentURLReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "commentName": $("#commentNameReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "captcha": $("#commentValidateReply").val(),
                                "commentOriginalCommentId": id
                            };

                            jsonRpc.commentService.addCommentToArticle(function (result, error) {
                                if (result && !error) {
                                    switch (result.sc) {
                                        case "COMMENT_ARTICLE_SUCC":
                                            $("#replyForm").remove();
                                            window.location.reload();
                                            break;
                                        case "CAPTCHA_ERROR":
                                            $("#commentErrorTipReply").html("${captchaErrorLabel}");
                                            $("#captchaReply").attr("src", "/captcha.do?code=" + Math.random());
                                            $("#commentValidateReply").val("").focus();
                                            break
                                        default:
                                            break;
                                    }
                                }
                            }, requestJSONObject);
                        }
                    }

                    var submitComment = function () {
                        if (validateComment()) {
                            $("#commentErrorTip").html("${loadingLabel}");
                            var requestJSONObject = {
                                "oId": "${article.oId}",
                                "commentContent": $("#comment").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "commentEmail": $("#commentEmail").val(),
                                "commentURL": $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "commentName": $("#commentName").val().replace(/(^\s*)|(\s*$)/g, ""),
                                "captcha": $("#commentValidate").val()
                            };

                            jsonRpc.commentService.addCommentToArticle(function (result, error) {
                                if (result && !error) {
                                    switch (result.sc) {
                                        case "COMMENT_ARTICLE_SUCC":
                                            $("#commentErrorTip").html("");
                                            $("#comment").val("");
                                            $("#commentEmail").val("");
                                            $("#commentURL").val("http://");
                                            $("#commentName").val("");
                                            $("#commentValidate").val("");
                                            window.location.reload();
                                            break;
                                        case "CAPTCHA_ERROR":
                                            $("#commentErrorTip").html("${captchaErrorLabel}");
                                            $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
                                            $("#commentValidate").val("").focus();
                                            break
                                        default:
                                            break;
                                    }
                                }
                            }, requestJSONObject);
                        }
                    }

                    var showComment = function (it, id) {
                        if ( $("#commentItemRef" + id).length > 0) {
                            $("#commentItemRef" + id).show();
                        } else {
                            var $refComment = $("#commentItem" + id + " .comment-panel").clone();
                            $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                            $refComment.find(".comment-info .right").remove();
                            $("#comments").append($refComment);
                        }
                        var position =  $(it).position();
                        $("#commentItemRef" + id).css({
                            "top": (position.top + 18) + "px",
                            "left": "217px"
                        });
                    }

                    var hideComment = function (id) {
                        $("#commentItemRef" + id).hide();
                    }

                    var loadAction = function () {
                        // code high lighter
                        SyntaxHighlighter.autoloader(
                        'js jscript javascript  /js/lib/SyntaxHighlighter/scripts/shBrushJScript.js',
                        'java                   /js/lib/SyntaxHighlighter/scripts/shBrushJava.js',
                        'xml                    /js/lib/SyntaxHighlighter/scripts/shBrushXml.js'
                    );

                        SyntaxHighlighter.config.tagName = "pre";
                        SyntaxHighlighter.config.stripBrs = true;
                        SyntaxHighlighter.defaults['toolbar'] = false;
                        SyntaxHighlighter.all();

                        // submit comment
                        $("#commentValidate").keypress(function (event) {
                            if (event.keyCode === 13) {
                                submitComment();
                            }
                        });

                        // comment url
                        $("#commentURL").keyup(function (event) {
                            if (-1 === this.value.indexOf("http://")) {
                                this.value = "http://";
                            }
                            moveCursor(event);
                        }).focus(function (event) {
                            moveCursor(event);
                        });

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
                                    var randomArticleLiHtml = "<li>"
                                        + "<a href='" + article.articlePermalink +"'>"
                                        +  title + "</a></li>"
                                    listHtml += randomArticleLiHtml
                                }

                                var randomArticlesDiv = $("#randomArticles");
                                randomArticlesDiv.attr("class", "article-relative");
                                var randomArticleListHtml = "<h5>${randomArticles1Label}</h5>"
                                    + "<ul class='marginLeft12'>"
                                    + listHtml + "</ul>";
                                randomArticlesDiv.append(randomArticleListHtml);
                            }
                        });

                            <#if 0 != externalRelevantArticlesDisplayCount>
                            var tags = "<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>";
                        $.ajax({
                            url: "http://b3log-rhythm.appspot.com:80/get-articles-by-tags.do?tags=" + tags
                                + "&blogHost=${blogHost}&paginationPageSize=${externalRelevantArticlesDisplayCount}",
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

                                var externalRelevantArticlesDiv = $("#externalRelevantArticles");
                                externalRelevantArticlesDiv.attr("class", "article-relative");
                                var randomArticleListHtml = "<h5>${externalRelevantArticles1Label}</h5>"
                                    + "<ul class='marginLeft12'>"
                                    + listHtml + "</ul>";
                                externalRelevantArticlesDiv.append(randomArticleListHtml);
                            }
                        });
                            </#if>
                        }
                    loadAction();
                </script>
                <div class="footer">
                    <#include "article-footer.ftl">
                </div>
                <div class="stack addthis_toolbox">
                    <img src="/images/stack.png" alt="stack"/>
                    <ul id="stack" class="custom_images">
                        <li><a class="addthis_button_googlebuzz"><span>Buzz</span><img src="/images/buzz.png" alt="Share to Buzz" /></a></li>
                        <li><a class="addthis_button_twitter"><span>Twitter</span><img src="/images/twitter.png" alt="Share to Twitter" /></a></li>
                        <li><a class="addthis_button_delicious"><span>Delicious</span><img src="/images/delicious.png" alt="Share to Delicious" /></a></li>
                        <li><a class="addthis_button_facebook"><span>Facebook</span><img src="/images/facebook.png" alt="Share to Facebook" /></a></li>
                        <li><a class="addthis_button_more"><span>More...</span><img src="/images/addthis.png" alt="More..." /></a></li>
                    </ul>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            var loadTool = function () {
                // hide comments
                if ($("#comments div").length === 0) {
                    $("#comments").removeClass("comments");
                }
                
                // article view count
                jsonRpc.statisticService.incArticleViewCount(function (result, error) {}, "${article.oId}");

                // Stack initialize
                var openspeed = 300;
                var closespeed = 300;
                $('.stack>img').toggle(function(){
                    var vertical = 0;
                    var horizontal = 0;
                    var $el=$(this);
                    $el.next().children().each(function(){
                        $(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, openspeed);
                        vertical = vertical + 36;
                        horizontal = (horizontal+.42)*2;
                    });
                    $el.next().animate({top: '-21px', left: '-6px'}, openspeed).addClass('openStack')
                    .find('li a>img').animate({width: '28px', marginLeft: '9px'}, openspeed);
                    $el.animate({paddingTop: '0'});
                }, function(){
                    //reverse above
                    var $el=$(this);
                    $el.next().removeClass('openStack').children('li').animate({top: '32px', left: '6px'}, closespeed);
                    $el.next().find('li a>img').animate({width: '32px', marginLeft: '0'}, closespeed);
                    $el.animate({paddingTop: '9px'});
                });

                // Stacks additional animation
                $('.stack li a').hover(function(){
                    $("img",this).animate({width: '32px'}, 100);
                    $("span",this).animate({marginRight: '12px'});
                },function(){
                    $("img",this).animate({width: '28px'}, 100);
                    $("span",this).animate({marginRight: '0'});
                });
            }
            loadTool();
        </script>
    </body>
</html>
