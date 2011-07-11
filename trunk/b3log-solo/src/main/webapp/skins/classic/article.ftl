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
        <div class="content">
            <div class="header">
                <#include "header.ftl">
            </div>
            <div class="body">
                <div class="left main">
                    <div>
                        <div class="article">
                            <div class="article-header">
                                <div class="article-date">
                                    <#if article.hasUpdated>
                                    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    </#if>
                                </div>
                                <div class="article-title">
                                    <h2>
                                        <a class="no-underline" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                                    <div class="article-tags">
                                        ${tags1Label}
                                        <#list article.articleTags?split(",") as articleTag>
                                        <span>
                                            <a href="/tags/${articleTag?url('UTF-8')}">
                                                ${articleTag}</a><#if articleTag_has_next>,</#if>
                                        </span>
                                        </#list>
                                        by 
                                        <a href="/authors/${article.authorId}">
                                            ${article.authorName}
                                        </a>
                                    </div>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="article-body">
                                ${article.articleContent}
                                <#if "" != article.articleSign.signHTML?trim>
                                <div class="marginTop12">
                                    ${article.articleSign.signHTML}
                                </div>
                                </#if>
                            </div>
                            <div class="article-details-footer">
                                <div class="left">
                                    <#if nextArticlePermalink??>
                                    <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a><br/>
                                    </#if>
                                    <#if previousArticlePermalink??>
                                    <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                    </#if>
                                </div>
                                <div class="right">
                                    <span class="article-create-date left">
                                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                                    </span>
                                    <a href="${article.articlePermalink}#comments" class="left">
                                        <span class="left commentIcon" title="${commentLabel}"></span>
                                        <span class="left">${article.articleCommentCount}</span>&nbsp;&nbsp;
                                    </a>
                                    <a href="${article.articlePermalink}" class="left">
                                        <span class="left browserIcon" title="${viewLabel}"></span>
                                        <span id="articleViewCount">${article.articleViewCount}</span>
                                    </a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <#if 0 != relevantArticles?size>
                            <div class="article-relative left" style="width: 50%;">
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
                            <div id="randomArticles" class="left article-relative"></div>
                            <div class="clear"></div>
                            <div id="externalRelevantArticles" class="article-relative"></div>
                        </div>
                        <h2 class="marginLeft12 marginBottom12">${commentLabel}</h2>
                        <div class="comments" id="comments">
                            <#if 0 == articleComments?size>
                            ${noCommentLabel}
                            </#if>
                            <#list articleComments as comment>
                            <div id="${comment.oId}">
                                <div class="comment-panel">
                                    <div class="comment-title">
                                        <div class="left">
                                            <#if "http://" == comment.commentURL>
                                            <a>${comment.commentName}</a>
                                            <#else>
                                            <a href="${comment.commentURL}"
                                               target="_blank">${comment.commentName}</a>
                                            </#if>
                                            <#if comment.isReply>
                                            @
                                            <a href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                                                onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                                onmouseout="article.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                            </#if>
                                        </div>
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
                        </div>
                        <div class="comments">
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
                                                <textarea rows="10" id="comment"></textarea>
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
                                                <button id="submitCommentButton" onclick="article.submitComment();">${submmitCommentLabel}</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="clear"></div>
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
                var commentHTML = '<div id="' + result.oId + '"><div class="comment-panel"><div class="comment-title"><div class="left">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a>' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#" + article.currentCommentId).find(".comment-title a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + article.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + article.currentCommentId + '\');"'
                        + 'onmouseout="article.hideComment(\'' + article.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '</div><div class="right">' + result.commentDate
                    + '&nbsp;<a class="no-underline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="clear"></div></div><div class="comment-body">'
                    + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
                    + '" src="' + result.commentThumbnailURL + '"/>'
                    + '</div><div class="comment-content">' 
                    + article.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div class="clear"></div>'
                    + '</div></div></div>';

                article.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {                
                var commentFormHTML = "<table class='form comment-reply' id='replyForm'>";
                article.addReplyForm(id, commentFormHTML);
                
                // reply comment url
                $("#commentURLReply").focus(function (event) {
                    $("#commentURLLabelReply").css({"border":"2px solid #73A6FF","border-right":"0px"});
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
                var position =  $(it).position();
                $("#commentRef" + id).css("top", (position.top + 23) + "px");
            };

            (function () {
                // comment url
                $("#commentURL").focus(function (event) {
                    $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                });

                // emotions
                article.replaceCommentsEm("#comments .comment-content");

                article.load();
                article.loadRandomArticles();

                // externalRelevantArticles
                    <#if 0 != externalRelevantArticlesDisplayCount>
                    article.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
                    </#if>
                })();
        </script>
    </body>
</html>
