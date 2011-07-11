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
        <div class="header">
            <#include "header.ftl">
        </div>
        <div class="content">
            <div class="article-body marginBottom40">
                ${page.pageContent}
            </div>
            <div class="comments" id="comments" name="comments">
                <#list pageComments as comment>
                <div id="${comment.oId}">
                    <img class="left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                    <div class="comment-panel left">
                        <div class="comment-top">
                            <#if "http://" == comment.commentURL>
                            <a name="${comment.oId}" class="left">${comment.commentName}</a>
                            <#else>
                            <a name="${comment.oId}" href="${comment.commentURL}"
                               target="_blank" class="left">${comment.commentName}</a>
                            </#if>
                            <#if comment.isReply>
                            @
                            <a href="http://${blogHost}${page.pagePermalink}#${comment.commentOriginalCommentId}"
                               onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                               onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">
                                ${comment.commentOriginalCommentName}</a>
                            </#if>
                            ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                        </div>
                        <div class="comment-content">
                            ${comment.commentContent}
                        </div>
                        <div class="reply">
                            <a href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
                </#list>
            </div>
            <div class="comment-title">
                ${postCommentsLabel}
            </div>
            <table class="comment" cellpadding="0" cellspacing="0" width="100%" style="margin-bottom: 40px;">
                <tbody>
                    <tr>
                        <th width="200px">
                            <div>
                                ${commentNameLabel}
                            </div>
                            <span class="arrow-right"></span>
                        </th>
                        <td colspan="2">
                            <input type="text" id="commentName"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <div>
                                ${commentEmailLabel}
                            </div>
                            <span class="arrow-right"></span>
                        </th>
                        <td colspan="2">
                            <input type="text" id="commentEmail"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <div>
                                ${commentURLLabel}
                            </div>
                            <span class="arrow-right"></span>
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
                            <div>
                                ${commentEmotionsLabel}
                            </div>
                            <span class="arrow-right"></span>
                        </th>
                        <td id="emotions" colspan="2">
                            <img class="[em00]" src="/skins/${skinDirName}/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                            <img class="[em01]" src="/skins/${skinDirName}/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                            <img class="[em02]" src="/skins/${skinDirName}/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                            <img class="[em03]" src="/skins/${skinDirName}/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                            <img class="[em04]" src="/skins/${skinDirName}/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                            <img class="[em05]" src="/skins/${skinDirName}/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                            <img class="[em06]" src="/skins/${skinDirName}/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                            <img class="[em07]" src="/skins/${skinDirName}/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                            <img class="[em08]" src="/skins/${skinDirName}/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                            <img class="[em09]" src="/skins/${skinDirName}/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                            <img class="[em10]" src="/skins/${skinDirName}/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                            <img class="[em11]" src="/skins/${skinDirName}/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                            <img class="[em12]" src="/skins/${skinDirName}/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                            <img class="[em13]" src="/skins/${skinDirName}/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                            <img class="[em14]" src="/skins/${skinDirName}/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
                        </td>
                    </tr>
                    <tr>
                        <th valign="top">
                            <div>
                                ${commentContentLabel}
                            </div>
                            <span class="arrow-right"></span>
                        </th>
                        <td colspan="2">
                            <textarea rows="10" id="comment"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <div>
                                ${captchaLabel}
                            </div>
                            <span class="arrow-right"></span>
                        </th>
                        <td>
                            <input type="text" id="commentValidate"/>
                            <img id="captcha" alt="validate" src="/captcha.do"></img>
                        </td>
                        <th>
                            <span class="right error-msg" id="commentErrorTip"/>
                        </th>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <input id="submitCommentButton" type="button" onclick="page.submitComment();" value="${submmitCommentLabel}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div>
            <#include "side.ftl">
        </div>
        <div class="footer">
            <#include "footer.ftl">
        </div>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript" src="/js/article.js"></script>
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
                var commentHTML = '<div id="' + result.oId + '">'
                    + '<img class="left" alt="' + $("#commentName" + state).val() + '" src="' + result.commentThumbnailURL
                    + '"/><div class="comment-panel left"><div class="comment-top">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a>' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#" + page.currentCommentId + " .comment-top a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + page.currentCommentId + '\');"'
                        + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '&nbsp;' + result.commentDate
                    + '</div><div class="comment-content">' + page.replaceCommentsEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div class="reply"><a href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div></div><div class="clear"></div></div>';

                page.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table width='100%' cellspacing='0' cellpadding='0' class='comment' id='replyForm'>";
                page.addReplyForm(id, commentFormHTML);
            }

            var showComment = function (it, id) {
                if ( $("#commentRef" + id).length > 0) {
                    $("#commentRef" + id).show();
                } else {
                    var $refComment = $("#comment" + id).clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentRef" + id);
                    $refComment.find("#replyForm, .reply").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentRef" + id).css("top", (position.top + 11) + "px");
            };

            (function () {
                page.load();
                page.replaceCommentsEm("#comments .comment-content");
            })();
        </script>
    </body>
</html>
