# 使用常见问题 #

---



---

## `为何修改偏好设定一直出现“载入中”？` ##
请先检查您是否使用了代理程序（例如 GoAgent）访问？
如果不是代理的问题，请报 [Issue](http://code.google.com/p/b3log-solo/issues/entry)

---

<p align='right'><a href='#Top.md'>#Top</a></p>
## `如何重新初始化？` ##
重新初始化将删除所有数据：
  1. 正常登录
  1. 访问 /rm-all-data.do

---

<p align='right'><a href='#Top.md'>#Top</a></p>
## `GAE 版上传时出现 javax.net.ssl.SSLHandshakeException` ##
上传时出现报错：
```
javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException:
No name matching appengine.google.com found
```
可能是您修改了 hosts 文件，导致 HTTPS 认证失败，请尝试：
  1. 使用非安全方式上传，增加参数如：appcfg **--insecure** update xxxxx
  1. 删除 appengine.google.com 的 hosts 配置，尝试以其他方式（例如[代理方式](https://code.google.com/appengine/docs/java/tools/uploadinganapp.html#Using_an_HTTP_Proxy)）进行上传

---

<p align='right'><a href='#Top.md'>#Top</a></p>
## `为什浏览统计数么一直不变？` ##
博客访问数、文章浏览计数默认是半小时从缓存同步数据存储的，可以配置 WEB-INF/cron.xml 修改这个定时策略。

对数据有操作时（例如发/删/更新文章）都会清空缓存，这时的统计数是不会同步写入数据存储的。所以，统计数不会非常准确。

另外，由于[页面缓存](https://code.google.com/p/b3log-solo/wiki/page_cache)（默认开启）的原因，统计数在页面上是不会有改变的，除非页面缓存被清空了。

---

<p align='right'><a href='#Top.md'>#Top</a></p>
## `忘记登录口令怎么办？` ##
  * GAE 版本请到 [GAE 管理控制台](http://appengine.google.com) Datasotre 一栏查看 User 存储
  * 本地容器版本请直接查看数据表 User

---