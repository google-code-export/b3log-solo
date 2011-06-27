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
            <div class="content">
                <div class="header">
                    <h1 class="title">
                        <a href="/" id="logoTitle" >
                            ${blogTitle}
                        </a>
                    </h1>
                    <span class="sub-title">${blogSubtitle}</span>
                </div>
                <div id="header-navi">
                    <div class="left">
                        <ul>
                            <li>
                                <a class="home" href="/"></a>
                            </li>
                            <li>
                                <a href="/tags.html">${allTagsLabel}</a>
                            </li>
                            <li>
                                <a href="/blog-articles-feed.do">
                                    ${atomLabel}
                                    <img src="/images/feed.png" alt="Atom"/>
                                </a>
                            </li>
                            <li>
                                <a class="lastNavi" href="javascript:void(0);"></a>
                            </li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="error-body">
                    <div class="error-panel">
                        <h1 class="error-title">${notFoundLabel}</h1>
                        <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>
                    </div>
                </div>
                <div class="footer">
                    <div class="left copyright">
                        <span style="color: gray;">&copy; ${year}</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
                        Powered by
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <span style="color: orange;">B</span>
                            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                            <span style="color: green;">L</span>
                            <span style="color: red;">O</span>
                            <span style="color: blue;">G</span>&nbsp;
                            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                        ver ${version}&nbsp;&nbsp;
                        Theme by <a href="http://www.neoease.com" target="_blank">NeoEase</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
