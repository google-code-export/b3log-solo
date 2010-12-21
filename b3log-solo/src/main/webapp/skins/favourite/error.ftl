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
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div class="wrapper">
            <div class="content">
                <div class="header">
                    <h1 class="title">
                        <a href="/" id="logoTitle" >
                            ${blogTitle}
                        </a>
                    </h1>
                    <span class="sub-title">${blogSubtitle}</span>
                </div>
                <div class="roundtop"></div>
                <div class="error-body">
                    <div class="left main">
                        <div class="error-panel">
                            <h1 class="error-title">${notFoundLabel}</h1>
                            <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>

                        </div>
                    </div>
                </div>
                <div class="roundbottom"></div>
            </div>
        </div>
        <div class="footer">
            <div class="footer-icon"></div>
            <div class="info">
            <div class="left copyright">
                <span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
                Powered by
                <a href="http://b3log-solo.googlecode.com" target="_blank">
                <span style="color: orange;">B</span>
                <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                <span style="color: green;">L</span>
                <span style="color: red;">O</span>
                <span style="color: blue;">G</span>&nbsp;
                <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                ver ${version}
            </div>
            </div>
        </div>
    </body>
</html>
