<div class="comments" id="comments" name="comments">
    <#list articleComments as comment>
    <div id="commentItem${comment.oId}" class="comment-body">
        <div class="comment-panel">
            <div class="left comment-author">
                <div>
                    <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                </div>
                <#if "http://" == comment.commentURL>
                <a name="${comment.oId}" class="left">${comment.commentName}</a>
                <#else>
                <a name="${comment.oId}" href="${comment.commentURL}"
                   target="_blank">${comment.commentName}</a>
                </#if>
            </div>
            <div class="left comment-info">
                <div class="left">
                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                    <#if comment.isReply>
                    &nbsp;@&nbsp;<a
                        href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                        onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                        onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                    </#if>
                </div>
                <div class="right">
                    <a class="no-underline"
                       href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                </div>
                <div class="clear">
                </div>
                <div class="comment-content">
                    ${comment.commentContent}
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    </#list>
</div>
<table class="comment-form">
    <tbody>
        <tr>
            <td width="208px">
                <input class="normalInput" id="commentName"/>
            </td>
            <td colspan="2" width="400px">
                ${commentNameLabel}
            </td>
        </tr>
        <tr>
            <td>
                <input class="normalInput" id="commentEmail"/>
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
                <input id="commentURL"/>
            </td>
            <td colspan="2">
                ${commentURLLabel}
            </td>
        </tr>
        <tr>
            <td id="emotions" colspan="3">
                <#include "phiz.ftl">
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <textarea rows="10" cols="96" id="comment"></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input class="normalInput" id="commentValidate"/>
            </td>
            <td>
                <img id="captcha" alt="validate" src="/captcha.do"></img>
            </td>
            <th align="right">
                <span class="error-msg" id="commentErrorTip"/>
            </th>
        </tr>
        <tr>
            <td colspan="3" align="right">
                <button onclick="articleUtil.submitComment();">${submmitCommentLabel}</button>
            </td>
        </tr>
    </tbody>
</table>