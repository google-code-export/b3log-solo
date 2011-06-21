<div id="cacheList"></div>
<div id="cachePagination" class="margin12 right"></div>
<div class="clear"></div>
<script type="text/javascript">
    var getCacheList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE
        };
        
        jsonRpc.pageCacheListService.getPages(function (result, error) {
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
                    
                    $("#cachePagination").paginate({
                        update: {
                            currentPage: pageNum,
                            pageCount: result.pagination.paginationPageCount
                        }
                    });
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    }
        
    var initCache = function () {          
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