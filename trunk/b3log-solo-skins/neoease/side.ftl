<div class="side">
    <#if "" != noticeBoard>
    <div>
        <h4>${noticeBoardLabel}</h4>
        <div>${noticeBoard}</div>
    </div>
    </#if>
    <#if 0 != recentComments?size>
    <div>
        <h4>${recentCommentsLabel}</h4>
        <ul>
            <#list recentComments as comment>
            <li>
                <#if "http://" == comment.commentURL>
                ${comment.commentName}<#else>
                <a target="_blank" href="${comment.commentURL}">
                    ${comment.commentName}</a></#if>:
                <a title="${comment.commentContent}" href="${comment.commentSharpURL}">
                    ${comment.commentContent}
                </a>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
    <#if 0 != mostCommentArticles?size>
    <div>
        <h4>${mostCommentArticlesLabel}</h4>
        <ul>
            <#list mostCommentArticles as article>
            <li>
                <sup>[${article.articleCommentCount}]</sup><a title="${article.articleTitle}" href="${article.articlePermalink}">${article.articleTitle}</a>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
    <#if 0 != mostViewCountArticles?size>
    <div>
        <h4>${mostViewCountArticlesLabel}</h4>
        <ul>
            <#list mostViewCountArticles as article>
            <li>
                <sup>[${article.articleViewCount}]</sup><a title="${article.articleTitle}" href="${article.articlePermalink}">${article.articleTitle}</a>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
    <#if 0 != mostUsedTags?size>
    <div>
        <h4>${popTagsLabel}</h4>
        <ul id="tagsSide">
            <#list mostUsedTags as tag>
            <li>
                <a target="_blank" href="/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}(${tag.tagPublishedRefCount})">
                    ${tag.tagTitle}
                </a>
            </li>
            </#list>
        </ul>
        <div class="clear"></div>
    </div>
    </#if>
    <#if 0 != links?size>
    <div>
        <h4>${linkLabel}</h4>
        <ul>
            <#list links as link>
            <li>
                <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                    <img alt="${link.linkTitle}"
                         src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" /></a>
                <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">${link.linkTitle}
                </a>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
    <#if 0 != archiveDates?size>
    <div>
        <h4>${archiveLabel}</h4>
        <ul id="archiveSide">
            <#list archiveDates as archiveDate>
            <li data-year="${archiveDate.archiveDateYear}">
                <#if "en" == localeString?substring(0, 2)>
                <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                <#else>
                <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
                </#if>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
</div>
