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
Theme by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript" src="/js/lib/json2.js"></script>
<script type="text/javascript">
    var goingTop = false;
    var goingBottom = false;

    var goTop = function (acceleration, time) {
        if (goingBottom) {
            return;
        }
        
        goingTop = true;
        acceleration = acceleration || 0.1;
        time = time || 16;

        var x1 = 0;
        var y1 = 0;
        var x2 = 0;
        var y2 = 0;
        var x3 = 0;
        var y3 = 0;

        if (document.documentElement) {
            x1 = document.documentElement.scrollLeft || 0;
            y1 = document.documentElement.scrollTop || 0;
        }

        if (document.body) {
            x2 = document.body.scrollLeft || 0;
            y2 = document.body.scrollTop || 0;
        }

        var x3 = window.scrollX || 0;
        var y3 = window.scrollY || 0;

        var x = Math.max(x1, Math.max(x2, x3));
        var y = Math.max(y1, Math.max(y2, y3));
        var speed = 1 + acceleration;
        window.scrollTo(Math.floor(x / speed), Math.floor(y / speed));
        
        if(x > 0 || y > 0) {
            var invokeFunction = "goTop(" + acceleration + ", " + time + ")";
            window.setTimeout(invokeFunction, time);
        } else {
            goingTop = false;
        }
    }
    
    var goBottom = function (acceleration, time) {
        if (goingTop) {
            return;
        }
        
        goingBottom = true;
        acceleration = acceleration || 0.1;
        acceleration = acceleration > 1 ? 1 : acceleration;
        time = time || 16;

        var x1 = 0;
        var x2 = 0;
        var x3 = 0;
        var y1 = 0;
        var y2 = 0;
        var y3 = 0;
        var clientHeight = 0;
        var scrollHeight = 0;

        if (document.documentElement) {
            x1 = document.documentElement.scrollLeft || 0;
            y1 = document.documentElement.scrollTop || 0;
        }

        if (document.body) {
            x2 = document.body.scrollLeft || 0;
            y2 = document.body.scrollTop || 0;
        }

        var x3 = window.scrollX || 0;
        var y3 = window.scrollY || 0;

        var x = Math.max(x1, Math.max(x2, x3));
        var y = Math.max(y1, Math.max(y2, y3));

        if(document.body.clientHeight && document.documentElement.clientHeight) {
            clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        } else {
            clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
        }

        scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
        var speed = acceleration;
        window.scrollTo(0, y + Math.ceil(((scrollHeight - y - clientHeight) * speed)));

        if (clientHeight + y < scrollHeight) {
            var invokeFunction = "goBottom(" + acceleration + ", " + time + ")";
            window.setTimeout(invokeFunction, time);
        } else {
            goingBottom = false;
        }
    }
  
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}",
        "skinDirName": "${skinDirName}",
        "loginLabel": "${loginLabel}"
    });
    util.init();
    util.replaceCommentsEm("#recentComments li .side-comment");
</script>
