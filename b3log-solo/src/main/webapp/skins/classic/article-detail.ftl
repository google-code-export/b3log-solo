<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${article.articleTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="<#list article.articleTags?split(',') as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>"/>
        <meta name="description" content="${article.articleAbstract}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log,${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="content">
            <div class="header">
                <#include "article-header.ftl">
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
                                                onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
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
                                                <input class="normalInput" id="commentName"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                ${commentEmail1Label}
                                            </th>
                                            <td colspan="2">
                                                <input class="normalInput" id="commentEmail"/>
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
                                                <input id="commentURL"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>
                                                ${commentEmotions1Label}
                                            </th>
                                            <td id="emotions">
                                                <img class="[em00]" src="/skins/${skinDirName}/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                                                <img class="[em01]" src="/skins/${skinDirName}/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                                                <img class="[em02]" src="/skins/${skinDirName}/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                                                <img class="[em03]" src="/skins/${skinDirName}/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                                                <img class="[em04]" src="/skins/${skinDirName}/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                                                <img class="[em05]" src="/skins/${skinDirName}/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                                                <img class="[em06]" src="/skins/${skinDirName}/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                                                <img class="[em07]" src="/skins/${skinDirName}/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                                                <img class="[em08]" src="/skins/${skinDirName}/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                                                <img class="[em09]" src="/skins/${skinDirName}/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                                                <img class="[em10]" src="/skins/${skinDirName}/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                                                <img class="[em11]" src="/skins/${skinDirName}/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                                                <img class="[em12]" src="/skins/${skinDirName}/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                                                <img class="[em13]" src="/skins/${skinDirName}/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                                                <img class="[em14]" src="/skins/${skinDirName}/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
                                                <input class="normalInput" id="commentValidate"/>
                                                <img id="captcha" alt="validate" src="/captcha.do"></img>
                                            </td>
                                            <th>
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
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="right side">
                    <#include "article-side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
            <div class="stack addthis_toolbox">
                <img src="/images/stack.png" alt="stack"/>
                <ul id="stack" class="custom_images">
                    <li><a class="addthis_button_googlebuzz"><span>Buzz</span><img src="/images/buzz.png" alt="Share to Buzz" /></a></li>
                    <li><a class="addthis_button_twitter"><span>Twitter</span><img src="/images/twitter.png" alt="Share to Twitter" /></a></li>
                    <li><a class="addthis_button_delicious"><span>Delicious</span><img src="/images/delicious.png" alt="Share to Delicious" /></a></li>
                    <li><a class="addthis_button_facebook"><span>Facebook</span><img src="/images/facebook.png" alt="Share to Facebook" /></a></li>
                    <li><a class="addthis_button_more"><span>More...</span><img src="/images/addthis.png" alt="More..." /></a></li>
                </ul>
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
        <script type="text/javascript" src="/js/articleUtil.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            var articleUtil = new ArticleUtil({
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
                    var commentOriginalCommentName = $("#commentItem" + articleUtil.currentCommentId).find(".comment-title a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + articleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + articleUtil.currentCommentId + '\');"'
                        + 'onmouseout="articleUtil.hideComment(\'' + articleUtil.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '</div><div class="right">' + result.commentDate
                    + '&nbsp;<a class="no-underline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="clear"></div></div><div class="comment-body">'
                    + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
                    + '" src="' + result.commentThumbnailURL + '"/>'
                    + '</div><div class="comment-content">' 
                    + articleUtil.replaceCommentsEmString($("#comment" + state).val().replace(/\n/g,"<br/>").replace(/</g, "&lt;").replace(/>/g, "&gt;"))
                    + '</div><div class="clear"></div>'
                    + '</div></div></div>';

                articleUtil.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table class='form comment-reply' id='replyForm'><tbody><tr><th>${commentName1Label}"
                    + "</th><td colspan='2'><input class='normalInput' id='commentNameReply' value='" + Cookie.readCookie("commentName") + "'/>"
                    + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                    + "<input class='normalInput' id='commentEmailReply' value='" + Cookie.readCookie("commentEmail") + "'/></td></tr><tr>"
                    + "<th>${commentURL1Label}</th><td colspan='2'><div id='commentURLLabelReply'>"
                    + "http://</div><input id='commentURLReply' value='" + Cookie.readCookie("commentURL") + "'/>"
                    + "</td></tr><tr><th>${commentEmotions1Label}</th><td id='emotionsReply'>" + $("#emotions").html()
                    + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                    + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                    + "<th>${captcha1Label}</th><td><input class='normalInput' id='commentValidateReply'/>"
                    + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                    + "<span class='error-msg' id='commentErrorTipReply'/>"
                    + "</th></tr><tr><td colspan='3' align='right'>"
                    + "<button onclick=\"articleUtil.submitComment('" + id + "', 'Reply');\">${submmitCommentLabel}</button>"
                    + "</td></tr></tbody></table>";
                articleUtil.addReplyForm(id, commentFormHTML);

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

            var loadAction = function () {
                // comment url
                $("#commentURL").focus(function (event) {
                    if ($.browser.version !== "7.0") {
                        $("#commentURLLabel").css({"border":"2px solid #73A6FF","border-right":"0px"});
                    }
                }).blur(function () {
                    $("#commentURLLabel").css({"border":"2px inset #CCCCCC","border-right":"0px"});
                }).width($("#comment").width() - $("#commentURLLabel").width());

                // emotions
                util.replaceCommentsEm("#comments .comment-content");

                articleUtil.load();
                articleUtil.loadRandomArticles();

                // externalRelevantArticles
                    <#if 0 != externalRelevantArticlesDisplayCount>
                    articleUtil.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
                    </#if>
                    articleUtil.loadTool();
            }
            loadAction();
        </script>
    </body>
</html>
