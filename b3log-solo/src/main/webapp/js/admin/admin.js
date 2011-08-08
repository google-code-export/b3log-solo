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
 *  index for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.8, Aug 6, 2011
 */

var Admin = function () {
    this.register = {};
    this.tools = ['#page-list', '#file-list', '#link-list', '#preference', 
    '#user-list', '#plugin-list', '#others'];
    // 多用户时，一般用户不能使用的功能
    this.adTools = ['link-list', 'preference', 'file-list', 'page-list', 'others', 
    'user-list', 'plugin-list'];
};

$.extend(Admin.prototype, {    
    /*
     * 登出
     */
    logout: function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    },
    
    /*
     * 根据当前页数设置 hash
     * @currentPage 当前页
     */
    setHashByPage: function (currentPage) {
        var hash = window.location.hash,
        hashList = hash.split("/");
        if (/^\d*$/.test(hashList[hashList.length - 1])) {
            hashList[hashList.length - 1] = currentPage;
        } else {
            hashList.push(currentPage);
        }
        window.location.hash = hashList.join("/");
    },
    
    /*
     * 设置某个 tab 被选择
     * @id tab id
     */
    selectTab: function (id) {
        window.location.hash = "#" + id;
       
    },
    
    analyseHash: function () {
        var hash = window.location.hash;
        var tag = hash.substr(1, hash.length - 1);
        var tagList = tag.split("/");
        var tags = {};
        tags.page = 1,
        tags.hashList = [];
        for (var i = 0; i < tagList.length; i++) {
            if (i === tagList.length - 1) {
                if (/^\d+$/.test(tagList[i])) {
                    tags.page = tagList[i];
                } else {
                    tags.hashList.push(tagList[i]);
                }
            } else {
                tags.hashList.push(tagList[i]);
            }
        }
        return tags;
    },
    
    /*
     * 根据当前 hash 设置当前 tab
     */
    setCurByHash: function () {
        var tags = this.analyseHash();
        var tab = tags.hashList[1], 
        subTab = tags.hashList[2];
        
        if (tags.hashList[0] === "main" || tags.hashList[0] === "comment-list") {
            tab = tags.hashList[0];
        }
        
        
        if (tinyMCE) {
            if (tinyMCE.get('articleContent')) {
                if (tab !== "article" && admin.article.isConfirm &&
                    tinyMCE.get('articleContent').getContent().replace(/\s/g, '') !== "") {
                    if (!confirm(Label.editorLeaveLabel)) {
                        window.location.hash = "#article/article";
                        return;
                    }
                }
                if (tab === "article" && admin.article.isConfirm &&
                    tinyMCE.get('articleContent').getContent().replace(/\s/g, '') !== "") {
                    return;
                }
            }
        }
        
        //console.log(tags.tab);
        // clear article 
        if (tab !== "article") {
            admin.article.clear();
        }
        admin.article.isConfirm = true;
        
        $("#tabs").tabs("setCurrent", tab);
        if ($("#tabsPanel_" + tab).html().replace(/\s/g, "") === "") {
            // 还未加载 HTML
            $("#loadMsg").text(Label.loadingLabel);
            $("#tabsPanel_" + tab).load("admin-" + tab + ".do", function () {
                // 回调加载页面初始化函数
                if (tab === "article" && admin.article.status.id) {
                    admin.register[tab].init.call(admin.register[tab].obj, admin.article.getAndSet);
                } else {
                    admin.register[tab].init.call(admin.register[tab].obj, tags.page);
                }
                if (subTab) {
                    $("#tabPreference").tabs("setCurrent", subTab);
                }
        
                admin.plugin.setCurByHash(tags);
            });
        } else {
            if (tab === "article" && admin.article.status.id) {
                admin.article.getAndSet();
                return;
            }
            
            // 已加载过 HTML
            if (admin.register[tab] && admin.register[tab].refresh) {
                admin.register[tab].refresh.call(admin.register[tab].obj, tags.page);
            }
            
            if (subTab) {
                $("#tabPreference").tabs("setCurrent", subTab);
            }
            
            admin.plugin.setCurByHash(tags);
        }  
    },
    
    /*
     * 初始化整个后台
     */
    init: function () {
        // 不支持 IE 6
        Util.killIE();   
        $("#loadMsg").text(Label.loadingLabel);
        
        // 构建 tabs
        $("#tabs").tabs();
            
        // tipMsg
        setInterval(function () {
            if($("#tipMsg").text() !== "") {
                setTimeout(function () {
                    $("#tipMsg").text("");
                }, 7000);
            }
        }, 6000);
        $("#loadMsg").text("");
    },
    
    collapseNav: function (it) {
        var subNav = $(it).next()[0];
        if (subNav.className === "none") {
            $(it).find(".ico-arrow-down")[0].className = "ico-arrow-up";
            subNav.className = "collapsed";
        } else {
            $(it).find(".ico-arrow-up")[0].className = "ico-arrow-down";
            subNav.className = "none";
        }
    },
    
    inited: function () {
        // Removes functions with the current user role
        if (Label.userRole !== "adminRole") {
            for (var i = 0; i < this.adTools.length; i++) {
                $("#tabs").tabs("remove", this.adTools[i]);
            }
            $("#tabs>ul>li").last().remove();
        } else {
            // 当前 tab 属于 Tools 时，设其展开
            for (var j = 0; j < this.tools.length; j++) {
                if ("#" + window.location.hash.split("/")[1] === this.tools[j]) {
                    $("#tabToolsTitle").click();
                    break;
                }
            }
        }
        this.setCurByHash();
    }
});
var admin = new Admin();