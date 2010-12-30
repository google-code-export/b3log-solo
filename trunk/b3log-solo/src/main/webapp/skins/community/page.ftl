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
        <link type="text/css" rel="stylesheet" href="/skins/community/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="header">
            <#include "article-header.ftl">
        </div>
        <div class="content">
            <div class="article-body marginBottom40">
                ${page.pageContent}
            </div>
            <div class="comments" id="comments" name="comments">
                <#list pageComments as comment>
                <div id="commentItem${comment.oId}">
                    <img class="left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                    <div class="comment-panel left">
                        <div class="comment-top">
                            <#if "http://" == comment.commentURL>
                            <a name="${comment.oId}" class="left">${comment.commentName}</a>&nbsp;
                            <#else>
                            <a name="${comment.oId}" href="${comment.commentURL}"
                               target="_blank" class="left">${comment.commentName}</a>&nbsp;
                            </#if>
                            <#if comment.isReply>
                            @
                            <a href="http://${blogHost}${page.pagePermalink}#${comment.commentOriginalCommentId}"
                               onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                               onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">
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
                            <img class="[em00]" src="/skins/community/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                            <img class="[em01]" src="/skins/community/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                            <img class="[em02]" src="/skins/community/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                            <img class="[em03]" src="/skins/community/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                            <img class="[em04]" src="/skins/community/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                            <img class="[em05]" src="/skins/community/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                            <img class="[em06]" src="/skins/community/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                            <img class="[em07]" src="/skins/community/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                            <img class="[em08]" src="/skins/community/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                            <img class="[em09]" src="/skins/community/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                            <img class="[em10]" src="/skins/community/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                            <img class="[em11]" src="/skins/community/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                            <img class="[em12]" src="/skins/community/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                            <img class="[em13]" src="/skins/community/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                            <img class="[em14]" src="/skins/community/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
                            <input type="button" onclick="articleUtil.submitComment();" value="${submmitCommentLabel}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div>
            <#include "article-side.ftl">
        </div>
        <div class="footer">
            <#include "article-footer.ftl">
        </div>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript" src="/js/articleUtil.js"></script>
        <script type="text/javascript">
            var articleUtil = new ArticleUtil({
                "nameTooLongLabel": "${nameTooLongLabel}",
                "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
                "mailInvalidLabel": "${mailInvalidLabel}",
                "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
                "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                "captchaErrorLabel": "${captchaErrorLabel}",
                "loadingLabel": "${loadingLabel}",
                "oId": "${page.oId}",
                "blogHost": "${blogHost}"
            });

            var addComment = function (result, state) {
                var commentHTML = '<div id="commentItem' + result.oId + '">'
                    + '<img class="left" alt="' + $("#commentName" + state).val() + '" src="' + result.commentThumbnailURL
                    + '"/><div class="comment-panel left"><div class="comment-top">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + articleUtil.currentCommentId + " .comment-top a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + articleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + articleUtil.currentCommentId + '\');"'
                        + 'onmouseout="articleUtil.hideComment(\'' + articleUtil.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '&nbsp;' + articleUtil.getDate(result.commentDate.time, 'yyyy-mm-dd hh:mm:ss')
                    + '</div><div class="comment-content">' + articleUtil.replaceEmotions($("#comment" + state).val(), "community")
                    + '</div><div class="reply"><a href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div></div><div class="clear"></div></div>';

                articleUtil.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table width='100%' cellspacing='0' cellpadding='0' class='comment' id='replyForm'><tbody>"
                    + "<tr><th width='200px'><div>${commentNameLabel}</div><span class='arrow-right'></span></th>"
                    + "<td colspan='2'><input type='text' id='commentNameReply' value='" + Cookie.readCookie("commentName") + "'/></td></tr>"
                    + "<tr><th><div>${commentEmailLabel}</div><span class='arrow-right'></span></th>"
                    + "<td colspan='2'><input type='text' id='commentEmailReply' value='" + Cookie.readCookie("commentEmail") + "'/></td></tr>"
                    + "<tr><th><div>${commentURL1Label}</div><span class='arrow-right'></span></th>"
                    + "<td colspan='2'><div id='commentURLLabelReply'>http://</div><input type='text' id='commentURLReply' value='" + Cookie.readCookie("commentURL") + "'/></td></tr>"
                    + "<tr><th><div>${commentEmotionsLabel}</div><span class='arrow-right'></span></th>"
                    + "<td id='emotionsReply'>" + $("#emotions").html() + "</td></tr>"
                    + "<tr><th valign='top'><div>${commentContentLabel}</div><span class='arrow-right'></span></th>"
                    + "<td colspan='2'><textarea rows='10' cols='96' id='commentReply'></textarea></td></tr>"
                    + "<tr><th><div>${captchaLabel}</div><span class='arrow-right'></span></th>"
                    + "<td><input type='text' id='commentValidateReply'/><img id='captchaReply' alt='validate' src='/captcha.do?"
                    + new Date().getTime() + "'></img></td><th><span class='error-msg right' id='commentErrorTipReply'/></th></tr>"
                    + "<tr><td colspan='3'><input type='button' onclick=\"articleUtil.submitComment('" + id + "', 'Reply');\" value='${submmitCommentLabel}'/>"
                    + "</td></tr></tbody></table>";
                articleUtil.addReplyForm(id, commentFormHTML);
            }

            var showComment = function (it, id) {
                if ( $("#commentItemRef" + id).length > 0) {
                    $("#commentItemRef" + id).show();
                } else {
                    var $refComment = $("#commentItem" + id).clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                    $refComment.find(".reply").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentItemRef" + id).css({
                    "top": (position.top + 16) + "px",
                    "left": "177px"
                });
            }

            var loadAction = function () {
                articleUtil.load();

                // emotions
                util.replaceCommentsEm("#comments .comment-content", "community");
            }
            loadAction();
        </script>
    </body>
</html>
