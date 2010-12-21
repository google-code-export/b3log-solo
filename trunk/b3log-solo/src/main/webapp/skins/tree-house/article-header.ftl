<div class="header-navi right">
    <ul>
        <#list pageNavigations as page>
        <li>
            <a href="${page.pagePermalink}">
                ${page.pageTitle}
            </a>&nbsp;&nbsp;
        </li>
        </#list>
        <li>
            <a href="/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
        </li>
        <li>
            <a href="/blog-articles-feed.do">${atomLabel}</a><a href="/blog-articles-feed.do"><img src="/images/feed.png" alt="Atom"/></a>
        </li>
    </ul>
</div>
<div class="header-title">
    <h1>
        <a href="/" id="logoTitle" >
            ${blogTitle}
        </a>
    </h1>
    <div>${blogSubtitle}</div>
    <embed width="228" height="239" type="application/x-shockwave-flash"
           menu="false" name="http://blog.thepixel.com/wp-content/themes/PixelBlog2/flash/fan"
           wmode="transparent" loop="true" pluginspage="http://www.adobe.com/go/getflashplayer"
           quality="high" src="/skins/tree-house/images/fan.swf"
           style="position: absolute;top:112px;left:265px;">
</div>