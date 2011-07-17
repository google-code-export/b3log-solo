<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>${notFoundLabel} - ${blogTitle}</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="robots" content="noindex, follow"/>
        <link type="text/css" rel="stylesheet" href="/css/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
        <link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
    </head>
    <body>
        <#include "common-top.ftl">
        <div id="wrapper-sub">
            <div id="header-page"><!-- header --></div>
            <div id="content-page">
                <div id="single-pagecontents">
                    <div class="error-panel">
                        <h1 class="error-title">娴犲骸澧犻張澶婇獓鐏炴唻绱濈仦閬嶅櫡閺堝楠囨惔娆欑礉<br/>
                            鎼存瑩鍣烽張澶夐嚋妞ょ敻娼伴敍宀�箛閸︼拷{notFoundLabel}</h1>
                        <a href="http://${blogHost}">${returnTo1Label}${blogTitle}</a>
                    </div>
                </div>
                <div id="left-sidebar">
                    <div id="about-tag"><!--about tag --></div>
                    <div id="sidebar">
                        <p>&nbsp;</p>
                        <div class="sidebar-titletagcolor">
                            <h3 class="pagetitle">Powered By</h3>
                        </div>
                        <p align="center">
                            <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif"
                                 alt="閻拷Google App Engine 閹绘劒绶甸弨顖涘瘮" />
                        </p>

                        <p align="center">
                            <a href="http://www.b3log.org" target="_blank">
                                <img height="55" width="140" alt="B3log Logo" src="http://code.google.com/p/b3log-solo/logo?cct=1287802701" />
                            </a>
                        </p>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                    </div>
                </div>
            </div>
            <div id="prefooter-page">
                <div id="rss-page"><!-- RSS Icon--></div>
                <div id="rss-lefttext"><a href="/blog-articles-feed.do">Subscribe to RSS</a></div>
            </div>
            <!-- start footer -->
            <div id="footer">
                <div class="left copyright">
                    <span style="color:white;">&copy; ${year}</span> - <a style="color:white;" href="http://${blogHost}">${blogTitle}</a><br/>
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        <span style="color: orange;">B</span>
                        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                        <span style="color: green;">L</span>
                        <span style="color: red;">O</span>
                        <span style="color: blue;">G</span>&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                    ver ${version}&nbsp;&nbsp;
                    Theme by <a style="color:white;" href="http://www.iprimidieci.com/" target="_blank">Primi</a> & <a style="color:white;" href="http://lamb.b3log.org" target="_blank">Lamb</a>.
                </div>
            </div>
        </div>
    </body>
</html>
