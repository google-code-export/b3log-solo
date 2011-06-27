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
        <div class="wrapper">
            <div class="header">
                <div class="left">
                    <h1>
                        <a href="/">
                            ${blogTitle}
                        </a>
                    </h1>
                    <span class="sub-title">${blogSubtitle}</span>
                </div>
                <div class="right">
                    <ul>
                        <li>
                            <a class="home" href="/">Blog</a>
                        </li>
                        <li>
                            <a href="/tags.html">Tags</a>
                        </li>
                        <li>
                            <a href="/blog-articles-feed.do">
                                Atom
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="clear"></div>
            </div>
            <div>
                <div class="main">
                    <div class="main-content">
                        <div class="paddingBottom12">
                            <h1>${notFoundLabel}</h1>
                            <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>
                        </div>
                    </div>
                    <div class="main-footer"></div>
                </div>
                <div class="side-navi">
                </div>
                <div class="clear"></div>
                <div class="footer">
                    <div class="copyright">
                        &copy; ${year} - <a href="http://${blogHost}">${blogTitle}</a><br/>
                        Powered by
                        <a class="b3log" href="http://b3log-solo.googlecode.com" target="_blank">
                            <span style="color: orange;">B</span>
                            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                            <span style="color: green;">L</span>
                            <span style="color: red;">O</span>
                            <span style="color: blue;">G</span>&nbsp;
                            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                        ver ${version}<br/>
                        Theme by <a href="http://www.madeincima.eu/" target="_blank">Andrea</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
