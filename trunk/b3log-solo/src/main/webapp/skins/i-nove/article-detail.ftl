<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head> 
        <title>${article.articleTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>"/>
        <meta name="description" content="${article.articleAbstract}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log,${article.articleCreateDate?string('yyyy-MM-dd HH:mm:ss')}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shThemeEclipse.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/i-nove/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="body">
                    <div class="left main">
                        <div class="article">
                            <h2 class="article-title">
                                <a class="noUnderline" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                            <div class="margin5">
                                <div class="article-date left">
                                    <span class="dateIcon left"></span>
                                    <#if article.hasUpdated>
                                    ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                    </#if>
                                </div>
                                <div class="right">
                                    <a href="${article.articlePermalink}#comments" class="left">
                                        <span class="left articles-commentIcon" title="${commentLabel}"></span>
                                        ${article.articleCommentCount}
                                    </a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="article-body">
                                ${article.articleContent}
                                <div class="marginTop12">
                                    ${article.articleSign.signHTML}
                                </div>
                            </div>
                            <div class="margin5 paddingTop12">
                                <a class="left" href="${article.articlePermalink}">
                                    <span title="${viewLabel}" class="left article-browserIcon"></span>
                                    ${article.articleViewCount}
                                </a>
                                <div class="left">
                                    <span title="${tagLabel}" class="tagsIcon"></span>
                                    <#list articleTags as articleTag>
                                    <span>
                                        <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                                            ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                                    </span>
                                    </#list>
                                </div>
                                <div class="clear"></div>
                            </div>
                            <div class="article-relative">
                                <#if nextArticlePermalink??>
                                <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                <br/>
                                </#if>
                                <#if previousArticlePermalink??>
                                <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                                <br/>
                                </#if>
                            </div>
                            <#if 0 != relevantArticles?size>
                            <div class="article-relative">
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
                            <div id="randomArticles"></div>
                            <div id="externalRelevantArticles"></div>
                        </div>
                        <div class="comments" id="comments" name="comments">
                            <#list articleComments as comment>
                            <div id="commentItem${comment.oId}" class="comment-body">
                                <div class="comment-panel">
                                    <div class="left comment-author">
                                        <div>
                                            <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                        </div>
                                        <#if "http://" == comment.commentURL>
                                        <a name="${comment.oId}">${comment.commentName}</a>
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
                                            <a class="noUnderline"
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
                                        <img class="[em00]" src="/skins/i-nove/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                                        <img class="[em01]" src="/skins/i-nove/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                                        <img class="[em02]" src="/skins/i-nove/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                                        <img class="[em03]" src="/skins/i-nove/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                                        <img class="[em04]" src="/skins/i-nove/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                                        <img class="[em05]" src="/skins/i-nove/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                                        <img class="[em06]" src="/skins/i-nove/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                                        <img class="[em07]" src="/skins/i-nove/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                                        <img class="[em08]" src="/skins/i-nove/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                                        <img class="[em09]" src="/skins/i-nove/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                                        <img class="[em10]" src="/skins/i-nove/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                                        <img class="[em11]" src="/skins/i-nove/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                                        <img class="[em12]" src="/skins/i-nove/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                                        <img class="[em13]" src="/skins/i-nove/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                                        <img class="[em14]" src="/skins/i-nove/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
                    </div>
                    <div class="right">
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
        </div>
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
                "externalRelevantArticles1Label": "${externalRelevantArticles1Label}"
            });

            var addComment = function (result, state) {
                var commentHTML = '<div id="commentItem' + result.oId
                    + '" class="comment-body"><div class="comment-panel"><div class="left comment-author">'
                    + '<div><img alt="' + $("#commentName" + state).val() + '" src="' + result.commentThumbnailURL + '"/></div>';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '">' + $("#commentName" + state).val() + '</a>';
                }
                commentHTML += '</div><div class="left comment-info"><div class="left">' + articleUtil.getDate(result.commentDate.time, 'yyyy-mm-dd hh:mm:ss');
                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + articleUtil.currentCommentId).find(".comment-author a").text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + articleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + articleUtil.currentCommentId + '\');"'
                        + 'onmouseout="articleUtil.hideComment(\'' + articleUtil.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }
                commentHTML += '</div><div class="right"> <a class="noUnderline" href="javascript:replyTo(\''
                    + result.oId + '\');">${replyLabel}</a>'
                    +'</div><div class="clear"></div><div class="comment-content">'
                    + articleUtil.replaceEmotions($("#comment" + state).val(), "i-nove")
                    + '</div></div><div class="clear"></div></div></div>';

                articleUtil.addCommentAjax(commentHTML, state);
                $("#comments").addClass("comments");
            }

            var replyTo = function (id) {
                var commentFormHTML = "<table class='marginTop12 comment-form' id='replyForm'><tbody><tr>"
                    + "<td width='208px'><input class='normalInput' id='commentNameReply' value='" + Cookie.readCookie("commentName") + "'/>"
                    + "</td><td colspan='2' width='400px'>${commentNameLabel}</td></tr><tr><td>"
                    + "<input class='normalInput' id='commentEmailReply' value='" + Cookie.readCookie("commentEmail") + "'/></td><td colspan='2'>${commentEmailLabel}</td></tr><tr>"
                    + "<td><div id='commentURLLabelReply'>http://</div><input id='commentURLReply' value='" + Cookie.readCookie("commentURL") + "'/>"
                    + "</td><td colspan='2'>${commentURLLabel}</td></tr><tr><td id='emotionsReply' colspan='3'>"
                    + $("#emotions").html() + "</td></tr><tr><td colspan='3'>"
                    + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                    + "<td><input class='normalInput' id='commentValidateReply'/>"
                    + "</td><td><img id='captchaReply' alt='validate' src='/captcha.do?"
                    + new Date().getTime() + "'></img></td><th align='right'>"
                    + "<span class='error-msg' id='commentErrorTipReply'/>"
                    + "</th></tr><tr><td colspan='3' align='right'>"
                    + "<button onclick=\"articleUtil.submitComment('" + id + "', 'Reply');\">${submmitCommentLabel}</button>"
                    + "</td></tr></tbody></table>";
                articleUtil.addReplyForm(id, commentFormHTML);
            }

            var showComment = function (it, id) {
                if ( $("#commentItemRef" + id).length > 0) {
                    $("#commentItemRef" + id).show();
                } else {
                    var $refComment = $("#commentItem" + id + " .comment-panel").clone();
                    $refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                    $refComment.find(".comment-info .right").remove();
                    $("#comments").append($refComment);
                }
                var position =  $(it).position();
                $("#commentItemRef" + id).css({
                    "top": (position.top + 18) + "px",
                    "left": "217px"
                });
            }

            var loadAction = function () {
                if ($("#comments div").length === 0) {
                    $("#comments").removeClass("comments");
                }

                // emotions
                util.replaceCommentsEm("#comments .comment-content", "i-nove");

                articleUtil.load();
                articleUtil.loadRandomArticles();
                articleUtil.loadTool();
                    <#if 0 != externalRelevantArticlesDisplayCount>
                    articleUtil.loadExternalRelevantArticles("<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>");
                    </#if>
                }
            loadAction();
        </script>
    </body>
</html>
