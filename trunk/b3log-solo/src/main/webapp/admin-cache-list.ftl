<div class="padding12" id="cacheContent">
</div>
<script type="text/javascript">    
    (function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminService.getPageCache(function (result, error) {
            try {
                var cacheHTML = "<div>${cachedBytes1Label}" + result.cacheCachedBytes
                    + "${cachedCount1Label}" + result.cacheCachedCount
                    + "${hitCount1Label}" + result.cacheHitCount
                    + "${hitBytes1Label}" + result.cacheHitBytes
                    + "${missCount1Label}" + result.cacheMissCount + "</div>";
                $("#cacheContent").html(cacheHTML);
                $("#loadMsg").text("");
            } catch (e) {}
        });
    })();
</script>
${plugins}