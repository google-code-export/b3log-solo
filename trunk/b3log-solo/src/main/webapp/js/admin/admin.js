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
 *  index for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.1, Jun 28, 2011
 */

var Admin = function () {
};

$.extend(Admin.prototype, {
    adminUtil: {
        version:"0.0.0.2",
        author: "lly219@gmail.com"
    },
    
    /*
     * 登出
     */
    logout: function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    },
    
    /*
     * 设置某个 tab 被选择
     * @id tab id
     * @action 选中 tab 后触发的事件
     */
    selectTab: function (id, action) {
        $("#tabs").tabs("select", id);
        this.tabsAction(id, action, action);
        window.location.hash = "#" + id;
    },
    
    /*
     * 点击 tab 后产生的事件
     * @hash location.hash
     * @action 当前页面还未载入，载入后执行的 action
     * @action2 当前页面已经载入，载入后执行的 action
     */
    tabsAction: function (hash, action, action2) {
        if ($("#tabs_" + hash).length > 0) {
            if ($("#tabs_" + hash).html().replace(/\s/g, "") === "") {
                $("#tabs_" + hash).load("admin-" + hash + ".do", function () {
                    switch (hash) {
                        case "article":
                            admin.article.init();
                            break;
                        case "page-list":
                            admin.pageList.init();
                            break;
                        case "article-list":
                            admin.articleList.init();
                            break;
                        case "draft-list":
                            admin.draftList.init();
                            break;
                        case "file-list":
                            admin.fileList.init();
                            break;
                        case "others":
                            $("#loadMsg").text("");
                            break;
                        case "link-list":
                            admin.linkList.init();
                            break;
                        case "article-sync":
                            admin.articleSync.init();
                            break;
                        case "preference":
                            admin.preference.init();
                            break;
                        case "plugin-list":
                            admin.pluginList.init();
                            break;
                        case "user-list":
                            admin.userList.init();
                            break;
                        default:
                            break;
                    }
                
                    if (action) {
                        action();
                    }
                });
            } else {
                switch (hash) {
                    case "article":
                        admin.article.clear();
                        $("#loadMsg").text("");
                        break;
                    case "page-list":
                        admin.pageList.getList(1);
                        break;
                    case "article-list":
                        admin.articleList.getList(1);
                        break;
                    case "draft-list":
                        admin.draftList.getList(1);
                        break;
                    default:
                        $("#loadMsg").text("");                        
                        break;
                }
                if (action2) {
                    action2();
                }
            }  
        }
    },
    
    /*
     * 根据当前 hash 设置当前 tab
     */
    setCurByHash: function () {
        // 根据 hash 设置当前 tab，如果 hash 为空时，默认为发布文章
        var hash = window.location.hash;
        var tag = hash.substr(1, hash.length - 1);
        var tab = tag.split("/")[0],
        subTab = tag.split("/")[1];
        if (hash !== "") {
            if (subTab) {
                this.tabsAction(tab, function () {
                    $("#tabs" + tab.replace("-", "")).tabs("select", subTab);
                });
            } else {
                this.tabsAction(tab);
            }
        } else {
            $("#loadMsg").text("");
        }
    },
    
    /*
     * 初始化整个后台
     */
    init: function () {
        // 不支持 IE 6
        Util.killIE();   
        
        $("#loadMsg").text(Label.loadingLabel);
        // 构建 tabs 及其点击事件
        $("#tabs").tabs({
            "bind":[{
                "type": "click",
                "action": function (event, data) {
                    $("#loadMsg").text(Label.loadingLabel);
                    admin.tabsAction(data.id);
                }
            }]
        });
        
        this.setCurByHash();
        
        // Removes functions with the current user role
        if (Label.userRole !== "adminRole") {
            var unUsed = ['link-list', 'preference', 'file-list', 'article-sync', 'page', 'others', 'user-list'];
            for (var i = 0; i < unUsed.length; i++) {
                $("#tab").tabs("remove", unUsed[i]);
            }
        }
        
        // tipMsg
        setInterval(function () {
            if($("#tipMsg").text() !== "") {
                setTimeout(function () {
                    $("#tipMsg").text("");
                }, 8000);
            }
        }, 6000);
    }
});