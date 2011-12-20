<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${article.articleTitle} - ${blogTitle}">
    <meta name="keywords" content="${article.articleTags}" />
    <meta name="description" content="${article.articleAbstract?html}" />
    </@head>
</head>
<body class="classic-wptouch-bg ">
    <#include "header.ftl">
	<div class="content single">
		<div class="post">
			<a class="sh2" href="${article.articlePermalink}" rel="bookmark">${article.articleTitle}</a>
			<div class="single-post-meta-top">
                <#if article.hasUpdated>
                ${article.articleUpdateDate?string("yyyy-MM-dd HH:mm:ss")}
                <#else>
                ${article.articleCreateDate?string("yyyy-MM-dd HH:mm:ss")}
                </#if>
				&rsaquo; ${article.authorName}<br />
				<a href="#com-head">&darr; Skip to comments</a><!--TODO-->
			</div>
		</div>
		<div class="clearer"></div>
        <div class="post" id="post-${article.oId}">
			<div id="singlentry" class="left-justified">
				${article.articleContent}
				<#if "" != article.articleSign.signHTML?trim>
				<div class=""><!--TODO sign class-->
				    ${article.articleSign.signHTML}
				</div>
				</#if>
			</div>
			<!-- Categories and Tags post footer -->
			<div class="single-post-meta-bottom">
<!-- TODO remove this
			    Categories: <a href="http://localhost/blog/?cat=4" title="View all posts in c1" rel="category">c1</a>, 
							<a href="http://localhost/blog/?cat=7" title="View all posts in c1-1" rel="category">c1-1</a><br />
-->
				${tags1Label}
                <#list article.articleTags?split(",") as articleTag>
				<a href="/tags/${articleTag?url('UTF-8')}" rel="tag">${articleTag}</a><#if articleTag_has_next>,</#if>
                </#list>
		    </div>   
			<ul id="post-options">
				<#if nextArticlePermalink??>
				<li><a href="${nextArticlePermalink}" id="oprev"></a></li>
                </#if>
				<li><a href="mailto:?subject=${article.authorName} - ${article.articleTitle}&body=Check out this post: http://${blogHost}${article.articlePermalink}" onclick="return confirm('Mail a link to this post?');" id="omail"></a></li>
				<li><a href="javascript:(function(){var%20f=false,t=true,a=f,b=f,u='',w=window,d=document,g=w.open(),p,linkArr=d.getElementsByTagName('link');for(var%20i=0;i%3ClinkArr.length&&!a;i++){var%20l=linkArr[i];for(var%20x=0;x%3Cl.attributes.length;x++){if(l.attributes[x].nodeName.toLowerCase()=='rel'){p=l.attributes[x].nodeValue.split('%20');for(y=0;y%3Cp.length;y++){if(p[y]=='short_url'||p[y]=='shorturl'||p[y]=='shortlink'){a=t;}}}if(l.attributes[x].nodeName.toLowerCase()=='rev'&&l.attributes[x].nodeValue=='canonical'){a=t;}if(a){u=l.href;}}}if(a){go(u);}else{var%20h=d.getElementsByTagName('head')[0]||d.documentElement,s=d.createElement('script');s.src='http://api.bit.ly/shorten?callback=bxtShCb&longUrl='+encodeURIComponent(window.location.href)+'&version=2.0.1&login=amoebe&apiKey=R_60a24cf53d0d1913c5708ea73fa69684';s.charSet='utf-8';h.appendChild(s);}bxtShCb=function(data){var%20rs,r;for(r%20in%20data.results){rs=data.results[r];break;}go(rs['shortUrl']);};function%20go(u){return%20g.document.location.href=('http://mobile.twitter.com/home/?status='+encodeURIComponent(document.title+'%20'+u));}})();" id="otweet"></a></li>		<li><a href="javascript:var%20d=document,f='http://www.facebook.com/share',l=d.location,e=encodeURIComponent,p='.php?src=bm&v=4&i=1297484757&u='+e(l.href)+'&t='+e(d.title);1;try{if%20(!/^(.*\.)?facebook\.[^.]*$/.test(l.host))throw(0);share_internal_bookmarklet(p)}catch(z)%20{a=function()%20{if%20(!window.open(f+'r'+p,'sharer','toolbar=0,status=0,resizable=1,width=626,height=436'))l.href=f+p};if%20(/Firefox/.test(navigator.userAgent))setTimeout(a,0);else{a()}}void(0)" id="facebook"></a></li>		<li><a href="javascript:void(0)" id="obook"></a></li>
                <#if previousArticlePermalink??>
				<li><a href="${previousArticlePermalink}" id="onext"></a></li>
                </#if>
			</ul>
    	</div>
<!--TODO bookmark
  		<div id="bookmark-box" style="display:none">
			<ul>
				<li><a  href="http://del.icio.us/post?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/delicious.jpg" alt="" /> Del.icio.us</a></li>
				<li><a href="http://digg.com/submit?phase=2&url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/digg.jpg" alt="" /> Digg</a></li>
				<li><a href="http://technorati.com/faves?add=http://localhost/blog/?p=12" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/technorati.jpg" alt="" /> Technorati</a></li>
				<li><a href="http://ma.gnolia.com/bookmarklet/add?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/magnolia.jpg" alt="" /> Magnolia</a></li>
				<li><a href="http://www.newsvine.com/_wine/save?popoff=0&u=http://localhost/blog/?p=12&h=${article.articleTitle}" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/newsvine.jpg" target="_blank"> Newsvine</a></li>
				<li class="noborder"><a href="http://reddit.com/submit?url=http://localhost/blog/?p=12&title=${article.articleTitle}" target="_blank"><img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/bookmarks/reddit.jpg" alt="" /> Reddit</a></li>
			</ul>
		</div>
-->
		<@comments commentList=articleComments permalink=article.articlePermalink></@comments>
	</div>
	<#include "footer.ftl">    
    <@comment_script oId=article.oId>
    page.tips.externalRelevantArticlesDisplayCount = "${externalRelevantArticlesDisplayCount}";
    <#if 0 != randomArticlesDisplayCount>
    page.loadRandomArticles();
    </#if>
    <#if 0 != relevantArticlesDisplayCount>
    page.loadRelevantArticles('${article.oId}', '${relevantArticles1Label}');
    </#if>
    <#if 0 != externalRelevantArticlesDisplayCount>
    page.loadExternalRelevantArticles("<#list article.articleTags?split(",") as articleTag>${articleTag}<#if articleTag_has_next>,</#if></#list>");
    </#if>
    </@comment_script>    
</body>
</html>