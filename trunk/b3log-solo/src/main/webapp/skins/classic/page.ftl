<#include "macro.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${page.pageTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${page.pageTitle}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
    </head>
    <body>
        <#include "top-nav.ftl">
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <div class="left main">
                    <div>
                        <div class="article">
                            <div class="article-body">
                                ${page.pageContent}
                            </div>
                        </div>
                        <h2 class="marginLeft12 marginBottom12">${commentLabel}</h2>
                        <div class="comments" id="comments">
                            <#if 0 == pageComments?size>
                            ${noCommentLabel}
                            </#if>
                            <#list pageComments as comment>
                            <div id="${comment.oId}">
                                <div class="comment-panel">
                                    <div class="comment-title">
                                        <#if "http://" == comment.commentURL>
                                        <a>${comment.commentName}</a>
                                        <#else>
                                        <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                        </#if>
                                        <#if comment.isReply>
                                        &nbsp;@&nbsp;<a
                                            href="http://${blogHost}${page.pagePermalink}#${comment.commentOriginalCommentId}"
                                            onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                            onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                        </#if>
                                        <div class="right">
                                            ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                            <a class="no-underline"
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
                                                <input type="text" class="normalInput" id="commentName"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                ${commentEmail1Label}
                                            </th>
                                            <td colspan="2">
                                                <input type="text" class="normalInput" id="commentEmail"/>
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
                                                <input type="text" id="commentURL"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                ${commentEmotions1Label}
                                            </th>
                                            <td id="emotions">
                                                <span class="em00" title="${em00Label}"></span>
                                                <span class="em01" title="${em01Label}"></span>
                                                <span class="em02" title="${em02Label}"></span>
                                                <span class="em03" title="${em03Label}"></span>
                                                <span class="em04" title="${em04Label}"></span>
                                                <span class="em05" title="${em05Label}"></span>
                                                <span class="em06" title="${em06Label}"></span>
                                                <span class="em07" title="${em07Label}"></span>
                                                <span class="em08" title="${em08Label}"></span>
                                                <span class="em09" title="${em09Label}"></span>
                                                <span class="em10" title="${em10Label}"></span>
                                                <span class="em11" title="${em11Label}"></span>
                                                <span class="em12" title="${em12Label}"></span>
                                                <span class="em13" title="${em13Label}"></span>
                                                <span class="em14" title="${em14Label}"></span>
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
                                                <input type="text" class="normalInput" id="commentValidate"/>
                                                <img id="captcha" alt="validate" src="/captcha.do"></img>
                                            </td>
                                            <th>
                                                <span class="error-msg" id="commentErrorTip"/>
                                            </th>
                                        </tr>
                                        <tr>
                                            <td colspan="3" align="right">
                                                <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right side">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "footer.ftl">
            </div>
        </div>
        <script type="text/javascript" src="/js/article.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript">
            var page = new Article({
                "nameTooLongLabel": "${nameTooLongLabel}",
                "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
                "mailInvalidLabel": "${mailInvalidLabel}",
                "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
                "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                "captchaErrorLabel": "${captchaErrorLabel}",
                "loadingLabel": "${loadingLabel}",
                "oId": "${page.oId}",
                "blogHost": "${blogHost}",
                "skinDirName": "${skinDirName}"
            });

            var addComment = function (result, state) {
                var commentHTML = '<div id="' + result.oId + '"><div class="comment-panel"><div class="comment-title">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a>' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + 
                        '" target="_blank">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#" + page.currentCommentId).find(".comment-title a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + page.currentCommentId + '\');"'
                        + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '<div class="right">' + result.commentDate
                    + '&nbsp;<a class="no-underline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="clear"></div></div><div class="comment-body">'
                    + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
                    + '" src="' + result.commentThumbnailURL + '"/>'
                    + '</div><div class="comment-content">' + page.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div class="clear"></div>'
                    + '</div></div></div>';

                page.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table class='form comment-reply' id='replyForm'>";
                
                page.addReplyForm(id, commentFormHTML);
                $("#commentURLReply").focus(function (event) {
                    if ($.browser.version !== "7.0") {
                        $("#commentURLLabelReply").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }
                }).blur(function () {
                    $("#commentURLLabelReply").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                });
            }
            
            var showComment = function (it, id) {
                if ( $("#commentRef" + id).length > 0) {
                    $("#commentRef" + id).show();
                } else {
                    var $refComment = $("#" + id + " .comment-panel").clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentRef" + id);
                    $refComment.find(".comment-title .right a").remove();
                    $("#comments").append($refComment);
                }
                $("#commentRef" + id).css("top", ($(it).position().top + 23) + "px");
            };

            (function () {
                page.load();

                // comment url
                $("#commentURL").focus(function (event) {
                    $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                });
                
                // emotions
                page.replaceCommentsEm("#comments .comment-content");
            })();
        </script>
    </body>
</html>
