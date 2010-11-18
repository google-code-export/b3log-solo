<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="revised" content="${blogTitle}, 9/10/10"/>
        <meta name="generator" content="b3log"/>
        <meta name="author" content="${blogTitle}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${page.pageTitle} - ${blogTitle}</title>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        ${htmlHead}
    </head>
    <body>
        <div class="wrapper">
            <div class="bg-bottom">
                <#include "common-top.ftl">
                <div class="content">
                    <div class="header">
                        <#include "article-header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div class="article">
                                <div class="article-body">
                                    ${page.pageContent}
                                </div>
                            </div>
                            <div class="line right"></div>
                            <div class="comments marginTop12" id="comments" name="comments">
                                <div class="comments-header"></div>
                                <#list pageComments as comment>
                                <div id="commentItem${comment.oId}" class="comment">
                                    <div class="comment-panel">
                                        <div class="comment-top"></div>
                                        <div class="comment-body">
                                            <div class="comment-title">
                                                <#if "http://" == comment.commentURL>
                                                <a name="${comment.oId}" class="left">${comment.commentName}</a>
                                                <#else>
                                                <a name="${comment.oId}" href="${comment.commentURL}"
                                                   target="_blank" class="left">${comment.commentName}</a>
                                                </#if>
                                                <#if comment.isReply>
                                                &nbsp;@&nbsp;<a
                                                    href="http://${blogHost}/page.do?oId=${page.oId}#${comment.commentOriginalCommentId}"
                                                    onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                                    onmouseout="hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                                </#if>
                                                <div class="right">
                                                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                                    <a class="noUnderline"
                                                       href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                                </div>
                                                <div class="clear"></div>
                                            </div>
                                            <div>
                                                <img class="comment-picture left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                                <div class="comment-content">
                                                    ${comment.commentContent}
                                                </div>
                                                <div class="clear"></div>
                                            </div>
                                        </div>
                                        <div class="comment-bottom"></div>
                                    </div>
                                </div>
                                </#list>
                                <div class="comment">
                                    <div class="comment-top"></div>
                                    <div class="comment-body">
                                        <div class="comment-title">
                                            <a>${postCommentsLabel}</a>
                                        </div>
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
                                                        <div id="commentURLLabel">
                                                            http://
                                                        </div>
                                                        <input id="commentURL"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td id="emotions" colspan="3">
                                                        <img class="[em00]" src="/skins/tree-house/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                                                        <img class="[em01]" src="/skins/tree-house/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                                                        <img class="[em02]" src="/skins/tree-house/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                                                        <img class="[em03]" src="/skins/tree-house/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                                                        <img class="[em04]" src="/skins/tree-house/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                                                        <img class="[em05]" src="/skins/tree-house/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                                                        <img class="[em06]" src="/skins/tree-house/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                                                        <img class="[em07]" src="/skins/tree-house/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                                                        <img class="[em08]" src="/skins/tree-house/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                                                        <img class="[em09]" src="/skins/tree-house/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                                                        <img class="[em10]" src="/skins/tree-house/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                                                        <img class="[em11]" src="/skins/tree-house/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                                                        <img class="[em12]" src="/skins/tree-house/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                                                        <img class="[em13]" src="/skins/tree-house/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                                                        <img class="[em14]" src="/skins/tree-house/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
                                                    <th valign="top">
                                                        ${captcha1Label}
                                                    </th>
                                                    <td valign="top" style="min-width: 190px;">
                                                        <input class="normalInput" id="commentValidate"/>
                                                        <img id="captcha" alt="validate" src="/captcha.do"></img>
                                                    </td>
                                                    <td>
                                                        <span class="error-msg" id="commentErrorTip"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colspan="3" align="right">
                                                        <button onclick="submitComment();">${submmitCommentLabel}</button>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="comment-bottom"></div>
                                </div>
                            </div>
                        </div>
                        <div class="left side">
                            <#include "article-side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="footer">
                        <#include "article-footer.ftl">
                    </div>
                </div>
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
        <script type="text/javascript">
            var currentCommentId = "";

var insertEmotions = function (name) {
                $("#emotions" + name + " img").click(function () {
                    // TODO: should be insert it at the after of cursor
                    var key = this.className;
                    $("#comment" + name).val($("#comment" + name).val() + key).focus();
                });
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
                $("#commentURL").focus(function (event) {
                    $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#comment").width() - $("#commentURLLabel").width());

                 // emotions
                insertEmotions("");
                replaceCommentsEm("#comments .comment-content");
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
                    var commentFormHTML = "<tr><th>${commentName1Label}"
                        + "</th><td colspan='2'><input class='normalInput' id='commentNameReply'/>"
                        + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                        + "<input class='normalInput' id='commentEmailReply'/></td></tr><tr>"
                        + "<th>${commentURL1Label}</th><td colspan='2'><div id='commentURLLabelReply'>"
                        + "http://</div><input id='commentURLReply'/>"
                        + "</td></tr><tr><td id='emotionsReply' colspan='3'>" + $("#emotions").html()
                        + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                        + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                        + "<th valign='top'>${captcha1Label}</th><td valign='top'>"
                        + "<input class='normalInput' id='commentValidateReply'/>"
                        + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                        + "<span class='error-msg' id='commentErrorTipReply'/>"
                        + "</th></tr><tr><td colspan='3' align='right'>"
                        + "<button onclick=\"submitCommentReply('" + id + "');\">${submmitCommentLabel}</button>"
                        + "</td></tr>";

                    $("#commentItem" + id).append("<div id='replyForm'><div class='comment-top'></div>"
                        + "<div class='comment-body'><table class='form comment-reply'>" + commentFormHTML
                        +"</table></div><div class='comment-bottom'></div></div>");
                    
                    $("#commentValidateReply").keypress(function (event) {
                        if (event.keyCode === 13) {
                            submitCommentReply(id);
                        }
                    });

                    insertEmotions("Reply");

                    $("#commentURLReply").focus(function (event) {
                        $("#commentURLLabelReply").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }).blur(function () {
                        $("#commentURLLabelReply").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                    }).width($("#commentReply").width() - $("#commentURLLabelReply").width());

                    $("#commentNameReply").focus();
                }
                currentCommentId = id;
            }

            var submitCommentReply = function (id) {
                if (validateComment("Reply")) {
                    $("#commentErrorTipReply").html("${loadingLabel}");
                    var requestJSONObject = {
                        "oId": "${page.oId}",
                        "commentContent": $("#commentReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmailReply").val(),
                        "commentURL": "http://" + $("#commentURLReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentNameReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidateReply").val(),
                        "commentOriginalCommentId": id
                    };

                    jsonRpc.commentService.addCommentToPage(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_PAGE_SUCC":
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
                        "oId": "${page.oId}",
                        "commentContent": $("#comment").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmail").val(),
                        "commentURL": "http://" + $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentName").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidate").val()
                    };

                    jsonRpc.commentService.addCommentToPage(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_PAGE_SUCC":
                                    $("#commentErrorTip").html("");
                                    $("#comment").val("");
                                    $("#commentEmail").val("");
                                    $("#commentURL").val("");
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
                    $refComment.find(".comment-title .right a").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentItemRef" + id).css({
                    "top": (position.top + 12) + "px",
                    "left": "182px"
                });
            }

            var hideComment = function (id) {
                $("#commentItemRef" + id).hide();
            }
        </script>
    </body>
</html>
