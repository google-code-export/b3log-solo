<div class="padding12" id="cacheContent">
</div>
<script type="text/javascript">    
    (function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminService.getPageCache(function (result, error) {
            try {
                var cacheHTML = "<div>${cachedBytes1Label}" + result.cacheCachedBytes + "</div>"
                    + "<div>${cachedCount1Label}" + result.cacheCachedCount + "</div>"
                    + "<div>${hitCount1Label}" + result.cacheHitCount + "</div>"
                    + "<div>${hitBytes1Label}" + result.cacheHitBytes + "</div>"
                    + "<div>${missCount1Label}" + result.cacheMissCount + "</div>";
                $("#cacheContent").html(cacheHTML);
                $("#loadMsg").text("");
            } catch (e) {}
        });
    })();
</script>
${plugins}