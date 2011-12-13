<!--TODO i18n-->
<!-- New noscript check, we need js on now folks -->
<noscript>
<div id="noscript-wrap">
	<div id="noscript">
		<h2>Notice</h2>
		<p>JavaScript for Mobile Safari is currently turned off.</p>
		<p>Turn it on in <em>Settings &rsaquo; Safari</em><br /> to view this website.</p>
	</div>
</div>
</noscript>
<!-- Prowl: if DM is sent, let's tell the user what happened -->
 <!-- #start The Search Overlay -->
	<div id="wptouch-search"> 
 		<div id="wptouch-search-inner">
			<form id="searchform" method="get" action="http://www.google.com/search">
	            <input type="hidden" name="oe" value="UTF-8" />
	            <input type="hidden" name="ie" value="UTF-8" />
	            <input type="hidden" name="newwindow" value="0" />
	            <input type="hidden" name="sitesearch" value="dx.b3log.org" />
				<input type="text" placeholder="Search..." id="search" name="q" /> 
				<input name="submit" type="submit" tabindex="1" id="search-submit" placeholder="Search..."  />
			<a href="javascript:return false;"><img class="head-close" src="/skins/${skinDirName}/themes/core/core-images/head-close.png" alt="close" /></a>
			</form>
		</div>
	</div>
	<div id="wptouch-menu" class="dropper"> 		
        <div id="wptouch-menu-inner">
	        <div id="menu-head">
	        	<div id="tabnav">
					<a href="#head-pages"><img src="/skins/${skinDirName}/images/icon-pool/Notes.png" alt=""/></a>
		        	<a href="#head-tags"><img src="/skins/${skinDirName}/images/icon-pool/Podcast.png" alt=""/></a>
					<a href="#head-cats"><img src="/skins/${skinDirName}/images/icon-pool/Squares.png" alt=""/></a>
				</div>
	
				<ul id="head-pages">
					<li><a href="/"><img src="/skins/${skinDirName}/images/icon-pool/Home.png" alt=""/>Home</a></li>
				    <#list pageNavigations as page>
				    <li><a href="${page.pagePermalink}"><img src="/skins/${skinDirName}/images/icon-pool/Apps.png" alt=""/>${page.pageTitle}</a></li>
				    </#list>           
					<li><a href="/blog-articles-rss.do"><img src="/skins/${skinDirName}/images/icon-pool/RSS.png" alt="" />RSS Feed</a></li>
					<li><a href="mailto:alnxcc@gmail.com"><img src="/skins/${skinDirName}/images/icon-pool/Mail.png" alt="" />E-Mail</a></li>           
				</ul>
				<ul id="head-tags">
    				<#if 0 != mostUsedTags?size>
		                <#list mostUsedTags as tag>
							<li><a href="/tags/${tag.tagTitle?url('UTF-8')}">${tag.tagTitle} <span>(${tag.tagPublishedRefCount})</span></a></li>
		                </#list>
   		 			</#if>
				</ul>
				<ul id="head-cats">
    				<#if 0 != archiveDates?size>
		                <#list archiveDates as archiveDate>
		                <li>
		                    <#if "en" == localeString?substring(0, 2)>
		                    <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
		                        ${archiveDate.monthName} ${archiveDate.archiveDateYear} <span>(${archiveDate.archiveDatePublishedArticleCount})</span></a>
		                    <#else>
		                    <a href="/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
		                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel} <span>(${archiveDate.archiveDatePublishedArticleCount})</span></a>
		                    </#if>
		                </li>
		                </#list>
    				</#if>
				</ul>
				
				</div>
		</div>
    </div>

<div id="headerbar">
	<div id="headerbar-title">
		<!-- This fetches the admin selection logo icon for the header, which is also the bookmark icon -->
		<img id="logo-icon" src="/skins/${skinDirName}/images/icon-pool/Apps.png" alt="aln" />
		<a href="/">${blogTitle}</a>
	</div>
	<div id="headerbar-menu">
		    <a href="javascript:return false;"></a>
	</div>
</div>


<div id="drop-fade">
				    
    	<a id="searchopen" class="top" href="javascript:return false;">Search</a>
 <!-- #start the Prowl Message Area -->
 <div id="prowl-message" style="display:none">
 	 <div id="push-style-bar"></div><!-- filler to get the styling just right -->
	 <img src="http://localhost/blog/wp-content/plugins/wptouch/themes/core/core-images/push-icon.png" alt="push icon" />
	 <h4>Send a Message</h4>
	 <p>This message will be pushed to the admin's iPhone instantly.</p>
	 
	 <form id="prowl-direct-message" method="post" action="/blog/">
	 	<p>
	 		<input name="prowl-msg-name"  id="prowl-msg-name" type="text" />
	 		<label for="prowl-msg-name">Name</label>
	 	</p>

		<p>
			<input name="prowl-msg-email" id="prowl-msg-email" type="text" />
			<label for="prowl-msg-email">E-Mail</label>
		</p>

		<textarea name="prowl-msg-message"></textarea>
		<input type="hidden" name="wptouch-prowl-message" value="1" /> 
		<input type="hidden" name="_nonce" value="3690953c13" />			
		<input type="submit" name="prowl-submit" value="Send Now" id="prowl-submit" />
	 </form>
	<div class="clearer"></div>
 </div>
</div>