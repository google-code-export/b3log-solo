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
            <li class="form">
                <span class="label">${chooseBlogType1Label}</span>
                <select id="blogType" onchange="changeBlogType();">
                    <option>&nbsp;</option>
                    <option>CSDN</option>
                    <option>BlogJava</option>
                    <option>CnBlogs</option>
                </select>
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
                        <th width="96px">
                            ${userName1Label}
                        </th>
                        <td>
                            <input id="userName"/>
                        </td>
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
        blogType = $("#blogType").val();
        jsonRpc.blogSyncService['getBlogSyncMgmtFor' + blogType + 'Blog'](function (result, error) {
            if (null === result) {
                return;
            }
            $("#magName").val(result.blogSyncExternalBloggingSysUserName);
            $("#magPassword").val(result.blogSyncExternalBloggingSysUserPassword);
            result.blogSyncMgmtAddEnabled ? $("#addSync").attr("checked", "checked") : $("#addSync").removeAttr("checked");
            result.blogSyncMgmtUpdateEnabled ? $("#updateSync").attr("checked", "checked") : $("#updateSync").removeAttr("checked");
            result.blogSyncMgmtRemoveEnabled ? $("#deleteSync").attr("checked", "checked") : $("#deleteSync").removeAttr("checked");
        });
    }

    var syncSetting = function () {
        var addSync = $("#addSync").attr("checked"),
        updateSync = $("#updateSync").attr("checked"),
        deleteSync =  $("#deleteSync").attr("checked");
        var requestJSONObject = {
            "blogSyncExternalBloggingSys": bolgType,
            "blogSyncExternalBloggingSysUserName": $("#magName").val(),
            "blogSyncExternalBloggingSysUserPassword": $("#magPassword").val(),
            "blogSyncMgmtAddEnabled": addSync,
            "blogSyncMgmtUpdateEnabled": updateSync,
            "blogSyncMgmtRemoveEnabled": deleteSync
        };

        var result =
            jsonRpc.blogSyncService.setBlogSyncMgmtFor(requestJSONObject);
       
        if (result.sc === "SET_BLOG_SYNC_MGMT_SUCC") {
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

    var getBlogArticleArchiveDate = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        $("#archiveDatePanel").hide(500);
        userName = $("#userName").val();
        password = $("#password").val();
        var archveDates = "";
        var result = jsonRpc.blogSyncService['get' + blogType + 'BlogArticleArchiveDate']({
            "blogSyncExternalBloggingSysUserName": userName,
            "blogSyncExternalBloggingSysUserPassword": password
        });
        if (result['blogSync' + blogType + 'BlogArchiveDates'].length === 0) {
            $("#tipMsg").text("${syncImportErrorLabel}").show();
        } else {
            for (var i = 0; i < result['blogSync' + blogType + 'BlogArchiveDates'].length; i++) {
                archveDates += "<option>" + result['blogSync' + blogType + 'BlogArchiveDates'][i] + "</option>";
            }
            $("#archiveDate").html(archveDates);
            $("#archiveDatePanel").show(500);
            $("#tipMsg").text("").hide();
        }
    }

    var getBlogArticlesByArchiveDate = function () {
        $("#tipMsg").html("${loadingLabel}").show();
        $("#articlesPanel").show();

        var requestJSONObject = {
            "blogSyncExternalBloggingSysUserName": userName,
            "blogSyncExternalBloggingSysUserPassword": password,
            "blogSyncBlogArchiveDate": $("#archiveDate").val()
        };

        while (true) {
            var result =
                jsonRpc.blogSyncService['get' + blogType + 'BlogArticlesByArchiveDate'](requestJSONObject);
            var articles = result['blogSync' + blogType + 'BlogArticles'];
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
                    articleData[i].title = articles[i]['blogSync' + blogType + 'BlogArticleTitle'];
                    articleData[i].date = $.bowknot.getDate(articles[i]['blogSync' + blogType+ 'BlogArticleCreateDate'].time);
                    articleData[i].tags = articles[i]['blogSync' + blogType + 'BlogArticleCategories'];
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
            jsonRpc.blogSyncService['import' + blogType + 'BlogArticles']({
                "oIds": $("#articleList_selected").data("id")
            });
            $("#tipMsg").text("${importSuccLabel}").show();
        }
    }
</script>