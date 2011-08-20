<div class="footer-secondary">
    <div class="content">
        <#if "" != noticeBoard>
        <h4>
            ${noticeBoardLabel}
        </h4>
        <div class="arrow-right"></div>
        <div class="notice">
            ${noticeBoard}
        </div>
        <div class="clear"></div>
        <div class="hr"></div>
        </#if>
        <#if 0 != mostViewCountArticles?size>
        <h4>${mostViewCountArticlesLabel}</h4>
        <div class="arrow-right"></div>
        <div class="most-view-count-articles">
            <#list mostViewCountArticles as article>
            <span class="nowrap">
                <sup>[${article.articleViewCount}]</sup>
                <a title="${article.articleTitle}" href="${article.articlePermalink}">
                    <b>${article.articleTitle}</b>
                </a>
            </span>
            </#list>
        </div>
        <div class="clear"></div>
        </#if>
        <#if 0 != recentComments?size>
        <div class="hr"></div>
        <h4>${recentCommentsLabel}</h4>
        <div class="arrow-right"></div>
        <div class="recent-comments">
            <#list recentComments as comment>
            <div>
                <a href="${comment.commentSharpURL}">
                    <img class='left' title="${comment.commentContent?html}"
                         alt='${comment.commentName}'
                         src='${comment.commentThumbnailURL}'/>
                </a>
                <a title="${comment.commentName}" class="comment-author" target="_blank" href="${comment.commentURL}">
                    ${comment.commentName}
                </a>
            </div>
            </#list>
        </div>
        <div class="clear"></div>
        </#if>
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
                    <a title="${article.articleTitle}" href="${article.articlePermalink}">
                        ${article.articleTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </div>
        <div class="left footer-block">
            <h4><span class="left">${popTagsLabel}</span></h4>
            <span class="clear"></span>
            <ul>
                <#list mostUsedTags as tag>
                <li class="mostUsedTags">
                    <a title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})
                    </a>
                    <img onclick="window.location='/tag-articles-feed.do?oId=${tag.oId}'"
                         alt="${tag.tagTitle}" src="/images/feed.png"/>
                </li>
                </#list>
            </ul>
        </div>
        <div class="left footer-block">
            <h4><span class="left">${linkLabel}</span></h4>
            <span class="clear"></span>
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
        <div class="left footer-block" style="margin-right: 0px;">
            <h4><span class="left">${archiveLabel}</span></h4>
            <span class="clear"></span>
            <ul>
                <#list archiveDates as archiveDate>
                <li>
                    <#if "en" == localeString?substring(0, 2)>
                    <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                       title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    <#else>
                    <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    </#if>
                </li>
                </#list>
            </ul>
        </div>
        <div class="clear"></div>
    </div>
</div>
