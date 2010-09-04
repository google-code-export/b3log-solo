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
                            <button onclick="getCSDNBlogArticlesByArchiveDate();">
                                ${getArticleLabel}
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table class="form left" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th width="96px">
                            ${csdnUserName1Label}
                        </th>
                        <td>
                            <input id="userName"/>
                        </td>
                        <td>
                            <input id="password" type="password"/>
                        </td>
                        <td>
                            <button onclick="getCSDNBlogArticleArchiveDate();">${getDateLabel}</button>
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
                    ${syncMgmtCSDNLabel}
                </legend>
                <table class="form" cellpadding="12px" cellspacing="12px;">
                    <tbody>
                        <tr>
                            <th width="58px">
                                ${userName1Label}
                            </th>
                            <td colspan="5">
                                <input id="nameCSDN"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                ${userPassword1Label}
                            </th>
                            <td colspan="5">
                                <input type="password" id="passwordCSDN"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <input type="checkbox" id="addSyncCSDN" class="normalInput"/>
                            </th>
                            <td>
                                ${syncPostLabel}
                            </td>
                            <th>
                                <input type="checkbox" id="updateSyncCSDN"
                            </th>
                            <td>
                                ${syncUpdateLabel}
                            </td>
                            <th>
                                <input type="checkbox" id="deleteSyncCSDN"/>
                            </th>
                            <td>
                                ${syncRemoveLabel}
                            </td>
                        </tr>
                        <tr>
                            <th colspan="6">
                                <button onclick="syncSettingCSDN();">${updateLabel}</button>
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
    password = "";
    var initSync = function () {
        // CSDN Blog table
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
                    width: 50
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        // enter
        $("#password").keypress(function (event) {
            if (event.keyCode === 13) {
                getCSDNBlogArticleArchiveDate();
            }
        });

        // get sync
        $("#tipMsg").text("${loadingLabel}").show();
        jsonRpc.blogSyncService.getBlogSyncMgmtForCSDNBlog(function (result, error) {
            if (null === result) {
                return;
            }

            $("#nameCSDN").val(result.blogSyncExternalBloggingSysUserName);
            $("#passwordCSDN").val(result.blogSyncExternalBloggingSysUserPassword);
            result.blogSyncMgmtAddEnabled ? $("#addSyncCSDN").attr("checked", "checked") : $("#addSyncCSDN").removeAttr("checked");
            result.blogSyncMgmtUpdateEnabled ? $("#updateSyncCSDN").attr("checked", "checked") : $("#updateSyncCSDN").removeAttr("checked");
            result.blogSyncMgmtRemoveEnabled ? $("#deleteSyncCSDN").attr("checked", "checked") : $("#deleteSyncCSDN").removeAttr("checked");
        });
        $("#tipMsg").text("").hide();
    }

    initSync();

    var syncSettingCSDN = function () {
        var requestJSONObject = {
            "blogSyncExternalBloggingSysUserName": $("#nameCSDN").val(),
            "blogSyncExternalBloggingSysUserPassword": $("#passwordCSDN").val(),
            "blogSyncMgmtAddEnabled": $("#addSyncCSDN").attr("checked"),
            "blogSyncMgmtUpdateEnabled": $("#updateSyncCSDN").attr("checked"),
            "blogSyncMgmtRemoveEnabled": $("#deleteSyncCSDN").attr("checked")
        };

        var result =
            jsonRpc.blogSyncService.setBlogSyncMgmtForCSDNBlog(requestJSONObject);
       
        if (result.sc === "SET_BLOG_SYNC_MGMT_FOR_CSDN_BLOG_SUCC") {
            $("#tipMsg").html("${updateSuccLabel}").show();
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

    var getCSDNBlogArticleArchiveDate = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        $("#archiveDatePanel").hide(500);
        userName = $("#userName").val();
        password = $("#password").val();
        var archveDates = "";
        var result = jsonRpc.blogSyncService.getCSDNBlogArticleArchiveDate({
            "blogSyncExternalBloggingSysUserName": userName,
            "blogSyncExternalBloggingSysPassword": password
        });
        for (var i = 0; i < result.blogSyncCSDNBlogArchiveDates.length; i++) {
            archveDates += "<option>" + result.blogSyncCSDNBlogArchiveDates[i] + "</option>";
        }
        $("#archiveDate").html(archveDates);
        $("#archiveDatePanel").show(500);
        $("#tipMsg").text("").hide();
    }

    var getCSDNBlogArticlesByArchiveDate = function () {
        $("#tipMsg").html("${loadingLabel}").show();
        $("#articlesPanel").show();

        var requestJSONObject = {
            "blogSyncExternalBloggingSysUserName": userName,
            "blogSyncExternalBloggingSysPassword": password,
            "blogSyncCSDNBlogArchiveDate": $("#archiveDate").val()
        };

        while (true) {
            var result =
                jsonRpc.blogSyncService.getCSDNBlogArticlesByArchiveDate(requestJSONObject);
            var articles = result.blogSyncCSDNBlogArticles;
            if (articles.length == $("#articleListTableMain tr").length) {
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
                    articleData[i].title = articles[i].blogSyncCSDNBlogArticleTitle;
                    articleData[i].date = $.bowknot.getDate(articles[i].blogSyncCSDNBlogArticleCreateDate.time);
                    articleData[i].tags = articles[i].blogSyncCSDNBlogArticleCategories;
                    articleData[i].id = articles[i].oId;
                    articleData[i].imported = articles[i].blogSyncImported;
                }
                
                $("#articleList").table({
                    update:{
                        data: articleData
                    }
                });
                
                $("#articlesCount").html("${sumLabel} " + articleData.length + " ${countLabel}");
            } else {
                $("#tipMsg").text("${getFailLabel}").show();
            }
        }
        $("#tipMsg").text("").hide();
    }

    var sync = function () {
        if ($("#articleList_selected").data("id").length === 0) {
            $("#tipMsg").text("{choose article}").show();
        } else {
            $("#tipMsg").text("${loadingLabel}").show();
            jsonRpc.blogSyncService.importCSDNBlogArticles({
                "oIds": $("#articleList_selected").data("id")
            });
            $("#tipMsg").text("${importSuccLabel}").show();
        }
    }
</script>