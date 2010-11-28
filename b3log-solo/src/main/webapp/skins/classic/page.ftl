<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${page.pageTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
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
                            <div class="comment-panel">
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
                                        onmouseout="ArticleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
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
                                    <div class="comment-content">
                                        ${comment.commentContent}
                                    </div>
                                    <div class="clear"></div>
                                </div>
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
                                        <th>
                                            ${commentEmotions1Label}
                                        </th>
                                        <td id="emotions">
                                            <img class="[em00]" src="/skins/classic/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                                            <img class="[em01]" src="/skins/classic/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                                            <img class="[em02]" src="/skins/classic/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                                            <img class="[em03]" src="/skins/classic/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                                            <img class="[em04]" src="/skins/classic/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                                            <img class="[em05]" src="/skins/classic/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                                            <img class="[em06]" src="/skins/classic/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                                            <img class="[em07]" src="/skins/classic/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                                            <img class="[em08]" src="/skins/classic/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                                            <img class="[em09]" src="/skins/classic/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                                            <img class="[em10]" src="/skins/classic/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                                            <img class="[em11]" src="/skins/classic/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                                            <img class="[em12]" src="/skins/classic/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                                            <img class="[em13]" src="/skins/classic/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                                            <img class="[em14]" src="/skins/classic/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
        <script type="text/javascript" src="/js/articleUtil.js"></script>
        <script type="text/javascript">
            ArticleUtil.tip = {
                nameTooLong: "${nameTooLongLabel}",
                mailCannotEmpty: "${mailCannotEmptyLabel}",
                mailInvalid: "${mailInvalidLabel}",
                commentContentCannotEmpty: "${commentContentCannotEmptyLabel}",
                captchaCannotEmpty: "${captchaCannotEmptyLabel}"
            };

            var addComment = function (result, state) {
                if (state === undefined) {
                    state = "";
                }

                var commentHTML = '<div id="commentItem' + result.oId + '"><div class="comment-panel"><div class="comment-title">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + ArticleUtil.currentCommentId).find(".comment-title a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + ArticleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + ArticleUtil.currentCommentId + '\');"'
                        + 'onmouseout="ArticleUtil.hideComment(\'' + ArticleUtil.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '<div class="right">' + ArticleUtil.getDate(result.commentDate.time, 'yyyy-mm-dd hh:mm:ss')
                    + '&nbsp;<a class="noUnderline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="clear"></div></div><div class="comment-body">'
                    + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
                    + '" src="' + result.commentThumbnailURL + '"/>'
                    + '</div><div class="comment-content">' + ArticleUtil.replaceEmotions($("#comment" + state).val(), "classic") + '</div><div class="clear"></div>'
                    + '</div></div></div>';

                ArticleUtil.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                if (id === ArticleUtil.currentCommentId) {
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
                        + "</td></tr><tr><th>${commentEmotions1Label}</th><td id='emotionsReply'>" + $("#emotions").html()
                        + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                        + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                        + "<th>${captcha1Label}</th><td><input class='normalInput' id='commentValidateReply'/>"
                        + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                        + "<span class='error-msg' id='commentErrorTipReply'/>"
                        + "</th></tr><tr><td colspan='3' align='right'>"
                        + "<button onclick=\"submitCommentReply('" + id + "');\">${submmitCommentLabel}</button>"
                        + "</td></tr></tbody></table>";

                    $("#commentItem" + id).append(commentFormHTML);

                    ArticleUtil.insertEmotions("Reply");
                    $("#commentValidateReply").keypress(function (event) {
                        if (event.keyCode === 13) {
                            submitCommentReply(id);
                        }
                    });

                    $("#commentURLReply").focus(function (event) {
                        if ($.browser.version !== "7.0") {
                            $("#commentURLLabelReply").css({"border":"2px solid #73A6FF","border-right":"0px"});
                        }
                    }).blur(function () {
                        $("#commentURLLabelReply").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                    }).width($("#commentReply").width() - $("#commentURLLabelReply").width());

                    $("#commentNameReply").focus();
                }
                ArticleUtil.currentCommentId = id;
            }

            var submitCommentReply = function (id) {
                if (ArticleUtil.validateComment("Reply")) {
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
                                   addComment(result, "Reply");
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTipReply").html("${captchaErrorLabel}");
                                    $("#captchaReply").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidateReply").val("").focus();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, requestJSONObject);
                }
            }

            var submitComment = function () {
                if (ArticleUtil.validateComment()) {
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
                                     addComment(result);
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTip").html("${captchaErrorLabel}");
                                    $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidate").val("").focus();
                                    break;
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
                    "top": (position.top + 23) + "px",
                    "left": "88px"
                });
            }

            var loadAction = function () {
                ArticleUtil.load();

                // comment url
                $("#commentURL").focus(function (event) {
                    if ($.browser.version !== "7.0") {
                        $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#comment").width() - $("#commentURLLabel").width());
                
                // emotions
                ArticleUtil.insertEmotions();
                replaceCommentsEm("#comments .comment-content");
            }
            loadAction();
        </script>
    </body>
</html>
