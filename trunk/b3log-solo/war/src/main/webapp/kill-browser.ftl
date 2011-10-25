<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${blogTitle}</title>
        <meta name="keywords" content="GAE 博客,blog,b3log,kill IE" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客,Let's kill IE6" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css" charset="utf-8" />
        <link type="text/css" rel="stylesheet" href="/css/default-init${miniPostfix}.css" charset="utf-8" />
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="logo">
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" style="width: 153px;height:56px;" alt="B3log" title="B3log" src="/images/logo.png"/>
                        </a>
                    </div>
                    <div class="main">
                        ${killBrowserLabel}
                        <img class="kill" src='/images/kill-browser.png' title='Kill IE6' alt='Kill IE6'/>
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" class="icon" alt="B3log" title="B3log" src="/favicon.png"/>
                        </a>
                    </div>

                    <span class="clear"></span>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year} - <a href="http://${blogHost}">${blogTitle}</a><br/>
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        ${b3logLabel}&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                    ver ${version}
                </div>
            </div>
        </div>
    </body>
</html>
