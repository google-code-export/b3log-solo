### 部署至本地容器（Tomcat） ###

环境
  * MySQL 5+
  * Tomcat 6+

<ol>
<li>在 MySQL 中创建名为 <code>b3log</code> 的数据库</li>
<li>配置 JDBC 数据源</li>
按照数据库连接配置修改 <code>${b3log-solo-mysql-x.x.x}/WEB-INF/classes/local.properties</code> 文件里<br>
<pre><code>#### JDBC database Configurations ####<br>
</code></pre>
区域中<br>
<pre><code>jdbc.URL=jdbc:mysql://localhost:3306/b3log?useUnicode=yes&amp;characterEncoding=UTF-8<br>
jdbc.username=root<br>
jdbc.password=<br>
</code></pre>
<li>修改应用路径</li>
修改 <code>${b3log-solo-x.x.x}/WEB-INF/classes/latke.properties</code> 中的 <code>Server</code> 区域，配置成部署路径。<br>
<pre><code>serverScheme=http<br>
serverHost=你自己的域名<br>
serverPort=端口填 80<br>
staticServerScheme=http<br>
staticServerHost=你自己的域名<br>
staticServerPort=端口填 80<br>
</code></pre>
<li>部署</li>
将 <code>${b3log-solo-mysql-x.x.x}</code> 下的内容拷贝到部署目录下（tomcat/webapps/${contextPath}）<br>
<li>启动容器</li>
<li>在浏览器中访问：<a href='http://${server}:${port}'>http://${server}:${port}</a> 进行初始化</li>
</ol>


_备注_：
  * `${server}:${port}` 为你容器的访问地址与端口
  * `${b3log-solo-mysql-x.x.x}` 为 B3log Solo for MySQL 根目录（请注意解压目录）

---

_注意_：
  * 0.4.1 只能部署到 ROOT 下
  * 0.4.5 后可以部署到 tomcat/webapps/${contextPath} 下，请注意配置 latke.properties
  * 关注启动日志