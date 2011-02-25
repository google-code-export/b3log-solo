<p class="slogantext">Detailing the day to day happenings of the two.</p>
<p class="slogantext-small">
    <a href="/">GO HOME</a>
</p>
<p>&nbsp;</p>
<div class="sidebar-titletagcolor">
    <h3 class="pagetitle">${noticeBoardLabel}</h3>
</div>
<p class="slogantext">${noticeBoard}</p>
<div id="sidebar">
    <p>&nbsp;</p>
    <ul>
        <li>
            <div class="sidebar-titletagcolor">
                <h3 class="pagetitle">Pages</h3>
            </div>
            <ul>
                <li><a href="/" class="home">${homeLabel}</a></li>
                <li><a href="/tags.html" class="about">${allTagsLabel}</a></li>
                <#list pageNavigations as page>
                <li><a href="${page.pagePermalink}" class="${page.pageTitle}">${page.pageTitle}</a></li>
                </#list>
                <li><a href="/blog-articles-feed.do" class="classifiche">${atomLabel}</a></li>
            </ul>
        </li>

        <li>
            <div class="sidebar-titletagcolor">
                <h3 class="pagetitle">${popTagsLabel}</h3>
            </div>
            <ul>
                <#list mostUsedTags as tag>
                <li>
                    <a href="/tag-articles-feed.do?oId=${tag.oId}" class="no-underline">
                        <img alt="${tag.tagTitle}" src="/images/feed.png"/>
                    </a>
                    <a title="${tag.tagTitle}" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})</a>
                </li>
                </#list>
            </ul>
        </li>

        <li>
            <div class="sidebar-titletagcolor">
                <h3 class="pagetitle">${linkLabel}</h3>
            </div>
            <ul>
                <#list links as link>
                <li>
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        ${link.linkTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </li>

        <li>
            <div class="sidebar-titletagcolor">
                <h3 class="pagetitle">${archiveLabel}</h3>
            </div>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <#if "en" == localeString?substring(0, 2)>
                    <a href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                    <#else>
                    <a href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})
                    </a>
                    </#if>
                </li>
                </#list>
            </ul>
        </li>

    </ul>
    <p>&nbsp;</p>
    <div class="sidebar-titletagcolor">
        <h3 class="pagetitle">Powered By</h3>
    </div>
    <p align="center">
        <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif"
             alt="由 Google App Engine 提供支持" />
    </p>
    <p align="center">
        <a href="http://www.b3log.org" target="_blank">
            <img height="55" width="140" alt="B3log Logo" src="http://code.google.com/p/b3log-solo/logo?cct=1287802701" />
        </a>
    </p>
    <p>&nbsp;</p>
    <p>&nbsp;</p>
</div>