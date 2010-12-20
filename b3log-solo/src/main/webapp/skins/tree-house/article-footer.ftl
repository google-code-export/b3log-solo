<span style="color: gray;">© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank">
    <span style="color: orange;">B</span>
    <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
    <span style="color: green;">L</span>
    <span style="color: red;">O</span>
    <span style="color: blue;">G</span>&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span></a>,
ver ${version}&nbsp;&nbsp;
Theme by <a href="http://www.thepixel.com/blog" target="_blank">Pixel</a> & <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript">
    var goTop = function () {
        window.scrollTo(0, 0);
    }

    var goBottom = function () {
        var clientHeight  = 0, scrollHeight = 0;
        if(document.body.clientHeight && document.documentElement.clientHeight) {
            clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        } else {
            clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        }
        scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
        window.scrollTo(0, scrollHeight - clientHeight - 350);
        
    }
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}"
    });
    util.init();
    replaceCommentsEm("#recentComments li a");
</script>