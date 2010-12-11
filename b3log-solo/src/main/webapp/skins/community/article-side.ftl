<div class="footer-secondary">
    <div class="content">
        <h4>${recentCommentsLabel}</h4>
        <div class="arrow-right"></div>
        <div class="recentComments">
            <#list recentComments as comment>
            <#if "http://" == comment.commentURL>
            ${comment.commentName}<#else>
            <a target="_blank" href="${comment.commentURL}">
                ${comment.commentName}</a></#if>:
            <a class='side-comment' href="${comment.commentSharpURL}">
                ${comment.commentContent}
            </a>
            </#list>
        </div>
        <div class="clear"></div>
        <div class="hr"></div>
        <h4>${mostViewCountArticlesLabel}</h4>
        <div class="arrow-right"></div>
        <ul id="mostViewCountArticles">
            <#list mostViewCountArticles as article>
            <li>
                <sup>[${article.articleViewCount}]</sup>
                <a name="mostView${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
                    ${article.articleTitle}
                </a>
            </li>
            </#list>
        </ul>
        <div class="clear"></div>
    </div>
</div>
<div class="footer-widgets">
    <div class="content">
        <div class="left footer-block">
            <h4>${mostCommentArticlesLabel}</h4>
            <ul>
                <#list mostCommentArticles as article>
                <li>
                    <sup>[${article.articleCommentCount}]</sup>
                    <a name="mostComment${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">
                        ${article.articleTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        <div class="left footer-block">
            <h4>${popTagsLabel}</h4>
            <ul>
                <#list mostUsedTags as tag>
                <li class="mostUsedTags">
                    <a name="tags${tag.oId}" title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})
                    </a>
                    <img onclick="window.location='/tag-articles-feed.do?oId=${tag.oId}'"
                         alt="${tag.tagTitle}" src="/images/feed.png"/>
                </li>
                </#list>
            </ul>
        </div>
        <div class="left footer-block">
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
        </div>
        <div class="left footer-block">
            <h4>${archiveLabel}</h4>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <#if "en" == localeString?substring(0, 2)>
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}
                        (${archiveDate.archiveDatePublishedArticleCount})
                    </a>
                    <#else>
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                        (${archiveDate.archiveDatePublishedArticleCount})
                    </a>
                    </#if>
                </li>
                </#list>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
</div>
