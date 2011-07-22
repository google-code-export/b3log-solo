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
 * article sync for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.2, Jun 30, 2011
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
                index: "selected",
                width: 26,
                type:"checkbox",
                allSelected: true,
                style: "padding-left: 4px;"
            }, {
                text: Label.titleLabel,
                index: "title",
                width: 320,
                style: "padding-left: 12px;"
            },  {
                text: Label.tagsLabel,
                index: "tags",
                minWidth: 120,
                style: "padding-left: 12px;"
            }, {
                align: "center",
                text: Label.createDateLabel,
                index: "date",
                width: 79,
                style: "padding-left: 12px;"
            }, {
                align: "center",
                text: Label.importedLabel,
                index: "imported",
                width: 61,
                style: "padding-left: 12px;"
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
            $("#tipMsg").text(Label.blogEmptyLabel);
            $("#blogType").focus();
        } else if ("" === $("#magName").val().replace(/\s/g, "")) {
            $("#tipMsg").text(Label.nameEmptyLabel);
            $("#magName").focus().val("");
        } else if ("" === $("#magPassword").val()){
            $("#tipMsg").text(Label.passwordEmptyLabel);
            $("#magPassword").focus().val();
        } else {
            return true;
        }
        return false;
    },
    
    changeBlogType: function () {
        if ("" !== $("#blogType").val()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            jsonRpc.blogSyncService.getBlogSyncMgmt(function (result, error) {
                try {
                    if (result) {
                        $("#magName").val(result.blogSyncExternalBloggingSysUserName);
                        $("#magPassword").val(result.blogSyncExternalBloggingSysUserPassword);
                        $("#addSync").prop("checked", result.blogSyncMgmtAddEnabled) 
                        $("#updateSync").prop("checked", result.blogSyncMgmtUpdateEnabled);
                        $("#deleteSync").prop("checked", result.blogSyncMgmtRemoveEnabled);
                        $("#tipMsg").text(Label.getSuccLabel);
                        $("#getDateButton").show();
                    } else {
                        $("#magName").val("");
                        $("#magPassword").val("");
                        $("#addSync").prop("checked", false);
                        $("#updateSync").prop("checked", false);
                        $("#deleteSync").prop("checked", false);
                        $("#tipMsg").text(Label.noSettingLabel);
                        $("#getDateButton").hide();
                    }
                    $("#archiveDatePanel").hide();
                    $("#articlesPanel").hide();
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, {
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        } else {
            $("#magName").val("");
            $("#magPassword").val("");
            $("#addSync").prop("checked", false);
            $("#updateSync").prop("checked", false);
            $("#deleteSync").prop("checked", false);
            $("#tipMsg").text(Label.blogEmptyLabel);
            $("#getDateButton").hide();
        }
    },
    
    syncSetting: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var addSync = $("#addSync").prop("checked"),
            updateSync = $("#updateSync").prop("checked"),
            deleteSync =  $("#deleteSync").prop("checked");
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
                        $("#tipMsg").html(Label.updateSuccLabel);
                        $("#getDateButton").show();
                    } else {
                        $("#tipMsg").html(Label.setFailLabel);
                    }
                    $("#archiveDatePanel").hide();
                    $("#articlesPanel").hide();
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    },

    getDate: function () {
        if (this.validate()) {
            $("#tipMsg").text("");
            $("#loadMsg").text(Label.loadingLabel);
            $("#archiveDatePanel").hide();
            jsonRpc.blogSyncService.getExternalArticleArchiveDate(function (result, error) {
                try {
                    var archveDates = "";
                    if (result.blogSyncExternalArchiveDates.length === 0) {
                        $("#tipMsg").text(Label.syncImportErrorLabel);
                    } else {
                        for (var i = 0; i < result.blogSyncExternalArchiveDates.length; i++) {
                            archveDates += "<option>" + result.blogSyncExternalArchiveDates[i] + "</option>";
                        }
                        $("#archiveDate").html(archveDates);
                        $("#archiveDatePanel").show();
                        $("#articlesPanel").hide();
                        $("#getDateButton").hide();
                        $("#tipMsg").text(Label.getSuccLabel);
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

    getList: function () {
        $("#tipMsg").html("");
        $("#loadMsg").html(Label.loadingLabel);
        $("#articlesPanel").show();
        $("#articlesCount").html(Label.sumLabel + " 0 " + Label.countLabel);

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
                $("#tipMsg").text(Label.getSuccLabel);
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
                    data: [{
                        "groupName": "all", 
                        "groupData": articleData
                    }]
                });

                articleSyncDataTemp = articleData;
                $("#articlesCount").html(Label.sumLabel + " " + articleData.length + " " + Label.countLabel);
            } else {
                $("#tipMsg").text(Label.getFailLabel);
                break;
            }
        }
        $("#loadMsg").html("");
    },
    
    sync: function () {
        var selectedList = $("#articleSyncList").table("getRows"),
        oIds = [];
        for (var i =  0; i < selectedList.length; i++) {
            oIds.push(selectedList[i].id);
        }
        if (selectedList.length === 0) {
            $("#tipMsg").text(Label.blogArticleEmptyLabel);
        } else {
            $("#loadMsg").text(Label.loadingLabel);
            jsonRpc.blogSyncService.importExternalArticles(function (result, error) {
                try {
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
                        data: [{
                            "groupName": "all", 
                            "groupData": articleSyncDataTemp
                        }]
                    });

                    if (selectedList.length !== oIds.length) {
                        $("#tipMsg").text(Label.importFailLabel);
                    } else {
                        $("#tipMsg").text(Label.importSuccLabel);
                    }
                
                    $("#loadMsg").text("");
                } catch (e) {
                    console.error(e);
                }
            }, {
                "oIds": oIds,
                "blogSyncExternalBloggingSys": $("#blogType").val()
            });
        }
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["article-sync"] =  {
    "obj": admin.articleSync,
    "init": admin.articleSync.init
}