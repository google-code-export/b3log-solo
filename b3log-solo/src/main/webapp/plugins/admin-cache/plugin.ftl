<style type="text/css">
    #cacheContent {
        line-height: 28px;
        padding: 12px;
    }
</style>
<div id="cachePlugin">
    <div id="cacheContent"></div>
    <div id="cacheTable"></div>
    <div id="cachePagination" class="margin12 right"></div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    plugins["cache-list"] = {
        tablePagination:  new TablePaginate("cache"),
        getList: function (pageNum) {
            var that = this;
            $("#loadMsg").text("${loadingLabel}");
        
            var requestJSONObject = {
                "paginationCurrentPageNum": pageNum,
                "paginationPageSize": Label.PAGE_SIZE,
                "paginationWindowSize": Label.WINDOW_SIZE
            };
        
            jsonRpc.adminCacheService.getPages(function (result, error) {
                try {
                    if (!result) {
                        alert("${pageLabel}" + ":" + pageNum + " " + "${noDataLable}");
                        return;
                    }
                    if (result.sc) {
                        var caches = result.pages;
                        var cacheData = caches;
                        for (var i = 0; i < caches.length; i++) {
                            cacheData[i].cachedTitle = "<a href='" + caches[i].cachedLink + "'  target='_blank'>" 
                                + caches[i].cachedTitle + "</a>";
                            cacheData[i].cachedTime = $.bowknot.getDate(cacheData[i].cachedTime, 1);
                        }

                        that.tablePagination.updateTablePagination(cacheData, pageNum, result.pagination);
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(3);
                }
            }, requestJSONObject);
        },        
    
        changeStatus: function (it) {
            $("#loadMsg").text("${loadingLabel}");
            var $it = $(it),
            requestJSONObject = {};
            if ($it.text() === "${enabledLabel}") {
                requestJSONObject.pageCacheEnabled = false;
            } else {
                requestJSONObject.pageCacheEnabled = true;
            }
            
            jsonRpc.adminCacheService.setPageCache(function () {
                if ($it.text() === "${enabledLabel}") {
                    $it.text("${disabledLabel}");
                } else {
                    $it.text("${enabledLabel}");
                }
                $("#tipMsg").text("${updateSuccLabel}");
                $("#loadMsg").text("");
            }, requestJSONObject);
        },
        
        getCache: function () {
            $("#loadMsg").text("${loadingLabel}");
            jsonRpc.adminCacheService.getPageCache(function (result, error) {
                try {
                    var pageCacheStatusLabel = "${disabledLabel}";
                    if (result.pageCacheEnabled) {
                        pageCacheStatusLabel = "${enabledLabel}";
                    }
                    var cacheHTML = "</span>${cachedBytes1Label}<span class='f-blue'> " + result.cacheCachedBytes
                        + " </span>&nbsp;${cachedCount1Label}<span class='f-blue'>" + result.cacheCachedCount
                        + " </span>&nbsp;${hitCount1Label}<span class='f-blue'>" + result.cacheHitCount
                        + " </span>&nbsp;${hitBytes1Label}<span class='f-blue'>" + result.cacheHitBytes
                        + " </span>&nbsp;${missCount1Label}<span class='f-blue'>" + result.cacheMissCount 
                        + " </span>&nbsp;${pageCacheStatus1Label} &nbsp; <button onclick=\"window.plugins['cache-list'].changeStatus(this);\">" 
                        + pageCacheStatusLabel
                        + "</button><br/>"
                        + "${pageCachedCnt1Label}<span class='f-blue'>" + result.pageCachedCnt; 
                    $("#cacheContent").html(cacheHTML);
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            });
        },
    
        init: function (page) {   
            this.tablePagination.buildTable([{
                    style: "padding-left: 6px;",
                    text: "${typeLabel}",
                    index: "cachedType",
                    width: 220
                }, {
                    style: "padding-left: 6px;",
                    text: "${titleLabel}",
                    index: "cachedTitle",
                    minWidth: 300
                }, {
                    style: "padding-left: 6px;",
                    text: "${hitCountLabel}",
                    index: "cachedHitCount",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    text: "${sizeLabel}(Byte)",
                    index: "cachedBtypesLength",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    text: "${createDateLabel}",
                    index: "cachedTime",
                    width: 160
                }]);
    
            this.tablePagination.initPagination();
            this.getList(page);
            this.getCache();
        },
        
        refresh: function (page) {
            this.getList(page);
            this.getCache();
        }
    };
    
    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "cache-list",
        "text": "${cacheMgmtLabel}",
        "path": "/tools",
        "index": 7,
        "content": $("#cachePlugin").html()
    });
    
    // 移除现有内容
    $("#cachePlugin").remove();
</script>