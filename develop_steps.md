### 文件结构 ###
| **名称** | **说明** |
|:-----------|:-----------|
| /css/`*` | 存放皮肤样式。 |
| /images/`*` | 存放皮肤用到的图片。 |
| /lang/`*` | 存放多语言配置文件。 |
| archive-articles.ftl | 某年某月所写文章的列表页面。 |
| article-list.ftl | 文章列表，复用于archive-articles.ftl，author-articles.ftl，tag-articles。 |
| article.ftl | 文章页面。 |
| author-articles.ftl | 某用户所写文章的列表页面。 |
| error.ftl | 错误页面。 |
| footer.ftl | 页尾，复用于各个页面。 |
| header.ftl | 页头，复用于各个页面。 |
| index.ftl | 首页页面。 |
| macro-comments.ftl | 评论列表及回复的宏定义，用于文章页面和自定义页面。 |
| macro-head.ftl | head 标签中的宏定义，用于各个页面。 |
| page.ftl | 自定义页面。在博客后台的页面管理中所建页面均使用该模板来进行显示。 |
| side.ftl | 侧边栏，包括公告、最新评论、评论最多的文章访问最多的文章、分类标签、友情链接、存档。复用于各个页面。 |
| tag-articles.ftl | 含有某标签文章的列表页面。 |
| tags.ftl | 罗列所有标签的页面。 |
| preview.png | 此皮肤首页截图（280\*160）。 |
| skin.properties | 皮肤信息。<br>name：皮肤名称（此名称将作为皮肤名列在 “后台管理->偏好设定->皮肤” 中）。 <br>version：皮肤版本号。<br>forSolo：该皮肤适用哪一版本的。<br>memo：备注，可记录所仿皮肤的站点或名称等信息。</tbody></table>

<h3>具体开发</h3>
<ol><li>在 skins 目录中新建文件夹，用于存放一款皮肤所需的各个文件，文件结构如上所示。<br>
</li><li>可参照 /neoease 目录中的文件进行创建及相应的修改。<br>
</li><li>建议从 index.ftl 开始修改。一旦 index.ftl 的结构确定下来，其余页面的结构与此基本类似。当然，其中通过 <#include "xxx.ftl"> 包含了一些通用的页面。<br>
</li><li>如 JS、CSS 文件需要压缩。可分别创建两个文件，在开发环境中使用 XXX.css/js, 在线上环境使用 XXX.min.css/js. 其中 ${miniPostfix} 变量用于区别线上环境和本地环境。使用示例如下：<br>
<pre><code><br>
&lt;pre&gt;&lt;code&gt;&lt;link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/css/${skinDirName}${miniPostfix}.css" charset="utf-8" /&gt;<br>
&lt;script type="text/javascript" src="/skins/${skinDirName}/js/${skinDirName}${miniPostfix}.js" charset="utf-8"&gt;&lt;/script&gt;<br>
</code></pre>
</code></pre></li></ol>

<hr />
<h3>注意事项</h3>
<ol><li>macro-comments.ftl 中标签 id 不可进行更改。<br>
</li><li>部分公用方法已被提取到 /js/common.js 和 /js/page.js 文件中。<br>
</li><li>/css/default-base.css 重置样式较多，可选择性的进行拷贝，如标签、标签、文章内容等。<br>
</li><li>文章内容须加上 class="article-body"，否则不能进行代码高亮。<br>
</li><li>Let's kill IE7- ,如果想要对所有浏览器进行支持，可修改 js/common.js 中 killIE 函数为 "killIE: function () {},"。<br>
</li><li>皮肤以外的 js、css 文件修改后需进行 build，否则在 XXX.min.js/css 下进行修改。<br>
</li><li>如有任何疑问或想把您做的皮肤与大家分享请与 <a href='http://vanessa.b3log.org'>Vanessa</a> 联系。<br>
</li><li>footer.ftl 中版权声明格式如下：<br>
<blockquote>© 2012 - XXX(博客作者) Powered by B3LOG  Solo, ver 0.4.0   Theme by XXX(皮肤作者).<br>