<div id="cachePlugin">
    <div id="cacheContent"></div>
    <div id="cacheList"></div>
    <div id="cachePagination" class="margin12 right"></div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    plugins.cacheList = {};
    
    
    
    
    
    
    plugins.cacheList.getCacheList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE
        };
        
        jsonRpc.adminCacheService.getPages(function (result, error) {
            try {
                if (result.sc) {
                    var caches = result.pages;
                    var cacheData = [];
                    
                    for (var i = 0; i < caches.length; i++) {
                        cacheData[i] = {};
                        cacheData[i].cachedTitle = "<a href='" + caches[i].link + "'  target='_blank'>" 
                            + caches[i].cachedTitle + "</a>";
                        cacheData[i].cachedType = caches[i].cachedType;
                    }

                    $("#cacheList").table("update",{
                        data: [{
                                groupName: "all",
                                groupData: cacheData
                            }]
                    });
                    
                    // pagination
                    if (0 === result.pagination.paginationPageCount) {
                        result.pagination.paginationPageCount = 1;
                    }
                    
                    $("#cachePagination").paginate("update", {
                        currentPage: pageNum,
                        pageCount: result.pagination.paginationPageCount
                    });
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    }
        
    
    plugins["cacheList"].changeCacheStatus = function (it) {
        var $it = $(it);
        if ($it.text() === "${enabledLabel}") {
            $it.text("${disabledLabel}");
            
            var requestJSONObject = {
                "pageCacheEnabled": false
            };
            jsonRpc.adminCacheService.setPageCache(requestJSONObject);
        } else {
            $it.text("${enabledLabel}");
            
            var requestJSONObject = {
                "pageCacheEnabled": true
            };
            jsonRpc.adminCacheService.setPageCache(requestJSONObject);
        }
    }
    
    plugins.cacheList.initCache = function () {     
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.adminCacheService.getPageCache(function (result, error) {
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
                    + " &nbsp; </span>${pageCacheStatus1Label} &nbsp; <button onclick=\"window.plugins.cacheList.changeCacheStatus(this);\">" 
                    + pageCacheStatusLabel
                    + "</button>"; 
                $("#cacheContent").html(cacheHTML);
                $("#loadMsg").text("");
            } catch (e) {}
        });
        
        $("#cacheList").table({
            colModel: [{
                    style: "padding-left: 6px;",
                    text: "${typeLabel}",
                    index: "cachedType",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    text: "${titleLabel}",
                    index: "cachedTitle",
                    minWidth: 300
                }]
        });
        
        $("#cachePagination").paginate({
            "bind": function(currentPage) {
                plugins.cacheList.getCacheList(currentPage);
                return true;
            },
            "currentPage": 1,
            "errorMessage": "${inputErrorLabel}",
            "nextPageText": "${nextPagePabel}",
            "previousPageText": "${previousPageLabel}",
            "goText": "${gotoLabel}"            
        });
        plugins.cacheList.getCacheList(1);
    }
    
    
    
    
    
    
    
    
    
    
    $("#tabs").tabs("add", {
        "id": "cacheList",
        "text": "<a href=\"#cache-list\"><div class=\"left cacheIcon\"></div>cache</a>",
        "content": $("#cachePlugin").html()
    });
    
    var hash = window.location.hash;
    if (hash === "#cache-list") {
        plugins.cacheList.initCache();
    } else {
        var index = hash.length - 1;
        if(hash.indexOf("/") > -1) {
            index = hash.indexOf("/") - 1;
        }
        var tag = hash.substr(1, index);
        $("#tabs").tabs("select", tag);
    }
    $("#tabs").tabs("bind", [{
            "type": "click",
            "action": function (event, data) {
                plugins.cacheList.initCache();
            }
        }], "cacheList");
        
    $("#cachePlugin").remove();
</script>