<#include "macro.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${article.articleTitle} - ${blogTitle}">
        <meta name="keywords" content="<#list article.articleTags?split(',') as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>"/>
        <meta name="description" content="${article.articleAbstract}"/>
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
            <div class="marginBottom40">
                <div class="article-header">
                    <div class="article-date">
                        <#if article.hasUpdated>
                        ${article.articleUpdateDate?string("yyyy-MM-dd HH")}
                        <#else>
                        ${article.articleCreateDate?string("yyyy-MM-dd HH")}
                        </#if>
                    </div>
                    <div class="arrow-right"></div>
                    <div class="clear"></div>
                    <ul>
                        <li>
                            <span class="left">
                                by&nbsp;
                            </span>
                            <a class="left" title="${article.authorName}" href="/authors/${article.authorId}}">
                                ${article.authorName}
                            </a>
                            <span class="clear"></span>
                        </li>
                        <li>
                            <a href="${article.articlePermalink}" title="${viewLabel}">
                                ${viewLabel} (${article.articleViewCount})
                            </a>
                        </li>
                        <li>
                            <a title="${commentLabel}" href="${article.articlePermalink}#comments">
                                ${commentLabel} (${article.articleCommentCount})
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="article-main article-detail-body">
                    <h2 class="title">
                        <a href="${article.articlePermalink}">${article.articleTitle}</a>
                        <#if article.hasUpdated>
                        <sup class="red">
                            ${updatedLabel}
                        </sup>
                        </#if>
                        <#if article.articlePutTop>
                        <sup class="red">
                            ${topArticleLabel}
                        </sup>
                        </#if>
                    </h2>
                    <div class="article-body">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <div class="tags">
                        <span class="tag-icon" title="${tagsLabel}"></span>
                        ${tags1Label}
                        <#list article.articleTags?split(",") as articleTag>
                        <a href="/tags/${articleTag?url('UTF-8')}">
                            ${articleTag}</a><#if articleTag_has_next>,</#if>
                        </#list>
                    </div>
                </div>
                <div class="clear"></div>
                <div class="article-detail-footer">
                    <#if nextArticlePermalink??>
                    <a href="${nextArticlePermalink}" class="left">${nextArticle1Label} ${nextArticleTitle}</a>
                    </#if>
                    <#if previousArticlePermalink??>
                    <a href="${previousArticlePermalink}" class="right">${previousArticle1Label} ${previousArticleTitle}</a>
                    </#if>
                    <div class="clear"></div>
                    <div id="randomArticles" class="left article-relative"></div>
                    <#if 0 != relevantArticles?size>
                    <div class="article-relative right" style="width: 48%;">
                        <h5>${relevantArticles1Label}</h5>
                        <ul class="marginLeft12">
                            <#list relevantArticles as relevantArticle>
                            <li>
                                <a href="${relevantArticle.articlePermalink}">
                                    ${relevantArticle.articleTitle}
                                </a>
                            </li>
                            </#list>
                        </ul>
                    </div>
                    </#if>
                    <div class="clear"></div>
                    <div id="externalRelevantArticles" class="article-relative"></div>
                </div>
            </div>
            <div id="comments">
                <#list articleComments as comment>
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
                            <a href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                               onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                               onmouseout="article.hideComment('${comment.commentOriginalCommentId}')">
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
                        <input id="submitCommentButton" type="button" onclick="article.submitComment();" value="${submmitCommentLabel}"/>
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
        <script type="text/javascript" src="/js/article.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript">
            var article = new Article({
                "nameTooLongLabel": "${nameTooLongLabel}",
                "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
                "mailInvalidLabel": "${mailInvalidLabel}",
                "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
                "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
                "randomArticles1Label": "${randomArticles1Label}",
                "captchaErrorLabel": "${captchaErrorLabel}",
                "loadingLabel": "${loadingLabel}",
                "oId": "${article.oId}",
                "blogHost": "${blogHost}",
                "externalRelevantArticlesDisplayCount": "${externalRelevantArticlesDisplayCount}",
                "externalRelevantArticles1Label": "${externalRelevantArticles1Label}",
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
                    var commentOriginalCommentName = $("#" + article.currentCommentId + " .comment-top a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + article.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + article.currentCommentId + '\');"'
                        + ' onmouseout="article.hideComment(\'' + article.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '&nbsp;' + result.commentDate
                    + '</div><div class="comment-content">' 
                    + article.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div class="clear"></div><div class="reply"><a href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div></div><div class="clear"></div></div>';

                article.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table width='100%' cellspacing='0' cellpadding='0' class='comment' id='replyForm'>";
                article.addReplyForm(id, commentFormHTML);
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
                // emotions
                article.replaceCommentsEm("#comments .comment-content");

                article.load();
                article.loadRandomArticles();
                    <#if 0 != externalRelevantArticlesDisplayCount>
                    article.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
                    </#if>
                })();
        </script>
    </body>
</html>
