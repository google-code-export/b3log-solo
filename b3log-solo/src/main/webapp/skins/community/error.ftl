<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${notFoundLabel} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="author" content="B3log Team"/>
        <meta name="generator" content="B3log"/>
        <meta name="copyright" content="B3log"/>
        <meta name="revised" content="B3log, 2010"/>
        <meta name="robots" content="noindex, follow"/>
        <meta http-equiv="Window-target" content="_top"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/community/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="header">
            <div class="header-navi">
                <div class="header-navi-main content">
                    <div class="left">
                        <a href="/" class="header-title">
                            ${blogTitle}
                        </a>
                        <span class="sub-title">${blogSubtitle}</span>
                    </div>
                    <div class="right">
                        <ul class="tabs">
                            <li class="tab">
                                <a href="/">${homeLabel}</a>
                            </li>
                            <li class="tab">
                                <a href="/tags.html">${allTagsLabel}</a>
                            </li>
                            <li class="tab">
                                <a href="/blog-articles-feed.do">
                                    <span class="left">${atomLabel}</span>
                                    <span class="atom-icon"></span>
                                    <span class="clear"></span>
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
        <div class="content">
            <h1 class="error-title">${sorryLabel}</h1>
            <div class="error-panel">
                <h1>${notFoundLabel}</h1>
                ${returnTo1Label}<a href="http://${blogHost}">${blogTitle}</a>
            </div>
        </div>
        <div class="footer">
            <span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
            Powered by
            <a href="http://b3log-solo.googlecode.com" target="_blank" style="text-decoration: none;">
                <span style="color: orange;">B</span>
                <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                <span style="color: green;">L</span>
                <span style="color: red;">O</span>
                <span style="color: blue;">G</span>&nbsp;
                <span style="color: orangered; font-weight: bold;">Solo</span></a>,
            ver ${version}&nbsp;&nbsp;
            Theme by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a> & <a href="http://demo.woothemes.com/skeptical/" target="_blank">Skeptical</a>.
        </div>
    </body>
</html>
