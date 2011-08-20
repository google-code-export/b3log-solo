<#macro comments commentList permalink>
<h2 class="comment-label">${commentLabel}</h2>
<div id="comments">
    <#list commentList as comment>
    <div id="${comment.oId}"
         class="comment-body <#if comment_index % 2 == 0>comment-even<#else>comment-odd</#if>">
        <div class="comment-panel">
            <div class="left comment-author">
                <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
            </div>
            <div class="left comment-info">
                <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
                <#else>
                <a href="${comment.commentURL}"
                   target="_blank">${comment.commentName}</a>
                </#if><#if comment.isReply>
                @
                <a href="${permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
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
                <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="/js/page${miniPostfix}.js"></script>
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
        var oddEven = "";
        if ($("#comments div").first().hasClass("comment-even")) {
            oddEven = "comment-odd";
        } else {
            oddEven = "comment-even";
        }

        var commentHTML = '<div id="' + result.oId
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
            var commentOriginalCommentName = $("#" + page.currentCommentId).find(".comment-info a").first().text();
            commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0]
                + '#' + page.currentCommentId + '"'
                + 'onmouseover="showComment(this, \'' + page.currentCommentId + '\');"'
                + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">'
                + commentOriginalCommentName + '</a>';
        }
        commentHTML += '&nbsp;' + result.commentDate + '<div class="comment-content">'
            + Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div><div><a href="javascript:replyTo(\''
            + result.oId + '\');">${replyLabel}</a>'
            +'</div></div><div class="clear"></div></div>';

        page.addCommentAjax(commentHTML, state);
    }

    var replyTo = function (id) {
        var commentFormHTML = "<table class='comment-form' id='replyForm' cellpadding='0' cellspacing='0'>";
                
        page.addReplyForm(id, commentFormHTML);
        $("#commentURLReply").focus(function () {
            $("#commentURLLabelReply").addClass("selected");
        }).blur(function () {
            $("#commentURLLabelReply").removeClass("selected");
        });
    }
            
    var showComment = function (it, id) {
        if ( $("#commentRef" + id).length > 0) {
            $("#commentRef" + id).show();
        } else {
            var $refComment = $("#" + id + " .comment-panel").clone();
            $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentRef" + id);
            $refComment.find(".comment-info div").last().remove();
            $("#comments").append($refComment);
        }
        var position =  $(it).position();
        $("#commentRef" + id).css("top", (position.top + 20) + "px");
    };

    (function () {
        page.load();
        // comment url
        $("#commentURL").focus(function () {
            $("#commentURLLabel").addClass("selected");
        }).blur(function () {
            $("#commentURLLabel").removeClass("selected");
        });
        // emotions
        page.replaceCommentsEm("#comments .comment-content");
            <#nested>
        })();
</script>
</#macro>