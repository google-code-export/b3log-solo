<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="author" content="b3log-solo.googlecode.com"/>
        <meta name="revised" content="b3log, 9/10/10"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${page.pageTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
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
                        <div class="article-body">
                            ${page.pageContent}
                        </div>
                    </div>
                    <div class="comments" id="comments" name="comments">
                        <#list pageComments as comment>
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
                                    href="http://${blogHost}/page.do?oId=${page.oId}#${comment.commentOriginalCommentId}"
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
                                            <div id="commentURLLabel">
                                                http://
                                            </div>
                                            <input id="commentURL"/>
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
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
        <script type="text/javascript">
            var currentCommentId = "";

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
                        + "<th>${commentURL1Label}</th><td colspan='2'><div id='commentURLLabelReply'>"
                        + "http://</div><input id='commentURLReply'/>"
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

            var showComment = function (id, oId) {
                $("#commentItemRef" + id).remove();
                var refComment = $("#commentItem" + id).clone();
                refComment.find(".comment-body-ref").remove();
                refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                $("#commentItem" + oId + " .comment-title").append(refComment);
                $("#commentItemRef" + id + " #replyForm").remove();
                $("#commentItemRef" + id + " .comment-title").css("border-top-style", "hidden");
                $("#commentItemRef" + id + " .comment-title .right a").remove();
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
                $("#commentURL").focus(function (event) {
                    $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#comment").width() - $("#commentURLLabel").width());
            }
            loadAction();
        </script>
    </body>
</html>
