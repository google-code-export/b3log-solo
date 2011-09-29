<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${welcomeToSoloLabel}</title>
        <meta name="keywords" content="GAE 博客,GAE blog,b3log,init" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客,初始化程序" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css" charset="utf-8" />
        <link type="text/css" rel="stylesheet" href="/css/default-init${miniPostfix}.css" charset="utf-8" />
        <link rel="icon" type="image/png" href="/favicon.png" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js" charset="utf-8"></script>
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="introImg">
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" style="width: 153px;height:56px;" alt="B3log" title="B3log" src="/images/logo.png"/>
                        </a>
                    </div>
                    <div class="introContent">
                        <h2>
                            <span>Welcome to</span>
                            <a target="_blank" href="http://b3log-solo.googlecode.com">
                                ${b3logLabel}
                                <span style="color: orangered;">&nbsp;Solo</span>
                            </a>
                        </h2>
                        <div>
                            ${userName1Label}<input /><br>
                            ${userPassword1Label}<input /><br>
                            重复${userPassword1Label}<input /><br>
                            <button onclick='initSys();'>${initLabel}</button>
                            <span class="clear"></span>
                        </div>
                        <div>
                            <p>This web page provides the initialization of "Preferences" and "Blog Statistic".</p>
                            <ul>
                                <li>
                                    If you had initialized your blog, MUST NOT click it to prevent 
                                    <font color="red">data loss</font>! (
                                    <a href="/admin-index.do#main">Click</a> 
                                    to return Admin Console).
                                </li>
                                <li>
                                    If this is your first time using 
                                    <a target="_blank" href="http://b3log-solo.googlecode.com">
                                        ${b3logLabel}&nbsp;
                                        <span style="color: orangered; font-weight: bold;">Solo</span>
                                    </a>
                                    , please click "Initial" button to initialize your blog.
                                </li>
                            </ul>
                            <button onclick='initSys();'>${initLabel}</button>
                            <span class="clear"></span>
                        </div>
                    </div>

                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        <img border="0" style="width:16px;height:16px;" class="introSign" alt="B3log" title="B3log" src="/favicon.png"/>
                    </a>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year}
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
                        ${b3logLabel}&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span></a>,
                    ver ${version}
                </div>
            </div>
        </div>
        <script type="text/javascript" src="js/common.js"></script>
        <script type="text/javascript">
            var initSys = function () {
                if(confirm("${confirmInitLabel}")){
                    var rslt = jsonRpc.adminService.init();
                    if ("INIT_B3LOG_SOLO_SUCC" === rslt.sc) {
                        window.location.href = "/admin-index.do#tools/user-list";
                    } else {
                        alert("init error!");
                    }
                }
            }
        </script>
    </body>
</html>
