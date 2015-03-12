### 模板变量 ###
  * 如果模板变量的类型是 JSONObject，其字段属性参考 org.b3log.solo.model 包下的类
  * 每个页面都包含一些公共模板变量，这些模板变量单独列出（不在 `*`.ftl 表中给出）
  * 每个页面的模板变量如以“Label”结果，则为多语言配置文件，参看 lang\_zh\_CN.properties

---

**公共的模板变量**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>servePath</td><td>String</td><td>应用路径，可在 latke.properties 中配置</td><td>0.4.5 引入</td></tr>
<tr><td>staticServePath</td><td>String</td><td>静态资源路径，可在 latke.properties 中配置</td><td>0.4.5 引入</td></tr>
<tr><td>staticResourceVersion</td><td>String</td><td>js, css 版本号，防止缓存</td><td>0.4.0 引入</td></tr>
<tr><td>topBarReplacement</td><td>String</td><td>公用 top-bar.ftl 内容</td><td>0.3.5 引入</td></tr>
<tr><td>path</td><td>String</td><td>Action 路径</td><td></td></tr>
<tr><td>archiveDates</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>存档日期列表</td><td></td></tr>
<tr><td>articles</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>存档文章列表</td><td></td></tr>
<tr><td>blogTitle</td><td>String</td><td>博客标题</td><td></td></tr>
<tr><td>blogHost</td><td>String</td><td>博客地址</td><td>值如："vanessa.b3log.org:80"</td></tr>
<tr><td>blogSubtitle</td><td>String</td><td>博客子标题</td><td></td></tr>
<tr><td>htmlHead</td><td>String</td><td>用户自定义的 HTML Head</td><td></td></tr>
<tr><td>links</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>链接列表</td><td></td></tr>
<tr><td>localeString</td><td>String</td><td>区域设定字符串</td><td>值如："zh_CN"</td></tr>
<tr><td>metaKeywords</td><td>String</td><td>用户自定义的关键字</td><td></td></tr>
<tr><td>metaDescription</td><td>String</td><td>用户自定义的描述</td><td></td></tr>
<tr><td>mostCommentArticles</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>评论最多的文章列表</td><td></td></tr>
<tr><td>mostUsedTags</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>引用最多的标签列表</td><td></td></tr>
<tr><td>mostViewCountArticles</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>访问最多的文章列表</td><td></td></tr>
<tr><td>noticeBoard</td><td>String</td><td>用户自定义的公告栏</td><td></td></tr>
<tr><td>oId</td><td>String</td><td>存档日期对象 Id</td><td></td></tr>
<tr><td>pageNavigations</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>自定义页面列表</td><td></td></tr>
<tr><td>paginationFirstPageNum</td><td>Integer</td><td>文章列表分页第一页页码</td><td></td></tr>
<tr><td>paginationLastPageNum</td><td>Integer</td><td>文章列表分页最末页页码</td><td></td></tr>
<tr><td>paginationPageNums</td><td>List<code>&lt;</code>Integer<code>&gt;</code></td><td>文章列表分页页号</td><td></td></tr>
<tr><td>paginationPageCount</td><td>Integer</td><td>文章列表页数</td><td></td></tr>
<tr><td>recentComments</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>最新评论列表</td><td></td></tr>
<tr><td>skinDirName</td><td>String</td><td>当前使用的皮肤目录名</td><td></td></tr>
<tr><td>statistic</td><td>JSONObject</td><td>统计信息对象</td><td></td></tr>
<tr><td>users</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>用户列表</td><td></td></tr>
<tr><td>version</td><td>String</td><td>当前使用的 B3log Solo 版本</td><td></td></tr>
<tr><td>year</td><td>String</td><td>当前年份</td><td></td></tr>
</blockquote><blockquote></table></blockquote>

**archive-date-articles.ftl**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>archiveDate</td><td>JSONObject</td><td>存档日期对象</td><td></td></tr>
</blockquote><blockquote></table></blockquote>

**article-detial.ftl**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>article</td><td>JSONObject</td><td>文章对象</td><td></td></tr>
<tr><td>articleComments</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>文章评论列表</td><td></td></tr>
<tr><td>externalRelevantArticlesDisplayCount</td><td>Integer</td><td>站外相关文章显示数</td><td></td></tr>
<tr><td>nextArticlePermalink</td><td>String</td><td>下一篇文章链接</td><td></td></tr>
<tr><td>nextArticleTitle</td><td>String</td><td>下一篇文章标题</td><td></td></tr>
<tr><td>previousArticlePermalink</td><td>String</td><td>上一篇文章链接</td><td></td></tr>
<tr><td>previousArticleTitle</td><td>String</td><td>上一篇文章标题</td><td></td></tr>
</blockquote><blockquote></table></blockquote>

**page.ftl**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>page</td><td>JSONObject</td><td>自定义页面对象</td><td></td></tr>
<tr><td>pageComments</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>自定义页面评论列表</td><td></td></tr>
</blockquote><blockquote></table></blockquote>

**tag-articles.ftl**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>tag</td><td>JSONObject</td><td>标签对象</td><td></td></tr>
</blockquote><blockquote></table></blockquote>

**tags.ftl**
> <table border='1'>
<blockquote><tr><th>变量名</th><th>类型</th><th>说明</th><th>备注</th></tr>
<tr><td>tags</td><td>List<code>&lt;</code>JSONObject<code>&gt;</code></td><td>标签列表</td><td></td></tr>
</blockquote><blockquote></table>