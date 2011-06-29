/*
 * Copyright (c) 2011, B3log Team
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
 * article sync for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.1, Jun 29, 2011
 */

/* article sync 相关操作 */
admin.articleSync = {
    articleSyncDataTemp: [],
    /*
     * 初始化
     */
    init: function () {
        // Blog table
        $("#articleSyncList").table({
            height: 357,
            colModel: [{
                text: "选择",
                index: "selected",
                width: 26,
                inputType: "checkbox",
                align: "center",
                allSelected: true
            }, {
                text: "${titleLabel}",
                index: "title",
                width: 320,
                style: "padding-left: 6px;"
            },  {
                text: "${categoryLabel}",
                index: "tags",
                minWidth: 120
            }, {
                align: "center",
                text: "${createDateLabel}",
                index: "date",
                width: 79
            }, {
                align: "center",
                text: "${importedLabel}",
                index: "imported",
                width: 61,
                style: "margin-left:22px;"
            }]
        });

        // enter
        $("#password").keypress(function (event) {
            if (event.keyCode === 13) {
                getBlogArticleArchiveDate();
            }
        });
        
        // tabs
        $("#tabsarticlesync").tabs();
        
        $("#loadMsg").text("");
    },
    
    validate: function () {
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
    },
    
    changeBlogType: function () {
        if ("" !== $("#blogType").val()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            jsonRpc.blogSyncService.getBlogSyncMgmt(function (result, error) {
                try {
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
                } catch (e) {}
            }, {
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        } else {
            $("#magName").val("");
            $("#magPassword").val("");
            $("#addSync").removeAttr("checked");
            $("#updateSync").removeAttr("checked");
            $("#deleteSync").removeAttr("checked");
            $("#tipMsg").text("${blogEmptyLabel}");
            $("#blogSyncTip").text("${blogEmptyLabel}");
            $("#getDateButton").hide();
        }
    },
    
    syncSetting: function () {
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
                try {
                    if (result.sc === "SET_BLOG_SYNC_MGMT_SUCC") {
                        $("#tipMsg").html("${updateSuccLabel}");
                        $("#getDateButton").show();
                    } else {
                        $("#tipMsg").html("${setFailLabel}");
                    }
                    $("#archiveDatePanel").hide();
                    $("#articlesPanel").hide();
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },

    getBlogArticleArchiveDate: function () {
        if (validateSyncSetting()) {
            $("#tipMsg").text("");
            $("#loadMsg").text("${loadingLabel}");
            $("#archiveDatePanel").hide();
            jsonRpc.blogSyncService.getExternalArticleArchiveDate(function (result, error) {
                try {
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
                } catch (e) {}
            }, {
                "blogSyncExternalBloggingSysUserName": $("#magName").val(),
                "blogSyncExternalBloggingSysUserPassword": $("#magPassword").val(),
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        }
    },

    getBlogArticlesByArchiveDate: function () {
        $("#tipMsg").html("");
        $("#loadMsg").html("${loadingLabel}");
        $("#articlesPanel").show();
        $("#articlesCount").html("${sumLabel} 0 ${countLabel}");

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
                $("#tipMsg").text("${getSuccLabel}");
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
                
                $("#articleSyncList").table("update",{
                    data: {
                        "groupName": "all", 
                        "groupData": articleData
                    }
                });

                articleSyncDataTemp = articleData;
                $("#articlesCount").html("${sumLabel} " + articleData.length + " ${countLabel}");
            } else {
                $("#tipMsg").text("${getFailLabel}");
                break;
            }
        }
        $("#loadMsg").html("");
    },
    
    sync: function () {
        var selectedOIds = $("#articleSyncList_selected").data("id");
        if (selectedOIds.length === 0) {
            $("#tipMsg").text("${blogArticleEmptyLabel}");
        } else {
            $("#loadMsg").text("${loadingLabel}");
            jsonRpc.blogSyncService.importExternalArticles(function (result, error) {
                try {
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

                    $("#articleSyncList").table("update",{
                        data: {
                            "groupName": "all", 
                            "groupData": articleSyncDataTemp
                        }
                    });

                    if (selectedOIds.length !== oIds.length) {
                        $("#tipMsg").text("${importFailLabel}");
                    } else {
                        $("#tipMsg").text("${importSuccLabel}");
                    }
                
                    $("#loadMsg").text("");
                } catch (e) {}
            }, {
                "oIds": selectedOIds,
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        }
    }
};