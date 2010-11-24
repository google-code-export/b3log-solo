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
            <ul id="recentComments">
                <#list recentComments as comment>
                <li>
                    <a target="_blank" href="${comment.commentURL}">
                        ${comment.commentName}</a>:
                    <a class='side-comment' title="${comment.commentContent}" href="${comment.commentSharpURL}">
                       ${comment.commentContent}
                    </a>
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
            <ul>
                <#list mostCommentArticles as article>
                <li>
                    <sup>[${article.articleCommentCount}]</sup><a name="mostComment${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                    <sup>[${article.articleViewCount}]</sup><a name="mostView${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">${article.articleTitle}</a>
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
                    <a name="tags${tag.oId}" title="${tag.tagTitle}(${tag.tagReferenceCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}</a>
                    (${tag.tagReferenceCount})
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
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDateArticleCount})">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDateArticleCount})
                    <#else>
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDateArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDateArticleCount})
                    </#if>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
</div>
