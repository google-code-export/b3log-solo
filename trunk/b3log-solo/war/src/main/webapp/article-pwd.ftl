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
        <div>
            ${articleTitle}   
        </div>
        <div>
            ${articleAbstract}
        </div>

        <div>
            <input id="pwdTyped" />
            <button id="confirm" onclick="confirm();">${confirmLabel}</button>
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
                        if (!result.sc) {
                            alert(result.msg);
                            
                            return;
                        }
                    }
                });
            }
        </script>
    </body>
</html>
