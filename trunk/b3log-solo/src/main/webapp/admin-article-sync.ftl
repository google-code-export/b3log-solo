<div class="tabPanel">
    <div class="tabs">
        <span id="syncSetting" class="selected" onclick="changeSyncTab(this);">
            ${blogSyncMgmtLabel}
        </span>
        <span id="sync" onclick="changeSyncTab(this);">
            ${blogArticleImportLabel}
        </span>
        <div class="clear"></div>
        <div id="syncBlogType">
            <span class="label">${chooseBlog1Label}</span>
            <select id="blogType" onchange="changeBlogType();">
                <option value="">&nbsp;</option>
                <option value="blogSyncCSDNBlog">${CSDNBlogLabel}</option>
                <option value="blogSyncBlogJava">${BlogJavaLabel}</option>
                <option value="blogSyncCnBlogs">${CnBlogsLabel}</option>
            </select>
            <span class="error-msg" id="blogSyncTip">
                ${blogEmptyLabel}
            </span>
        </div>
    </div>
    <div class="tabMain">
        <div id="syncSettingPanel">
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
        <div id="syncPanel" class="none">
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
            <button id="getDateButton" class="left none" onclick="getBlogArticleArchiveDate();">${getDateLabel}</button>
            <div class="clear"></div>
            <div id="articlesPanel" class="none">
                <button onclick="sync();" class="left">${importLabel}</button>
                <div id="articlesCount" class="right red marginTop12 marginRight12">
                </div>
                <div class="clear"></div>
                <div id="articleSyncList" class="paddingTop12 paddingBottom12"></div>
                <button onclick="sync();">${importLabel}</button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var articleSyncDataTemp = [];
    var initSync = function () {
        // Blog table
        $("#articleSyncList").table({
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
                    width: 460,
                    style: "padding-left: 6px;"
                },  {
                    name: "${categoryLabel}",
                    index: "tags",
                    minWidth: 380
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
        $("#loadMsg").text("");
    }
    initSync();

    var changeBlogType = function () {
        if ("" !== $("#blogType").val()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            jsonRpc.blogSyncService.getBlogSyncMgmt(function (result, error) {
                if (result) {
                    $("#magName").val(result.blogSyncExternalBloggingSysUserName);
                    $("#magPassword").val(result.blogSyncExternalBloggingSysUserPassword);
                    result.blogSyncMgmtAddEnabled ? $("#addSync").attr("checked", "checked") : $("#addSync").removeAttr("checked");
                    result.blogSyncMgmtUpdateEnabled ? $("#updateSync").attr("checked", "checked") : $("#updateSync").removeAttr("checked");
                    result.blogSyncMgmtRemoveEnabled ? $("#deleteSync").attr("checked", "checked") : $("#deleteSync").removeAttr("checked");
                    $("#tipMsg").text("${getSuccLabel}");
                    $("#blogSyncTip").text("");
                    $("#getDateButton").show();
                } else {
                    $("#magName").val("");
                    $("#magPassword").val("");
                    $("#addSync").removeAttr("checked");
                    $("#updateSync").removeAttr("checked");
                    $("#deleteSync").removeAttr("checked");
                    $("#tipMsg").text("${noSettingLabel}");
                    $("#blogSyncTip").text("");
                    $("#getDateButton").hide();
                }
                $("#archiveDatePanel").hide();
                $("#articlesPanel").hide();
                $("#loadMsg").text("");
            }, {
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        } else {
            $("#tipMsg").text("${blogEmptyLabel}");
            $("#blogSyncTip").text("${blogEmptyLabel}");
            $("#getDateButton").hide();
        }
    }

    var validateSyncSetting = function () {
        if ("" === $("#blogType").val()) {
            $("#tipMsg").text("${blogEmptyLabel}");
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
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
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

            jsonRpc.blogSyncService.setBlogSyncMgmt(function (result, error) {
                if (result.sc === "SET_BLOG_SYNC_MGMT_SUCC") {
                    $("#tipMsg").html("${updateSuccLabel}");
                    $("#getDateButton").show();
                } else {
                    $("#tipMsg").html("${setFailLabel}");
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var changeSyncTab = function (it) {
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

    var getBlogArticleArchiveDate = function () {
        if (validateSyncSetting()) {
            $("#tipMsg").text("");
            $("#loadMsg").text("${loadingLabel}");
            $("#archiveDatePanel").hide();
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
                    $("#getDateButton").hide();
                    $("#tipMsg").text("${getSuccLabel}");
                }
                $("#loadMsg").text("");
            }, {
                "blogSyncExternalBloggingSysUserName": $("#magName").val(),
                "blogSyncExternalBloggingSysUserPassword": $("#magPassword").val(),
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        }
    }

    var getBlogArticlesByArchiveDate = function () {
        $("#tipMsg").html("");
        $("#loadMsg").html("${loadingLabel}");
        $("#articlesPanel").show();
        $("#articlesCount").html("${sumLabel} 0 ${countLabel}");
        $("#articleSyncList").table({
            update:{
                data: []
            }
        });

        var requestJSONObject = {
            "blogSyncExternalBloggingSys": $("#blogType").val(),
            "blogSyncExternalBloggingSysUserName": $("#magName").val(),
            "blogSyncExternalBloggingSysUserPassword": $("#magPassword").val(),
            "blogSyncExternalArchiveDate": $("#archiveDate").val()
        };

        while (true) {
            var result =
                jsonRpc.blogSyncService.getExternalArticlesByArchiveDate(requestJSONObject);
            var articles = result.blogSyncExternalArticles;
            if (articles.length === $("#articleSyncListTableMain tr").length && articles.length !== 0) {
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
                
                $("#articleSyncList").table({
                    update:{
                        data: articleData
                    }
                });

                articleSyncDataTemp = articleData;
                $("#articlesCount").html("${sumLabel} " + articleData.length + " ${countLabel}");
                $("#tipMsg").text("${getSuccLabel}");
            } else {
                $("#tipMsg").text("${getFailLabel}");
                $("#loadMsg").text("");
                break;
            }
        }
    }

    var sync = function () {
        var selectedOIds = $("#articleSyncList_selected").data("id");
        if (selectedOIds.length === 0) {
            $("#tipMsg").text("${blogArticleEmptyLabel}");
        } else {
            $("#loadMsg").text("${loadingLabel}");
            jsonRpc.blogSyncService.importExternalArticles(function (result, error) {
                if (typeof(getArticleList) === "function") {
                    getArticleList(1);
                }
                var oIds = result.oIds;
                
                for (var i = 0; i < articleSyncDataTemp.length; i++) {
                    for (var j = 0; j < oIds.length; j++) {
                        if (oIds[j] === articleSyncDataTemp[i].id) {
                            articleSyncDataTemp[i].selected = {
                                value: false,
                                disabled: true
                            };
                            articleSyncDataTemp[i].imported = "<div class='falseIcon'></div>";
                        }
                    }
                }

                $("#articleSyncList").table({
                    update:{
                        data: articleSyncDataTemp
                    }
                });

                if (selectedOIds.length !== oIds.length) {
                    $("#tipMsg").text("${importFailLabel}");
                } else {
                    $("#tipMsg").text("${importSuccLabel}");
                }
                
                $("#loadMsg").text("");
            }, {
                "oIds": selectedOIds,
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        }
    }
</script>