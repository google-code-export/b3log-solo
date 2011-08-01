<#macro comments commentList permalink>
<div class="comments-header"></div>
<div class="comments marginTop12" id="comments">
    <#list commentList as comment>
    <div id="${comment.oId}" class="comment">
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
                        href="${permalink}#${comment.commentOriginalCommentId}"
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
</div>
<div class="comments">
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
                        <input type="text" class="normalInput" id="commentValidate"/>
                        <img id="captcha" alt="validate" src="/captcha.do"></img>
                    </td>
                    <td>
                        <span class="error-msg" id="commentErrorTip"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" align="right">
                        <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="comment-bottom"></div>
</div>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="/js/page.js"></script>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
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
        var commentHTML = '<div id="' + result.oId + '" class="comment"><div class="comment-panel">'
            + '<div class="comment-top"></div><div class="comment-body"><div class="comment-title">';

        if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
            commentHTML += '<a class="left">' + $("#commentName" + state).val() + '</a>';
        } else {
            commentHTML += '<a href="http://' + $("#commentURL" + state).val() + 
                ' target="_blank" class="left">' + $("#commentName" + state).val() + '</a>';
        }

        if (state !== "") {
            var commentOriginalCommentName = $("#" + page.currentCommentId).find(".comment-title a").first().text();
            commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                + 'onmouseover="showComment(this, \'' + page.currentCommentId + '\');"'
                + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
        }

        commentHTML += '<div class="right">' + result.commentDate
            + '&nbsp;<a class="no-underline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
            + '</div><div class="clear"></div></div><div><img alt="' + $("#commentName" + state).val()
            + '" src="' + result.commentThumbnailURL + '" class="comment-picture left"/>'
            + '<div class="comment-content">'
            + Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div>'
            + ' <div class="clear"></div></div></div><div class="comment-bottom"></div></div></div>';

        page.addCommentAjax(commentHTML, state);
    }

    var replyTo = function (id) {
        var commentFormHTML = "<div id='replyForm'><div class='comment-top'></div>"
            + "<div class='comment-body'><table class='form comment-reply'>";
                
        page.addReplyForm(id, commentFormHTML, "</div><div class='comment-bottom'></div></div>");
        
        // reply comment url
        $("#commentURLReply").focus(function (event) {
            $("#commentURLLabelReply").css("box-shadow", "0 1px 2px rgba(0, 0, 0, 0.3) inset");
        }).blur(function () {
            $("#commentURLLabelReply").css("box-shadow", "");
        });
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
        $("#commentRef" + id).css("top", (position.top + 15) + "px");
    };

    (function () {
        page.load();
        // comment url
        $("#commentURL").focus(function (event) {
            $("#commentURLLabel").css("box-shadow", "0 1px 2px rgba(0, 0, 0, 0.3) inset");
        }).blur(function () {
            $("#commentURLLabel").css("box-shadow", "");
        });
        // emotions
        page.replaceCommentsEm("#comments .comment-content");
            <#nested>
        })();
</script>
</#macro>