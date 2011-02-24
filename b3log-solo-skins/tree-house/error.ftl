<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${notFoundLabel} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="robots" content="noindex, follow"/>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <div class="wrapper">
            <div class="bg-bottom">
                <#include "common-top.ftl">
                <div class="content">
                    <div class="header">
                        <div class="header-navi right">
                            <ul>
                                <li>
                                    <a href="/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
                                </li>
                                <li>
                                    <a href="/blog-articles-feed.do">${atomLabel}</a><a href="/blog-articles-feed.do"><img src="/images/feed.png" alt="Atom"/></a>
                                </li>
                            </ul>
                        </div>
                        <div class="header-title">
                            <h1>
                                <a href="/" id="logoTitle" >
                                    ${blogTitle}
                                </a>
                            </h1>
                            <div>${blogSubtitle}</div>
                            <embed width="228" height="239" type="application/x-shockwave-flash"
                                   menu="false" name="http://blog.thepixel.com/wp-content/themes/PixelBlog2/flash/fan"
                                   wmode="transparent" loop="true" pluginspage="http://www.adobe.com/go/getflashplayer"
                                   quality="high" src="/skins/${skinDirName}/images/fan.swf"
                                   style="position: absolute;top:112px;left:265px;">
                        </div>
                    </div>
                    <div class="body">
                        <h1 class="error-title">${sorryLabel}</h1>
                        <div class="error-panel">
                            <h2>${notFoundLabel}</h2>
                            ${returnTo1Label}<a href="http://${blogHost}">${blogTitle}</a>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="footer">
                        <span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
                        Powered by
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <span style="color: orange;">B</span>
                            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                            <span style="color: green;">L</span>
                            <span style="color: red;">O</span>
                            <span style="color: blue;">G</span>&nbsp;
                            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                        ver ${version}&nbsp;&nbsp;
                        Theme by <a href="http://www.thepixel.com/blog" target="_blank">Pixel</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
