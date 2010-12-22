<div id="sideNavi" class="side-navi">
    <div class="item">
        <h4>${noticeBoardLabel}</h4>
        <div class="marginLeft12 marginTop12">
            ${noticeBoard}
        </div>
    </div>
    <div class="line"></div>
    <div class="item navi-comments">
        <h4>${recentCommentsLabel}</h4>
        <ul>
             <#list recentComments as comment>
            <li>
                <img class='left' title='${comment.commentName}'
                     alt='${comment.commentName}'
                     src='${comment.commentThumbnailURL}'/>
                <div class='left'>
                    <div>
                        <a target="_blank" href="${comment.commentURL}">
                        ${comment.commentName}
                        </a>
                    </div>
                    <div>
                        <a title="${comment.commentContent}" class='side-comment' href="${comment.commentSharpURL}">
                        ${comment.commentContent}
                        </a>
                    </div>
                </div>
                <div class='clear'></div>
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
        <h4>${mostCommentArticlesLabel}</h4>
        <ul id="mostCommentArticles">
            <#list mostCommentArticles as article>
            <li>
                <sup>[${article.articleCommentCount}]</sup><a
                   title="${article.articleTitle}"
                   href="${article.articlePermalink}">${article.articleTitle}
                </a>
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item"><h4>${mostViewCountArticlesLabel}</h4>
        <ul id="mostViewCountArticles">
            <#list mostViewCountArticles as article>
            <li>
                <sup>[${article.articleViewCount}]</sup><a title="${article.articleTitle}"
                   href="${article.articlePermalink}">
                    ${article.articleTitle}
                </a>
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
        <h4>${popTagsLabel}</h4>
        <ul class="navi-tags">
            <#list mostUsedTags as tag>
            <li>
                <a href="/tag-articles-feed.do?oId=${tag.oId}" class="noUnderline">
                    <img alt="${tag.tagTitle}" src="/images/feed.png"/>
                </a>
                <a title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                    ${tag.tagTitle}</a>(${tag.tagPublishedRefCount})
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
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
    <div class="line"></div>
    <div class="item">
        <h4>${archiveLabel}</h4>
        <ul>
            <#list archiveDates as archiveDate>
            <li>
                <#if "en" == localeString?substring(0, 2)>
                <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                   title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                <#else>
                <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}${archiveDate.archiveDatePublishedArticleCount}">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
                </#if>
            </li>
            </#list>
        </ul>
    </div>
</div>
