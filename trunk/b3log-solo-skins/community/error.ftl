<#include "macro.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${notFoundLabel} - ${blogTitle}">
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="robots" content="noindex, follow"/>
        </@head>
    </head>
    <body>
        <#include "top-nav.ftl">
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
            <div class="error-panel">
                <h2>${notFoundLabel}</h2>
                ${returnTo1Label} <a href="http://${blogHost}">${blogTitle}</a>
            </div>
        </div>
        <div class="footer error-footer">
            <div class="content">
                <div>
                    <span style="color: gray;">&copy; ${year}</span> - <a href="http://${blogHost}">${blogTitle}</a>
                </div>
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
        </div>
    </body>
</html>
