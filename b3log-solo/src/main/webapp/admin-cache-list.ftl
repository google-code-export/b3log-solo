<div id="cacheContent">
</div>
<script type="text/javascript">    
    var getCacheState = function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminService.getPageCache(function (result, error) {
            try {
                var pageCacheStatusLabel = "${disabledLabel}";
                if (result.pageCacheEnabled) {
                    pageCacheStatusLabel = "${enabledLabel}";
                }
                var cacheHTML = "${pageCachedCnt1Label}<span class='f-blue'>" + result.pageCachedCnt
                    + " &nbsp; </span>${cachedBytes1Label}<span class='f-blue'> " + result.cacheCachedBytes
                    + " &nbsp; </span>${cachedCount1Label}<span class='f-blue'>" + result.cacheCachedCount
                    + " &nbsp; </span>${hitCount1Label}<span class='f-blue'>" + result.cacheHitCount
                    + " &nbsp; </span>${hitBytes1Label}<span class='f-blue'>" + result.cacheHitBytes
                    + " &nbsp; </span>${missCount1Label}<span class='f-blue'>" + result.cacheMissCount 
                    + " &nbsp; </span>${pageCacheStatus1Label} &nbsp; <button onclick='changeCacheStatus(this);'>" 
                    + pageCacheStatusLabel
                    + "</button>";
                $("#cacheContent").html(cacheHTML);
                $("#loadMsg").text("");
            } catch (e) {}
        });
    };
    getCacheState();
    
    var changeCacheStatus = function (it) {
        var $it = $(it);
        if ($it.text() === "${enabledLabel}") {
            $it.text("${disabledLabel}");
            
            var requestJSONObject = {
                "pageCacheEnabled": false
            };
            jsonRpc.adminService.setPageCache(requestJSONObject);
        } else {
            $it.text("${enabledLabel}");
            
            var requestJSONObject = {
                "pageCacheEnabled": true
            };
            jsonRpc.adminService.setPageCache(requestJSONObject);
        }
    }
</script>
${plugins}