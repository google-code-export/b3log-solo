#summary IDE

### IDE ###
[B3log Solo](http://b3log-solo.googlecode.com) 是使用 [Maven2](http://maven.apache.org) 进行的项目构建，所以并不依赖于任何一款 Java IDE，只要支持 [Maven](http://maven.apache.org) 的 IDE 都可以进行开发。

[在线浏览代码](https://code.google.com/p/b3log-solo/source/browse)

下面是对 GAE 版的运行方式进行介绍。标准 Servlet 容器版本的运行方式与通常项目一样，这里不再描述。

#### `NetBeans`_（_<font color='red'>推荐的</font>）_####
  * `NetBeans`:  http://netbeans.org/downloads/index.html
##### 配置 #####
  1. 工具 -> 选项 -> 其他 -> Maven -> 外部&Maven主页 -> 浏览，把 Maven 的路径加上
  1. 用 SVN 把项目迁出 http://b3log-solo.googlecode.com/svn/trunk/b3log-solo
  1. 在根项目上进行构建（mvn install）
  1. 在 IDE 中打开项目，并展开（打开）所有模块项目。找到 B3log Solo (GAE) 项目，右键 ->定制 -> gae:unpack（_在每次 GAE SDK 升级后都需要运行此命令_）_<br /><img src='http://b3log-solo.googlecode.com/svn/wiki/src/images/gae-unpack.jpg' />
  1. 运行项目。右键项目，定制 -> gae:run
  1. 部署项目，定制 -> gae:deploy，按提示在控制台输入账号密码即可
#### `Eclipse` ####
  * Eclipse : http://www.eclipse.org
##### 配置 #####
  1. 安装 Maven 的插件。插件地址如下(感谢 tomaer 的提供)：
    * m2eclipse Core Update Sites (http://m2eclipse.sonatype.org/sites/m2e): Maven Integration for Eclipse (Required)
    * m2eclipse Extras Update Sites (http://m2eclipse.sonatype.org/sites/m2e-extras): Maven SCM handler for Subclipse
  1. Window -> Preferences -> Maven -> installations -> Add，把 Maven 的路径添加上
  1. Window -> Show View -> Other -> SVN -> SVN资源库
  1. SVN资源库 -> 右键 -> 新建 -> 资源库位置 http://b3log-solo.googlecode.com/svn/trunk
  1. 在b3log文件夹上面右键 -> Check out as Maven project...
  1. 静静等待，这个过程较慢，请淡定...
  1. 检出结束会发现项目上都有黑色的星号，这是因为生成了一些新文件，我们需要忽略它。Window -> Preferences -> Team -> Ignored Resources -> Add Patterns (星.project，星.classpath，星.settings)
  1. 在根项目上进行构建（mvn install）。右键 solo 项目,run as maven install
  1. 更新资源。右键 solo-gae 项目,run as maven build... --> Goals: gae:unpack
  1. 运行项目。右键 solo-gae 项目,run as maven build... --> Goals: gae:run
  1. 停止项目。右键 solo-gae 项目,run as maven build... --> Goals: gae:stop
  1. 部署项目。右键 solo-gae 项目,run as maven build... --> Goals: gae:deploy

#### `IntelliJ IDEA Ultimate Edition` ####
  * IntelliJ IDEA : http://www.jetbrains.com/idea/
##### 配置 #####
  1. VCS -> Checkout from Version Control -> Subversion 然后点击黄色“+”把http://b3log-solo.googlecode.com/svn/trunk/b3log-solo 填入 Repository URL
  1. 或者将已迁出的项目导入到 IntellJ， File -> New Project -> Import project from external model -> Maven 然后选择项目的根目录填入 Root directory
  1. 选择右侧 Maven Projects 标签 -> B3log Solo (GAE) -> Plugins -> gae 可以看到所有的 Goals，选择需要的双击即可运行