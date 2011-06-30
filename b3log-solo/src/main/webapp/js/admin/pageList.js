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
 * page list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.2, Jun 30, 2011
 */

/* page-list 相关操作 */
admin.pageList = {
    tablePagination:  new TablePaginate("page"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    id: "",
    
    /* 
     * 初始化 table, pagination, comments dialog
     */
    init: function () {
        this.tablePagination.buildTable([{
            text: "",
            index: "pageOrder",
            width: 60,
            style: "padding-left: 12px;font-size:16px;"
        }, {
            style: "padding-left: 12px;",
            text: Label.titleLabel,
            index: "pageTitle",
            width: 300
        }, {
            style: "padding-left: 12px;",
            text: Label.permalinkLabel,
            index: "pagePermalink",
            minWidth: 300
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(1);
        
        var language = Label.localeString.substring(0, 2);
        tinyMCE.init({
            // General options
            language: language,
            mode : "exact",
            elements : "pageContent",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen,syntaxhl",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen,syntaxhl",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class],iframe[src|width|height|name|align]",

            relative_urls: false,
            remove_script_host: false
        });
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
        this.pageInfo.currentPage = pageNum;
        
        jsonRpc.pageService.getPages(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_PAGES_SUCC":
                        var pages = result.pages;
                        var pageData = [];
                        admin.pageList.pageInfo.currentCount = pages.length;
                        admin.pageList.pageInfo.pageCount = result.pagination.paginationPageCount;
                        for (var i = 0; i < pages.length; i++) {
                            pageData[i] = {};
                            if (i === 0) {
                                if (pages.length === 1) {
                                    pageData[i].pageOrder = "";
                                } else {
                                    pageData[i].pageOrder = '<div class="table-center" style="width:16px">\
                                        <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" \
                                        class="table-downIcon"></span></div>';
                                }
                            } else if (i === pages.length - 1) {
                                pageData[i].pageOrder = '<div class="table-center" style="width:16px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                    </div>';
                            } else {
                                pageData[i].pageOrder = '<div class="table-center" style="width:38px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                                    </div>';
                            }
                            
                            pageData[i].pageTitle = "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>" +
                            pages[i].pageTitle + "</a>";
                            pageData[i].pagePermalink = "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>"
                            + pages[i].pagePermalink + "</a>";
                            pageData[i].comments = pages[i].pageCommentCount;
                            pageData[i].expendRow = "<span><a href='" + pages[i].pagePermalink + "' target='_blank'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.pageList.get('" + pages[i].oId + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.pageList.del('" + pages[i].oId + "')\">" + Label.removeLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + pages[i].oId + "', 'page')\">" + Label.commentLabel + "</a></span>";
                        }
                        
                        that.tablePagination.updateTablePagination(pageData, pageNum, result.pagination.paginationPageCount);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    },
    
    /*
     * 获取自定义页面
     * @id 自定义页面 id
     */
    get: function (id) {
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        var requestJSONObject = {
            "oId": id
        };

        jsonRpc.pageService.getPage(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_PAGE_SUCC":
                        admin.pageList.id = id;
                        tinyMCE.get('pageContent').setContent(result.page.pageContent);
                        $("#pagePermalink").val(result.page.pagePermalink);
                        $("#pageTitle").val(result.page.pageTitle);
                        $("#tipMsg").text(Label.getSuccLabel);
                        break;
                    case "GET_PAGE_FAIL_":
                        $("#tipMsg").text(Label.getFailLabels);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    },

    /* 
     * 删除自定义页面
     * @id 自定义页面 id
     */
    del: function (id) {
        var isDelete = confirm(Label.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": id
            };

            jsonRpc.pageService.removePage(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_PAGE_SUCC":
                            var pageNum = admin.pageList.pageInfo.currentPage;
                            if (admin.pageList.pageInfo.currentCount === 1 && admin.pageList.pageInfo.pageCount !== 1 &&
                                admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                                admin.pageList.pageInfo.pageCount--;
                                pageNum = admin.pageList.pageInfo.pageCount;
                            }
                            admin.pageList.getList(pageNum);
                            $("#tipMsg").text(Label.removeSuccLabel);
                            break;
                        case "REMOVE_PAGE_FAIL_":
                            $("#tipMsg").text(Label.removeFailLabel);
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, requestJSONObject);
        }
    },
    
    /*
     * 添加自定义页面
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            jsonRpc.pageService.addPage(function (result, error) {
                try {
                    switch (result.sc) {
                        case "ADD_PAGE_FAIL_DUPLICATED_PERMALINK":
                            var msg = Label.addFailLabel + ", " + Label.duplicatedPermalinkLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "ADD_PAGE_SUCC":
                            admin.pageList.id = "";
                            $("#pagePermalink").val("");
                            $("#pageTitle").val("");
                            if (tinyMCE.get("pageContent")) {
                                tinyMCE.get('pageContent').setContent("");
                            } else {
                                $("#pageContent").val("");
                            }
                            
                            if (admin.pageList.pageInfo.currentCount === Label.PAGE_SIZE &&
                                admin.pageList.pageInfo.currentPage === dmin.pageList.pageInfo.pageCount) {
                                admin.pageList.pageInfo.pageCount++;
                            }
                            admin.pageList.getList(admin.pageList.pageInfo.pageCount);
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
     * 跟新自定义页面
     */
    update: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": this.id,
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            jsonRpc.pageService.updatePage(function (result, error) {
                try {
                    switch (result.sc) {
                        case "UPDATE_PAGE_FAIL_DUPLICATED_PERMALINK":
                            var msg = Label.addFailLabel + ", " + Label.duplicatedPermalinkLabel;
                            $("#tipMsg").text(msg);
                            break;
                        case "UPDATE_PAGE_SUCC":
                            admin.pageList.getList(admin.pageList.pageInfo.currentPage);
                            admin.pageList.id = "";
                            $("#tipMsg").text(Label.updateSuccLabel);
                            $("#pageTitle").val("");
                            tinyMCE.get('pageContent').setContent("");
                            $("#pagePermalink").val("");
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
     */
    validate: function () {
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#pageTitle").focus();
        } else if (tinyMCE.get('pageContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.contentEmptyLabel);
        } else {
            return true;
        }
        return false;
    },
    
    /*
     * 提交自定义页面
     */
    submit: function () {
        if (this.id !== "") {
            this.update();
        } else {
            this.add();
        }
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

        jsonRpc.pageService.changeOrder(function (result, error) {
            try {
                if (result) {
                    admin.pageList.getList(admin.pageList.pageInfo.currentPage);
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

/*
 * 注册到 admin 进行管理 
 */
admin.register["page-list"] =  {
    "obj": admin.pageList,
    "init": admin.pageList.init,
    "refresh": admin.pageList.getList
}