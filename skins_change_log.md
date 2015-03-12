### ver 0.4.5 ###
  1. 皮肤中删除 error.ftl，参考 [Issue 411](https://code.google.com/p/b3log-solo/issues/detail?id=411)
  1. images/emotions 中添加表情图片（从 /images/emotions 下任选一套)； CSS 中存在表情路径则进行修改
  1. tags.ftl
```
common.buildTags() 修改为 Util.buildTags()
```
  1. footer.ftl
```
var common = new Common({XXX}); 修改为 var Label = {XXX};
common.goTop();common.goBottom();common.init();common.replaceSideEm(XXX); 相应修改为 
Util.goTop();Util.goBottom();Util.init();Util.replaceSideEm(XXX); 并放入 $(document).ready(function () {}); 中
```
  1. article.ftl 如果“随机阅读显示数目”/“相关阅读显示数目”/“站外相关阅读显示数目”配置为 0 时，不发加载请求：
```
<#if 0 != randomArticlesDisplayCount>
    page.loadRandomArticles();
</#if>
<#if 0 != relevantArticlesDisplayCount>
    page.loadRelevantArticles('${article.oId}', '<h4>${relevantArticles1Label}</h4>');
</#if>
<#if 0 != externalRelevantArticlesDisplayCount>
    page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
</#if>
```
  1. 服务器请求路径
> 请求服务器资源时，需要使用 ${serverScheme}://${serverHost}:${serverPort}${contextPath}/xxx

> 其中，${serverScheme}://${serverHost}:${serverPort} 可以使用 ${server} 表示；
> ${serverScheme}://${serverHost}:${serverPort}${contextPath} 可以使用 <font color='red'>${servePath}</font>_表示。_

> 请求静态服务器资源时，需要使用 ${staticServerScheme}://${staticServerHost}:${staticServerPort}/xxx

> 其中，${staticServerScheme}://${staticServerHost}:${staticServerPort} 可以使用 ${staticServer} 表示；
> ${staticServerScheme}://${staticServerHost}:${staticServerPort} 可以使用 <font color='red'>${staticServePath}</font>_表示。_

> 这样做主要是为了能使应用部署在不同的上下文路径（context path）时代码可以不用修改；可以进行静态资源分离，单独将静态资源部署到另一 HTTP 服务器。

> <b>修改步骤</b>：

> 查找皮肤模版中 `<link>`、`<script>`、`<img>`、`<a>` 等标签的链接地址，静态资源请求加入 `${staticServePath}`作为前缀，比如引入 css；其他请求加入 `${servePath}` 作为前缀，比如点击链接。

> <b>关注</b>：
    * CSS 中请求资源时一定要使用相对路径（../）
    * JS 中请求资源（AJAX）时一定要使用 ${servePath}/${staticServePath} 等服务器变量，建议在 footer.ftl 中初始化一个 JS 对象 latkeConfig：
```
var latkeConfig = {
    "servePath": "${servePath}",
    "staticServePath": "${staticServePath}"
};
```
> > 在 JS 请求资源时统一使用该对象
    * 插件模版 plugin.ftl 中也可以使用服务器变量

### ver 0.4.1 ###
  1. footer.ftl
> > 底部添加 ${plugins}
  1. page.ftl
```
<@comments commentList=pageComments permalink=page.pagePermalink></@comments>
改为
<@comments commentList=pageComments article=page></@comments>
```
  1. article.ftl
```
<@comments commentList=articleComments permalink=article.articlePermalink></@comments>
改为
<@comments commentList=articleComments article=article></@comments>
```
  1. header.ftl
```
<a href="${page.pagePermalink}">${page.pageTitle}</a>
改为
<a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>
```
  1. macro-comments.ftl

> 由于添加了“是否允许评论”功能，须在回复按钮和评论表单外加上
```
<#if article.commentable>
XXX
</#if>
```
```
<#macro comments commentList permalink>
改为
<#macro comments commentList article>

${permalink}
改为
${article.permalink}
```

### ver 0.4.0 ###
  1. 使用 SyntaxHighlighter 时，可通过在 macro-comments.ftl 中的 page.load({language: {SHTheme: "shCoreEclipse"}}) language.SHTheme 参数指定所加载的样式。默认为 shCoreEclipse, 可设置为 shCoreDefault, shCoreDjango, shCoreEmacs, shCoreFadeToGrey, shCoreMDUltra, shCoreMidnight, shCoreRDark.
  1. jQuery 升级为 1.7，采用本地 js。修改 footer.ftl 如下：
```
<script type="text/javascript" src="/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
```
  1. 删除 article.ftl 和 page.ftl head 中的 <link .../>; 删除 macro-comment.ftl 中引入的 JS 文件：shAutoloader.js 和 shCore.js
  1. js, css 文件后添加 "?${staticResourceVersion}"，如：
```
<script type="text/javascript" src="/js/page${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
```

### ver 0.3.5 ###
  1. 删除 top-nav.ftl 文件
  1. 把所有 <#include "top-nav.ftl"> 换为 ${topBarReplacement}
  1. 在 article.ftl 中的 <@comment\_script oId=article.oId> 内添加 page.loadRelevantArticles('${article.oId}', '${relevantArticles1Label}');
  1. 在 article.ftl 中找到 <#if 0 != relevantArticles?size>，对其中的 div 加上 id="relevantArticles"，并移除其内部所有元素；删除 <#if 0 != relevantArticles?size> 及相匹配的 </#if>
  1. 修改 macro-comments.ftl 中 addComment
![http://imgmi.net/b/b55cff18.png](http://imgmi.net/b/b55cff18.png)