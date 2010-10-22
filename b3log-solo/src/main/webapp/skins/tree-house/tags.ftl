<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="GAE 博客,GAE blog,b3log solo,b3log rhythm,b3log"/>
        <meta name="description" content="An open source blog based on GAE. 基于 GAE 的开源博客。"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="author" content="b3log-solo.googlecode.com"/>
        <meta name="revised" content="b3log, 9/10/10"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${allTagsLabel} - ${blogTitle}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="favicon.png"/>
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
                            <div id="tagsPanel">
                            </div>
                        </div>
                        <div class="left side">
                            <#include "article-side.ftl">
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="footer">
                        <#include "article-footer.ftl">
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            var randomColor = function () {
                var arrHex = ["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"];
                var strHex = "#";
                var index;
                for(var i = 0; i < 6; i++) {
                    index = Math.round(Math.random() * 15);
                    strHex += arrHex[index];
                }
                return strHex;
            }

            var getMaxCount = function (tags) {
                var maxCount = 0;
                for (var i = 0; i < tags.length; i++) {
                    if (tags[i].tagCount > maxCount) {
                        maxCount = tags[i].tagCount;
                    }
                }

                return maxCount;
            }

            var getStyle = function (maxCount, currentCount) {
                var styleHTML = {
                    padding: "",
                    font: "",
                    color: ""
                };
                styleHTML.padding =  "padding:" + parseInt(16 * currentCount / maxCount) + "px;";

                styleHTML.color = "color:" + randomColor() + ";";

                fontSize = parseInt(36 * currentCount / maxCount);
                if (fontSize < 10) {
                    fontSize = 10;
                }
                styleHTML.font = "font-size:" + fontSize + "px;";
                if (maxCount === currentCount) {
                    styleHTML.font += "font-weight:bold;";
                }
                return styleHTML;
            }

            var setTagsPanel = function () {
                var tags = [<#list tags as tag>{
                        tagName: "${tag.tagTitle}",
                        tagCount: ${tag.tagReferenceCount},
                        tagId: ${tag.oId}
                    }<#if tag_has_next>,</#if>
                        </#list>],
                tagsHTML = "";

                var maxCount = getMaxCount(tags);

                for (var i = 0; i < tags.length; i++) {
                    var style = getStyle(maxCount, tags[i].tagCount);
                    tagsHTML += "<a title='" + tags[i].tagCount + "' class='tagPanel' style='"
                        + style.font + style.color + style.padding + "' href='tag-articles.do?oId="
                        + tags[i].tagId + "&paginationCurrentPageNum=1'>" + tags[i].tagName + "</a> ";
                }
                $("#tagsPanel").append(tagsHTML + "<div class='clear'></div>");
            }
            setTagsPanel();
        </script>
    </body>
</html>
