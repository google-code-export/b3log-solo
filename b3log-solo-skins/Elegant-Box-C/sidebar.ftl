<!-- Style Switcher START -->
<div class="widget">
    <div id="styleswitcher">
        <span id="style-text">主题风格: </span>
        <span id="style-black" class="color"><a onclick="TSS.setActiveStyleSheet('black');" href="javascript:void(0);" title="将主题风格更换为black"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <span id="style-blue" class="color"><a onclick="TSS.setActiveStyleSheet('blue');" href="javascript:void(0);" title="将主题风格更换为blue"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <span id="style-brown" class="color"><a onclick="TSS.setActiveStyleSheet('brown');" href="javascript:void(0);" title="将主题风格更换为brown"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <span id="style-green" class="color"><a onclick="TSS.setActiveStyleSheet('green');" href="javascript:void(0);" title="将主题风格更换为green"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <span id="style-purple" class="color"><a onclick="TSS.setActiveStyleSheet('purple');" href="javascript:void(0);" title="将主题风格更换为purple"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <span id="style-white" class="color"><a onclick="TSS.setActiveStyleSheet('white');" href="javascript:void(0);" title="将主题风格更换为white"><img src="http://${blogHost}/skins/${skinDirName}/images/transparent.gif" alt="" /></a></span>
        <div class="fixed"></div>
    </div>
</div>
<!-- Style Switcher END -->

<!-- showcase -->

<ul id="widgets">

    <li class="widget widget_categories">
        <h3>${mostCommentArticlesLabel}</h3>
        <ul>
            <#list mostCommentArticles as article>
            <li class="cat-item cat-item-1">
                <a href="${article.articlePermalink}">${article.articleTitle}</a> (${article.articleCommentCount})
            </li>
            </#list>
        </ul>
    </li>
    <li class="widget widget_recent_entries">
        <h3>${mostViewCountArticlesLabel}</h3>
        <ul>
            <#list mostViewCountArticles as article>
            <li><a href="${article.articlePermalink}">${article.articleTitle}</a> (${article.articleViewCount})</li>
            </#list>
        </ul>
    </li>
    <li class="widget widget_recent_comments">
        <h3>${recentCommentsLabel}</h3>
        <ul id="recentcomments">
            <#list recentComments as comment>
            <li class="rc_item" id="rc_item_1">
                <div class="rc_avatar rc_left">
                    <img width="32" height="32" class="avatar avatar-32 photo" src="${comment.commentThumbnailURL}" alt="">
                </div>
                <div class="rc_info">
                    <span class="author_name">
                        <#if comment.commentURL?starts_with("http://")>
                        <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                        <#else>
                        ${comment.commentName}
                        </#if>
                    </span>
                </div>
                <div class="rc_excerpt">
                    ${comment.commentContent}
                    <span class="rc_expand">
                        <a href="${comment.commentSharpURL}">»</a>
                    </span>
                </div>
            </li>
            </#list>
        </ul>
    </li>
    <li class="widget widget_tag_cloud">
        <h3>${popTagsLabel}</h3>
        <div>
            <#list mostUsedTags as tag>
            <a href="/tags/${tag.tagTitle?url('UTF-8')}" title='${tag.tagPublishedRefCount}篇文章' style='font-size: 8pt;'>${tag.tagTitle}</a>
            </#list>
        </div>
    </li>
    <!-- 友情链接 -->
    <li class="widget widget_links">
        <h3>${linkLabel}</h3>
        <ul class='xoxo blogroll'>
            <#list links as link>
            <#if link?starts_with("http://")>
            <li><a href="${link.linkAddress}" title="${link.linkTitle}" target="_blank">${link.linkTitle}</a></li>
            <#else>
            <li><a href="http://${link.linkAddress}" title="${link.linkTitle}" target="_blank">${link.linkTitle}</a></li>
            </#if>
            </#list>
        </ul>
    </li>
    <li class="widget widget_archive">
        <h3>${archiveLabel}</h3>
        <ul>
            <#list archiveDates as archiveDate>
            <li>
                <#if "en" == localeString?substring(0, 2)>
                <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                   title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}</a>(${archiveDate.archiveDatePublishedArticleCount})
                <#else>
                <a href="/archive-date-articles.do?oId=${archiveDate.oId}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
                </#if>
            </li>
            </#list>
        </ul>
    </li>
</ul>

<!-- showcase 2 -->