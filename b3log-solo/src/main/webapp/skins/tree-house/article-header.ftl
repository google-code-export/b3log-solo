<div class="header-navi right">
    <ul>
        <#list pageNavigations as page>
        <li>
            <a href="page.do?oId=${page.oId}">
                ${page.pageTitle}
            </a>&nbsp;&nbsp;
        </li>
        </#list>
        <li>
            <a href="tags.do">${allTagsLabel}</a>&nbsp;&nbsp;
        </li>
        <li>
            <a href="blog-articles-feed.do">${atomLabel}</a><a href="blog-articles-feed.do"><img src="images/feed.png" alt="Atom"/></a>
        </li>
    </ul>
</div>
<div class="header-title">
    <h1>
        <a href="index.do" id="logoTitle" >
            ${blogTitle}
        </a>
    </h1>
    <div>${blogSubtitle}</div>
</div>
