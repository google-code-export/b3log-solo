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

        <!-- Stylesheets -->
        <link rel="stylesheet" href="/skins/${skinDirName}/style.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="/skins/${skinDirName}/jquery.lightbox-0.5.css" type="text/css" media="screen" />
        <link rel='stylesheet' id='/skins/${skinDirName}/wp-pagenavi-css'  href='pagenavi-css.css' type='text/css' media='all' />
        <link rel="stylesheet" href="/skins/${skinDirName}/dd-formmailer.css" type="text/css" media="screen" />
        <style type="text/css">
            #aktt_tweet_form {
                    margin: 0;
                    padding: 5px 0;
            }
            #aktt_tweet_form fieldset {
                    border: 0;
            }
            #aktt_tweet_form fieldset #aktt_tweet_submit {
                    float: right;
                    margin-right: 10px;
            }
            #aktt_tweet_form fieldset #aktt_char_count {
                    color: #666;
            }
            #aktt_tweet_posted_msg {
                    background: #ffc;
                    display: none;
                    margin: 0 0 5px 0;
                    padding: 5px;
            }
            #aktt_tweet_form div.clear {
                    clear: both;
                    float: none;
            }
        </style>
        <link rel="stylesheet" href="/skins/${skinDirName}/wp-syntax.css" type="text/css" media="screen" />
        <style type="text/css">.broken_link, a.broken_link {text-decoration: line-through}</style>
        <link rel="stylesheet" href="/skins/${skinDirName}/print.css" type="text/css" media="print" />


        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>

        <!--  js ---->
        <script type="text/javascript">
            var currentSkinRoot = "/skins/${skinDirName}/";
        </script>
        <script type="text/javascript" src="/skins/${skinDirName}/js/jquery.lightbox-0.5.js"></script>
        <script type="text/javascript" src="/skins/${skinDirName}/js/cookies.js"></script>
        <script type="text/javascript" src="/skins/${skinDirName}/js/utility.js"></script>

        ${htmlHead}
    </head>
    <body class="home blog">
        <#include "common-top.ftl">
        <div id="wrapper">
            <div id="header">
                <#include "article-header.ftl">
            </div>
            <div id="content">
                <div class="main">
                    <div id="main">
                        <div id="main-aux">
                            <div id="patch"></div>
                            <div id="main-aux1">
                                <div id="tagsPanel">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="sidebar">
                    <#include "article-side.ftl">
                </div>
                <div class="clear"></div>
            </div>
            <div class="footer">
                <#include "article-footer.ftl">
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
