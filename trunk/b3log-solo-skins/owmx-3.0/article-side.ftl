<aside>
	<!--
	<h4>Search</h4>
	<form action="http://www.google.com/cse" id="cse-search-box" class="s" target="_blank">
	  <div>
	    <input type="hidden" name="cx" value="014052262704486520429:eeg5ule8tro" />
	    <input type="hidden" name="ie" value="UTF-8" />
		<input type="hidden" name="oe" value="UTF-8">
		<input type="hidden" name="hl" value="zh-CN">
	    <input type="text" name="q" size="15" value="&#x7AD9;&#x5185;&#x641C;&#x7D22;" onclick="this.value=''"/>
	    <input type="hidden" name="sa" value="site-search"/>
	  </div>
	</form>-->
	<nav>
		<h4>Navigation</h4>
		<ul>
		    <#list pageNavigations as page>
		    <li>
		        <a href="${page.pagePermalink}">
		            ${page.pageTitle}
		        </a>
		    </li>
		    </#list>
		    <li>
		        <a href="/tags.html">${allTagsLabel}</a>
		    </li>
		    <li>
		        <a href="/blog-articles-feed.do">
		            ${atomLabel}
		            <img src="/images/feed.png" alt="Atom"/>
		        </a>
		    </li>
		    <li>
		        <a class="lastNavi" href="javascript:void(0);"></a>
		    </li>
		</ul>
	</nav>
    <h4>${noticeBoardLabel}</h4>
    <div id="c">
        <p>
            ${noticeBoard}
        </p>
    </div>
    <h4>${recentCommentsLabel}</h4>
    <ul class="aside-comments">
        <#list recentComments as comment>
        <li>
            <img class="left" title='${comment.commentName}'
                 alt='${comment.commentName}'
                 src='${comment.commentThumbnailURL}' width="32" height="32"/>
            <div class="left">
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
    <h4>${mostCommentArticlesLabel}</h4>
    <ul id="mostCommentArticles">
        <#list mostCommentArticles as article>
        <li>
            <sup>[${article.articleCommentCount}]</sup>&nbsp<a
               title="${article.articleTitle}"
               href="${article.articlePermalink}"><#if 20 < article.articleTitle?length>${article.articleTitle[0..20]}...<#else>${article.articleTitle}</#if></a>
        </li>
        </#list>
    </ul>
    <h4>${mostViewCountArticlesLabel}</h4>
    <ul id="mostViewCountArticles">
        <#list mostViewCountArticles as article>
        <li>
            <sup>[${article.articleViewCount}]</sup>&nbsp<a title="${article.articleTitle}"
               href="${article.articlePermalink}"><#if 20 < article.articleTitle?length>${article.articleTitle[0..20]}...<#else>${article.articleTitle}</#if></a>
        </li>
        </#list>
    </ul>
    <h4>${popTagsLabel}</h4>
    <ul class="navi-tags">
        <#list mostUsedTags as tag>
        <li>
            <a href="/tag-articles-feed.do?oId=${tag.oId}" class="no-underline">
                <img alt="${tag.tagTitle}" src="/images/feed.png"/>
            </a>
            <a title="${tag.tagTitle}(${tag.tagPublishedRefCount})" href="/tags/${tag.tagTitle?url('UTF-8')}">
                ${tag.tagTitle}</a>(${tag.tagPublishedRefCount})
        </li>
        </#list>
    </ul>
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
               title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} (${archiveDate.archiveDatePublishedArticleCount})">
                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}</a>(${archiveDate.archiveDatePublishedArticleCount})
            </#if>
        </li>
        </#list>
    </ul>
</aside>