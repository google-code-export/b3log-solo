<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${page.pageTitle} - ${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${page.pageTitle}" />
    <meta name="description" content="${metaDescription}" />
    </@head>
</head>
<body class="classic-wptouch-bg ">
    <#include "header.ftl">
	<div class="content single">
		<div class="post">
			${page.pageContent}
		</div>
		<@comments commentList=pageComments permalink=page.pagePermalink></@comments>
	</div>
	<#include "footer.ftl">
    <@comment_script oId=page.oId></@comment_script>
</body>
</html>