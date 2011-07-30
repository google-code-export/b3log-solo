/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * plugin list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.6, July 27, 2011
 */

/* plugin-list 相关操作 */
admin.pluginList = {
    tablePagination:  new TablePaginate("plugin"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    /* 
     * 初始化 table, pagination
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            style: "padding-left: 12px;",
            text: Label.pluginNameLabel,
            index: "name",
            width: 230
        }, {
            style: "padding-left: 12px;",
            text: Label.statusLabel,
            index: "status",
            minWidth: 180
        }, {
            style: "padding-left: 12px;",
            text: Label.authorLabel,
            index: "author",
            width: 200
        }, {
            style: "padding-left: 12px;",
            text: Label.versionLabel,
            index: "version",
            width: 120
        }]);
    
        this.tablePagination.initPagination();
        this.getList(page);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        var that = this;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE
        };
        jsonRpc.pluginService.getPlugins(function (result, error) {
            try {
                if (!result) {
                    alert(Label.pageLabel + ":" + pageNum + " " + Label.noDataLable);
                     $("#loadMsg").text("");
                    return;
                }
                switch (result.sc) {
                    case "GET_PLUGINS_SUCC":
                        admin.pluginList.pageInfo.currentPage = pageNum;
                        var datas = result.plugins;
                        for (var i = 0; i < datas.length; i++) {
                            datas[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.pluginList.changeStatus('" + 
                            datas[i].oId + "', '" + datas[i].status + "')\">";
                            if (datas[i].status === "ENABLED") {
                                datas[i].status = Label.enabledLabel;
                                datas[i].expendRow += Label.disabledLabel;
                            } else {
                                datas[i].status = Label.disabledLabel;
                                datas[i].expendRow += Label.enabledLabel;
                            }
                            datas[i].expendRow += "</a>";
                        }
                        that.tablePagination.updateTablePagination(result.plugins, pageNum, result.pagination);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        }, requestJSONObject);
    },
    
    changeStatus: function (pluginId, status) {
        if (status === "ENABLED") {
            status = "DISABLED";
        } else {
            status = "ENABLED";
        }
        jsonRpc.pluginService.setPluginStatus(function () {
            //admin.pluginList.getList(admin.pluginList.pageInfo.currentPage);
            $("#tipMsg").text(Label.updateSuccLabel);
            window.location.reload();
        }, pluginId, status);
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["plugin-list"] =  {
    "obj": admin.pluginList,
    "init": admin.pluginList.init,
    "refresh": admin.pluginList.getList
}