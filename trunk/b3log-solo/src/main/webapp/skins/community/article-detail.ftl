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
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="header">
            <#include "article-header.ftl">
        </div>
        <div class="content">
            <div class="article">
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
                            <a class="left" title="${article.authorName}" href="/author-articles.do?oId=${article.authorId}">
                                ${article.authorName}
                            </a>
                            <span class="clear"/>
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
                <div class="article-body article-detail-body">
                    <h2 class="title">
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
                    ${article.articleContent}
                    <div class="tags">
                        <img width="16px" height="16px" src="/skins/community/images/tags.gif" alt="${tagsLabel}"/>
                        ${tags1Label}
                        <#list articleTags as articleTag>
                        <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                            ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
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
                    <div class="clear"></div><div id="randomArticles" class="article-relative left"></div>
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
                    <div id="externalRelevantArticles"></div>
                </div>
            </div>
            <div id="comments" name="comments">
                <#list articleComments as comment>
                <div id="commentItem${comment.oId}">
                    <div class="comment-panel">
                        <div class="comment-title">
                            <#if "http://" == comment.commentURL>
                            <a name="${comment.oId}" class="left">${comment.commentName}</a>
                            <#else>
                            <a name="${comment.oId}" href="${comment.commentURL}"
                               target="_blank" class="left">${comment.commentName}</a>
                            </#if>
                            <#if comment.isReply>
                            &nbsp;@&nbsp;<a
                                href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                                onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                            </#if>
                            <div class="right">
                                ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                <a class="noUnderline"
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
                    <table class="comment" cellpadding="0" cellspacing="0" width="100%">
                        <tbody>
                            <tr>
                                <th width="200px">
                                    <div>
                                        ${commentNameLabel}
                                    </div>
                                    <span class="arrow-right"></span>
                                </th>
                                <td colspan="2">
                                    <input id="commentName"/>
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
                                    <input id="commentEmail"/>
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
                                    <input id="commentURL"/>
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
                                    <img class="[em00]" src="/skins/classic/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                                    <img class="[em01]" src="/skins/classic/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                                    <img class="[em02]" src="/skins/classic/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                                    <img class="[em03]" src="/skins/classic/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                                    <img class="[em04]" src="/skins/classic/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                                    <img class="[em05]" src="/skins/classic/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                                    <img class="[em06]" src="/skins/classic/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                                    <img class="[em07]" src="/skins/classic/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                                    <img class="[em08]" src="/skins/classic/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                                    <img class="[em09]" src="/skins/classic/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                                    <img class="[em10]" src="/skins/classic/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                                    <img class="[em11]" src="/skins/classic/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                                    <img class="[em12]" src="/skins/classic/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                                    <img class="[em13]" src="/skins/classic/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                                    <img class="[em14]" src="/skins/classic/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
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
                                    <input id="commentValidate"/>
                                    <img id="captcha" alt="validate" src="/captcha.do"></img>
                                </td>
                                <th>
                                    <span class="error-msg" id="commentErrorTip"/>
                                </th>
                            </tr>
                            <tr>
                                <td colspan="3" align="right">
                                    <button onclick="submitComment();">${submmitCommentLabel}</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="clear"></div>
        </div>
        <script type="text/javascript" src="/js/articleUtil.js"></script>
        <script type="text/javascript">
            var articleUtil = new ArticleUtil({
                nameTooLong: "${nameTooLongLabel}",
                mailCannotEmpty: "${mailCannotEmptyLabel}",
                mailInvalid: "${mailInvalidLabel}",
                commentContentCannotEmpty: "${commentContentCannotEmptyLabel}",
                captchaCannotEmpty: "${captchaCannotEmptyLabel}",
                randomArticles: "${randomArticles1Label}"
            });
                
            var addComment = function (result, state) {
                if (state === undefined) {
                    state = "";
                }
                    
                var commentHTML = '<div id="commentItem' + result.oId + '"><div class="comment-panel"><div class="comment-title">';

                if ($("#commentURL" + state).val().replace(/\s/g, "") === "") {
                    commentHTML += '<a name="' + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                } else {
                    commentHTML += '<a href="http://' + $("#commentURL" + state).val() + '" target="_blank" name="'
                        + result.oId + '" class="left">' + $("#commentName" + state).val() + '</a>';
                }

                if (state !== "") {
                    var commentOriginalCommentName = $("#commentItem" + articleUtil.currentCommentId).find(".comment-title a").first().text();
                    commentHTML += '&nbsp;@&nbsp;<a href="' + result.commentSharpURL.split("#")[0] + '#' + articleUtil.currentCommentId + '"'
                        + 'onmouseover="showComment(this, \'' + articleUtil.currentCommentId + '\');"'
                        + 'onmouseout="articleUtil.hideComment(\'' + articleUtil.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
                }

                commentHTML += '<div class="right">' + articleUtil.getDate(result.commentDate.time, 'yyyy-mm-dd hh:mm:ss')
                    + '&nbsp;<a class="noUnderline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
                    + '</div><div class="clear"></div></div><div class="comment-body">'
                    + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
                    + '" src="' + result.commentThumbnailURL + '"/>'
                    + '</div><div class="comment-content">' + articleUtil.replaceEmotions($("#comment" + state).val(), "classic") + '</div><div class="clear"></div>'
                    + '</div></div></div>';

                articleUtil.addCommentAjax(commentHTML, state);
            }

            var replyTo = function (id) {
                if (id === articleUtil.currentCommentId) {
                    $("#commentNameReply").focus();
                    return;
                } else {
                    $("#replyForm").remove();

                    var commentFormHTML = "<table class='form comment-reply' id='replyForm'><tbody><tr><th>${commentName1Label}"
                        + "</th><td colspan='2'><input id='commentNameReply'/>"
                        + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                        + "<input id='commentEmailReply'/></td></tr><tr>"
                        + "<th>${commentURL1Label}</th><td colspan='2'><div id='commentURLLabelReply'>"
                        + "http://</div><input id='commentURLReply'/>"
                        + "</td></tr><tr><th>${commentEmotions1Label}</th><td id='emotionsReply'>" + $("#emotions").html()
                        + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                        + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                        + "<th>${captcha1Label}</th><td><input id='commentValidateReply'/>"
                        + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                        + "<span class='error-msg' id='commentErrorTipReply'/>"
                        + "</th></tr><tr><td colspan='3' align='right'>"
                        + "<button onclick=\"submitCommentReply('" + id + "');\">${submmitCommentLabel}</button>"
                        + "</td></tr></tbody></table>";

                    $("#commentItem" + id).append(commentFormHTML);

                    $("#commentValidateReply").keypress(function (event) {
                        if (event.keyCode === 13) {
                            submitCommentReply(id);
                        }
                    });

                    articleUtil.insertEmotions("Reply");

                    $("#commentNameReply").focus();
                }
                articleUtil.currentCommentId = id;
            }

            var submitCommentReply = function (id) {
                if (articleUtil.validateComment("Reply")) {
                    $("#commentErrorTipReply").html("${loadingLabel}");
                    var requestJSONObject = {
                        "oId": "${article.oId}",
                        "commentContent": $("#commentReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmailReply").val(),
                        "commentURL": "http://" + $("#commentURLReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentNameReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidateReply").val(),
                        "commentOriginalCommentId": id
                    };

                    jsonRpc.commentService.addCommentToArticle(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_ARTICLE_SUCC":
                                    addComment(result, "Reply");
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTipReply").html("${captchaErrorLabel}");
                                    $("#captchaReply").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidateReply").val("").focus();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, requestJSONObject);
                }
            }

            var submitComment = function () {
                if (articleUtil.validateComment()) {
                    $("#commentErrorTip").html("${loadingLabel}");
                    var requestJSONObject = {
                        "oId": "${article.oId}",
                        "commentContent": $("#comment").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmail").val(),
                        "commentURL": "http://" + $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentName").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidate").val()
                    };

                    jsonRpc.commentService.addCommentToArticle(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_ARTICLE_SUCC":
                                    addComment(result);
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTip").html("${captchaErrorLabel}");
                                    $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidate").val("").focus();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, requestJSONObject);
                }
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
                // emotions
                articleUtil.insertEmotions();
                replaceCommentsEm("#comments .comment-content");

                articleUtil.load();
                articleUtil.loadRandomArticles();
                    
                // externalRelevantArticles
                    <#if 0 != externalRelevantArticlesDisplayCount>
                    var tags = "<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>";
                $.ajax({
                    url: "http://rhythm.b3log.org:80/get-articles-by-tags.do?tags=" + tags
                        + "&blogHost=${blogHost}&paginationPageSize=${externalRelevantArticlesDisplayCount}",
                    type: "GET",
                    dataType:"jsonp",
                    jsonp: "callback",
                    error: function(){
                        alert("Error loading articles from Rhythm");
                    },
                    success: function(data, textStatus){
                        var articles = data.articles;
                        if (0 === articles.length) {
                            return;
                        }
                        var listHtml = "";
                        for (var i = 0; i < articles.length; i++) {
                            var article = articles[i];
                            var title = article.articleTitle;
                            var articleLiHtml = "<li>"
                                + "<a target='_blank' href='" + article.articlePermalink + "'>"
                                +  title + "</a></li>"
                            listHtml += articleLiHtml
                        }

                        var externalRelevantArticlesDiv = $("#externalRelevantArticles");
                        var randomArticleListHtml = "<h5>${externalRelevantArticles1Label}</h5>"
                            + "<ul class='marginLeft12'>"
                            + listHtml + "</ul>";
                        $("#externalRelevantArticles").append(randomArticleListHtml).addClass("article-relative");
                    }
                });
                    </#if>
                }
            loadAction();
        </script>
        <div>
            <#include "article-side.ftl">
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
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            articleUtil.loadTool("${article.oId}");
        </script>
    </body>
</html>
