<div id="pluginList">
</div>
<div id="pluginPagination" class="margin12 right">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var getPluginList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE
        };
        jsonRpc.adminService.getPlugins(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_PLUGINS_SUCC":
                        var pluginList = result.plugins;
                        var pluginData = [];
                        for (var i = 0; i < pluginList.length; i++) {
                            pluginData[i] = {};
                            pluginData[i].author = pluginList[i].author;
                            pluginData[i].name = pluginList[i].name;
                            pluginData[i].version= pluginList[i].version;
                            pluginData[i].status= pluginList[i].status.javaClass;
                        }

                        $("#pluginList").table("update",{
                            data: [{
                                    "groupName": "all",
                                    "groupData": pluginData
                                }]
                        });

                        if (result.pagination.paginationPageCount === 0) {
                            result.pagination.paginationPageCount = 1;
                        }

                        $("#pluginPagination").paginate({
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
        }, requestJSONObject);
    }
    
    var initPlugin = function () {
        $("#pluginList").table({
            colModel: [{
                    style: "padding-left: 6px;",
                    text: "${titleLabel}",
                    index: "name",
                    width: 230
                }, {
                    style: "padding-left: 6px;",
                    text: "${statusLabel}",
                    index: "status",
                    minWidth: 180
                }, {
                    style: "padding-left: 6px;",
                    text: "${authorLabel}",
                    index: "author",
                    width: 200
                }, {
                    style: "padding-left: 3px;",
                    text: "${versionLabel}",
                    index: "version",
                    width: 120
                }]
        });

        $("#pluginPagination").paginate({
            bindEvent: "getPluginList",
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
        getPluginList(1);
    }
    initPlugin();
</script>
${plugins}
