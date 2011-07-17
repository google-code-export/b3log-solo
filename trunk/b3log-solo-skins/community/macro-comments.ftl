<#macro comments commentList permalink>
<div id="comments">
    <#list commentList as comment>
    <div id="${comment.oId}">
        <img class="left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
        <div class="comment-panel left">
            <div class="comment-top">
                <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
                <#else>
                <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
                <#if comment.isReply>
                @
                <a href="${permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">
                    ${comment.commentOriginalCommentName}</a>
                </#if>
                ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
            </div>
            <div class="comment-content">
                ${comment.commentContent}
            </div>
            <div class="clear"></div>
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
                <span class="comment-label">
                    ${commentNameLabel}
                </span>
                <span class="arrow-right"></span>
            </th>
            <td colspan="2">
                <input type="text" id="commentName"/>
            </td>
        </tr>
        <tr>
            <th>
                <span class="comment-label">
                    ${commentEmailLabel}
                </span>
                <span class="arrow-right"></span>
            </th>
            <td colspan="2">
                <input type="text" id="commentEmail"/>
            </td>
        </tr>
        <tr>
            <th>
                <span class="comment-label">
                    ${commentURLLabel}
                </span>
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
                <span class="comment-label">
                    ${commentEmotionsLabel}
                </span>
                <span class="arrow-right"></span>
            </th>
            <td id="emotions" colspan="2">
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
                <span class="comment-label">
                    ${commentContentLabel}
                </span>
                <span class="arrow-right"></span>
            </th>
            <td colspan="2">
                <textarea rows="10" id="comment"></textarea>
            </td>
        </tr>
        <tr>
            <th>
                <span class="comment-label">
                    ${captchaLabel}
                </span>
                <span class="arrow-right"></span>
            </th>
            <td>
                <input type="text" id="commentValidate"/>
                <img id="captcha" alt="validate" src="/captcha.do"></img>
            </td>
            <th>
                <span class="right error-msg" id="commentErrorTip"></span>
            </th>
        </tr>
        <tr>
            <td colspan="3">
                <input id="submitCommentButton" type="button" onclick="page.submitComment();" value="${submmitCommentLabel}"/>
            </td>
        </tr>
    </tbody>
</table>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
<script type="text/javascript" src="/js/page.js"></script>
<script type="text/javascript">
    var page = new Page({
        "nameTooLongLabel": "${nameTooLongLabel}",
        "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
        "mailInvalidLabel": "${mailInvalidLabel}",
        "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
        "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
        "captchaErrorLabel": "${captchaErrorLabel}",
        "loadingLabel": "${loadingLabel}",
        "oId": "${oId}",
        "skinDirName": "${skinDirName}",
        "blogHost": "${blogHost}",
        "randomArticles1Label": "${randomArticles1Label}",
        "externalRelevantArticles1Label": "${externalRelevantArticles1Label}"
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
            + '</div><div class="comment-content">' + Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div><div class="clear"></div><div class="reply"><a href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
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
            var $refComment = $("#" + id).clone();
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
            <#nested>
        })();
</script>
</#macro>