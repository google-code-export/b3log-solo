<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="${metaDescription}"/>
        <meta http-equiv="pragma" content="no-cache"/>
        <meta name="author" content="b3log-solo.googlecode.com"/>
        <meta name="revised" content="b3log, 9/10/10"/>
        <meta name="generator" content="NetBeans, GAE"/>
        <meta http-equiv="Window-target" content="_top"/>
        <title>${blogTitle}</title>
        <link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="/blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
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
                                <#list pageNavigations as page>
                                <li>
                                    <a href="/page.do?oId=${page.oId}">
                                        ${page.pageTitle}
                                    </a>&nbsp;&nbsp;
                                </li>
                                </#list>
                                <li>
                                    <a href="/tags.do">${allTagsLabel}</a>&nbsp;&nbsp;
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
                                   quality="high" src="/skins/tree-house/images/fan.swf"
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
                        <#include "article-footer.ftl">
                    </div>
                </div>
            </div>
        </div>
        <div class='goTopIcon' onclick='goTop();'></div>
        <div class='goBottomIcon' onclick='goBottom();'></div>
    </body>
</html>
