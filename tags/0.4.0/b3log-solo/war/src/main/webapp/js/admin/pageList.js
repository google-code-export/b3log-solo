/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Jan 7, 2012
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
    init: function (page) {
        this.tablePagination.buildTable([{
            text: "",
            index: "pageOrder",
            width: 60,
            style: "padding-left: 12px;font-size:14px;"
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
        this.getList(page);
        
        var language = Label.localeString.substring(0, 2);
        if (language === "zh") {
            language = "zh-cn";
        }
        try {
            tinyMCE.init({
                // General options
                language: language,
                mode : "exact",
                elements : "pageContent",
                theme : "advanced",
                plugins : "spellchecker,autosave,style,advhr,advimage,advlink,preview,inlinepopups,media,paste,fullscreen,syntaxhl,wordcount",

                // Theme options
                theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,bullist,numlist,blockquote,|,justifyleft,justifycenter,justifyright,justifyfull,|,link,unlink,advhr,spellchecker,fullscreen,syntaxhl",
                theme_advanced_buttons2 : "formatselect,forecolor,|,pastetext,pasteword,cleanup,charmap,|,outdent,indent,undo,redo,|,image,iespell,media,code,preview,",
                theme_advanced_buttons3 : "",
                theme_advanced_toolbar_location : "top",
                theme_advanced_toolbar_align : "left",
                theme_advanced_resizing : true,
                theme_advanced_statusbar_location : "bottom",
                
                extended_valid_elements: "link[type|rel|href|charset],pre[name|class],iframe[src|width|height|name|align]",

                valid_children : "+body[style]",
                relative_urls: false,
                remove_script_host: false
            });
        } catch (e) {
            $("#tipMsg").text("TinyMCE load fail");
        }
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        var that = this;
        
        $.ajax({
            url: "/console/pages/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                if (!result.sc) {
                    $("#loadMsg").text(result.msg);
                    
                    return;
                }
                
                var pages = result.pages;
                var pageData = [];
                admin.pageList.pageInfo.currentCount = pages.length;
                admin.pageList.pageInfo.pageCount = result.pagination.paginationPageCount === 0 ? 1 : result.pagination.paginationPageCount;
                for (var i = 0; i < pages.length; i++) {
                    pageData[i] = {};
                    if (i === 0) {
                        if (pages.length === 1) {
                            pageData[i].pageOrder = "";
                        } else {
                            pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
                                        <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" \
                                        class="table-downIcon"></span></div>';
                        }
                    } else if (i === pages.length - 1) {
                        pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
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
                        
                that.tablePagination.updateTablePagination(pageData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /*
     * 获取自定义页面
     * @id 自定义页面 id
     */
    get: function (id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        $.ajax({
            url: "/console/page/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                if (!result.sc) {
                    return;
                }
                
                admin.pageList.id = id;
                
                try {
                    tinyMCE.get('pageContent').setContent(result.page.pageContent);
                } catch (e) {
                    $("#pageContent").val(result.page.pageContent);
                }
                
                $("#pagePermalink").val(result.page.pagePermalink);
                $("#pageTitle").val(result.page.pageTitle);
                
                $("#tipMsg").text(result.msg);
                
                $("#loadMsg").text("");
            }
        });
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

            $.ajax({
                url: "/console/page/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    var pageNum = admin.pageList.pageInfo.currentPage;
                    if (admin.pageList.pageInfo.currentCount === 1 && admin.pageList.pageInfo.pageCount !== 1 &&
                        admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount--;
                        pageNum = admin.pageList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum == hashList[hashList.length - 1]) {
                        admin.pageList.getList(pageNum);
                    } else {
                        admin.setHashByPage(pageNum);
                    }
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 添加自定义页面
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            
            var pageContent = "";
            try {
                pageContent = tinyMCE.get('pageContent').getContent();
            } catch (e) {
                pageContent = $("#pageContent").val();
            }
            
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pageContent": pageContent,
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            
            $.ajax({
                url: "/console/page/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    admin.pageList.id = "";
                    $("#pagePermalink").val("");
                    $("#pageTitle").val("");
                    
                    try {
                        if (tinyMCE.get("pageContent")) {
                            tinyMCE.get('pageContent').setContent("");
                        } else {
                            $("#pageContent").val("");
                        }
                    } catch (e) {
                        $("#pageContent").val("");
                    }
                   
                    if (admin.pageList.pageInfo.currentCount === Label.PAGE_SIZE &&
                        admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.pageList.pageInfo.pageCount == hashList[hashList.length - 1]) {
                        admin.pageList.getList(admin.pageList.pageInfo.pageCount);
                    } else {
                        admin.setHashByPage(admin.pageList.pageInfo.pageCount);
                    }
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 跟新自定义页面
     */
    update: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            
            var pageContent = "";
            try {
                pageContent = tinyMCE.get('pageContent').getContent();
            } catch (e) {
                pageContent = $("#pageContent").val();
            }
            
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": this.id,
                    "pageContent": pageContent,
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            
            $.ajax({
                url: "/console/page/",
                type: "PUT",
            cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    admin.pageList.getList(admin.pageList.pageInfo.currentPage);
                    admin.pageList.id = "";
                    $("#pageTitle").val("");
                    try {
                        tinyMCE.get('pageContent').setContent("");
                    } catch (e) {
                        $("#pageContent").val("");
                    }
                    
                    $("#pagePermalink").val("");
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 验证字段
     */
    validate: function () {
        var pageContent = "";
        try {
            pageContent = tinyMCE.get('pageContent').getContent();
        } catch (e) {
            pageContent = $("#pageContent").val();
        }
        
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#pageTitle").focus();
        } else if (pageContent.replace(/\s/g, "") === "") {
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
        
        var requestJSONObject = {
            "oId": id.toString(),
            "direction": status
        };
        
        $.ajax({
            url: "/console/page/order/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                
                // Refershes the page list
                admin.pageList.getList(admin.pageList.pageInfo.currentPage);
                
                $("#loadMsg").text("");
            }
        });
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