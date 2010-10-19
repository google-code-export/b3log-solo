<div id="sideNavi">
    <div id="userIntro">
    </div>
    <div class="block notice">
        <h3>${noticeBoardLabel}</h3>
        <ul>
            <li>${noticeBoard}</li>
        </ul>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3 id="recentComments">${recentCommentsLabel}</h3>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3>${mostCommentArticlesLabel}</h3>
        <ul id="mostCommentArticles">
            <#list mostCommentArticles as article>
            <li>
                <a class="test" name="mostComment${article.oId}" title="${article.articleTitle}" href="article-detail.do?oId=${article.oId}">
                    ${article.articleTitle}
                    <span>
                        (${article.articleCommentCount})
                    </span>
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3>${mostViewCountArticlesLabel}</h3>
        <ul id="mostViewCountArticles">
            <#list mostViewCountArticles as article>
            <li>
                <a name="mostView${article.oId}" title="${article.articleTitle}" href="article-detail.do?oId=${article.oId}">
                    ${article.articleTitle}
                    <span>
                        (${article.articleViewCount})
                    </span>
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3>${popTagsLabel}</h3>
        <ul>
            <#list mostUsedTags as tag>
            <li>
                <a href="tag-articles-feed.do?oId=${tag.oId}" class="noUnderline">
                    <img alt="${tag.tagTitle}" src="images/feed.png"/>
                </a>
                <a name="tags${tag.oId}" title="${tag.tagTitle}" href="tag-articles.do?oId=${tag.oId}&paginationCurrentPageNum=1">
                    ${tag.tagTitle}</a>(${tag.tagReferenceCount})
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3>${linkLabel}</h3>
        <ul id="sideLink">
            <#list links as link>
            <li>
                <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                    ${link.linkTitle}
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    <div class="block">
        <h3>${archiveLabel}</h3>
        <ul>
            <#list archiveDates as archiveDate>
            <li>
                <#if "en" == localeString?substring(0, 2)>
                <a name="archiveDates${archiveDate.oId}" href="archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}">
                    ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDateArticleCount})
                <#else>
                <a name="archiveDates${archiveDate.oId}" href="archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDateArticleCount})
                </a>
                </#if>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
</div>
