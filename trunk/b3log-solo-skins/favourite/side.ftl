<div id="sideNavi" class="side-navi">
    <div class="rings"></div>
    <div class="null"></div>
    <div class="item">
        <div class="antefatto">
            <h4>${noticeBoardLabel}</h4>
        </div>
        <div class="marginLeft12 marginTop12">
            ${noticeBoard}
        </div>
    </div>
    <div class="line"></div>
    <div class="item navi-comments">
        <div class="ads">
            <h4 id="recentComments">${recentCommentsLabel}</h4>
        </div>
        <ul>
            <#list recentComments as comment>
            <li>
                <img class='left' title='${comment.commentName}'
                     alt='${comment.commentName}'
                     src='${comment.commentThumbnailURL}'/>
                <div class='left'>
                    <div>
                        <a href="${comment.commentURL}">
                            ${comment.commentName}
                        </a>
                    </div>
                    <div class="comm">
                        <a class='side-comment' href="${comment.commentSharpURL}">
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
        <div class="esclamativo">
            <h4>${mostCommentArticlesLabel}</h4>
        </div>
        <ul id="mostCommentArticles">
            <#list mostCommentArticles as article>
            <li>
                <a title="${article.articleTitle}" href="${article.articlePermalink}">
                    ${article.articleTitle}
                </a>(${article.articleCommentCount})
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
        <div class="cuore">
            <h4>${mostViewCountArticlesLabel}</h4>
        </div>
        <ul id="mostViewCountArticles">
            <#list mostViewCountArticles as article>
            <li>
                <a title="${article.articleTitle}" href="${article.articlePermalink}">
                    ${article.articleTitle}
                </a>(${article.articleViewCount})
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
        <div class="categorie">
            <h4>${popTagsLabel}</h4>
        </div>
        <ul class="navi-tags">
            <#list mostUsedTags as tag>
            <li>
                <a href="/tag-articles-feed.do?oId=${tag.oId}" class="no-underline">
                    <img alt="${tag.tagTitle}" src="/images/feed.png"/>
                </a>
                <a title="${tag.tagTitle}" href="/tags/${tag.tagTitle?url('UTF-8')}">
                    ${tag.tagTitle}(${tag.tagPublishedRefCount})
                </a>
            </li>
            </#list>
        </ul>
    </div>
    <div class="line"></div>
    <div class="item">
        <div class="blog">
            <h4>${linkLabel}</h4>
        </div>
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
    <#if 1 = users?size>
    <div class="rings"></div>
    </#if>
    <div class="item">
        <div class="archivio">
            <h4>${archiveLabel}</h4>
        </div>
        <ul id="save">
            <#list archiveDates as archiveDate>
            <li>
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
    <#if 1 != users?size>
    <div class="line"></div>
    <div class="rings"></div>
    <div class="item">
        <div class="archivio">
            <h4>${authorLabel}</h4>
        </div>
        <ul id="sideAuthor">
            <#list users as user>
            <li>
                <a href="/authors/${user.oId}" title="${user.userName}">
                    ${user.userName}
                </a>
            </li>
            </#list>
        </ul>
    </div>
    </#if>
</div>