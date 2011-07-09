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
                                    <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                    </#if>
                                    <#if previousArticlePermalink??>
                                    <br/>
                                    <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                    </#if>
                                </div>
                                <div class="right">
                                    <span class="article-create-date left">
                                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;
                                    </span>
                                    <span class="left commentIcon" title="${commentLabel}"></span>
                                    <span class="left">
                                        &nbsp;${article.articleCommentCount}&nbsp;&nbsp;
                                    </span>
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
                            <div id="randomArticles" class="left"></div>
                            <div class="clear"></div>
                            <div id="externalRelevantArticles"></div>
                        </div>
                        <div class="comments" id="comments" name="comments">
                            <#list articleComments as comment>
                            <div id="commentItem${comment.oId}">
                                <div class="comment-panel">
                                    <div class="comment-title">
                                        <div class="left">
                                            <#if "http://" == comment.commentURL>
                                            <a name="${comment.oId}">${comment.commentName}</a>
                                            <#else>
                                            <a name="${comment.oId}" href="${comment.commentURL}"
                                               target="_blank">${comment.commentName}</a>
                                            </#if>
                                            <#if comment.isReply>
                                            &nbsp;@&nbsp;<a
                                                href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
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
        <script type="text/javascript" src="/js/${miniDir}article${miniPostfix}.js"></script>
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
                var commentHTML = '<div id="commentItem' + result.oId + '"><div class="comment-panel"><div class="comment-title"><div class="left">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + article.currentCommentId).find(".comment-title a").first().text();
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
                var commentFormHTML = "<table class='form comment-reply' id='replyForm'><tbody><tr><th>${commentName1Label}"
                    + "</th><td colspan='2'><input type='text' class='normalInput' id='commentNameReply' value='" + Cookie.readCookie("commentName") + "'/>"
                    + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                    + "<input type='text' class='normalInput' id='commentEmailReply' value='" + Cookie.readCookie("commentEmail") + "'/></td></tr><tr>"
                    + "<th>${commentURL1Label}</th><td colspan='2'><div id='commentURLLabelReply'>"
                    + "http://</div><input type='text' id='commentURLReply' value='" + Cookie.readCookie("commentURL") + "'/>"
                    + "</td></tr><tr><th>${commentEmotions1Label}</th><td id='emotionsReply'>" + $("#emotions").html()
                    + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                    + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                    + "<th>${captcha1Label}</th><td><input type='text' class='normalInput' id='commentValidateReply'/>"
                    + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                    + "<span class='error-msg' id='commentErrorTipReply'/>"
                    + "</th></tr><tr><td colspan='3' align='right'>"
                    + "<button id=\"submitCommentButtonReply\" onclick=\"article.submitComment('" + id + "', 'Reply');\">${submmitCommentLabel}</button>"
                    + "</td></tr></tbody></table>";
                article.addReplyForm(id, commentFormHTML);

                // reply comment url
                $("#commentURLReply").focus(function (event) {
                    if ($.browser.version !== "7.0") {
                        $("#commentURLLabelReply").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }
                }).blur(function () {
                    $("#commentURLLabelReply").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#commentReply").width() - $("#commentURLLabelReply").width());
            }

            var showComment = function (it, id) {
                if ( $("#commentItemRef" + id).length > 0) {
                    $("#commentItemRef" + id).show();
                } else {
                    var $refComment = $("#commentItem" + id + " .comment-panel").clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                    $refComment.find(".comment-title .right a").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentItemRef" + id).css({
                    "top": (position.top + 23) + "px",
                    "left": "88px"
                });
            }

            (function () {
                // comment url
                $("#commentURL").focus(function (event) {
                    if ($.browser.version !== "7.0") {
                        $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#comment").width() - $("#commentURLLabel").width());

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
