<div class="post">
    <div class="title">
        <h2>${article.articleTitle}</h2>
        <div class="fixed"></div>
    </div>
    <div class="info">
        <span>
            <#if article.hasUpdated>
            ${article.articleUpdateDate?string("${Elegant_Box_C_articleTimeFormatLabel}")}
            <#else>
            ${article.articleCreateDate?string("${Elegant_Box_C_articleTimeFormatLabel}")}
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
        <a href="#respond" name="comments">${postCommentsLabel}</a>
    </div>
</div>

<#if (articleComments?size = 0)>
<div class="messagebox">
    <div class="content small">
        ${Elegant_Box_C_noCommentsYetLabel}
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
                    &nbsp;@<a href="${article.articlePermalink}#${comment.commentOriginalCommentId}">${comment.commentOriginalCommentName?trim}</a>
                    </#if>
                </div>
                <div class="date">${comment.commentDate?string("${Elegant_Box_C_commentTimeFormatLabel}")}</div>
            </div>
            <div class="count">
                <a onclick="CMT.reply('commentauthor-${comment.oId}', 'comment-${comment.oId}','${comment.oId}','${comment.commentName}');" href="javascript:void(0);">${replyLabel}</a> |
                <a href="#${comment.oId}" name="${comment.oId}">${permalinkLabel}</a>
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
            $('#commentValidate').select();
        };
        function commentSubmit(form){
            form=document.getElementById('commentform');
            var params=$(form).serializeArray();
            var data={};
            for(var i in params){
                var param=params[i];
                if(param.name=='commentContent'){
                    var commentContentLength=param.value.length;
                    if(commentContentLength<2||commentContentLength>500){
                         $("#commitStatus").text('${commentContentCannotEmptyLabel}');
                         return;
                    }
                    if(param.value.indexOf('@'+window['CMT']['commentName']+' ')==0){
                        param.value=param.value.replace('@'+window['CMT']['commentName']+' ','');
                        data['commentOriginalCommentId']=window['CMT']['commentOriginalCommentId'];
                    }
                }else if($.trim(param.value).length==0){
                    switch(param.name){
                        case 'commentName':
                            $("#commitStatus").text('${nameEmptyLabel}');
                            return;
                        case 'commentEmail':
                            $("#commitStatus").text('${mailCannotEmptyLabel}');
                            return;
                        case 'captcha':
                            $("#commitStatus").text('${captchaCannotEmptyLabel}');
                            return;
                    }
                }
                data[param.name]=param.value;
            }
            if(data['commentURL'].indexOf('http://')!=0)
                data['commentURL']='http://' + data['commentURL'];
            $.ajax({
                type:form.method,
                url:form.action,
                data:JSON.stringify(data),
                beforeSend:function(){
                    $("#commitStatus").text('${loadingLabel}');
                },
                success: function(result){
                    switch(result.sc){
                        case "COMMENT_ARTICLE_SUCC":
                            window.location.reload();
                            break;
                        case 'CAPTCHA_ERROR':
                            $("#commitStatus").text('${captchaErrorLabel}');
                            changeCaptcha();
                            break;
                    }
                }
            });
            return false;
        }
        window['CMT']['commentSubmit']=commentSubmit;
    </script>
    <form id="commentform" method="post" action="/add-article-comment.do">
        <div id="comment_header">
            <div id="comment_info">
                <div id="author_info">
                    <div class="row">
                        <input type="text" tabindex="1" size="24" value="" class="textfield" id="author" name="commentName" gtbfieldid="6"/>
                        <label class="small" for="author">${commentNameLabel} (${Elegant_Box_C_requiredLabel})</label>
                    </div>
                    <div class="row">
                        <input type="text" tabindex="2" size="24" value="" class="textfield" id="email" name="commentEmail" gtbfieldid="7"/>
                        <label class="small" for="email">${commentEmailLabel} (${Elegant_Box_C_willNotBePublishedLabel}) (${Elegant_Box_C_requiredLabel})</label>
                    </div>
                    <div class="row">
                        <input type="text" tabindex="3" size="24" value="" class="textfield" id="url" name="commentURL" gtbfieldid="8"/>
                        <label class="small" for="url">${Elegant_Box_C_websiteLabel}</label>
                    </div>
                </div>
            </div>
        </div>
        <!-- comment input -->
        <textarea cols="50" rows="8" tabindex="4" id="comment" name="commentContent"></textarea>
        <!-- comment submit and rss -->
        <div id="submitbox">
            <div style="float:left;">
                <input type="text" style="vertical-align:top;" tabindex="4" size="24" maxlength="4" value="" class="textfield" id="commentValidate" name="captcha" gtbfieldid="9">
                <img id="captcha" alt="validate" src="/captcha.do" onclick="changeCaptcha();"/>
            </div>
            <span style="line-height:22px;margin-right: 15px;color:red;" id="commitStatus">
            </span>
            <div class="act">
                <input type="hidden" name="oId" value="${article.oId}"/>
		<input type="button" value="${Elegant_Box_C_submitCommentLabel}" tabindex="5" class="button" id="submit" name="submit" onclick="return CMT.commentSubmit();" title="(Ctrl+Enter)"/>
            </div>
            <div class="fixed"></div>
        </div>
    </form>
    <script type="text/javascript">window['CMT']['loadCommentShortcut']('commentform','submit','');</script>
</div>