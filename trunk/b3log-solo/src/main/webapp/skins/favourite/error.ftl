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
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
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
                        <a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=61357158&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:61357158:43" alt="点击这里给我发消息" title="点击这里给我发消息"></a>
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
        <script type="text/javascript">
            var initIndex = function () {
                // common-top.ftl use state
                jsonRpc.adminService.isAdminLoggedIn(function (result, error) {
                    if (result && !error) {
                        var loginHTML = "<span class='left' onclick='clearAllCache();'>${clearAllCacheLabel}&nbsp;|&nbsp;</span>"
                            + "<span class='left' onclick='clearCache();'>${clearCacheLabel}&nbsp;|&nbsp;</span>"
                            + "<div class='left adminIcon' onclick=\"window.location='/admin-index.do';\" title='${adminLabel}'></div>"
                            + "<div class='left'>&nbsp;|&nbsp;</div>"
                            + "<div onclick='adminLogout();' class='left logoutIcon' title='${logoutLabel}'></div>";
                        $("#admin").append(loginHTML);
                    } else {
                        $("#admin").append("<div class='left loginIcon' onclick='adminLogin();' title='${loginLabel}'></div>");
                    }
                });
                jsonRpc.statisticService.incBlogViewCount(function (result, error) {});
            }
            initIndex();

            var clearCache = function () {
                var locationString = window.location.toString();
                var indexOfSharp = locationString.indexOf("#");
                var url = locationString.substring(locationString.lastIndexOf("/"),
                (-1 == indexOfSharp)? locationString.length : indexOfSharp);
                jsonRpc.adminService.clearPageCache(url);
                window.location.reload();
            }

            var clearAllCache = function () {
                jsonRpc.adminService.clearAllPageCache();
                window.location.reload();
            }
        </script>
    </body>
</html>
