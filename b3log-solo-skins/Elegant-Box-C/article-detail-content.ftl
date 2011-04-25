<div class="post">
    <div class="title">
        <h2>${article.articleTitle}</h2>
        <div class="fixed"></div>
    </div>
    <div class="info">
        <span>
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("yyyy年MM月dd日")}
            <#else>
            ${article.articleCreateDate?string("yyyy年MM月dd日")}
            </#if>
        </span>
        <span>
            | ${tags1Label}
            <#list article.articleTags?split(",") as articleTag>
            <a href="/tags/${articleTag?url('UTF-8')}" rel="tag">${articleTag}</a><#if articleTag_has_next>,</#if>
            </#list>
        </span>

        <div class="fixed"></div>
    </div>
    <div class="content">
        <p>${article.articleContent}</p>
        <div class="fixed"></div>
    </div>
    <div class="comments comments_single">
        <a href="#respond">发表评论</a>
    </div>
    <!-- related posts START -->
    <!-- related posts END -->
</div>

<#if (articleComments?size = 0)>
<div class="messagebox">
    <div class="content small">
        目前还没有任何评论.
    </div>
</div>
<#else>
<ol class="commentlist">
    <#list articleComments as comment>
    <li id="comment-${comment.oId}" class="comment ">
        <div class="userinfo">
            <div class="userpic">
                <img width="24" height="24" class="avatar avatar-24 photo" src="${comment.commentThumbnailURL}" alt="">
            </div>
            <div class="usertext">
                <div class="username">
                    <#if comment.commentURL?starts_with("http://")>
                    <a rel="external nofollow" id="commentauthor-${comment.oId}" href="${comment.commentURL}">${comment.commentName}</a>
                    <#else>
                    <a rel="external nofollow" id="commentauthor-${comment.oId}" href="http://${comment.commentURL}"
                       target="_blank">${comment.commentName}</a>
                    </#if>
                    <#if comment.isReply>
                    &nbsp;@&nbsp;<a href="${article.articlePermalink}#${comment.commentOriginalCommentId}">${comment.commentOriginalCommentName?trim}</a>
                    </#if>
                </div>
                <div class="date">${comment.commentDate?string("yyyy年MM月dd日HH:mm:ss")}</div>
            </div>
            <div class="count">
                <a onclick="CMT.reply('commentauthor-${comment.oId}', 'comment-${comment.oId}','${comment.oId}', 'comment');" href="javascript:void(0);">回复</a> |
                <a href="#${comment.oId}" name="${comment.oId}">链接</a>
            </div>
            <div class="fixed"></div>
        </div>
        <div class="comment_text">

            <div id="commentbody-${comment.oId}">
                <p>
                    ${comment.commentContent}
                </p>
            </div>
        </div>

    </li>
    </#list>
</ol>
</#if>

<div id="respond">
    <script type="text/javascript">
        var changeCaptcha=function(){
            $('#captcha').attr('src','/captcha.do?'+ Math.random());
        };
    </script>
    <form id="commentform" method="post" action="/add-article-comment.do">
        <!-- comment info -->
        <div id="comment_header">
            <div id="comment_info">
                <div id="author_info">
                    <div class="row">
                        <input type="text" tabindex="1" size="24" value="" class="textfield" id="author" name="commentName" gtbfieldid="6">
                        <label class="small" for="author">昵称 (必填)</label>
                    </div>
                    <div class="row">
                        <input type="text" tabindex="2" size="24" value="" class="textfield" id="email" name="commentEmail" gtbfieldid="7">
                        <label class="small" for="email">电子邮箱 (我们会为您保密) (必填)</label>
                    </div>
                    <div class="row">
                        <input type="text" tabindex="3" size="24" value="" class="textfield" id="url" name="commentURL" gtbfieldid="8">
                        <label class="small" for="url">网址</label>
                    </div>
                </div>
            </div>
        </div>
        <!-- comment input -->
        <textarea cols="50" rows="8" tabindex="4" id="comment" name="commentContent"></textarea>
        <!-- comment submit and rss -->
        <div id="submitbox">
            <div style="float:left;">
                <input style="vertical-align:top;" tabindex="4" size="24" maxlength="4" value="" class="textfield" id="commentValidate" name="captcha" gtbfieldid="9">
                <img id="captcha" alt="validate" src="/captcha.do" onclick="changeCaptcha();"/>
            </div>
            <span style="line-height:22px;margin-right: 15px;color:red;" id="commitStatus">
            </span>
            <div class="act">
                <input type="hidden" name="oId" value="${article.oId}"/>
		<input type="submit" value="提交评论" tabindex="5" class="button" id="submit" name="submit">
            </div>
            <div class="fixed"></div>
        </div>
    </form>
</div>