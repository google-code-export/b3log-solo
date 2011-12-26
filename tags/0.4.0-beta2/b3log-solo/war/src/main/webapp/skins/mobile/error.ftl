<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${notFoundLabel} - ${blogTitle}">
        <meta name="keywords" content="${notFoundLabel},${metaKeywords}"/>
        <meta name="description" content="${sorryLabel},${notFoundLabel},${metaDescription}"/>
        <meta name="robots" content="noindex, follow"/>
        </@head>
    </head>
    <body class="classic-wptouch-bg ">
      	<#include "header.ftl">
		<div class="content single">
            <div class="post">
                <h1 class="error-title">${sorryLabel}</h1>
                <div class="error-panel">
                    <h1>${notFoundLabel}</h1>
                    ${returnTo1Label}<a href="http://${blogHost}">${blogTitle}</a>
                </div>
        	</div>
        </div>
  		<#include "footer.ftl">
    </body>
</html>