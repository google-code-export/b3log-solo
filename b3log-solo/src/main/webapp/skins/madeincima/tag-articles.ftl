<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${tag.tagTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
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
        <link href="tag-articles-feed.do?oId=${oId}" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
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
                    <#include "common-articles.ftl">
                    <script type="text/javascript" src="/skins/${skinDirName}/js/enMonth.js"></script>
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
    </body>
</html>
