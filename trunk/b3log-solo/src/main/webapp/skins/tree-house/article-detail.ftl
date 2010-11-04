<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="revised" content="${blogTitle}, ${article.articleCreateDate?string('MM/dd/yy')}"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta name="author" content="${blogTitle}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${article.articleTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
        <link type="text/css" rel="stylesheet" href="/js/lib/SyntaxHighlighter/styles/shCoreDefault.css"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <div class="wrapper">
            <div class="bg-bottom">
                <#include "common-top.ftl">
                <div class="content">
                    <div class="header">
                        <#include "article-header.ftl">
                    </div>
                    <div class="body">
                        <div class="left main">
                            <div class="article">
                                <div class="article-header">
                                    <h2>
                                        <a class="noUnderline" href="${article.articlePermalink}">
                                            ${article.articleTitle}
                                            <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                            <sup>
                                                ${updatedLabel}
                                            </sup>
                                            </#if>
                                            <#if article.articlePutTop>
                                            <sup>
                                                ${topArticleLabel}
                                            </sup>
                                            </#if>
                                            <span>
                                                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                                <#else>
                                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                                </#if>
                                            </span>
                                        </a>
                                    </h2>
                                    <em class="article-tags left marginTop12 marginLeft6">
                                        <#list articleTags as articleTag>
                                        <a href="/tag-articles.do?oId=${articleTag.oId}">
                                            ${articleTag.tagTitle}
                                        </a>
                                        <#if articleTag_has_next>,</#if>
                                        </#list>
                                    </em>
                                    <div class="clear"></div>
                                </div>
                                <div class="article-body">
                                    ${article.articleContent}
                                </div>
                                <div class="article-details-footer">
                                    <div class="left">
                                        <#if nextArticleId??>
                                        <a href="/article-detail.do?oId=${nextArticleId}">${nextArticle1Label}${nextArticleTitle}</a>
                                        </#if>
                                        <#if previousArticleId??>
                                        <br/>
                                        <a href="/article-detail.do?oId=${previousArticleId}">${previousArticle1Label}${previousArticleTitle}</a>
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
                                        <a href="/article-detail.do?oId=${article.oId}" class="left">
                                            <span class="left browserIcon" title="${viewLabel}"></span>
                                            ${article.articleViewCount}
                                        </a>
                                    </div>
                                    <div class="clear"></div>
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
                                <div id="randomArticles" class="article-relative"></div>
                                <div id="externalRelevantArticles" class="article-relative"></div>
                            </div>
                            <div class="line right"></div>
                            <div class="comments marginTop12" id="comments" name="comments">
                                <div class="comments-header"></div>
                                <#list articleComments as comment>
                                <div id="commentItem${comment.oId}" class="comment">
                                    <div class="comment-top"></div>
                                    <div class="comment-body">
                                        <div class="comment-title">
                                            <#if "http://" == comment.commentURL>
                                            <a name="${comment.oId}" class="left">${comment.commentName}</a>
                                            <#else>
                                            <a name="${comment.oId}" href="${comment.commentURL}"
                                               target="_blank" class="left">${comment.commentName}</a>
                                            </#if>
                                            <#if comment.isReply>
                                            &nbsp;@&nbsp;<a
                                                href="http://${blogHost}/article-detail.do?oId=${article.oId}#${comment.commentOriginalCommentId}"
                                                onmouseover="showComment('${comment.commentOriginalCommentId}', '${comment.oId}');"
                                                onmouseout="hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                                            </#if>
                                            <div class="right">
                                                ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                                                <a class="noUnderline"
                                                   href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                                            </div>
                                            <div class="clear"></div>
                                        </div>
                                        <div>
                                            <img class="comment-picture left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                                            <div>
                                                ${comment.commentContent}
                                            </div>
                                            <div class="clear"></div>
                                        </div>
                                    </div>
                                    <div class="comment-bottom"></div>
                                </div>
                                </#list>
                                <div class="comment">
                                    <div class="comment-top"></div>
                                    <div class="comment-body">
                                        <div class="comment-title">
                                            <a>${postCommentsLabel}</a>
                                        </div>
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
                                                        <input value="http://" id="commentURL"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th valign="top">
                                                        ${commentContent1Label}
                                                    </th>
                                                    <td colspan="2">
                                                        <textarea rows="10" cols="96" id="comment"></textarea>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th valign="top">
                                                        ${captcha1Label}
                                                    </th>
                                                    <td valign="top" style="min-width: 190px;">
                                                        <input class="normalInput" id="commentValidate"/>
                                                        <img id="captcha" alt="validate" src="/captcha.do"></img>
                                                    </td>
                                                    <td>
                                                        <span class="error-msg" id="commentErrorTip"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td colspan="3" align="right">
                                                        <button onclick="submitComment();">${submmitCommentLabel}</button>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="comment-bottom"></div>
                                </div>
                            </div>
                        </div>
                        <div class="left side">
                            <#include "article-side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <div class="footer">
                    <#include "article-footer.ftl">
                </div>
            </div>
        </div>
        <script type="text/javascript">
            var currentCommentId = "";

            var moveCursor = function(event) {
                if ($.browser.msie) {
                    var e = event.srcElement;
                    var r = e.createTextRange();
                    r.moveStart('character', e.value.length);
                    r.collapse(true);
                    r.select();
                } else {
                    var iCaretPos = event.target.value.length;
                    event.target.selectionStart = iCaretPos;
                    event.target.selectionEnd = iCaretPos;
                }
            }

            var loadAction = function () {
                // code high lighter
                SyntaxHighlighter.autoloader(
                'js jscript javascript  /js/lib/SyntaxHighlighter/scripts/shBrushJScript.js',
                'java                   /js/lib/SyntaxHighlighter/scripts/shBrushJava.js',
                'xml                    /js/lib/SyntaxHighlighter/scripts/shBrushXml.js'
            );

                SyntaxHighlighter.config.tagName = "pre";
                SyntaxHighlighter.config.stripBrs = true;
                SyntaxHighlighter.defaults['toolbar'] = false;
                SyntaxHighlighter.all();

                // submit comment
                $("#commentValidate").keypress(function (event) {
                    if (event.keyCode === 13) {
                        submitComment();
                    }
                });

                // comment url
                $("#commentURL").keyup(function (event) {
                    if (-1 === this.value.indexOf("http://")) {
                        this.value = "http://";
                    }
                    moveCursor(event);
                }).focus(function (event) {
                    moveCursor(event);
                });

                // article view count
                jsonRpc.statisticService.incArticleViewCount("${article.oId}");

                //getRandomArticles
                jsonRpc.articleService.getRandomArticles(function (result, error) {
                    if (result && !error) {
                        var randomArticles = result.list;
                        if (0 === randomArticles.length) {
                            return;
                        }

                        var listHtml = "";
                        for (var i = 0; i < randomArticles.length; i++) {
                            var article = randomArticles[i];
                            var title = article.articleTitle;
                            var randomArticleLiHtml = "<li>"
                                + "<a href='" + article.articlePermalink +"'>"
                                +  title + "</a></li>"
                            listHtml += randomArticleLiHtml
                        }

                        var randomArticlesDiv = $("#randomArticles");
                        var randomArticleListHtml = "<h5>${randomArticles1Label}</h5>"
                            + "<ul class='marginLeft12'>"
                            + listHtml + "</ul>";
                        randomArticlesDiv.append(randomArticleListHtml);
                    }
                });


                    <#if 0 != externalRelevantArticlesDisplayCount>
                    var tags = "<#list articleTags as articleTag>${articleTag.tagTitle}<#if articleTag_has_next>,</#if></#list>";
                $.ajax({
                    url: "http://b3log-rhythm.appspot.com:80/get-articles-by-tags.do?tags=" + tags
                        + "&blogHost=${blogHost}&paginationPageSize=${externalRelevantArticlesDisplayCount}",
                    type: "GET",
                    dataType:"jsonp",
                    jsonp: "callback",
                    error: function(){
                        alert("Error loading article from Rhythm");
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
                        externalRelevantArticlesDiv.append(randomArticleListHtml);
                    }
                });
                    </#if>
                }
            loadAction();

            var validateComment = function (state) {
                if (state === undefined) {
                    state = '';
                }
                var commentName = $("#commentName" + state).val().replace(/(^\s*)|(\s*$)/g, ""),
                commenterContent = $("#comment" + state).val().replace(/(^\s*)|(\s*$)/g, "");
                if (2 > commentName.length || commentName.length > 20) {
                    $("#commentErrorTip" + state).html("${nameTooLongLabel}");
                    $("#commentName" + state).focus();
                } else if ($("#commentEmail" + state).val().replace(/\s/g, "") === "") {
                    $("#commentErrorTip" + state).html("${mailCannotEmptyLabel}");
                    $("#commentEmail" + state).focus();
                } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#commentEmail" + state).val())) {
                    $("#commentErrorTip" + state).html("${mailInvalidLabel}");
                    $("#commentEmail" + state).focus();
                }  else if (2 > commenterContent.length || commenterContent.length > 500) {
                    $("#commentErrorTip" + state).html("${commentContentCannotEmptyLabel}");
                    $("#comment" + state).focus();
                } else if ($("#commentValidate" + state).val().replace(/\s/g, "") === "") {
                    $("#commentErrorTip" + state).html("${captchaCannotEmptyLabel}");
                    $("#commentValidate" + state).focus();
                } else {
                    return true;
                }
                return false;
            }

            var replyTo = function (id) {
                if (id === currentCommentId) {
                    $("#commentNameReply").focus();
                    return;
                } else {
                    $("#replyForm").remove();
                    var commentFormHTML = "<tr><th>${commentName1Label}"
                        + "</th><td colspan='2'><input class='normalInput' id='commentNameReply'/>"
                        + "</td></tr><tr><th>${commentEmail1Label}</th><td colspan='2'>"
                        + "<input class='normalInput' id='commentEmailReply'/></td></tr><tr>"
                        + "<th>${commentURL1Label}</th><td colspan='2'><input value='http://' id='commentURLReply'/>"
                        + "</td></tr><tr><th valign='top'>${commentContent1Label}</th><td colspan='2'>"
                        + "<textarea rows='10' cols='96' id='commentReply'></textarea></td></tr><tr>"
                        + "<th valign='top'>${captcha1Label}</th><td valign='top'>"
                        + "<input class='normalInput' id='commentValidateReply'/>"
                        + "<img id='captchaReply' alt='validate' src='/captcha.do?" + new Date().getTime() + "'></img></td><th>"
                        + "<span class='error-msg' id='commentErrorTipReply'/>"
                        + "</th></tr><tr><td colspan='3' align='right'>"
                        + "<button onclick=\"submitCommentReply('" + id + "');\">${submmitCommentLabel}</button>"
                        + "</td></tr>";

                    $("#commentItem" + id  + ">.comment-body").append("<table class='form comment-reply' id='replyForm'></table>");
                    $("#replyForm").append(commentFormHTML);
                    $("#commentValidateReply").keypress(function (event) {
                        if (event.keyCode === 13) {
                            submitCommentReply(id);
                        }
                    });

                    $("#commentURLReply").keyup(function (event) {
                        if (-1 === this.value.indexOf("http://")) {
                            this.value = "http://";
                        }
                        moveCursor(event);
                    }).focus(function (event) {
                        moveCursor(event);
                    });

                    $("#commentNameReply").focus();
                }
                currentCommentId = id;
            }

            var submitCommentReply = function (id) {
                if (validateComment("Reply")) {
                    $("#commentErrorTipReply").html("${loadingLabel}");
                    var requestJSONObject = {
                        "oId": "${article.oId}",
                        "commentContent": $("#commentReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmailReply").val(),
                        "commentURL": $("#commentURLReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentNameReply").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidateReply").val(),
                        "commentOriginalCommentId": id
                    };

                    jsonRpc.commentService.addCommentToArticle(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_ARTICLE_SUCC":
                                    $("#replyForm").remove();
                                    window.location.reload();
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTipReply").html("${captchaErrorLabel}");
                                    $("#captchaReply").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidateReply").val("").focus();
                                    break
                                default:
                                    break;
                            }
                        }
                    }, requestJSONObject);
                }
            }

            var submitComment = function () {
                if (validateComment()) {
                    $("#commentErrorTip").html("${loadingLabel}");
                    var requestJSONObject = {
                        "oId": "${article.oId}",
                        "commentContent": $("#comment").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentEmail": $("#commentEmail").val(),
                        "commentURL": $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "commentName": $("#commentName").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "captcha": $("#commentValidate").val()
                    };

                    jsonRpc.commentService.addCommentToArticle(function (result, error) {
                        if (result && !error) {
                            switch (result.sc) {
                                case "COMMENT_ARTICLE_SUCC":
                                    $("#commentErrorTip").html("");
                                    $("#comment").val("");
                                    $("#commentEmail").val("");
                                    $("#commentURL").val("http://");
                                    $("#commentName").val("");
                                    $("#commentValidate").val("");
                                    window.location.reload();
                                    break;
                                case "CAPTCHA_ERROR":
                                    $("#commentErrorTip").html("${captchaErrorLabel}");
                                    $("#captcha").attr("src", "/captcha.do?code=" + Math.random());
                                    $("#commentValidate").val("").focus();
                                    break
                                default:
                                    break;
                            }
                        }
                    }, requestJSONObject);
                }
            }

            var showComment = function (id, oId) {
                $("#commentItemRef" + id).show();
                var refComment = $("#commentItem" + id).clone();
                refComment.find(".comment-body-ref").remove();
                refComment.removeClass().addClass("comment-body-ref").attr("id", "commentItemRef" + id);
                $("#commentItem" + oId + " .comment-title").append(refComment);
                $("#commentItemRef" + id + " #replyForm").remove();
                $("#commentItemRef" + id + " .comment-title .right a").remove();
            }

            var hideComment = function (id) {
                $("#commentItemRef" + id).hide();
            }
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
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            var loadTool = function () {
                // article view count
                jsonRpc.statisticService.incArticleViewCount(function (result, error) {}, "${article.oId}");

                // Stack initialize
                var openspeed = 300;
                var closespeed = 300;
                $('.stack>img').toggle(function(){
                    var vertical = 0;
                    var horizontal = 0;
                    var $el=$(this);
                    $el.next().children().each(function(){
                        $(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, openspeed);
                        vertical = vertical + 36;
                        horizontal = (horizontal+.42)*2;
                    });
                    $el.next().animate({top: '-21px', left: '-6px'}, openspeed).addClass('openStack')
                    .find('li a>img').animate({width: '28px', marginLeft: '9px'}, openspeed);
                    $el.animate({paddingTop: '0'});
                }, function(){
                    //reverse above
                    var $el=$(this);
                    $el.next().removeClass('openStack').children('li').animate({top: '32px', left: '6px'}, closespeed);
                    $el.next().find('li a>img').animate({width: '32px', marginLeft: '0'}, closespeed);
                    $el.animate({paddingTop: '9px'});
                });

                // Stacks additional animation
                $('.stack li a').hover(function(){
                    $("img",this).animate({width: '32px'}, 100);
                    $("span",this).animate({marginRight: '12px'});
                },function(){
                    $("img",this).animate({width: '28px'}, 100);
                    $("span",this).animate({marginRight: '0'});
                });
            }
            loadTool();
        </script>
    </body>
</html>
