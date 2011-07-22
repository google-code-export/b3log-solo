<div id="cachePlugin">
    <div id="cacheContent"></div>
    <div id="cacheList"></div>
    <div id="cachePagination" class="margin12 right"></div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    plugins.cacheList = {
        getList: function (pageNum) {
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
                        if (result.pagination.paginationPageCount === 0) {
                            pageNum = 0;
                        }
                        $("#cachePagination").paginate("update", {
                            currentPage: pageNum,
                            pageCount: result.pagination.paginationPageCount
                        });
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(3);
                }
            }, requestJSONObject);
        },        
    
        changeStatus: function (it) {
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
        },
        
        getCache: function () {
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
                        + " </span>&nbsp;${pageCacheStatus1Label} &nbsp; <button onclick=\"window.plugins.cacheList.changeStatus(this);\">" 
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
    
        init: function () {     
            $("#loadMsg").text("${loadingLabel}");
        
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
                    plugins.cacheList.getList(currentPage);
                    return true;
                },
                "currentPage": 1,
                "errorMessage": "${inputErrorLabel}",
                "nextPageText": "${nextPagePabel}",
                "previousPageText": "${previousPageLabel}",
                "goText": "${gotoLabel}"            
            });
            this.getCache();
            this.getList(1);
            $("#loadMsg").text();
        }
    };
    
    /*
     * before load script, init namespace
     * 1. add tab
     * 2. remove origin html code
     * 3. register init and refresh function
     * 4. after add all plugins, invok setCurByHash()
     */ 
    plugins.cacheList.type = "plugin";
    
    
    $("#tabs").tabs("add", {
        "id": "cache-list",
        "text": "${cacheMgmtLabel}",
        "path": "/tools/plugin-list",
        "content": $("#cachePlugin").html()
    });
     
    $("#cachePlugin").remove();
    
    /*
     * 注册到 admin 进行管理 
     */
    admin.register["cache-list"] =  {
        "obj": plugins.cacheList,
        "init": plugins.cacheList.init,
        "refresh":  plugins.cacheList.init
    }
    
    admin.setCurByHash();
</script>