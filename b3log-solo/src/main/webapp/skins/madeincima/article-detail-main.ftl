<div id="main">
    <div id="main-aux">
        <div id="patch"></div>
        <div id="main-aux1">

            <div class="post-cont">
                <div class="headline">
                    <h2>
                        <a href="${article.articlePermalink}">${article.articleTitle}</a>
                        <#if article.hasUpdated>
                            <sup class="red">
                                ${updatedLabel}
                            </sup>
                            </#if>
                            <#if article.articlePutTop>
                            <sup class="red">
                                ${topArticleLabel}
                            </sup>
                        </#if>
                    </h2>
                    <p class="postmetadata">
                        <span class="calendar">
                           <span class="month">${article.articleCreateDate?string("MM")}</span>
                           <span class="day">${article.articleCreateDate?string("dd")}</span>
                        </span>
                        ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;

                        <a href="${article.articlePermalink}#comments">
                            ${commentLabel}:&nbsp;
                            ${article.articleCommentCount}
                        </a>
                        &nbsp;&nbsp;
                        <a href="${article.articlePermalink}">
                            ${viewLabel}:&nbsp;
                            ${article.articleViewCount}
                        </a>
                        <div class="article-tags">
                            ${tags1Label}
                            <#list articleTags as articleTag>
                            <span>
                                <a href="/tags/${articleTag.tagTitle?url('UTF-8')}">
                                    ${articleTag.tagTitle}</a><#if articleTag_has_next>,</#if>
                            </span>
                            </#list>
                        </div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="article-body">
                ${article.articleContent}
            </div>
            <div class="article-details-footer">
                <div class="left">
                    <#if nextArticlePermalink??>
                    <a href="${nextArticlePermalink}">${nextArticle1Label}${nextArticleTitle}</a>
                    </#if>
                    <#if previousArticlePermalink??>
                    <br/>
                    <a href="${previousArticlePermalink}">${previousArticle1Label}${previousArticleTitle}</a>
                    </#if>
                </div>
                <div class="clear"></div>
            </div>
            <#if 0 != relevantArticles?size>
            <div class="article-relative left" style="width: 50%;">
                <h5>${relevantArticles1Label}</h5>
                <ul class="marginLeft12">
                    <#list relevantArticles as relevantArticle>
                    <li>
                        <a href="${relevantArticle.articlePermalink}">
                            ${relevantArticle.articleTitle}
                        </a>
                    </li>
                    </#list>
                </ul>
            </div>
            </#if>
            <div id="randomArticles" class="article-relative left"></div>
            <div class="clear"></div>
            <div id="externalRelevantArticles"></div>
        </div>
        <div class="comments" id="comments" name="comments">
            <ol class="commentlist">
            <#list articleComments as comment>
                <#if comment_index % 2 == 0>
                <li class="comment even thread-even depth-1">
                <#else>
                <li class="comment odd alt thread-odd thread-alt depth-1">
                </#if>
                    <div id="commentItem${comment.oId}" class="comment-body">
                        <div class="comment-author vcard">
                            <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}" class='avatar avatar-64 photo'/>
                            <#if "http://" == comment.commentURL>
                            <a name="${comment.oId}" class="left">${comment.commentName}</a>&nbsp;
                            <#else>
                            <a name="${comment.oId}" href="${comment.commentURL}"
                               target="_blank" class="left">${comment.commentName}</a>&nbsp;
                            </#if>
                            <#if comment.isReply>
                            @&nbsp;<a
                                href="${article.articlePermalink}#${comment.commentOriginalCommentId}"
                                onmouseover="showComment(this, '${comment.commentOriginalCommentId}');"
                                onmouseout="articleUtil.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                            </#if>
                        </div>

                        <div class="comment-meta commentmetadata">
                            <a href="http://www.madeincima.eu/blog/seo-tips-for-findable-websites/comment-page-1/#comment-598">
                               ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                            </a>
                        </div>

                        <p>${comment.commentContent}</p>
                        <div class="reply">
                            <a rel="nofollow" class="comment-reply-link" href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                        </div>
                    </div>
                </li>
            </#list>
            </ol> 
            <div id="respond">
                <h3>${postCommentsLabel}</h3>
                <p class="fieldwrap">
                    <label for="commentName">${commentName1Label}
                        <span class="required"> *</span>
                    </label>
                    <input type="text" id="commentName" size="22" tabindex="1" />
                </p>
                <p class="fieldwrap">
                    <label for="commentEmail"> ${commentEmail1Label}
                        <span class="required"> *</span>
                    </label>
                    <input type="text" id="commentEmail" size="22" tabindex="2" />
                </p>
                <p class="fieldwrap">
                    <label for="commentURL">${commentURL1Label}
                        <span class="required">http://</span>
                    </label>
                    <input type="text" id="commentURL"  tabindex="3" />
                </p>
                <p class="fieldwrap" id="emotions">
                    <img class="[em00]" src="/skins/classic/emotions/em00.png" alt="${em00Label}" title="${em00Label}" />
                    <img class="[em01]" src="/skins/classic/emotions/em01.png" alt="${em01Label}" title="${em01Label}" />
                    <img class="[em02]" src="/skins/classic/emotions/em02.png" alt="${em02Label}" title="${em02Label}" />
                    <img class="[em03]" src="/skins/classic/emotions/em03.png" alt="${em03Label}" title="${em03Label}" />
                    <img class="[em04]" src="/skins/classic/emotions/em04.png" alt="${em04Label}" title="${em04Label}" />
                    <img class="[em05]" src="/skins/classic/emotions/em05.png" alt="${em05Label}" title="${em05Label}" />
                    <img class="[em06]" src="/skins/classic/emotions/em06.png" alt="${em06Label}" title="${em06Label}" />
                    <img class="[em07]" src="/skins/classic/emotions/em07.png" alt="${em07Label}" title="${em07Label}" />
                    <img class="[em08]" src="/skins/classic/emotions/em08.png" alt="${em08Label}" title="${em08Label}" />
                    <img class="[em09]" src="/skins/classic/emotions/em09.png" alt="${em09Label}" title="${em09Label}" />
                    <img class="[em10]" src="/skins/classic/emotions/em10.png" alt="${em10Label}" title="${em10Label}" />
                    <img class="[em11]" src="/skins/classic/emotions/em11.png" alt="${em11Label}" title="${em11Label}" />
                    <img class="[em12]" src="/skins/classic/emotions/em12.png" alt="${em12Label}" title="${em12Label}" />
                    <img class="[em13]" src="/skins/classic/emotions/em13.png" alt="${em13Label}" title="${em13Label}" />
                    <img class="[em14]" src="/skins/classic/emotions/em14.png" alt="${em14Label}" title="${em14Label}" />
                </p>
                <p class="fieldwrap">
                     <label for="comment"> ${commentContent1Label}
                         <span class="required"> *</span>
                     </label>
                    <textarea rows="10" id="comment"></textarea>
                </p>
                <p class="fieldwrap">
                     <label for="commentValidate"> ${captcha1Label}
                         <span class="required"> *</span>
                     </label>                   
                    <input class="normalInput" id="commentValidate" style="width:50px;" />
                    <img id="captcha" alt="validate" src="/captcha.do" />
                </p>
                <p class="fieldwrap"><span class="error-msg" id="commentErrorTip"/></p>
                <p class="submit" align="right">
                    <input type="button"  id="submit"  tabindex="5" onclick="submitComment();" value="${submmitCommentLabel}">
                </p>
            </div>
        </div>
    </div>
</div>
