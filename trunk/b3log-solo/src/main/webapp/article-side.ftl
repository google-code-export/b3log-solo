<div id="sideNavi" class="side-navi">
    <ul>
        <li>
            <h4></h4>
            <ul id="userIntro">
                <li></li>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${statisticLabel}</h4>
            <ul id="statistic">
                <li></li>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${popTagsLabel}</h4>
            <ul>
                <#list mostUsedTags as tag>
                <li>
                    <a href="tag-articles-feed.do?oId=${tag.oId}" class="noUnderline">
                        <img alt="${tag.tagTitle}" src="images/rss.gif"/>
                    </a>
                    <a name="tags${tag.oId}" title="${tag.tagTitle}" href="tag-articles.do?oId=${tag.oId}&paginationCurrentPageNum=1">
                        ${tag.tagTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4 id="recentComments">${recentCommentsLabel}</h4>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${mostCommentArticlesLabel}</h4>
            <ul id="mostCommentArticles">
                <#list mostCommentArticles as article>
                <li>
                    <a class="test" name="mostComment${article.oId}" title="${article.articleTitle}" href="article-detail.do?oId=${article.oId}">
                        ${article.articleTitle}
                    </a>(${article.articleCommentCount})
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${mostViewCountArticlesLabel}</h4>
            <ul id="mostViewCountArticles">
                <#list mostViewCountArticles as article>
                <li>
                    <a name="mostView${article.oId}" title="${article.articleTitle}" href="article-detail.do?oId=${article.oId}">
                        ${article.articleTitle}
                    </a>(${article.articleViewCount})
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${linkLabel}</h4>
            <ul id="sideLink">
                <#list links as link>
                <li>
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        ${link.linkTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${archiveLabel}</h4>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <a  name="archiveDates${archiveDate.oId}" href="archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDateArticleCount})
                    </a>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
</div>
