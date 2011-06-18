<div id="cacheContent">
</div>
<script type="text/javascript">    
    var getCacheState = function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminService.getPageCache(function (result, error) {
            try {
                var cacheStatusLabel = "${closeLabel}";
                if (result.pageCacheEnabled) {
                    cacheStatusLabel = "${openLabel}";
                }
                var cacheHTML = "${cachedBytes1Label}<span class='f-blue'> " + result.cacheCachedBytes
                    + " &nbsp; </span>${cachedCount1Label}<span class='f-blue'>" + result.cacheCachedCount
                    + " &nbsp; </span>${hitCount1Label}<span class='f-blue'>" + result.cacheHitCount
                    + " &nbsp; </span>${hitBytes1Label}<span class='f-blue'>" + result.cacheHitBytes
                    + " &nbsp; </span>${missCount1Label}<span class='f-blue'>" + result.cacheMissCount 
                    + " &nbsp; </span>${cacheStatusLabel} &nbsp; <button onclick='changeCacheStatus(this);'>" 
                    + cacheStatusLabel
                    + "</button>";
                $("#cacheContent").html(cacheHTML);
                $("#loadMsg").text("");
            } catch (e) {}
        });
    };
    getCacheState();
    
    var changeCacheStatus = function (it) {
        var $it = $(it);
        if ($it.text() === "${openLabel}") {
            $it.text("${closeLabel}")
        } else {
            $it.text("${openLabel}")
        }
    }
</script>
${plugins}