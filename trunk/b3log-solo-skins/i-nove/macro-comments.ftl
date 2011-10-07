<#macro comments commentList permalink>
<div class="comments" id="comments">
    <#list commentList as comment>
    <div id="${comment.oId}" class="comment-body">
        <div class="comment-panel">
            <div class="left comment-author">
                <div>
                    <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                </div>
                <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
                <#else>
                <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
            </div>
            <div class="left comment-info">
                <div class="left">
                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                    <#if comment.isReply>
                    @
                    <a href="${permalink}#${comment.commentOriginalCommentId}"
                       onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                       onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                    </#if>
                </div>
                <div class="right">
                    <a class="no-underline" href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                </div>
                <div class="clear"></div>
                <div class="comment-content">
                    ${comment.commentContent}
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    </#list>
</div>
<table id="commentForm" class="comment-form">
    <tbody>
        <tr>
            <td width="208px">
                <input type="text" class="normalInput" id="commentName"/>
            </td>
            <td colspan="2" width="400px">
                ${commentNameLabel}
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" class="normalInput" id="commentEmail"/>
            </td>
            <td colspan="2">
                ${commentEmailLabel}
            </td>
        </tr>
        <tr>
            <td>
                <div id="commentURLLabel">
                    http://
                </div>
                <input type="text" id="commentURL"/>
            </td>
            <td colspan="2">
                ${commentURLLabel}
            </td>
        </tr>
        <tr>
            <td id="emotions" colspan="3">
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
            <td colspan="3">
                <textarea rows="10" cols="96" id="comment"></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" class="normalInput" id="commentValidate"/>
            </td>
            <td>
                <img id="captcha" alt="validate" src="/captcha.do" />
            </td>
            <th align="right">
                <span class="error-msg" id="commentErrorTip"></span>
            </th>
        </tr>
        <tr>
            <td colspan="3" align="right">
                <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="/js/page${miniPostfix}.js" charset="utf-8"></script>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js" charset="utf-8"></script>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js" charset="utf-8"></script>
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
        var commentHTML = '<div id="' + result.oId
            + '" class="comment-body"><div class="comment-panel"><div class="left comment-author">'
            + '<div><img alt="' + $("#commentName" + state).val() + '" src="' +
            result.commentThumbnailURL + '"/></div>' + result.replyNameHTML;

        commentHTML += '</div><div class="left comment-info"><div class="left">' + result.commentDate;
        if (state !== "") {
            var commentOriginalCommentName = $("#" + page.currentCommentId).find(".comment-author a").text();
            commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                + 'onmouseover="showComment(this, \'' + page.currentCommentId + '\');"'
                + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
        }
        commentHTML += '</div><div class="right"> <a class="no-underline" href="javascript:replyTo(\''
            + result.oId + '\');">${replyLabel}</a>'
            +'</div><div class="clear"></div><div class="comment-content">'
            + Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div></div><div class="clear"></div></div></div>';

        $("#comments").addClass("comments");
        return commentHTML;
    }

    var replyTo = function (id) {
        var commentFormHTML = "<table class='marginTop12 comment-form' id='replyForm'>";
        page.addReplyForm(id, commentFormHTML);
    }

    var showComment = function (it, id) {
        if ( $("#commentRef" + id).length > 0) {
            $("#commentRef" + id).show();
        } else {
            var $refComment = $("#" + id + " .comment-panel").clone();
            $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentRef" + id);
            $("#comments").append($refComment);
        }
        var position =  $(it).position();
        $("#commentRef" + id).css("top", (position.top + 18) + "px");
    };

    (function () {
        page.load();
        // emotions
        page.replaceCommentsEm("#comments .comment-content");
      
        if ($("#comments div").length === 0) {
            $("#comments").removeClass("comments");
        }
        <#nested>
    })();
</script>
</#macro>