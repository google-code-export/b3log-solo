<#macro comments commentList permalink>
<!-- Let's rock the comments -->
<!-- You can start editing below here... but make a backup first!  -->
<div class="comment_wrapper" id="comments">
	<#if 0 lt commentList?size>
	<h3 onclick="bnc_showhide_coms_toggle();" id="com-head">
		<img id="com-arrow" src="/skins/${skinDirName}/themes/core/core-images/com_arrow.png" alt="arrow" />
		${commentList?size} Responses
	</h3>
	</#if>
    <ol class="commentlist" id="commentlist">
    <#list commentList as comment>
    <li id="${comment.oId}">
        <div class="comwrap">
			<div class="comtop"><!--TODO comment->comment_approved == '0') : comtop preview;-->
				<img alt='${comment.commentName}' src='${comment.commentThumbnailURL}' class='avatar avatar-64 photo' height='64' width='64' />
				<div class="com-author">
	                <#if "http://" == comment.commentURL>
	                <a>${comment.commentName}</a>
	                <#else>
					<a href='${comment.commentURL}' rel='external nofollow' target="_blank" class='url'>${comment.commentName}</a>
	                </#if>
	                <#if comment.isReply>
	                @
	                <a href="${permalink}#${comment.commentOriginalCommentId}"
	                   onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
	                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
	                </#if>
				</div>										
				<div class="comdater">
					<span><!--TODO wptouch_moderate_comment_link(get_comment_ID())--></span>
					${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}
                    <!--TODO<a href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>-->
				</div>									
			</div><!--end comtop-->
			<div class="combody">  
				<p>${comment.commentContent}</p>
			</div>
        </div>
    </li>
    </#list>
	</ol>
  	<div id="textinputwrap">
		<div id="refresher" style="display:none;">
			<img src="/skins/${skinDirName}/images/good.png" alt="checkmark" />
			<h3>Success! Comment added.</h3>
			&rsaquo; <a href="javascript:this.location.reload();">Refresh the page to see your comment.</a><br />
			(If your comment requires moderation it will be added soon.)
		</div>
	    <div id="commentForm">
			<h3 id="respond">${postCommentsLabel}</h3>
			<p>
                <input type="text" id="commentName" size="22" tabindex="1"/>
				<label for="author">${commentNameLabel} *</label>
			</p>

			<p>
                <input type="text" id="commentEmail" size="22" tabindex="2" />
				<label for="email">${commentEmailLabel} *</label>
			</p>
		
			<p>
                <input type="text" id="commentURL" size="22" tabindex="3" />
				<label for="url">${commentURLLabel}</label>
			</p>

			<p>
				<span id="commentErrorTip" style="display:none;"></span>
			</p>
			<p><textarea id="comment" tabindex="4"></textarea></p>
			
			<p>
                <input type="text" id="commentValidate" tabindex="5" />
				<label for="url">${captchaLabel}</label>
                <img id="captcha" alt="validate" src="/captcha.do" />
			</p>
			<p>
                <input id="submitCommentButton" type="submit" onclick="page.submitCommentMoblie();" value="${submmitCommentLabel}"  tabindex="6" />
				<div id="loading" style="display:none">
					<img src="/skins/${skinDirName}/themes/core/core-images/comment-ajax-loader.gif" alt="" /> <p>Publishing...</p>
				</div>
			</p>
	    </div>
	</div><!--textinputwrap div-->
</div>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="/js/page${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var page = new Page({
        "nameTooLongLabel": "${nameTooLongLabel}",
        "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
        "mailInvalidLabel": "${mailInvalidLabel}",
        "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
        "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
        "loadingLabel": "${loadingLabel}",
        "oId": "${oId}",
        "skinDirName": "${skinDirName}",
        "blogHost": "${blogHost}",
        "randomArticles1Label": "${randomArticles1Label}",
        "externalRelevantArticles1Label": "${externalRelevantArticles1Label}"
    });

    (function () {
        page.load();
        // emotions
        page.replaceCommentsEm("#comments .comment-content");
            <#nested>
        })();
</script>
</#macro>