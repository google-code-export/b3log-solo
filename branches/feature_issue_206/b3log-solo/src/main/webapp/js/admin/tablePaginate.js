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
 * table and paginate util
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, Jun 30, 2011
 */

var TablePaginate = function (id) {
    this.id = id;
    this.currentPage = 1;
};

$.extend(TablePaginate.prototype, {
    /*
     * 构建 table 框架
     * @colModel table 列宽，标题等数据
     */
    buildTable: function (colModel, noExpend) {
        var tableData = {
            colModel: colModel
        }
        if (!noExpend) {
            tableData.expendRow = {
                index: "expendRow"
            }
        }
        $("#" + this.id + "Table").table(tableData);
    
    },
    
    /*
     * 初始化分页
     */
    initPagination: function () {
        var id = this.id;
        $("#" + id + "Pagination").paginate({
            "bind": function(currentPage) {
                var hash = window.location.hash;
                var tag = hash.substr(1, hash.length - 1);
                admin.register[tag].obj.getList(currentPage);
                this.currentPage = currentPage;
                return true;
            },
            "currentPage": 1,
            "errorMessage": Label.inputErrorLabel,
            "nextPageText": Label.nextPagePabel,
            "previousPageText": Label.previousPageLabel,
            "goText": Label.gotoLabel
        });
    },

    /*
     * 初始化评论对话框
     */
    initCommentsDialog: function () {
        var that = this;
        $("#" + this.id + "Comments").dialog({
            width: 700,
            height:500,
            "modal": true,
            "hideFooter": true,
            "close": function () {
                admin[that.id + "List"].getList(that.currentPage);
                return true;
            }
        });
    },
    
    /*
     * 更新 table & paginateion
     */
    updateTablePagination: function (data, currentPage, pageCount) {
        $("#" + this.id + "Table").table("update",{
            data: [{
                groupName: "all",
                groupData: data
            }]
        });
                    
        if (pageCount === 0) {
            currentPage = 0;
        }
        $("#" + this.id + "Pagination").paginate("update", {
            pageCount: pageCount,
            currentPage: currentPage
        });
        this.currentPage = currentPage;
    }
});