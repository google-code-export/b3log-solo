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
                <input id="userName"/>
            </td>
        </tr>
        <tr>
            <th>
                ${commentEmail1Label}
            </th>
            <td>
                <input id="userEmail"/>
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
                    <input id="userNameUpdate"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${commentEmail1Label}
                </th>
                <td>
                    <input id="userEmailUpdate"/>
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
    usersLength = 1;


    var getUserList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        userListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        jsonRpc.adminService.getUsers(function (result, error) {
            switch (result.sc) {
                case "GET_USERS_SUCC":
                    var users = result.users;
                    var userData = [];
                    usersLength = users.length;
                    for (var i = 0; i < users.length; i++) {
                        userData[i] = {};
                        userData[i].userName = users[i].userName;
                        userData[i].userEmail = users[i].userEmail;
                        userData[i].update = "<div class='updateIcon'></div>";
                        userData[i].deleted = "<div class='deleteIcon'></div>";
                        if (i === 0) {
                            userData[i].userRole = "<div class='trueIcon'></div>";
                        } else {
                            userData[i].userRole = "<div class='falseIcon'></div>";
                        }
                        userData[i].id = users[i].oId;
                    }

                    $("#userList").table("update",{
                        data: userData
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
        }, requestJSONObject);
    }
    
    var initUser = function () {
        $("#userList").table({
            colModel: [{
                    style: "padding-left: 6px;",
                    name: "${commentNameLabel}",
                    index: "userName",
                    width: 230
                }, {
                    style: "padding-left: 6px;",
                    name: "${commentEmailLabel}",
                    index: "userEmail",
                    minWidth: 180
                }, {
                    textAlign: "center",
                    name: "${updateLabel}",
                    index: "update",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                $("#loadMsg").text("${loadingLabel}");
                                $("#userUpdate").dialog({
                                    width: 700,
                                    height:200
                                });
                                var requestJSONObject = {
                                    "oId": event.data.id[0]
                                };

                                jsonRpc.adminService.getUser(function (result, error) {
                                    switch (result.sc) {
                                        case "GET_USER_SUCC":
                                            $("#userNameUpdate").val(result.user.userName).data('oId', event.data.id[0]);
                                            $("#userEmailUpdate").val(result.user.userEmail);
                                            break;
                                        case "GET_USER_FAIL_":
                                            break;
                                        default:
                                            break;
                                    }
                                    $("#loadMsg").text("");
                                }, requestJSONObject);
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    name: "${removeLabel}",
                    index: "deleted",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                var isDelete = confirm("${confirmRemoveLabel}");
                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
                                    var requestJSONObject = {
                                        "oId": event.data.id[0]
                                    };

                                    jsonRpc.adminService.removeUser(function (result, error) {
                                        switch (result.sc) {
                                            case "REMOVE_USER_SUCC":
                                                getUserList(1);
                                                $("#tipMsg").text("${removeSuccLabel}");
                                                break;
                                            case "REMOVE_USER_FAIL_":
                                                $("#tipMsg").text("${removeFailLabel}");
                                                break;
                                            default:
                                                break;
                                        }
                                        $("#loadMsg").text("");
                                    }, requestJSONObject);
                                }
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    style: "padding-left: 6px;",
                    name: "${administratorLabel}",
                    index: "userRole",
                    minWidth: 80
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#userPagination").paginate({
            bindEvent: "getUserList",
            pageCount: 1,
            windowSize: WINDOW_SIZE,
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
            var requestJSONObject = {
                "userName": $("#userNameUpdate").val(),
                "oId": $("#userNameUpdate").data("oId"),
                "userEmail": $("#userEmailUpdate").val()
            };
            jsonRpc.adminService.updateUser(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_USER_SUCC":
                        $("#updateUser").dialog("close");
                        getUserList(userListCurrentPage);
                        $("#tipMsg").text("${updateSuccLabel}");
                        break;
                    case "UPDATE_USER_FAIL_DUPLICATED_EMAIL":
                        $("#tipMsg").text("${duplicatedEmailLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
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
                switch (result.sc) {
                    case "ADD_USER_SUCC":
                        $("#userName").val("");
                        $("#userEmail").val("");
                        if (usersLength === PAGE_SIZE) {
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
            }, requestJSONObject);
        }
    }
</script>
