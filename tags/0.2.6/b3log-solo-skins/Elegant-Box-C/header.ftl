<div class="caption">
    <h1 id="title"><a href="http://${blogHost}/" title="${blogSubtitle}">${blogTitle}</a></h1>
    <div id="tagline">${blogSubtitle}</div>
</div>
<!-- search box -->
<form action="http://www.google.com/search" id="search_box" method="get">
    <div id="searchbox">
        <input type="text" id="searchtxt" class="textfield" name="q" value="" />
    </div>
</form>
<script type="text/javascript">
    //<![CDATA[
    var search_box = document.getElementById("search_box");
    var searchbox = document.getElementById("searchbox");
    var searchtxt = document.getElementById("searchtxt");
    var tiptext = "${Elegant_Box_C_searchPhraseLabel}";
    if(searchtxt.value == "" || searchtxt.value == tiptext) {
        searchtxt.className += " searchtip";
        searchtxt.value = tiptext;
    }
    searchtxt.onfocus = function(e) {
        if(searchtxt.value == tiptext) {
            searchtxt.value = "";
            searchtxt.className = searchtxt.className.replace(" searchtip", "");
        }
    }
    searchtxt.onblur = function(e) {
        if(searchtxt.value == "") {
            searchtxt.className += " searchtip";
            searchtxt.value = tiptext;
        }
    }
    search_box.onsubmit=function(){
        searchtxt.value+=' site:'+window.location.hostname;
        return true;
    }
    //]]>
</script>
<!-- navigation START -->
<ul id="navigation">
    <li class="page_item page-item-1"><a href="http://${blogHost}/">${homeLabel}</a></li>
    <li class="page_item page-item-2"><a href="http://${blogHost}/tags.html">${allTagsLabel}</a></li>
    <li id="subscribe">
        <a rel="external nofollow" title="${Elegant_Box_C_subscribeToThisBlogLabel}" id="feed" href="http://${blogHost}/blog-articles-feed.do"><img src="http://${blogHost}/skins/${skinDirName}/images/src/feed.png" alt="RSS" /></a>
        <ul>
            <li class="first"><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} 有道" href="http://reader.youdao.com/b.do?url=http://${blogHost}/blog-articles-feed.do"> 有道</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} 鲜果" href="http://www.xianguo.com/subscribe.php?url=http://${blogHost}/blog-articles-feed.do"> 鲜果</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} 抓虾" href="http://www.zhuaxia.com/add_channel.php?url=http://${blogHost}/blog-articles-feed.do"> 抓虾</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} Google" href="http://fusion.google.com/add?feedurl=http://${blogHost}/blog-articles-feed.do">  Google</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} My Yahoo!" href="http://add.my.yahoo.com/rss?url=http://${blogHost}/blog-articles-feed.do">  My Yahoo!</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} newsgator" href="http://www.newsgator.com/ngs/subscriber/subfext.aspx?url=http://${blogHost}/blog-articles-feed.do">  newsgator</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} Bloglines" href="http://www.bloglines.com/sub/http://${blogHost}/blog-articles-feed.do">  Bloglines</a></li>
            <li><a rel="external nofollow" title="${Elegant_Box_C_subscribeToLabel} 哪吒" href="http://inezha.com/add?url=http://${blogHost}/blog-articles-feed.do">  哪吒</a></li>
        </ul>
    </li>
</ul>
<script type="text/javascript">
    $('#navigation li.page_item').each(function(i,dom){
        var href=$(this).find('a:only-child').attr('href');
        var port=window.location.port;
        if(!port){
            port=80;
        }
        if(href==window.location.protocol+'//'+window.location.hostname+':'+port+window.location.pathname){
            $(this).removeClass().addClass('current_page_item');
        }
    });
</script>
<!-- navigation END -->
