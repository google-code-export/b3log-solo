<div id="sideNavi" class="box">
    <div id="statistic" style="color: #FFFFFF;">
        <span>${viewCount1Label}
            <span>
                [${statistic.statisticBlogViewCount}]
            </span>
        </span>
        <span>
            ${articleCount1Label}
            <span>
               [${statistic.statisticPublishedBlogArticleCount}]
            </span>
        </span>
        <span>
            ${commentCount1Label}
            <span>
               [ ${statistic.statisticPublishedBlogCommentCount}]
            </span>
        </span>
    </div>
    <div class="line"></div>

    
    <ul class="marginTop12">
        <li>
            <h4>${noticeBoardLabel}</h4>
        </li>
        <li class="side-navi-notice">${noticeBoard}</li>
    </ul>
    <div class="line"></div>
    <ul>
        <li>
            <h4>${recentCommentsLabel}</h4>
        </li>
        <li>
            <ul id="recentComments">
                <#list recentComments as comment>
                <li style="color: #FFFFFF;">
                    <#if "http://" == comment.commentURL>
                    ${comment.commentName}<#else>
                    <a target="_blank" href="${comment.commentURL}">
                        ${comment.commentName}</a></#if>:
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
                    <a name="mostComment${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">[${article.articleCommentCount}]${article.articleTitle}</a>
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
                    <a name="mostView${article.oId}" title="${article.articleTitle}" href="${article.articlePermalink}">[${article.articleViewCount}]${article.articleTitle}</a>
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
                <li class="feedli">
                    <a href="/tag-articles-feed.do?oId=${tag.oId}" class="noUnderline">
                        <img alt="${tag.tagTitle}" src="/images/feed.png"/>
                    </a>
                    <a name="tags${tag.oId}" title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                        ${tag.tagTitle}(${tag.tagPublishedRefCount})
                    </a>
                    
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
                <li class="feedli">
                    <#if "en" == localeString?substring(0, 2)>
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    <#else>
                    <a name="archiveDates${archiveDate.oId}"
                       href="/archive-date-articles.do?oId=${archiveDate.oId}"
                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    </#if>
                </li>
                </#list>
            </ul>
        </li>
    </ul>
</div>
