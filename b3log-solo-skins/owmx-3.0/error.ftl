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
        <div id="a">
            <header>
			    <a href="/" id="logoTitle">${blogTitle}</a>
				<p>${blogSubtitle}</p>
			</header>
            <div id="b">
				<article>
                    <h1 class="error-title">${notFoundLabel}</h1>
                    <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>
				</article>
			    <aside><nav>
				<h4>Navigation</h4>
			        <ul>
			            <#list pageNavigations as page>
			            <li>
			                <a href="${page.pagePermalink}">
			                    ${page.pageTitle}
			                </a>
			            </li>
			            </#list>
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
				</nav></aside>
                <div class="clear"></div>
            </div>
            <#include "article-footer.ftl">
        </div>
    </body>
</html>
