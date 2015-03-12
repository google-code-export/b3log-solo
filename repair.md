## 修复 ##

在 B3log Solo 0.2.6 以前存在一些严重的缺陷，导致了数据出现问题；或由于配置不当造成后台不能登录。通过运行响应的修复程序可以将这些问题修复：

<ul>
<li>标签-文章计数不正确</li>
修复标签-文章计数：<code>/fix/tag-article-counter-repair.do</code>。<br>
<li>签名档配置问题</li>
将签名档恢复为默认的空内容：<code>/fix/restore-signs.do</code>。<br>
<i>注意</i>：使用这个修复功能，会自动发送原签名档内容到开发人员邮件。<br>
</ul>