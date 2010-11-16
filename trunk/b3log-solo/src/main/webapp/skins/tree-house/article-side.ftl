<div id="sideNavi">
    <div id="statistic">
        <div>
            ${viewCount1Label}
            <span class='error-msg'>
                ${statistic.statisticBlogViewCount}
            </span>
        </div>
        <div>
            ${articleCount1Label}
            <span class='error-msg'>
                ${statistic.statisticBlogArticleCount}
            </span>
        </div>
        <div>
            ${commentCount1Label}
            <span class='error-msg'>
                ${statistic.statisticBlogCommentCount}
            </span>
        </div>
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
        <ul>
            <#list recentComments as comment>
            <li>
                <a href="${comment.commentSharpURL}">
                    ${comment.commentName}:
                    <span>
                        ${comment.commentContent}
                    </span>
                </a>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
    <div class="line"></div>
    <div class="block mostCommentArticles">
        <h3>${mostCommentArticlesLabel}</h3>
        <ul id="mostCommentArticles">
            <#list mostCommentArticles as article>
            <li>
                <a class="test" name="mostComment${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
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
    <div class="block mostViewCountArticles">
        <h3>${mostViewCountArticlesLabel}</h3>
        <ul id="mostViewCountArticles">
            <#list mostViewCountArticles as article>
            <li>
                <a name="mostView${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
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
    <div class="block popTags">
        <h3>${popTagsLabel}</h3>
        <ul>
            <#list mostUsedTags as tag>
            <li>
                <a name="tags${tag.oId}" title="${tag.tagTitle}" href="/tags/${tag.tagTitle?url('UTF-8')}">
                    ${tag.tagTitle}
                    <span>
                        (${tag.tagReferenceCount})
                    </span>
                </a>
                <img onclick="window.location='/tag-articles-feed.do?oId=${tag.oId}'"
                     alt="${tag.tagTitle}" src="/images/feed.png"/>
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
                <a name="archiveDates${archiveDate.oId}" href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}">
                    ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}
                    <span>
                        (${archiveDate.archiveDateArticleCount})
                    </span>
                </a>
                <#else>
                <a name="archiveDates${archiveDate.oId}" href="/archive-date-articles.do?oId=${archiveDate.oId}" title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                    <span>
                        (${archiveDate.archiveDateArticleCount})
                    </span>
                </a>
                </#if>
            </li>
            </#list>
        </ul>
        <div class='clear'></div>
    </div>
</div>
