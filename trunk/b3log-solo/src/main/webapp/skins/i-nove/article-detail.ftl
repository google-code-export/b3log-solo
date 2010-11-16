<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head> 
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="revised" content="${blogTitle}, ${article.articleCreateDate?string('MM/dd/yy')}"/>
        <meta name="generator" content="b3log"/>
        <meta name="author" content="${blogTitle}"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${article.articleTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="/js/lib/SyntaxHighlighter/scripts/shAutoloader.js"></script>
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
        <div class="wrapper">
            <div class="content">
                <#include "article-header.ftl">
                <div class="body">
                    <div class="left main">
                        <div class="article">
                            <h2 class="article-title">
                                <a class="noUnderline" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                            <div class="margin5">
                                <div class="article-date left">
                                    <span class="dateIcon left"></span>
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
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
                                                onmouseout="hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
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
                <script type="text/javascript">
                    var currentCommentId = "";

                    var insertEmotions = function (name) {
                        $("#emotions" + name + " img").click(function () {
                            // TODO: should be insert it at the after of cursor
                            var key = this.className;
                            $("#comment" + name).val($("#comment" + name).val() + key).focus();
                        });
                    }

                    var processEmotions = function () {
                        var $commentContents = $("#comments .comment-content");
                        for (var i = 0; i < $commentContents.length; i++) {
                            var str = $commentContents[i].innerHTML;
                            var ems = str.split("[em");
                            var content = ems[0];
                            for (var j = 1; j < ems.length; j++) {
                                var key = ems[j].substr(0, 2),
                                emImgHTML = "<img src='/skins/i-nove/emotions/em" + key
                                    + ".png'/>";
                                content += emImgHTML + ems[j].slice(3);
                            }
                            $commentContents[i].innerHTML = content;
                        }
                    }

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

                            insertEmotions("Reply");

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
                                "commentURL": "http://" + $("#commentURLReply").val().replace(/(^\s*)|(\s*$)/g, ""),
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
                                "commentURL": "http://" + $("#commentURL").val().replace(/(^\s*)|(\s*$)/g, ""),
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
                                            $("#commentURL").val("");
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

                    var hideComment = function (id) {
                        $("#commentItemRef" + id).hide();
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

                        // emotions
                        insertEmotions("");
                        processEmotions();

                        // getRandomArticles
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
                                randomArticlesDiv.attr("class", "article-relative");
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
        <script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
        <script type="text/javascript">
            var loadTool = function () {
                // hide comments
                if ($("#comments div").length === 0) {
                    $("#comments").removeClass("comments");
                }
                
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
