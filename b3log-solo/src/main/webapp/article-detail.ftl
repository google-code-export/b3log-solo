<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>${article.articleTitle} - ${blogTitle}</title>
        <script type="text/javascript" src="js/lib/jquery/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/json-rpc.js"></script>
        <link type="text/css" rel="stylesheet" href="js/lib/SyntaxHighlighter/styles/shCoreDefault.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="shortcut icon" href="favicon.ico" />
        <link rel="icon" type="image/gif" href="favicon.gif"/>
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="content">
            <div class="header">
                <#include "article-header.ftl">
            </div>
            <div class="body">
                <div class="left side">
                    <#include "article-side.ftl">
                </div>
                <div class="right main">
                    <div class="article">
                        <div class="article-header">
                            <div class="article-date">
                                <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                                <#else>
                                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                                </#if>
                            </div>
                            <div class="article-title">
                                <h2>
                                    <a class="noUnderline" href="/article-detail.do?oId=${article.oId}">${article.articleTitle}</a>
                                    <#if article.articleUpdateDate?datetime != article.articleCreateDate?datetime>
                                    <sup class="red" style="font-size: 12px">
                                        ${updatedLabel}
                                    </sup>
                                    </#if>
                                </h2>
                                <div class="article-tags">
                                    ${tags1Label}
                                    <#list articleTags as articleTag>
                                    <span>
                                        <a href="tag-articles.do?oId=${articleTag.oId}">${articleTag.tagTitle}</a>
                                    </span>
                                    </#list>
                                </div>
                            </div>
                            <div class="clear"></div>
                        </div>
                        <div class="article-body">
                            ${article.articleContent}
                        </div>
                        <div class="article-footer">
                            <div class="left">
                                <#if nextArticleId??>
                                <a href="article-detail.do?oId=${nextArticleId}">${nextArticle1Label}${nextArticleTitle}</a>
                                </#if>
                                <#if previousArticleId??>
                                <br/>
                                <a href="article-detail.do?oId=${previousArticleId}">${previousArticle1Label}${previousArticleTitle}</a>
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
                                <a href="article-detail.do?oId=${article.oId}" class="left noUnderline">
                                    <span class="left browserIcon" title="${viewLabel}"></span>
                                    &nbsp;${article.articleViewCount}
                                </a>
                            </div>
                            <div class="clear"></div>
                        </div>
                    </div>
                    <div class="comments" id="comments" name="comments">
                        <#list articleComments as comment>
                        <div>
                            <div class="comment-title">
                                <#if comment.commentURL=="http://">
                                <a name="${comment.oId}" class="left">${comment.commentName}</a>
                                <#else>
                                <a name="${comment.oId}" href="${comment.commentURL}" class="left">${comment.commentName}</a>
                                </#if>
                                <span class="right">${comment.commentDate}</span>
                                <div class="clear"></div>
                            </div>
                            <div class="comment-body">
                                <div class="left comment-picture">
                                    <img src="${comment.commentThumbnailURL}"/>
                                </div>
                                <div>
                                    ${comment.commentContent}
                                </div>
                                <div class="clear"></div>
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
                                            <button onclick="submitComment();">${submmitCommentLabel}</button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
        </div>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shCore.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushJScript.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushJava.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushXml.js"></script>
        <script type="text/javascript" src="js/lib/SyntaxHighlighter/scripts/shBrushCss.js"></script>
        <script type="text/javascript">
            var loadAction = function () {
                // code high lighter
                $(".article-body textarea").addClass("brush: js;");
                SyntaxHighlighter.config.tagName = "textarea";
                SyntaxHighlighter.config.tagName = "pre";
                SyntaxHighlighter.config.stripBrs = true;
                SyntaxHighlighter.defaults["gutter"] = true;
                SyntaxHighlighter.all();

                // submit comment
                $("#commentValidate").keypress(function (event) {
                    if (event.keyCode === 13) {
                        submitComment();
                    }
                });
                
                // comment url
                $("#commentURL").keyup(function () {
                    if (-1 === this.value.indexOf("http://")) {
                        this.value = "http://";
                    }
                });

                // article view count
                jsonRpc.statisticService.incArticleViewCount("${article.oId}");
                jsonRpc.statisticService.getArticleViewCount(function (result, error) {
                    if (!result || error) {
                        return;
                    }
                    $("#articleViewCount").html(result);
                },"${article.oId}");
            }
            loadAction();

            var validateComment = function () {
                var commentName = $("#commentName").val().replace(/(^\s*)|(\s*$)/g, ""),
                commenterContent = $("#comment").val().replace(/(^\s*)|(\s*$)/g, "");
                if (2 > commentName.length || commentName.length > 20) {
                    $("#commentErrorTip").html("${nameTooLongLabel}");
                    $("#commentName").focus();
                } else if ($("#commentEmail").val().replace(/\s/g, "") === "") {
                    $("#commentErrorTip").html("${mailCannotEmptyLabel}");
                    $("#commentEmail").focus();
                } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#commentEmail").val())) {
                    $("#commentErrorTip").html("${mailInvalidLabel}");
                    $("#commentEmail").focus();
                }  else if (2 > commenterContent.length || commenterContent.length > 500) {
                    $("#commentErrorTip").html("${commentContentCannotEmptyLabel}");
                    $("#comment").focus();
                } else if ($("#commentValidate").val().replace(/\s/g, "") === "") {
                    $("#commentErrorTip").html("${captchaCannotEmptyLabel}");
                    $("#commentValidate").focus();
                } else {
                    return true;
                }
                return false;
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

                    jsonRpc.commentService.addComment(function (result, error) {
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
        </script>
    </body>
</html>
