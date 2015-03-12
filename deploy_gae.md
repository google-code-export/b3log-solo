### 部署至 GAE ###
<ol>
<li>创建 GAE 应用</li>
假设 ID 为 <code>${application-id}</code>,（没有 GAE 帐号的话请点击<a href='http://appengine.google.com/'>这里</a>申请）<br>
<li>配置 Solo</li>
将 <code>${b3log-solo-x.x.x}/WEB-INF/appengine-web.xml</code> 文件里的<br>
<pre><code>&lt;application&gt;solo-demo&lt;/application&gt;<br>
</code></pre>
修改为<br>
<pre><code>&lt;application&gt;${application-id}&lt;/application&gt;<br>
</code></pre>

修改 <code>${b3log-solo-x.x.x}/WEB-INF/classes/latke.properties</code> 中的 <code>Server</code> 区域，配置成自己的访问地址。<br>
<pre><code>serverScheme=http<br>
serverHost=你自己的域名<br>
serverPort=端口填 80<br>
staticServerScheme=http<br>
staticServerHost=你自己的域名<br>
staticServerPort=端口填 80<br>
</code></pre>


<li>上传 Solo</li>
（Windows）在命令行下 <code>cd</code> 进入 <code>${gae.home}/bin/</code> 目录，执行：<br>
<pre><code>appcfg.cmd update ${b3log-solo-x.x.x}<br>
</code></pre>
<li>部署完毕后请在<a href='http://appengine.google.com'>GAE 管理控制台</a> Verssions 一栏中确认启用刚部署的版本为默认版本</li>
<li>在浏览器中访问：<a href='http://${application-id}.appspot.com'>http://${application-id}.appspot.com</a> 进行初始化</li>
</ol>


_备注_：
  * `${application-id}` 为你自己的 GAE 应用 ID
  * `${gae.home}` 为 GAE SDK 根目录
  * `${b3log-solo-x.x.x}` 为 B3log Solo 根目录（请注意解压目录）

---

_注意_：
  * 如果你不能访问 http://${application-id}.appspot.com 说明你的网络受 [GFW](http://en.wikipedia.org/wiki/Golden_Shield_Project) 限制，请绑定域名。（可以考虑[使用免费的 b3log.org 二级域名](http://88250.b3log.org/apply-b3log-domain.html)）
  * 初始化以前请确保数据存储中没有其他应用的数据（例如 Micolog）
  * 一定要使用自己的 Google 帐号进行初始化，不然会出现文章、评论发布问题
  * 初始化功能只有在第一次部署时才可用，一旦初始化过后，该功能将被锁定
  * 如果出现 java.net.ConnectException: Connection timed out: connect，请重试