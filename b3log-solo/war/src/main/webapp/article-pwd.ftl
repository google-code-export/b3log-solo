<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${articleViewPwdLabel}</title>
        <meta name="keywords" content="GAE 博客,GAE blog,b3log" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="${contextPath}/css/default-init${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link rel="icon" type="image/png" href="${contextPath}/favicon.png" />
        <script type="text/javascript" src="${contextPath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="logo">
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" style="width: 153px;height:56px;" alt="B3log" title="B3log" src="/images/logo.jpg"/>
                        </a>
                    </div>
                    <div class="main">
                        <h2>
                            ${articleTitle}   
                        </h2>
                        <div>
                            ${articleAbstract}
                        </div>
                        <label for="pwdTyped">访问密码：</label>
                        <input type="password" id="pwdTyped" />
                        <button id="confirm" onclick="confirm();">${confirmLabel}</button>
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
        <script type="text/javascript">
            function confirm() {
                var requestJSONObject = {
                    "articleId": "${articleId}",
                    "articlePermalink": "${articlePermalink}",
                    "pwdTyped": $("#pwdTyped").val()
                };
                
                $.ajax({
                    url: "/console/article-pwd",
                    type: "POST",
                    data: JSON.stringify(requestJSONObject),
                    success: function(result, textStatus){
                        if (result.sc) {
                            alert(result.msg);
                        } else {
                            $("html").html(result);
                        }
                    }
                });
            }
        </script>
    </body>
</html>
