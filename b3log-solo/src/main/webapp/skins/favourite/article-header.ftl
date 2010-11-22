<div class="header">
    <h1 class="title">
        <a href="/" id="logoTitle" >
            ${blogTitle}
        </a>
    </h1>
    <span class="sub-title">${blogSubtitle}</span>
</div>
<script type="text/javascript">
    var replaceCommentsEm = function (selector) {
        var $commentContents = $(selector);
        for (var i = 0; i < $commentContents.length; i++) {
            var str = $commentContents[i].innerHTML;
            var ems = str.split("[em");
            var content = ems[0];
            for (var j = 1; j < ems.length; j++) {
                var key = ems[j].substr(0, 2),
                emImgHTML = "<img src='/skins/i-nove/emotions/em" + key
                    + ".png'/>";
                content += emImgHTML + ems[j].slice(3);
            }
            $commentContents[i].innerHTML = content;
        }
    }
</script>