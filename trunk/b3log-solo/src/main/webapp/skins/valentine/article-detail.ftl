<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head> 
        <title>${article.articleTitle} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="<#list article.articleTags?split(',') as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>"/>
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
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div id="wrapper-sub">
            <div id="header-rinside"><!-- header --></div>
            <div id="content-rinside">
                <div id="rin-pagecontents">
                    <div class="post-individual">
                        <div class="posttime-blue">
                            <div class="posttime-MY"></div>
                            <div class="posttime-D"></div>
                        </div>
                        <div class="posttitle-page">
                            <h1 class="blue sIFR-replaced">
                                <span class="sIFR-alternate">${article.articleTitle}</span></h1>
                        </div>
                        <p class="postdetails-blue">Posted by: author<br/>
                            filed under: <a href="#" title="View all" rel="category tag">under</a><br/>
                            Tags: <a href="#" rel="tag">tag</a></p>
                        <p>&nbsp;</p>
                        <!-- zomg Loop loop -->
                        <div class="homeentry">
                            text
                        </div>
                        <!-- Comments -->
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>

                        <!-- You can start editing here. -->

                        <h2 class="blue sIFR-replaced">
                            <span class="sIFR-alternate"> 1 comment to date:</span></h2>
                        <p>&nbsp;</p>
                        <#include "comment.ftl">
                        <p>&nbsp;</p>
                    </div>
                    <!-- End loop --></div>
                <div id="left-sidebar">
                    <div id="author-tag"><!--author tag --></div>
                    <#include "sidebar.ftl">
                </div>
            </div>
            <div id="prefooter-rin">
                <div id="rss-left"><!-- RSS Icon--></div>
                <div id="rss-lefttext"><a href="http://lambsand.appspot.com/feed">Subscribe to RSS</a></div>
            </div>
            <#include "footer.ftl">
        </div>
    </body>
</html>
