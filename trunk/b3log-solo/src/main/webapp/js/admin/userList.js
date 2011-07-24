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
 * user list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.4, July 24, 2011
 */

/* user-list 相关操作 */
admin.userList = {
    tablePagination:  new TablePaginate("user"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    userInfo: {
        'oId': "",
        "userRole": ""
    },
    
    /* 
     * 初始化 table, pagination
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            style: "padding-left: 12px;",
            text: Label.commentNameLabel,
            index: "userName",
            width: 230
        }, {
            style: "padding-left: 12px;",
            text: Label.commentEmailLabel,
            index: "userEmail",
            minWidth: 180
        }, {
            style: "padding-left: 12px;",
            text: Label.administratorLabel,
            index: "isAdmin",
            width: 120
        }]);
    
        this.tablePagination.initPagination();
        this.getList(page);
        
        $("#userUpdate").dialog({
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
        jsonRpc.adminService.getUsers(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_USERS_SUCC":
                        var users = result.users;
                        var userData = [];
                        admin.userList.pageInfo.currentCount = users.length;
                        admin.userList.pageInfo.pageCount = result.pagination.paginationPageCount;
                        if (users.length < 1) {
                            alert("A error occurs, please report this issue on http://code.google.com/p/b3log-solo/issues/list");
                        }
                    
                        for (var i = 0; i < users.length; i++) {
                            userData[i] = {};
                            userData[i].userName = users[i].userName;
                            userData[i].userEmail = users[i].userEmail;
                            
                            if ("adminRole" === users[i].userRole) {
                                userData[i].isAdmin = "&nbsp;" + Label.administratorLabel;
                                userData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.userList.get('" + 
                                users[i].oId + "', '" + users[i].userRole + "')\">" + Label.updateLabel + "</a>";
                            } else {
                                userData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.userList.get('" + 
                                users[i].oId + "', '" + users[i].userRole + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.userList.del('" + users[i].oId + "')\">" + Label.removeLabel + "</a>";
                                userData[i].isAdmin = Label.commonUserLabel;
                            }
                            
                        }
                        
                        that.tablePagination.updateTablePagination(userData, pageNum, result.pagination);
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
                "userName": $("#userName").val(),
                "userEmail": $("#userEmail").val()
            };
            jsonRpc.adminService.addUser(function (result, error) {
                try {
                    switch (result.sc) {
                        case "ADD_USER_SUCC":
                            $("#userName").val("");
                            $("#userEmail").val("");
                            if (admin.userList.pageInfo.currentCount === Label.PAGE_SIZE &&
                                admin.userList.pageInfo.currentPage === admin.userList.pageInfo.pageCount) {
                                admin.userList.pageInfo.pageCount++;
                            }
                            admin.userList.getList(admin.userList.pageInfo.pageCount);
                            $("#tipMsg").text(Label.addSuccLabel);
                            break;
                        case "ADD_USER_FAIL_DUPLICATED_EMAIL":
                            $("#tipMsg").text(Label.duplicatedEmailLabel);
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
     * 获取链接
     * @id 链接 id
     */
    get: function (id, userRole) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#userUpdate").dialog("open");
        var requestJSONObject = {
            "oId": id
        };

        jsonRpc.adminService.getUser(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_USER_SUCC":
                        var $userEmailUpdate = $("#userEmailUpdate");
                        $("#userNameUpdate").val(result.user.userName).data("userInfo", {
                            'oId': id,
                            "userRole": userRole
                        });
                        $userEmailUpdate.val(result.user.userEmail);
                        if ("adminRole" === userRole) {
                            $userEmailUpdate.attr("disabled", "disabled");
                        } else {
                            $userEmailUpdate.removeAttr("disabled");
                        }
                        break;
                    case "GET_USER_FAIL_":
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
            var userInfo = $("#userNameUpdate").data("userInfo");
            var requestJSONObject = {
                "userName": $("#userNameUpdate").val(),
                "oId": userInfo.oId,
                "userEmail": $("#userEmailUpdate").val(),
                "userRole": userInfo.userRole
            };
            jsonRpc.adminService.updateUser(function (result, error) {
                try {
                    switch (result.sc) {
                        case "UPDATE_USER_SUCC":
                            admin.userList.getList(admin.userList.pageInfo.currentPage);
                            $("#tipMsg").text(Label.updateSuccLabel);
                            $("#userUpdate").dialog("close");
                            break;
                        case "UPDATE_USER_FAIL_DUPLICATED_EMAIL":
                            $("#tipMsg").text(Label.duplicatedEmailLabel);
                            break;
                        case "UPDATE_USER_FAIL_":
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

            jsonRpc.adminService.removeUser(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_USER_SUCC":
                            var pageNum = admin.userList.pageInfo.currentPage;
                            if (admin.userList.pageInfo.currentCount === 1 && admin.userList.pageInfo.pageCount !== 1 &&
                                admin.userList.pageInfo.currentPage === admin.userList.pageInfo.pageCount) {
                                admin.userList.pageInfo.pageCount--;
                                pageNum = admin.userList.pageInfo.pageCount;
                            }
                            admin.userList.getList(pageNum);
                            $("#tipMsg").text(Label.removeSuccLabel);
                            break;
                        case "REMOVE_USER_FAIL_SKIN_NEED_MUL_USERS":
                            $("#tipMsg").text(Label.removeUserFailSkinNeedMulUsersLabel);
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
     * 验证字段
     * @status 更新或者添加时进行验证
     */
    validate: function (status) {
        if (!status) {
            status = "";
        }

        if ($("#userName" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.nameEmptyLabel);
            $("#userName" + status).focus();
        }else if ($("#userEmail" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.mailCannotEmptyLabel);
            $("#userEmail" + status).focus();
        } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#userEmail" + status).val())) {
            $("#tipMsg").text(Label.mailInvalidLabel);
            $("#userEmail" + status).focus();
        } else {
            return true;
        }
        return false;
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["user-list"] =  {
    "obj": admin.userList,
    "init": admin.userList.init,
    "refresh": admin.userList.getList
}