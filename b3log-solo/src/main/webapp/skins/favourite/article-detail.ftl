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
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="roundtop"></div>
                <div class="body">
                    <div class="left main">
                        <div class="article">
                            <h2 class="article-title">
                                <a class="noUnderline" href="${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
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
                            <div class="posttime-blue">
                                 <div class="posttime-MY">
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                    ${article.articleUpdateDate?string("MMM yyyy")}
                                    <#else>
                                    ${article.articleCreateDate?string("MMM yyyy")}
                                    </#if>
                                 </div>
                                 <div class="posttime-D">
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                    ${article.articleUpdateDate?string("dd")}
                                    <#else>
                                    ${article.articleCreateDate?string("dd")}
                                    </#if>
                                 </div>
                            </div>
                            <div class="article-body">
                                <div class="note">
                                    <div class="corner"></div>
                                    <div class="substance">
                                    ${article.articleContent}
                                    </div>
                                </div>
                            </div>
                            <div class="margin25">
                                <a href="${article.articlePermalink}" class="left">
                                    <span class="left article-browserIcon" title="${viewLabel}"></span>
                                    <span class="count">${article.articleViewCount}</span>
                                </a>
                                <div class="left">
                                    <span class="tagsIcon" title="${tagLabel}"></span>
                                    <#list articleTags as articleTag>
                                    <span class="count">
                                        <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                                            ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                                    </span>
                                    </#list>
                                </div>
                                <a href="${article.articlePermalink}#comments" class="left">
                                    <span class="left articles-commentIcon" title="${commentLabel}"></span>
                                    <span class="count">${article.articleCommentCount}</span>
                                </a>
                                <div class="right">
                                <a href="#comments" class="right">
                                    ${replyLabel}
                                </a>
                                </div>
                                <div class="clear"></div>
                            </div>
                            
                            <div class="article-relative">
                                <#if nextArticlePermalink??>
                                <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                                </#if>
                                <br/>
                                <#if previousArticlePermalink??>
                                <br/>
                                <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
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
                                        <button onclick="submitComment();">${submmitCommentLabel}</button>
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
                <div class="roundbottom"></div>
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
                            + articleUtil.replaceEmotions($("#comment" + state).val(), "favourite")
                            + '</div></div><div class="clear"></div></div></div>';

                        articleUtil.addCommentAjax(commentHTML, state);
                        $("#comments").addClass("comments");
                    }

                    var replyTo = function (id) {
                        if (id === articleUtil.currentCommentId) {
                            $("#commentNameReply").focus();
                            return;
                        } else {
                            $("#replyForm").remove();

                            var commentFormHTML = "<table class='marginTop12 comment-form' id='replyForm'><tbody><tr>"
                                + "<td width='208px'><input class='normalInput' id='commentNameReply'/>"
                                + "</td><td colspan='2' width='400px'>${commentNameLabel}</td></tr><tr><td>"
                                + "<input class='normalInput' id='commentEmailReply'/></td><td colspan='2'>${commentEmailLabel}</td></tr><tr>"
                                + "<td><div id='commentURLLabelReply'>http://</div><input id='commentURLReply'/>"
                                + "</td><td colspan='2'>${commentURLLabel}</td></tr><tr><td id='emotionsReply' colspan='3'>"
                                + $("#emotions").html() + "</td></tr><tr><td colspan='3'>"
                                + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                                + "<td><input class='normalInput' id='commentValidateReply'/>"
                                + "</td><td><img id='captchaReply' alt='validate' src='/captcha.do?"
                                + new Date().getTime() + "'></img></td><th align='right'>"
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
                        articleUtil.insertEmotions();
                        replaceCommentsEm("#comments .comment-content");

                        articleUtil.load();
                        articleUtil.loadRandomArticles();

                        // externalRelevantArticles
                            <#if 0 != externalRelevantArticlesDisplayCount>
                            var tags = "<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>";
                        $.ajax({
                            url: "http://b3log-rhythm.appspot.com:80/get-articles-by-tags.do?tags=" + tags
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
                                externalRelevantArticlesDiv.attr("class", "article-relative");
                                var randomArticleListHtml = "<h5>${externalRelevantArticles1Label}</h5>"
                                    + "<ul class='marginLeft12'>"
                                    + listHtml + "</ul>";
                                externalRelevantArticlesDiv.append(randomArticleListHtml);
                            }
                        });
                            </#if>
                        }
                    loadAction();
                </script>
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
        <div class="footer">
            <div class="footer-icon"><#include "statistic.ftl"></div>
            <#include "article-footer.ftl">
        </div>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            articleUtil.loadTool("${article.oId}");
        </script>
    </body>
</html>
