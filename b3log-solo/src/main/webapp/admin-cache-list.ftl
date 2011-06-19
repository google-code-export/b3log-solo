<div id="cacheContent">
</div>
<script type="text/javascript">    
    var getCacheState = function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminService.getPageCache(function (result, error) {
            try {
                var cacheStatus1Label = "${disabledLabel}";
                if (result.pageCacheEnabled) {
                    cacheStatus1Label = "${enabledLabel}";
                }
                var cacheHTML = "${cachedBytes1Label}<span class='f-blue'> " + result.cacheCachedBytes
                    + " &nbsp; </span>${cachedCount1Label}<span class='f-blue'>" + result.cacheCachedCount
                    + " &nbsp; </span>${hitCount1Label}<span class='f-blue'>" + result.cacheHitCount
                    + " &nbsp; </span>${hitBytes1Label}<span class='f-blue'>" + result.cacheHitBytes
                    + " &nbsp; </span>${missCount1Label}<span class='f-blue'>" + result.cacheMissCount 
                    + " &nbsp; </span>${cacheStatus1Label} &nbsp; <button onclick='changeCacheStatus(this);'>" 
                    + cacheStatus1Label
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