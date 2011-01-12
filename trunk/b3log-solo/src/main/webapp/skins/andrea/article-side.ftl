<div class="item" style="margin-top: -35px;">
    <h4>${noticeBoardLabel}</h4>
    <div class="marginLeft12 marginTop12">
        ${noticeBoard}
    </div>
</div>
<div class="item">
    <dl>
        <dd>
            <h4>${recentCommentsLabel}</h4>
            <ul id="naviComments">
                <#list recentComments as comment>
                <li>
                    <a class="author" title="${comment.commentName}" target="_blank" href="${comment.commentURL}">
                        ${comment.commentName}
                    </a>:
                    <a title="${comment.commentContent}" class='side-comment' href="${comment.commentSharpURL}">
                        ${comment.commentContent}
                    </a>
                </li>
                </#list>
            </ul>
        </dd>
        <dd>
            <h4>${mostCommentArticlesLabel}</h4>
            <ul>
                <#list mostCommentArticles as article>
                <li>
                    <a
                        title="${article.articleTitle}"
                        href="${article.articlePermalink}">
                        <sup>[${article.articleCommentCount}]</sup>
                        ${article.articleTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </dd>
        <dd>
            <h4>${mostViewCountArticlesLabel}</h4>
            <ul>
                <#list mostViewCountArticles as article>
                <li>
                    <a title="${article.articleTitle}"
                       href="${article.articlePermalink}">
                        <sup>[${article.articleViewCount}]</sup>
                        ${article.articleTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </dd>
    </dl>
</div>
<div class="item">
    <dl>
        <dd>
            <h4>${popTagsLabel}</h4>
            <ul class="navi-tags">
                <#list mostUsedTags as tag>
                <li>
                    <a title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})</a>
                    <img onclick="window.location='/tag-articles-feed.do?oId=${tag.oId}'"
                         alt="${tag.tagTitle}" src="/images/feed.png"/>
                </li>
                </#list>
            </ul>
        </dd>
    </dl>
</div>
<#if 0 != links?size>
<div class="item">
    <dl>
        <dd>
            <h4>${linkLabel}</h4>
            <ul>
                <#list links as link>
                <li>
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        ${link.linkTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </dd>
    </dl>
</div>
</#if>
<div class="item">
    <dl>
        <dd>
            <h4>${archiveLabel}</h4>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <#if "en" == localeString?substring(0, 2)>
                    <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    <#else>
                    <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateYear}${yearLabel}${archiveDate.archiveDateMonth}${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    </#if>
                </li>
                </#list>
            </ul>
        </dd>
    </dl>
</div>