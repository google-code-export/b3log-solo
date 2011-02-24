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
		<div id="top">
		    <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
		        <span style="color: orange;margin-left:0px;">B</span>
		        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
		        <span style="color: green;">L</span>
		        <span style="color: red;">O</span>
		        <span style="color: blue;">G</span>&nbsp;
		        <span style="color: orangered; font-weight: bold;">Solo</span>
		    </a>
		    <div class="clear"></div>
		</div>
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
			            <li>
			                <a class="home" href="/">Home</a>
			            </li>
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
			<footer>
				<a href="http://code.google.com/appengine" target="_blank"><img src="http://code.google.com/appengine/images/appengine-noborder-120x30.gif" alt="Powered by Google App Engine" /></a>&nbsp;&nbsp;
			    <span>© 2011</span> - <a href="http://${blogHost}">${blogTitle}</a>
			    Powered by
			    <a href="http://b3log-solo.googlecode.com" target="_blank">
			        <span style="color: orange;">B</span>
			        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
			        <span style="color: green;">L</span>
			        <span style="color: red;">O</span>
			        <span style="color: blue;">G</span>&nbsp;
			        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
			    ver ${version}&nbsp;&nbsp;
			    Theme by <a href="http://lightdian.b3log.org/" target="_blank">Dongxu Wang</a> & <a href="http://www.jabz.info/contact/jonas-jared-jacek/" title="Profile of Jonas Jacek">Jonas Jacek</a>.
			</footer>
        </div>
    </body>
</html>
