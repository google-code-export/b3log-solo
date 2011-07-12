<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${page.pageTitle} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        </@head>
    </head>
    <body>
        <#include "top-nav.ftl">
        <#include "side-tool.ftl">
        <div class="wrapper">
            <#include "header.ftl">
            <div>
                <div class="main">
                    <div class="main-content">
                        <div class="article">
                            <div class="article-body" style="margin-top: 0px;">
                                ${page.pageContent}
                            </div>
                        </div>
                        <h2 class="comment-label">${commentLabel}</h2>
                        <div id="comments" name="comments">
                            <#list pageComments as comment>
                            <div id="commentItem${comment.oId}"
                                 class="comment-body <#if comment_index % 2 == 0>comment-even<#else>comment-odd</#if>">
                                <div class="comment-panel">
                                    <div class="left comment-author">
                                        <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                    </div>
                                    <div class="left comment-info">
                                        <#if "http://" == comment.commentURL>
                                        <a name="${comment.oId}">${comment.commentName}</a>
                                        <#else>
                                        <a name="${comment.oId}" href="${comment.commentURL}"
                                           target="_blank">${comment.commentName}</a>
                                        </#if><#if comment.isReply>
                                        &nbsp;@&nbsp;<a
                                            href="http://${blogHost}${page.pagePermalink}#${comment.commentOriginalCommentId}"
                                            onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                            onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                        </#if>
                                        &nbsp;${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                        <div class="comment-content">
                                            ${comment.commentContent}
                                        </div>
                                        <div>
                                            <a href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                        </div>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                            </div>
                            </#list>
                        </div>
                        <table class="comment-form" cellpadding="0" cellspacing="0">
                            <tbody>
                                <tr>
                                    <th width="115px">
                                        ${commentNameLabel}
                                    </th>
                                    <td colspan="2">
                                        <input type="text" id="commentName"/>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${commentEmailLabel}
                                    </th>
                                    <td colspan="2">
                                        <input type="text" id="commentEmail"/>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${commentURLLabel}
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
                                        ${commentEmotionsLabel}
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
                                        ${commentContentLabel}
                                    </th>
                                    <td colspan="2">
                                        <textarea rows="10" id="comment"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${captchaLabel}
                                    </th>
                                    <td>
                                        <input type="text" id="commentValidate"/>
                                        <img id="captcha" alt="validate" src="/captcha.do"></img>
                                    </td>
                                    <th width="262px">
                                        <span class="right error-msg" id="commentErrorTip"/>
                                    </th>
                                </tr>
                                <tr>
                                    <td colspan="3" align="right">
                                        <button id="submitCommentButton" onclick="articleUtil.submitComment();">${submmitCommentLabel}</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="main-footer"></div>
                </div>
                <div class="side-navi">
                    <#include "side.ftl">
                </div>
                <div class="clear"></div>
                <div class="brush">
                    <div class="brush-icon"></div>
                    <div id="brush"></div>
                </div>
                <div class="footer">
                    <#include "footer.ftl">
                </div>
            </div>
        </div>
        <script type="text/javascript" src="/js/articleUtil.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript">
            var articleUtil = new Article({
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
                var oddEven = "";
                if ($("#comments div").first().hasClass("comment-even")) {
                    oddEven = "comment-odd";
                } else {
                    oddEven = "comment-even";
                }

                var commentHTML = '<div id="commentItem' + result.oId
                    + '" class="comment-body ' + oddEven + '"><div class="comment-panel"><div class="left comment-author">'
                    + '<img alt="' + $("#commentName" + state).val() + '" src="' + result.commentThumbnailURL
                    + '"/></div><div class="left comment-info">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + articleUtil.currentCommentId).find(".comment-info a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0]
                        + '#' + articleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + articleUtil.currentCommentId + '\');"'
                        + 'onmouseout="articleUtil.hideComment(\'' + articleUtil.currentCommentId + '\')">'
                        + commentOriginalCommentName + '</a>';
                }
                commentHTML += '&nbsp;' + result.commentDate + '<div class="comment-content">'
                    + articleUtil.replaceCommentsEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div><a href="javascript:replyTo(\''
                    + result.oId + '\');">${replyLabel}</a>'
                    +'</div></div><div class="clear"></div></div>';

                articleUtil.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table class='comment-form' id='replyForm' cellpadding='0' cellspacing='0'>\
                    <tr>\
                        <th width='100px'>${commentNameLabel}</th>\
                        <td colspan='2'>\
                            <input type='text' id='commentNameReply' value='" + Cookie.readCookie("commentName") + "'/>\
                        </td>\
                    </tr>\
                    <tr>\
                        <th>${commentEmailLabel}</th>\
                        <td colspan='2'>\
                            <input type='text' id='commentEmailReply' value='" + Cookie.readCookie("commentEmail") + "'/>\
                        </td>\
                    </tr>\
                    <tr>\
                        <th>${commentURLLabel}</th>\
                        <td colspan='2'>\
                            <div id='commentURLLabelReply'>http://</div>\
                            <input type='text' id='commentURLReply' value='" + Cookie.readCookie("commentURL") + "'/>\
                        </td>\
                    </tr>\
                    <tr>\
                        <th>${commentEmotionsLabel}</th>\
                        <td id='emotionsReply' colspan='2'>\
                            " + $("#emotions").html() + "\
                        </td>\
                    </tr>\
                    <tr>\
                        <th valign='top'>\
                            ${commentContentLabel}\
                        </th>\
                        <td colspan='2'>\
                            <textarea rows='10' cols='96' id='commentReply'></textarea>\
                        </td>\
                    </tr>\
                    <tr>\
                        <th>\
                            ${captchaLabel}\
                        </th>\
                        <td>\
                            <input type='text' class='normalInput' id='commentValidateReply'/>\
                            <img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img>\
                        </td>\
                        <th width='262px'>\
                            <span class='error-msg' id='commentErrorTipReply'/>\
                        </th>\
                    </tr>\
                    <tr>\
                        <td colspan='3' align='right'>\
                            <button id=\"submitCommentButtonReply\" onclick=\"articleUtil.submitComment('" + id + "', 'Reply');\">${submmitCommentLabel}</button>\
                        </td>\
                    </tr>\
                </table>";
                articleUtil.addReplyForm(id, commentFormHTML);
                if ($.browser.version !== "7.0") {
                    $("#commentURLReply").focus(function () {
                        $("#commentURLLabelReply").addClass("selected");
                    }).blur(function () {
                        $("#commentURLLabelReply").removeClass("selected");
                    });
                }
            }
            
            var showComment = function (it, id) {
                if ( $("#commentItemRef" + id).length > 0) {
                    $("#commentItemRef" + id).show();
                } else {
                    var $refComment = $("#commentItem" + id + " .comment-panel").clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                    $refComment.find(".comment-info div").last().remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentItemRef" + id).css({
                    "top": (position.top + 20) + "px",
                    "left": "180px"
                });
            }

            var loadAction = function () {
                if ($.browser.version !== "7.0") {
                    $("#commentURL").focus(function () {
                        $("#commentURLLabel").addClass("selected");
                    }).blur(function () {
                        $("#commentURLLabel").removeClass("selected");
                    });
                }

                // emotions
                util.replaceCommentsEm("#comments .comment-content");

                articleUtil.load();
            }
            loadAction();
        </script>
    </body>
</html>
