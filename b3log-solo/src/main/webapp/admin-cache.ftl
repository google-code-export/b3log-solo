<div class="padding12" id="cacheContent">
</div>
<script type="text/javascript">
    var getCacheState = function () {
        jsonRpc.adminService.getPageCache(function (result, error) {
            var cacheHTML = "</div><div>${maxCount1Label}" + result.cacheMaxCount
                + "<div>${cachedCount1Label}" + result.cacheCachedCount
                + "</div><div>${hitCount1Label}" + result.cacheHitCount
                + "</div><div>${missCount1Label}" + result.cacheMissCount + "</div>";
            $("#cacheContent").html(cacheHTML);
        });
    }
    getCacheState();
</script>
