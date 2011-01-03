<div id="footer">

    <div id="twitter">
        <h2 class="alt"></h2>
        <p></p>
        <p id="follow"></p>
    </div><hr />
    <div  style="padding-top:400px;text-align: center;color: #FFFFFF;">
        <span>© 2010</span> - <a href="http://${blogHost}">${blogTitle}</a><br/>
        Powered by
        <a href="http://b3log-solo.googlecode.com" target="_blank">
            <span style="color: orange;">B</span>
            <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
            <span style="color: green;">L</span>
            <span style="color: red;">O</span>
            <span style="color: blue;">G</span>&nbsp;
            <span style="color: orangered; font-weight: bold;">Solo</span></a>,
        ver ${version}
    </div>
</div>


<ul id="service-menu">
    <li id="contact-me"><a href="http://www.madeincima.eu/contact/">Contact</a></li>
    <li id="about-me"><a href="http://www.madeincima.eu/about/">About</a></li>
</ul>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
<script type="text/javascript">
    var util = new Util({
        "clearAllCacheLabel": "${clearAllCacheLabel}",
        "clearCacheLabel": "${clearCacheLabel}",
        "adminLabel": "${adminLabel}",
        "logoutLabel": "${logoutLabel}"
    });

    var init = function () {
        // article header: user list.
        var isAuthorArticle = false;
        $(".header-user a").each(function () {
            var it = this;
            if (window.location.search === it.search) {
                it.className = "star-current-icon";
                isAuthorArticle = true;
            }
        });
        if (isAuthorArticle) {
            $(".moon-current-icon").removeClass().addClass("moon-icon");
        }

        util.init();
    }

    init();
</script>
