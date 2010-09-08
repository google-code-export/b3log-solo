<div class="tabPanel">
    <div class="tabs">
        <span class="selected" id="sync" onclick="changeTab(this);">
            ${blogArticleImportLabel}
        </span>
        <span id="syncSetting" onclick="changeTab(this);">
            ${blogSyncMgmtLabel}
        </span>
        <div class="clear"></div>
        <div id="syncBlogType">
            <span class="label">${chooseBlogType1Label}</span>
            <select id="blogType" onchange="changeBlogType();">
                <option value="">&nbsp;</option>
                <option value="blogSyncCSDNBlog">${CSDNBlogLabel}</option>
                <option value="blogSyncBlogJava">${BlogJavaLabel}</option>
                <option value="blogSyncCnBlogs">${CnBlogsLabel}</option>
            </select>
        </div>
    </div>
    <div class="tabMain">
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
                <div id="articlesCount" class="right red marginTop12 marginRight12">
                </div>
                <div class="clear"></div>
                <div id="articleList" class="paddingTop12 paddingBottom12"></div>
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
                                <label for="addSync">
                                    ${syncPostLabel}
                                </label>
                            </td>
                            <th>
                                <input type="checkbox" id="updateSync"
                            </th>
                            <td>
                                <label for="updateSync">
                                    ${syncUpdateLabel}
                                </label>
                            </td>
                            <th>
                                <input type="checkbox" id="deleteSync"/>
                            </th>
                            <td>
                                <label for="deleteSync">
                                    ${syncRemoveLabel}
                                </label>
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
        $("#tipMsg").text("${loadingLabel}");
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
                    width: 60,
                    style: "margin-left:22px;"
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
        $("#tipMsg").text("");
    }
    initSync();

    var changeBlogType = function () {
        $("#tipMsg").text("${loadingLabel}");
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
                $("#tipMsg").text("${getSuccLabel}");
            } else {
                $("#magName").val("");
                $("#magPassword").val("");
                $("#userName").val("");
                $("#password").val("");
                $("#tipMsg").text("${noSettingLabel}");
            }
            $("#archiveDatePanel").hide();
            $("#articlesPanel").hide();
        }, {
            "blogSyncExternalBloggingSys": blogType
        });
    }

    var validateSyncSetting = function () {
        if ("" === blogType) {
            $("#tipMsg").text("${blogTypeEmptyLabel}");
            $("#blogType").focus();
        } else if ("" === $("#magName").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${nameEmptyLabel}");
            $("#magName").focus().val("");
        } else if ("" === $("#magPassword").val()){
            $("#tipMsg").text("${passwordEmptyLabel}");
            $("#magPassword").focus().val();
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
                $("#tipMsg").html("${updateSuccLabel}");
            } else {
                $("#tipMsg").html("${setFailLabel}");
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
        if ("" === blogType) {
            $("#tipMsg").text("${blogTypeEmptyLabel}");
            $("#blogType").focus();
        } else if ("" === $("#userName").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${nameEmptyLabel}");
            $("#userName").focus().val("");
        } else if ("" === $("#password").val()){
            $("#tipMsg").text("${passwordEmptyLabel}");
            $("#password").focus().val();
        } else {
            return true;
        }
        return false;
    }

    var getBlogArticleArchiveDate = function () {
        if (validateSync()) {
            $("#tipMsg").text("${loadingLabel}");
            $("#archiveDatePanel").hide();
            userName = $("#userName").val();
            password = $("#password").val();
            jsonRpc.blogSyncService.getExternalArticleArchiveDate(function (result, error) {
                var archveDates = "";
                if (result.blogSyncExternalArchiveDates.length === 0) {
                    $("#tipMsg").text("${syncImportErrorLabel}");
                } else {
                    for (var i = 0; i < result.blogSyncExternalArchiveDates.length; i++) {
                        archveDates += "<option>" + result.blogSyncExternalArchiveDates[i] + "</option>";
                    }
                    $("#archiveDate").html(archveDates);
                    $("#archiveDatePanel").show();
                    $("#articlesPanel").hide();
                    $("#tipMsg").text("${getSuccLabel}");
                }
            }, {
                "blogSyncExternalBloggingSysUserName": userName,
                "blogSyncExternalBloggingSysUserPassword": password,
                "blogSyncExternalBloggingSys": blogType
            });
        }
    }

    var getBlogArticlesByArchiveDate = function () {
        $("#articlesPanel").show();
        $("#tipMsg").html("${loadingLabel}");
        $("#articleList").table({
            update:{
                data: []
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
                    if (articles[i].blogSyncImported) {
                        articleData[i].imported = "<div class='falseIcon'></div>";
                    } else {
                        articleData[i].imported = "<div class='trueIcon'></div>";
                    }
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
        $("#tipMsg").text("${getSuccLabel}");
    }

    var sync = function () {
        if ($("#articleList_selected").data("id").length === 0) {
            $("#tipMsg").text("${blogArticleEmptyLabel}");
        } else {
            $("#tipMsg").text("${loadingLabel}");
            jsonRpc.blogSyncService.importExternalArticles({
                "oIds": $("#articleList_selected").data("id")
            });
            $("#tipMsg").text("${importSuccLabel}");
        }
    }
</script>