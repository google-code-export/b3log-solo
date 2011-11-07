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
            <a title="${article.articleTitle}" href="${article.articlePermalink}">
                <sup>[${article.articleViewCount}]</sup>${article.articleTitle}
            </a>
            </#list>
        </div>
        <div class="clear"></div>
        <#if 0 != recentComments?size>
        <div class="hr"></div>
        </#if>
        </#if>
        <#if 0 != recentComments?size>
        <h4>${recentCommentsLabel}</h4>
        <div class="arrow-right"></div>
        <div class="recent-comments">
            <#list recentComments as comment>
            <div>
                <a href="${comment.commentSharpURL}">
                    <img class='left' title="${comment.commentContent}"
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
        <#if 0 != mostCommentArticles?size>
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
        </#if>
        <#if 0 != mostUsedTags?size>
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
        </#if>
        <#if 0 != links?size>
        <div class="left footer-block">
            <h4><span class="left">${linkLabel}</span></h4>
            <span class="clear"></span>
            <ul id="sideLink">
                <#list links as link>
                <li class="mostUsedTags">
                    <a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">
                        ${link.linkTitle}
                    </a>
                    <img onclick="window.location='${link.linkAddress}'" 
                         alt="${link.linkTitle}" 
                         src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" />
                </li>
                </#list>
            </ul>
        </div>
        </#if>
        <#if 0 != archiveDates?size>
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
        </#if>
        <div class="clear"></div>
    </div>
</div>