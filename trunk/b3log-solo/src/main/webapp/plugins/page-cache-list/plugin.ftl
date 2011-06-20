<div id="cacheList"></div>
<div id="cachePagination" class="margin12 right"></div>
<script type="text/javascript">
    var getCacheList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        var caches = [{cachedType: 1, cachedTitle: 1, link: 1}];
        var cacheData = [];
                    
        for (var i = 0; i < caches.length; i++) {
            cacheData[i] = {};
            cacheData[i].cachedType = caches[i].cachedType;
            cacheData[i].cachedTitle = caches[i].cachedTitle;
            cacheData[i].link = caches[i].link;
            
            console.log(cacheData[i]);
        }

        $("#cacheList").table("update",{
            data: cacheData
        });

        $("#cachePagination").paginate({
            update: {
                currentPage: pageNum,
                pageCount: 10
            }
        });
        $("#loadMsg").text("");
        /*jsonRpc.pageCacheListService.getPages(function (result, error) {
                try {
                    switch (result.sc) {
                        case "GET_USERS_SUCC":
                            var caches = result.pages;
                            var cacheData = [];
                    
                            for (var i = 0; i < caches.length; i++) {
                                cacheData[i] = {};
                                cacheData[i].title = caches[i].userName;
                                cacheData[i].type = caches[i].userEmail;
                            }

                            $("#cacheList").table("update",{
                                data: cacheData
                            });

                            $("#cachePagination").paginate({
                                update: {
                                    currentPage: pageNum,
                                    pageCount: result.pagination.paginationPageCount
                                }
                            });
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);*/
    }
        
    var initCache = function () {
        $("#cacheList").table({
            colModel: [{
                    style: "padding-left: 6px;",
                    name: "${typeLabel}",
                    index: "cachedType",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    name: "${titleLabel}",
                    index: "cachedTitle",
                    minWidth: 300
                }, {
                    visible: false,
                    index: "link"
                }]
        });
                
        $("#cachePagination").paginate({
            bindEvent: "getCacheList",
            pageCount: 1,
            windowSize: adminUtil.WINDOW_SIZE,
            currentPage: 1,
            style: "google",
            isGoTo: false,
            lastPage: "${lastPageLabel}",
            nextPage: "${nextPagePabel}",
            previousPage: "${previousPageLabel}",
            firstPage: "${firstPageLabel}"
        });
        getCacheList(1);
    };
    initCache();
</script>