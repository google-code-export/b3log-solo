<div id="sideNavi" class="side-navi">
    <ul id="userIntro">
        <li></li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${noticeBoardLabel}</h4>
        </li>
        <li class="side-navi-notice">${noticeBoard}</li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4 id="recentComments">${recentCommentsLabel}</h4>
        </li>
         <li>
            <ul id="mostCommentArticles">
                <#list recentComments as comment>
                <li>
                    <a href="${comment.commentSharpURL}">
                        ${comment.commentName}
                    </a>: ${comment.commentContent}
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${mostCommentArticlesLabel}</h4>
        </li>
        <li>
            <ul id="mostCommentArticles">
                <#list mostCommentArticles as article>
                <li>
                    <a name="mostComment${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
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
        </li>
        <li>
            <ul id="mostViewCountArticles">
                <#list mostViewCountArticles as article>
                <li>
                    <a name="mostView${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
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
            <h4>${popTagsLabel}</h4>
        </li>
        <li>
            <ul>
                <#list mostUsedTags as tag>
                <li>
                    <a href="/tag-articles-feed.do?oId=${tag.oId}" class="noUnderline">
                        <img alt="${tag.tagTitle}" src="/images/feed.png"/>
                    </a>
                    <a name="tags${tag.oId}" title="${tag.tagTitle}" href="/tag-articles.do?oId=${tag.oId}&paginationCurrentPageNum=1">
                        ${tag.tagTitle}</a>(${tag.tagReferenceCount})
                </li>
                </#list>
            </ul>
        </li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${linkLabel}</h4>
        </li>
        <li>
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
        </li>
        <li>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <#if "en" == localeString?substring(0, 2)>
                    <a name="archiveDates${archiveDate.oId}" href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDateArticleCount})
                    <#else>
                    <a name="archiveDates${archiveDate.oId}" href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDateArticleCount})
                    </a>
                    </#if>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
</div>
