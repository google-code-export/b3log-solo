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
        <div class="wrapper">
            <div class="wrap header">
                <#include "header.ftl">
            </div>
            <div class="wrap">
                <div class="left main">
                    <div class="article-header">
                        <h2>
                            <a href="${article.articlePermalink}">
                                ${article.articleTitle}
                            </a>
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
                        <div>
                            ${createDateLabel}:
                            <a href="${article.articlePermalink}">
                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                            </a>
                            by
                            <a href="/authors/${article.authorId}">
                                ${article.authorName}
                            </a>
                            -
                            <a href="${article.articlePermalink}#comments">
                                ${article.articleCommentCount} ${commentLabel}
                            </a>
                        </div>
                    </div>
                    <div class="article-body">
                        ${article.articleContent}
                        <#if "" != article.articleSign.signHTML?trim>
                        <div class="marginTop12 right">
                            ${article.articleSign.signHTML}
                        </div>
                        </#if>
                    </div>
                    <div>
                        ${tags1Label}<#list article.articleTags?split(",") as articleTag><span><a href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></span></#list>
                        &nbsp;&nbsp;${viewCount1Label}
                        <a href="${article.articlePermalink}">
                            ${article.articleViewCount}  
                        </a>
                    </div>
                    <div class="marginTop12 marginBottom12 right">
                        <#if nextArticlePermalink??>
                        ${nextArticle1Label}<a href="${nextArticlePermalink}">${nextArticleTitle}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </#if>
                        <#if previousArticlePermalink??>
                        ${previousArticle1Label}<a href="${previousArticlePermalink}">${previousArticleTitle}</a>
                        </#if>
                    </div>
                    <div class="clear"></div>
                    <#if 0 != relevantArticles?size>
                    <div class="left" style="width: 50%;">
                        <h4>${relevantArticles1Label}</h4>
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
                    <div id="externalRelevantArticles" class="marginTop12"></div>
                    <h2 class="comments-title">${commentLabel}</h2>
                    <div class="comments" id="comments">
                        <#if 0 == articleComments?size>
                            ${noCommentLabel}
                        </#if>
                        <#list articleComments as comment>
                        <div id="${comment.oId}">
                            <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                            <div class="panel">
                                <div>
                                    <#if "http://" == comment.commentURL>
                                    <a><b>${comment.commentName}</b></a>
                                    <#else>
                                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                    </#if>
                                    <#if comment.isReply>
                                    @&nbsp;<a
                                        href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                                        onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                        onmouseout="article.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                    </#if> </br>
                                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <a href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                </div>
                                <div class="marginTop12">
                                    ${comment.commentContent}   
                                </div>          
                                <div class="clear"></div>
                            </div>
                        </div>
                        </#list>
                    </div>
                    <div class="post-comment">
                        <h3>${postCommentsLabel}</h3>
                        <table class="marginLeft12 reply">
                            <tbody>
                                <tr>
                                    <th width="65">
                                        ${commentName1Label}
                                    </th>
                                    <td colspan="2">
                                        <div class="input-reply">
                                            <div class="top"></div>
                                            <div class="bg">
                                                <span class="ico-name"></span>
                                                <input type="text" id="commentName"/>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="bottom"></div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${commentEmail1Label}
                                    </th>
                                    <td colspan="2">
                                        <div class="input-reply">
                                            <div class="top"></div>
                                            <div class="bg">
                                                <span class="ico-email"></span>
                                                <input type="text" id="commentEmail"/>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="bottom"></div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${commentURL1Label}
                                    </th>
                                    <td colspan="2">
                                        <div class="input-reply">
                                            <div class="top"></div>
                                            <div class="bg">
                                                <div id="commentURLLabel">
                                                    http://
                                                </div>
                                                <input type="text" id="commentURL"/>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="bottom"></div>
                                        </div>
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
                                        <div class="input-reply">
                                            <div class="top"></div>
                                            <div class="bg">
                                                <span class="ico-message"></span>
                                                <textarea rows="10" id="comment"></textarea>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="bottom"></div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        ${captcha1Label}
                                    </th>
                                    <td>
                                        <div class="input-reply">
                                            <div class="top"></div>
                                            <div class="bg">
                                                <img id="captcha" alt="validate" src="/captcha.do"></img>
                                                <input type="text" class="normalInput" id="commentValidate"/>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="bottom"></div>
                                        </div>
                                    </td>
                                    <th>
                                        <span class="error-msg" id="commentErrorTip"/>
                                    </th>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right">
                                        <button id="submitCommentButton" onclick="article.submitComment();">${submmitCommentLabel}</button>
                                    </td>
                                    <td></td>
                                </tr>
                            </tbody>
                        </table>
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
                var commentHTML = '<div id="' + result.oId + '"><img alt="' + 
                    $("#commentName" + state).val() + '" src="' + result.commentThumbnailURL +
                    '"/><div class="panel"><div>';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a>' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() +
                        '" target="_blank">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#" + article.currentCommentId).find("a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + article.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + article.currentCommentId + '\');"'
                        + 'onmouseout="article.hideComment(\'' + article.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '</br>' + result.commentDate
                    + '&nbsp;<a href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="marginTop12">' 
                    + article.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
                    + '</div><div class="clear"></div></div></div>';

                article.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table id='replyForm' class='reply marginLeft12 marginBotton12'>";
                article.addReplyForm(id, commentFormHTML);
            }

            var showComment = function (it, id) {
                if ( $("#commentRef" + id).length > 0) {
                    $("#commentRef" + id).show();
                } else {
                    var $refComment = $("#" + id).clone();
                    $refComment.attr("id", "commentRef" + id).addClass("ref");
                    $refComment.find("img").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentRef" + id).css("top", (position.top + 18) + "px");
            };

            (function () {
                // emotions
                article.replaceCommentsEm("#comments .marginTop12");

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
