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
        jsonRpc.pluginService.getPlugins(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_PLUGINS_SUCC":
                        $("#pluginList").table("update",{
                            data: [{
                                    "groupName": "all",
                                    "groupData": result.plugins
                                }]
                        });
                        
                        if (result.pagination.paginationPageCount === 0) {
                            result.pagination.paginationPageCount = 1;
                        }
                        
                        $("#pluginPagination").paginate("update", {
                                currentPage: pageNum,
                                pageCount: result.pagination.paginationPageCount
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
                    text: "${pluginNameLabel}",
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
            "bind": function(currentPage) {
                getPluginList(currentPage);
                return true;
            },
            "currentPage": 1,
            "errorMessage": "${inputErrorLabel}",
            "nextPageText": "${nextPagePabel}",
            "previousPageText": "${previousPageLabel}",
            "goText": "${gotoLabel}"
        });
        getPluginList(1);
    }
    initPlugin();
</script>
${plugins}
