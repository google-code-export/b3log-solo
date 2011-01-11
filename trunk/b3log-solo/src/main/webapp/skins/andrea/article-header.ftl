<div id="global-nav" style="padding-top: 30px;">
    <h1 class="title">
        <span id="logoTitle" >
            ${blogTitle}
        </span>
    </h1>
    <span class="sub-title">${blogSubtitle}</span>
    <ul class="blog">
        <li id="blog"><a href="http://${blogHost}" title="${blogTitle}">${blogTitle}</a></li>
        <li id="work"><a href="/tags.html" title="${allTagsLabel}">${allTagsLabel}</a></li>
        <li id="goodies"><a href="/blog-articles-feed.do" title="${atomLabel}">${atomLabel}</a></li>
    </ul>
</div>

<div class="right header-right">
    <div class="left marginLeft12">
        <#list pageNavigations as page>
        <span>
            <a href="${page.pagePermalink}">${page.pageTitle}</a>&nbsp;&nbsp;
        </span>
        </#list>
    </div>
    
    <div class="clear"></div>
</div>
<div class="clear"></div>
<a id="magic">Pure CSS. Check it out!</a>