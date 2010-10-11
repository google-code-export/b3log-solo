<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${articleTags?first.tagTitle}"/>
        <meta name="description" content="${article.articleTitle}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="revised" content="${blogTitle}, ${article.articleCreateDate?string('MM/dd/yy')}"/>
        <meta name="generator" content="b3log"/>
        <meta name="author" content="${blogTitle}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${article.articleTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <link type="text/css" rel="stylesheet" href="js/lib/SyntaxHighlighter/styles/shCoreDefault.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="content">
            <div class="header">
                <#include "article-header.ftl">
            </div>
            <div class="body">
                <div class="left side">
                    <#include "article-side.ftl">
                </div>
                <div class="right main">
                    <div class="article">
                        <div class="article-header">
                            <div class="article-date">
                                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                <#else>
                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                </#if>
                            </div>
                            <div class="article-title">
                                <h2>
                                    <a class="noUnderline" href="${article.articlePermalink}">${article.articleTitle}</a>
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                    <sup class="red" style="font-size: 12px">
                                        ${updatedLabel}
                                    </sup>
                                    </#if>
                                </h2>
                                <div class="article-tags">
                                    ${tags1Label}
                                    <#list articleTags as articleTag>
                                    <span>
                                        <a href="tag-articles.do?oId=${articleTag.oId}">
                                            ${articleTag.tagTitle}
                                        </a>
                                        <#if articleTag_has_next>,</#if>
                                    </span>
                                    </#list>
                                </div>
                            </div>
                            <div class="clear"></div>
                        </div>
                        <div class="article-body">
                            ${article.articleContent}
                        </div>
                        <div class="article-details-footer">
                            <div class="left">
                                <#if nextArticleId??>
                                <a href="article-detail.do?oId=${nextArticleId}">${nextArticle1Label}${nextArticleTitle}</a>
                                </#if>
                                <#if previousArticleId??>
                                <br/>
                                <a href="article-detail.do?oId=${previousArticleId}">${previousArticle1Label}${previousArticleTitle}</a>
                                </#if>
                            </div>
                            <div class="right">
                                <span class="article-create-date left">
                                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                                </span>
                                <span class="left commentIcon" title="${commentLabel}"></span>
                                <span class="left">
                                    &nbsp;${article.articleCommentCount}&nbsp;&nbsp;
                                </span>
                                <a href="article-detail.do?oId=${article.oId}" class="left">
                                    <span class="left browserIcon" title="${viewLabel}"></span>
                                    ${article.articleViewCount}
                                </a>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="comments" id="comments" name="comments">
                        <#list articleComments as comment>
                        <div id="commentItem${comment.oId}">
                            <div class="comment-title">
                                <#if "http://" == comment.commentURL>
                                <a name="${comment.oId}" class="left">${comment.commentName}</a>
                                <#else>
                                <a name="${comment.oId}" href="${comment.commentURL}"
                                   target="_blank" class="left">${comment.commentName}</a>
                                </#if>
                                <#if comment.isReply>
                                &nbsp;@&nbsp;<a 
                                    href="http://${blogHost}/article-detail.do?oId=${article.oId}#${comment.commentOriginalCommentId}"
                                    onmouseover="showComment('${comment.commentOriginalCommentId}', '${comment.oId}');"
                                    onmouseout="hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                </#if>
                                <div class="right">
                                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <a class="noUnderline" 
                                       href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="comment-body">
                                <div class="left comment-picture">
                                    <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                </div>
                                <div>
                                    ${comment.commentContent}
                                </div>
                                <div class="clear"></div>
                            </div>
                        </div>
                        </#list>
                        <div class="comment-title">
                            ${postCommentsLabel}
                        </div>
                        <div class="comment-body">
                            <table class="form">
                                <tbody>
                                    <tr>
                                        <th>
                                            ${commentName1Label}
                                        </th>
                                        <td colspan="2">
                                            <input class="normalInput" id="commentName"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            ${commentEmail1Label}
                                        </th>
                                        <td colspan="2">
                                            <input class="normalInput" id="commentEmail"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            ${commentURL1Label}
                                        </th>
                                        <td colspan="2">
                                            <input value="http://" id="commentURL"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th valign="top">
                                            ${commentContent1Label}
                                        </th>
                                        <td colspan="2">
                                            <textarea rows="10" cols="96" id="comment"></textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>
                                            ${captcha1Label}
                                        </th>
                                        <td>
                                            <input class="normalInput" id="commentValidate"/>
                                            <img id="captcha" alt="validate" src="/captcha.do"></img>
                                        </td>
                                        <th>
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
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
            <div class="stack addthis_toolbox">
                <img src="images/stack.png" alt="stack"/>
                <ul id="stack" class="custom_images">
                    <li><a class="addthis_button_googlebuzz"><span>Buzz</span><img src="images/buzz.png" alt="Share to Buzz" /></a></li>
                    <li><a class="addthis_button_twitter"><span>Twitter</span><img src="images/twitter.png" alt="Share to Twitter" /></a></li>
                    <li><a class="addthis_button_delicious"><span>Delicious</span><img src="images/delicious.png" alt="Share to Delicious" /></a></li>
                    <li><a class="addthis_button_facebook"><span>Facebook</span><img src="images/facebook.png" alt="Share to Facebook" /></a></li>
                    <li><a class="addthis_button_more"><span>More...</span><img src="images/addthis.png" alt="More..." /></a></li>
                </ul>
            </div>
        </div>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushJScript.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushJava.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushXml.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushCss.js"></script>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
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

            var loadAction = function () {
                // code high lighter
                $(".article-body textarea").addClass("brush: js;");
                SyntaxHighlighter.config.tagName = "textarea";
                SyntaxHighlighter.config.tagName = "pre";
                SyntaxHighlighter.config.stripBrs = true;
                SyntaxHighlighter.defaults["gutter"] = true;
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

                // article view count
                jsonRpc.statisticService.incArticleViewCount("${article.oId}");
                jsonRpc.statisticService.getArticleViewCount(function (result, error) {
                    if (!result || error) {
                        return;
                    }
                    $("#articleViewCount").html(result);
                },"${article.oId}");

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
            loadAction();

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

                    var commentFormHTML = "<table class='form comment-reply' id='replyForm'><tbody><tr><th>${commentName1Label}"
                        + "</th><td colspan='2'><input class='normalInput' id='commentNameReply'/>"
                        + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                        + "<input class='normalInput' id='commentEmailReply'/></td></tr><tr>"
                        + "<th>${commentURL1Label}</th><td colspan='2'><input value='http://' id='commentURLReply'/>"
                        + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                        + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                        + "<th>${captcha1Label}</th><td><input class='normalInput' id='commentValidateReply'/>"
                        + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
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

                    jsonRpc.commentService.addComment(function (result, error) {
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

                    jsonRpc.commentService.addComment(function (result, error) {
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

            var showComment = function (id, oId) {
                if ($("#commentItemRef" + id).length > 0) {
                    $("#commentItemRef" + id).fadeIn("normal");
                } else {
                    var refComment = $("#commentItem" + id).clone();
                    refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                    $("#commentItem" + oId + " .comment-title").append(refComment);
                    $("#commentItemRef" + id + " .comment-title").css("border-top-style", "hidden");
                    $("#commentItemRef" + id + " .comment-title .right a").remove();
                    $("#commentItemRef" + id).fadeTo("normal", 0.9);
                }
            }

            var hideComment = function (id) {
                $("#commentItemRef" + id).fadeOut("normal");
            }
        </script>
    </body>
</html>
