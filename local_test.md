### GAE 本地测试 ###
（Windows）在命令行下 `cd` 进入 `${gae.home}/bin/` 目录，执行：
```
dev_appserver.cmd ${b3log-solo-gae-x.x.x}
```
等启动完毕后，在浏览器中访问：http://localhost:8080。

_注_：
在本地测试环境下可能
  * 不能正常发送评论邮件
  * 不能同步社区文章、评论