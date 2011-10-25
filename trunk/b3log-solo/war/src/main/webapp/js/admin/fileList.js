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
 * file list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Oct 24, 2011
 */

/* file-list 相关操作 */
admin.fileList = {
    tablePagination:  new TablePaginate("file"),
    
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
            style: "padding-left: 12px;font-size:14px;",
            text: Label.fileNameLabel,
            index: "name",
            minWidth: 260
        }, {
            text: Label.uploadDateLabel,
            index: "uploadDate",
            width: 200,
            style: "padding-left: 12px;"
        }, {
            text: Label.sizeLabel + " (Bytes)",
            index: "size",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.downloadCountLabel,
            index: "downloadCnt",
            width: 80,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.getList(page);
        
        $("#formActionHidden").load(function () {
            var $iframe = $("#formActionHidden").contents();
            var tip = $iframe.find("pre").html();
            if (tip === "") {
                tip = Label.addSuccLabel; 
                $("#uploadFile").html("<input type='file' name='myFile' size='45'>");
                admin.fileList.getList(1);
            }
            $("#tipMsg").html(tip);
        });
    },

    /*
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        var that = this;
        
        this.pageInfo.currentPage = pageNum;
        
        $.ajax({
            url: "/console/files/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    $("#loadMsg").text(result.msg);
                    
                    return;
                }
                
                var files = result.files;
                var fileData = [];
                admin.fileList.pageInfo.currentCount = files.length;
                admin.fileList.pageInfo.pageCount = result.pagination.paginationPageCount;
                for (var i = 0; i < files.length; i++) {
                    fileData[i] = {};
                    fileData[i].name = "<a class='no-underline' href='" + files[i].fileDownloadURL + "'>"
                    + files[i].fileName + "</a>";
                    fileData[i].uploadDate = $.bowknot.getDate(files[i].fileUploadTime, 1);
                    fileData[i].downloadCnt = files[i].fileDownloadCount;
                    fileData[i].size = files[i].fileSize;
                    fileData[i].expendRow = "<a href='" + files[i].fileDownloadURL + "'>" + Label.downloadLabel + "</a>  \
                        <a href='javascript:void(0)' onclick=\"admin.fileList.del('" + files[i].oId + "')\">" + Label.removeLabel + "</a>";
                }

                that.tablePagination.updateTablePagination(fileData, pageNum, result.pagination);
            }
        });
        
        $("#loadMsg").text("");
    },

    /* 
     * 删除文件
     * @id 文件 id 
     */
    del: function (id) {
        var isDelete = confirm(Label.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            
            $.ajax({
                url: "/console/file/" + id,
                type: "DELETE",
                success: function(result, textStatus){
                    $("#loadMsg").text(result.msg);
                    
                    if (!result.sc) {
                        return;
                    }
                
                    var pageNum = admin.fileList.pageInfo.currentPage;
                    if (admin.fileList.pageInfo.currentCount === 1 && admin.fileList.pageInfo.pageCount !== 1 &&
                        admin.fileList.pageInfo.currentPage === admin.fileList.pageInfo.pageCount) {
                        admin.fileList.pageInfo.pageCount--;
                        pageNum = admin.fileList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(pageNum);
                    }
                    admin.fileList.getList(pageNum);
                }
            });
        }
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["file-list"] =  {
    "obj": admin.fileList,
    "init": admin.fileList.init,
    "refresh": function () {
        $("#loadMsg").text("");
    }
}