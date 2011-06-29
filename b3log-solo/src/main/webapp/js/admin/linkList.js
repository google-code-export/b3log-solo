/*
 * Copyright (c) 2011, B3log Team
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
 * link list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.1, Jun 29, 2011
 */

/* link-list 相关操作 */
admin.linkList = {
    tablePagination:  new TablePaginate("link"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    id: "",
    
    /* 
     * 初始化 table, pagination
     */
    init: function () {
        this.tablePagination.buildTable([{
                text: "",
                index: "linkOrder",
                width: 60
            },{
                style: "padding-left: 12px;",
                text: Label.linkTitleLabel,
                index: "linkTitle",
                width: 230
            }, {
                style: "padding-left: 12px;",
                text: Label.urlLabel,
                index: "linkAddress",
                minWidth: 180
            }]);
    
        this.tablePagination.initPagination();
        this.getList(1);
        
        $("#updateLink").dialog({
            width: 700,
            height: 160,
            "modal": true,
            "hideFooter": true
        });
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        this.pageInfo.currentPage = pageNum;
        var that = this;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE
        };
        jsonRpc.linkService.getLinks(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_LINKS_SUCC":
                        var links = result.links;
                        var linkData = [];
                        admin.linkList.pageInfo.currentCount = links.length;
                        admin.linkList.pageInfo.pageCount = result.pagination.paginationPageCount;

                        for (var i = 0; i < links.length; i++) {
                            linkData[i] = {};
                            if (i === 0) {
                                if (links.length === 1) {
                                    linkData[i].linkOrder = "";
                                } else {
                                    linkData[i].linkOrder = '<div class="table-center" style="width:16px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                                }
                            } else if (i === links.length - 1) {
                                linkData[i].linkOrder = '<div class="table-center" style="width:16px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                            </div>';
                            } else {
                                linkData[i].linkOrder = '<div class="table-center" style="width:38px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                            }
                            linkData[i].linkTitle = links[i].linkTitle;
                            linkData[i].linkAddress = "<a target='_blank' class='no-underline' href='" + links[i].linkAddress + "'>"
                                + links[i].linkAddress + "</a>";
                            linkData[i].expendRow = "<span><a href='" + links[i].linkAddress + "' target='_blank'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.linkList.get('" + links[i].oId + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.linkList.del('" + links[i].oId + "')\">" + Label.removeLabel + "</a></span>";
                        }

                        that.tablePagination.updateTablePagination(linkData, pageNum, result.pagination.paginationPageCount);
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
    
    /*
     * 添加链接
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitle").val(),
                    "linkAddress": $("#linkAddress").val()
                }
            };
            jsonRpc.linkService.addLink(function (result, error) {
                try {
                    switch (result.sc) {
                        case "ADD_LINK_SUCC":
                            $("#linkTitle").val("");
                            $("#linkAddress").val("");
                            if (admin.linkList.pageInfo.currentCount === Label.PAGE_SIZE &&
                                admin.linkList.pageInfo.currentPage === admin.linkList.pageInfo.pageCount) {
                                admin.linkList.pageInfo.pageCount++;
                            }
                            admin.linkList.getList(admin.linkList.pageInfo.pageCount);
                            $("#tipMsg").text(Label.addSuccLabel);
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },
    
    /*
     * 获取链接
     * @id 链接 id
     */
    get: function (id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#updateLink").dialog("open");
        var requestJSONObject = {
            "oId": id
        };

        jsonRpc.linkService.getLink(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_LINK_SUCC":
                        admin.linkList.id = id;
                        $("#linkTitleUpdate").val(result.link.linkTitle);
                        $("#linkAddressUpdate").val(result.link.linkAddress);
                        break;
                    case "GET_LINK_FAIL_":
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    },
    
    /*
     * 跟新自定义页面
     */
    update: function () {
        if (this.validate("Update")) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitleUpdate").val(),
                    "oId": this.id,
                    "linkAddress": $("#linkAddressUpdate").val()
                }
            };
            jsonRpc.linkService.updateLink(function (result, error) {
                try {
                    switch (result.sc) {
                        case "UPDATE_LINK_SUCC":
                            $("#updateLink").dialog("close");
                            admin.linkList.getList(admin.linkList.pageInfo.currentPage);
                            $("#tipMsg").text(Label.updateSuccLabel);
                            break;
                        case "UPDATE_LINK_FAIL_":
                            $("#updateLink").dialog("close");
                            $("#tipMsg").text(Label.updateFailLabel);
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },
    
    /*
     * 删除链接
     * @id 链接 id
     */
    del: function (id) {
        var isDelete = confirm(Label.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": id
            };

            jsonRpc.linkService.removeLink(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_LINK_SUCC":
                            var pageNum = admin.linkList.pageInfo.currentPage;
                            if (admin.linkList.pageInfo.currentCount === 1 && admin.linkList.pageInfo.pageCount !== 1 &&
                                admin.linkList.pageInfo.currentPage === admin.linkList.pageInfo.pageCount) {
                                admin.linkList.pageInfo.pageCount--;
                                pageNum = admin.linkList.pageInfo.pageCount;
                            }
                            admin.linkList.getList(pageNum);
                            $("#tipMsg").text(Label.removeSuccLabel);
                            break;
                        case "REMOVE_LINK_FAIL_":
                            $("#tipMsg").text(Label.removeFailLabel);
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },
    
    /*
     * 验证字段
     * @status 更新或者添加时进行验证
     */
    validate: function (status) {
        if (!status) {
            status = "";
        }
        if ($("#linkTitle" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#linkTitle" + status).focus().val("");
        } else if ($("#linkAddress" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.addressEmptyLabel);
            $("#linkAddress" + status).focus().val("");
        } else {
            return true;
        }
        return false;
    },
    
    /*
     * 调换顺序
     */
    changeOrder: function (id, order, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var srcOrder = order;
        if (status === "up") {
            srcOrder -= 1;
        } else {
            srcOrder += 1;
        }

        jsonRpc.linkService.changeOrder(function (result, error) {
            try {
                if (result) {
                    admin.linkList.getList(admin.linkList.pageInfo.currentPage);
                } else {
                    $("#tipMsg").text(Label.updateFailLabel);
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        }, id.toString(), srcOrder);
    }
};