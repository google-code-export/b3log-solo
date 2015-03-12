# 开发常见问题 #

---



---

<p align='right'><a href='#Top.md'>#Top</a></p>
## `本地开发初始化` ##
  1. 请使用邮件帐号：test@b3log.org 进行初始化。

---

## `code.google.com 查看项目密码被重置?` ##
> > 修改本地 dns 设置，添加 “66.249.89.104 code.google.com“ 记录，其中 66.249.89.104 为 google.com 的 dns 解析结果。
  1. 打开命令行窗口，执行 `ping code.google.com` ，得到当前可用的 Google ip “66.249.89.104” 。
  1. 找到本地的 dns 配置文件路径，windows 7 下为 “C:\Windows\System32\drivers\etc\hosts“ 。
  1. 复制该文件到桌面，编辑该文件并添加一行配置 “66.249.89.104 code.google.com” ，保存文件，将修改后的文件覆盖 “C:\Windows\System32\drivers\etc\hosts” 。（可避免 windows 7下直接修改 “C:\Windows\System32\drivers\etc\hosts” 文件后因为没有配置用户对 hosts 文件的权限而无法保存的问题）。
  1. 在命令行窗口执行 `ipconfig /flushdns` 刷新本地缓存。

---

## `Eclipse 开发的错误` ##
  1. gae:run 服务正常启动后，关闭服务时，点击控制台红色按钮关闭不了服务；这个需要同样运行一条 maven 命令，gae:stop 即可成功关闭服务。
  1. mvn eclipse:eclipse 你感觉很慢？这个和您的网络状况有关，只能麻烦您打坐等待，谁要咱们在墙内呢！
  1. pom.xml 报错 这个您可以忽略掉，这个目前原因不明，很有可能是 m2e 的一个 bug。

---

## `其他` ##
  * 有开发问题或者想与大家分享你解决的问题，可以发送你的内容至：DL88250@gmail.com，邮箱主题为：B3log FAQ。

---
