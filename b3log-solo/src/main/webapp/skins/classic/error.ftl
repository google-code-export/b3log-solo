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
        <div class="content">
            <div class="header">
                <div class="marginBottom12">
                    <h1 class="title">
                        <a href="/" id="logoTitle" >
                            ${blogTitle}
                        </a>
                        <span class="sub-title">${blogSubtitle}</span>
                    </h1>
                </div>
                <div class="marginLeft12">
                    <a href="/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
                    <a href="/blog-articles-feed.do">${atomLabel}</a><a href="/blog-articles-feed.do"><img src="/images/feed.png" alt="Atom"/></a>
                </div>
            </div>
            <div class="body">
                <h1 class="error-title">${sorryLabel}</h1>
                <div class="error-panel">
                    <h1>${notFoundLabel}</h1>
                    ${returnTo1Label}<a href="http://${blogHost}">${blogTitle}</a>
                </div>
            </div>
            <div class="footer error-footer">
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
                Theme by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
            </div>
        </div>
    </body>
</html>