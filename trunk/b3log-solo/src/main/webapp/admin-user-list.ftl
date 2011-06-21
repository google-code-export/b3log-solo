<div id="userList">
</div>
<div id="userPagination" class="margin12 right">
</div>
<div class="clear"></div>
<table class="form" width="100%" cellpadding="0px" cellspacing="9px">
    <thead>
        <tr>
            <th style="text-align: left" colspan="2">
                ${addUserLabel}
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <th width="48px">
                ${commentName1Label}
            </th>
            <td>
                <input id="userName" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                ${commentEmail1Label}
            </th>
            <td>
                <input id="userEmail" type="text"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <button onclick="submitUser();">${saveLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
<div id="userUpdate" class="none">
    <table class="form" width="100%" cellpadding="0px" cellspacing="9px">
        <thead>
            <tr>
                <th style="text-align: left" colspan="2">
                    ${updateUserLabel}
                </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <th width="48px">
                    ${commentName1Label}
                </th>
                <td>
                    <input id="userNameUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${commentEmail1Label}
                </th>
                <td>
                    <input id="userEmailUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                    <button onclick="updateUser();">${updateLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
<script type="text/javascript">
    var userListCurrentPage = 1,
    userListPageCount = 1,
    userListLength = 1;


    var getUserList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        userListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE
        };
        jsonRpc.adminService.getUsers(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_USERS_SUCC":
                        var users = result.users;
                        var userData = [];
                        userListLength = users.length;

                        if (1 < userListLength) {
                            // Disable article sync mgmt if exists more than one users
                            $("#article-syncTab").hide();
                        } else if (1 === userListLength) {
                            // Enable article sync mgmt if exists one user exactly
                            $("#article-syncTab").show();
                        } else {
                            alert("A error occurs, please report this issue on http://code.google.com/p/b3log-solo/issues/list");
                        }
                    
                        for (var i = 0; i < users.length; i++) {
                            userData[i] = {};
                            userData[i].userName = users[i].userName;
                            userData[i].userEmail = users[i].userEmail;
                            userData[i].update = "<div class='updateIcon'></div>";
                            userData[i].deleted = "<div class='deleteIcon'></div>";
                            if ("adminRole" === users[i].userRole) {
                                userData[i].deleted = "";
                                userData[i].isAdmin = "<div class='falseIcon'></div>";
                            } else {
                                userData[i].deleted = "<div class='deleteIcon'></div>";
                                userData[i].isAdmin = "<div class='trueIcon'></div>";
                            }
                            userData[i].userRole = users[i].userRole;
                            userData[i].id = users[i].oId;
                        }

                        $("#userList").table("update",{
                            data: [{
                                    "groupName": "all",
                                    "groupData": userData
                                }]
                        });

                        if (result.pagination.paginationPageCount === 0) {
                            userListPageCount = 1;
                        } else {
                            userListPageCount = result.pagination.paginationPageCount;
                        }

                        $("#userPagination").paginate({
                            update: {
                                currentPage: pageNum,
                                pageCount: userListPageCount
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
    
    var initUser = function () {
        $("#userList").table({
            colModel: [{
                    style: "padding-left: 6px;",
                    text: "${commentNameLabel}",
                    index: "userName",
                    width: 230
                }, {
                    style: "padding-left: 6px;",
                    text: "${commentEmailLabel}",
                    index: "userEmail",
                    minWidth: 180
                }, {
                    textAlign: "center",
                    text: "${updateLabel}",
                    index: "update",
                    width: 56,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                $("#loadMsg").text("${loadingLabel}");
                                $("#userUpdate").dialog({
                                    width: 700,
                                    height:200
                                });
                                var requestJSONObject = {
                                    "oId": data.id
                                };

                                jsonRpc.adminService.getUser(function (result, error) {
                                    try {
                                        switch (result.sc) {
                                            case "GET_USER_SUCC":
                                                var $userEmailUpdate = $("#userEmailUpdate");
                                                $("#userNameUpdate").val(result.user.userName).data("userInfo", {
                                                    'oId': data.id,
                                                    "userRole": data.userRole
                                                });
                                                $userEmailUpdate.val(result.user.userEmail);
                                                if ("adminRole" === data.userRole) {
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
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    text: "${removeLabel}",
                    index: "deleted",
                    width: 56,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                if ("adminRole" === data.userRole) {
                                    return;
                                }
                                var isDelete = confirm("${confirmRemoveLabel}");
                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
                                    var requestJSONObject = {
                                        "oId": data.id
                                    };

                                    jsonRpc.adminService.removeUser(function (result, error) {
                                        try {
                                            switch (result.sc) {
                                                case "REMOVE_USER_SUCC":
                                                    var pageNum = userListCurrentPage;
                                                    if (userListLength === 1 && userListPageCount !== 1 &&
                                                        userListCurrentPage === userListPageCount) {
                                                        userListPageCount--;
                                                        pageNum = userListPageCount;
                                                    }
                                                    getUserList(pageNum);
                                                    $("#tipMsg").text("${removeSuccLabel}");
                                                    break;
                                                case "REMOVE_USER_FAIL_SKIN_NEED_MUL_USERS":
                                                    $("#tipMsg").text("${removeUserFailSkinNeedMulUsersLabel}");
                                                    break;
                                                default:
                                                    break;
                                            }
                                            $("#loadMsg").text("");
                                        } catch (e) {}
                                    }, requestJSONObject);
                                }
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    style: "padding-left: 36px;",
                    text: "${administratorLabel}",
                    index: "isAdmin",
                    width: 89
                }]
        });

        $("#userPagination").paginate({
            bindEvent: "getUserList",
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
        getUserList(1);
    }
    initUser();
    
    var validateUser = function (status) {
        if (!status) {
            status = "";
        }

        if ($("#userName" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${nameEmptyLabel}");
            $("#userName" + status).focus();
        }else if ($("#userEmail" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${mailCannotEmptyLabel}");
            $("#userEmail" + status).focus();
        } else if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#userEmail" + status).val())) {
            $("#tipMsg").text("${mailInvalidLabel}");
            $("#userEmail" + status).focus();
        } else {
            return true;
        }
        return false;
    }
    
    var updateUser = function () {
        if (validateUser("Update")) {
            $("#loadMsg").text("${loadingLabel}");
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
                            getUserList(userListCurrentPage);
                            $("#tipMsg").text("${updateSuccLabel}");
                            $("#userUpdate").dialog("close");
                            break;
                        case "UPDATE_USER_FAIL_DUPLICATED_EMAIL":
                            $("#tipMsg").text("${duplicatedEmailLabel}");
                            break;
                        case "UPDATE_USER_FAIL_":
                            $("#tipMsg").text("${updateFailLabel}");
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    }

    var submitUser = function () {
        if (validateUser()) {
            $("#loadMsg").text("${loadingLabel}");
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
                            if (userListLength === adminUtil.PAGE_SIZE &&
                                userListCurrentPage === userListPageCount) {
                                userListPageCount++;
                            }
                            getUserList(userListPageCount);
                            $("#tipMsg").text("${addSuccLabel}");
                            break;
                        case "ADD_USER_FAIL_DUPLICATED_EMAIL":
                            $("#tipMsg").text("${duplicatedEmailLabel}");
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    }
</script>
${plugins}
