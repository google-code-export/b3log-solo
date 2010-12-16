<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${allTagsLabel} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list tags as tag>${tag.tagTitle}<#if tag_has_next>,</#if></#list>"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/classic/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        ${htmlHead}
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
                    <div id="tagsPanel">
                    </div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
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
                        tagNameURLEncoded: "${tag.tagTitle?url('UTF-8')}",
                        tagName: "${tag.tagTitle}",
                        tagCount: ${tag.tagPublishedRefCount},
                        tagId: ${tag.oId}
                    }<#if tag_has_next>,</#if>
                        </#list>],
                tagsHTML = "";

                var maxCount = getMaxCount(tags);

                for (var i = 0; i < tags.length; i++) {
                    var style = getStyle(maxCount, tags[i].tagCount);
                    tagsHTML += "<a title='" + tags[i].tagCount + "' class='tagPanel' style='"
                        + style.font + style.color + style.padding + "' href='/tags/"
                        + tags[i].tagNameURLEncoded +"'>" + tags[i].tagName + "</a> ";
                }
                $("#tagsPanel").append(tagsHTML + "<div class='clear'></div>");
            }
            setTagsPanel();
        </script>
    </body>
</html>
