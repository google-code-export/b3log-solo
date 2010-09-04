<div>
    <div class="tabs">
        <ul>
            <li>
                <span class="selected" id="sync" onclick="changeTab(this);">
                    ${blogArticleImportLabel}
                </span>
            </li>
            <li>
                <span id="syncSetting" onclick="changeTab(this);">
                    ${blogSyncMgmtLabel}
                </span>
            </li>
        </ul>
        <div class="clear"></div>
        <div class="form magin12">
            <span class="label">${chooseBlogType1Label}</span>
            <select id="blogType" onchange="changeBlogType();">
                <option value="">&nbsp;</option>
                <option value="blogSyncCSDNBlog">${CSDNBlogLabel}</option>
                <option value="blogSyncBlogJava">${BlogJavaLabel}</option>
                <option value="blogSyncCnBlogs">${CnBlogsLabel}</option>
            </select>
        </div>
    </div>
    <div class="tabPanels">
        <div id="syncPanel">
            <table id="archiveDatePanel" class="form left none" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th>
                            ${selectDate1Label}
                        </th>
                        <td>
                            <select id="archiveDate">
                                <option>${selectDateLabel}</option>
                            </select>
                        </td>
                        <td>
                            <button onclick="getBlogArticlesByArchiveDate();">
                                ${getArticleLabel}
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table class="form left" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th>
                            ${userName1Label}
                        </th>
                        <td>
                            <input id="userName"/>
                        </td>
                        <th width="66px">
                            ${userPassword1Label}
                        </th>
                        <td>
                            <input id="password" type="password"/>
                        </td>
                        <td>
                            <button onclick="getBlogArticleArchiveDate();">${getDateLabel}</button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="clear"></div>
            <div id="articlesPanel" class="none">
                <button onclick="sync();" class="left">${importLabel}</button>
                <div id="articlesCount" class="right error-msg maginTR12">
                </div>
                <div class="clear"></div>
                <div id="articleList" class="maginTB12"></div>
                <button onclick="sync();">${importLabel}</button>
            </div>
        </div>
        <div id="syncSettingPanel" class="none">
            <fieldset>
                <legend>
                    ${syncMgmtLabel}
                </legend>
                <table class="form" cellpadding="12px" cellspacing="12px;">
                    <tbody>
                        <tr>
                            <th width="58px">
                                ${userName1Label}
                            </th>
                            <td colspan="5">
                                <input id="magName"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                ${userPassword1Label}
                            </th>
                            <td colspan="5">
                                <input type="password" id="magPassword"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <input type="checkbox" id="addSync" class="normalInput"/>
                            </th>
                            <td>
                                ${syncPostLabel}
                            </td>
                            <th>
                                <input type="checkbox" id="updateSync"
                            </th>
                            <td>
                                ${syncUpdateLabel}
                            </td>
                            <th>
                                <input type="checkbox" id="deleteSync"/>
                            </th>
                            <td>
                                ${syncRemoveLabel}
                            </td>
                        </tr>
                        <tr>
                            <th colspan="6">
                                <button onclick="syncSetting();">${updateLabel}</button>
                            </th>
                        </tr>
                    </tbody>
                </table>
            </fieldset>
        </div>
    </div>
</div>
<script type="text/javascript">
    var userName = "",
    password = "",
    blogType = "";
    var initSync = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        // Blog table
        $("#articleList").table({
            height: 357,
            colModel: [{
                    name: "选择",
                    index: "selected",
                    width: 46,
                    inputType: "checkbox",
                    textAlign: "center",
                    allSelected: true
                }, {
                    name: "${titleLabel}",
                    index: "title",
                    minWidth: 180,
                    style: "padding-left: 6px;"
                },  {
                    name: "${categoryLabel}",
                    index: "tags",
                    width: 160
                }, {
                    textAlign: "center",
                    name: "${createDateLabel}",
                    index: "date",
                    width: 100
                }, {
                    textAlign: "center",
                    name: "${importedLabel}",
                    index: "imported",
                    width: 60
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        // enter
        $("#password").keypress(function (event) {
            if (event.keyCode === 13) {
                getBlogArticleArchiveDate();
            }
        });

        $("#tipMsg").text("").hide();
    }

    initSync();

    var changeBlogType = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        blogType = $("#blogType").val();
        jsonRpc.blogSyncService.getBlogSyncMgmt(function (result, error) {
            if (result) {
                $("#magName").val(result.blogSyncExternalBloggingSysUserName);
                $("#magPassword").val(result.blogSyncExternalBloggingSysUserPassword);
                $("#userName").val(result.blogSyncExternalBloggingSysUserName);
                $("#password").val(result.blogSyncExternalBloggingSysUserPassword);
                result.blogSyncMgmtAddEnabled ? $("#addSync").attr("checked", "checked") : $("#addSync").removeAttr("checked");
                result.blogSyncMgmtUpdateEnabled ? $("#updateSync").attr("checked", "checked") : $("#updateSync").removeAttr("checked");
                result.blogSyncMgmtRemoveEnabled ? $("#deleteSync").attr("checked", "checked") : $("#deleteSync").removeAttr("checked");
                $("#archiveDatePanel").hide();
                $("#articlesPanel").hide();
            } else {
                $("#magName").val("");
                $("#magPassword").val("");
                $("#userName").val("");
                $("#password").val("");
            }
            $("#tipMsg").text("").hide();
        }, {
            "blogSyncExternalBloggingSys": blogType
        });
    }

    var validateSyncSetting = function () {
        if ($("#magName").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${nameEmptyLabel}").show();
            $("#magName").focus().val("");
        } else if ($("#magPassword").val() === ""){
            $("#tipMsg").text("${passwordEmptyLabel}").show();
            $("#magPassword").focus().val();
        } else if (blogType === "") {
            $("#tipMsg").text("${blogTypeEmptyLabel}").show();
            $("#blogType").focus();
        } else {
            return true;
        }
        return false;
    }
    
    var syncSetting = function () {
        if (validateSyncSetting()) {
            var addSync = $("#addSync").attr("checked"),
            updateSync = $("#updateSync").attr("checked"),
            deleteSync =  $("#deleteSync").attr("checked");
            var requestJSONObject = {
                "blogSyncExternalBloggingSys": $("#blogType").val(),
                "blogSyncExternalBloggingSysUserName": $("#magName").val(),
                "blogSyncExternalBloggingSysUserPassword": $("#magPassword").val(),
                "blogSyncMgmtAddEnabled": addSync,
                "blogSyncMgmtUpdateEnabled": updateSync,
                "blogSyncMgmtRemoveEnabled": deleteSync
            };

            var result =
                jsonRpc.blogSyncService.setBlogSyncMgmt(requestJSONObject);

            if (result.sc === "SET_BLOG_SYNC_MGMT_SUCC") {
                $("#tipMsg").html("${updateSuccLabel}").show();
            } else {
                $("#tipMsg").html("${setFailLabel}").show();
            }
        }
    }

    var changeTab = function (it) {
        var tabs = ['sync', 'syncSetting'];
        for (var i = 0; i < tabs.length; i++) {
            if (it.id === tabs[i]) {
                $("#" + tabs[i] + "Panel").show();
                $("#" + tabs[i]).addClass("selected");
            } else {
                $("#" + tabs[i] + "Panel").hide();
                $("#" + tabs[i]).removeClass("selected");
            }
        }
    }

    var validateSync = function () {
        if ($("#userName").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${nameEmptyLabel}").show();
            $("#userName").focus().val("");
        } else if ($("#password").val() === ""){
            $("#tipMsg").text("${passwordEmptyLabel}").show();
            $("#password").focus().val();
        } else if (blogType === "") {
            $("#tipMsg").text("${blogTypeEmptyLabel}").show();
            $("#blogType").focus();
        } else {
            return true;
        }
        return false;
    }

    var getBlogArticleArchiveDate = function () {
        if (validateSync()) {
            $("#tipMsg").text("${loadingLabel}").show();
            $("#archiveDatePanel").hide(500);
            userName = $("#userName").val();
            password = $("#password").val();
            var archveDates = "";
            var result = jsonRpc.blogSyncService.getExternalArticleArchiveDate({
                "blogSyncExternalBloggingSysUserName": userName,
                "blogSyncExternalBloggingSysUserPassword": password,
                "blogSyncExternalBloggingSys": blogType
            });
            if (result.blogSyncExternalArchiveDates.length === 0) {
                $("#tipMsg").text("${syncImportErrorLabel}").show();
            } else {
                for (var i = 0; i < result.blogSyncExternalArchiveDates.length; i++) {
                    archveDates += "<option>" + result.blogSyncExternalArchiveDates[i] + "</option>";
                }
                $("#archiveDate").html(archveDates);
                $("#archiveDatePanel").show(500);
                $("#tipMsg").text("").hide();
            }
        }
    }

    var getBlogArticlesByArchiveDate = function () {
        $("#tipMsg").html("${loadingLabel}").show();
        $("#articlesPanel").show();
        $("#articleList").table({
            update:{
                data: articleData
            }
        });

        var requestJSONObject = {
            "blogSyncExternalBloggingSys": blogType,
            "blogSyncExternalBloggingSysUserName": userName,
            "blogSyncExternalBloggingSysUserPassword": password,
            "blogSyncExternalArchiveDate": $("#archiveDate").val()
        };

        while (true) {
            var result =
                jsonRpc.blogSyncService.getExternalArticlesByArchiveDate(requestJSONObject);
            var articles = result.blogSyncExternalArticles;
            if (articles.length === $("#articleListTableMain tr").length) {
                break;
            }
            if (articles.length > 0) {
                var articleData = [];
                for (var i = 0; i < articles.length; i++) {
                    articleData[i] = {};
                    articleData[i].selected = {
                        value: false,
                        disabled: articles[i].blogSyncImported
                    };
                    articleData[i].title = articles[i].blogSyncExternalArticleTitle;
                    articleData[i].date = $.bowknot.getDate(articles[i].blogSyncExternalArticleCreateDate.time);
                    articleData[i].tags = articles[i].blogSyncExternalArticleCategories;
                    articleData[i].id = articles[i].oId;
                    articleData[i].imported = articles[i].blogSyncImported;
                }
                
                $("#articleList").table({
                    update:{
                        data: articleData
                    }
                });
                
                $("#articlesCount").html("${sumLabel} " + articleData.length + " ${countLabel}");
                $("#tipMsg").text("${loadingLabel}");
            } else {
                $("#tipMsg").text("${getFailLabel}");
            }
        }
        $("#tipMsg").hide();
    }

    var sync = function () {
        if ($("#articleList_selected").data("id").length === 0) {
            $("#tipMsg").text("${blogArticleEmptyLabel}").show();
        } else {
            $("#tipMsg").text("${loadingLabel}").show();
            jsonRpc.blogSyncService.importExternalArticles({
                "oIds": $("#articleList_selected").data("id")
            });
            $("#tipMsg").text("${importSuccLabel}").show();
        }
    }
</script>