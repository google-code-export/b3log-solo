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
 * @version 1.0.0.6, July 27, 2011
 */

var Admin = function () {
    this.register = {};
    this.tools = ['#page-list', '#file-list', '#link-list', '#preference', 
    '#user-list', '#plugin-list', '#others'];
    // 多用户时，一般用户不能使用的功能
    this.adTools = ['link-list', 'preference', 'file-list', 'page-list', 'others', 
    'user-list', 'plugin-list']
};

$.extend(Admin.prototype, {
    copyright: {
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
     */
    selectTab: function (id) {
        window.location.hash = "#" + id;
    },
    
    /*
     * 点击 tab 后产生的事件
     * @hash location.hash
     * @action 当前页面还未载入，载入后执行的 action
     */
    tabsAction: function (hash, action, page) {
        console.log(hash);
        if (!$("#tabs_" + hash + " a").hasClass("tab-current")) {
            $("#tabs_" + hash).click();
        }
        if ($("#tabsPanel_" + hash).html().replace(/\s/g, "") === "") {
            // 还未加载 HTML
            $("#loadMsg").text(Label.loadingLabel);
            $("#tabsPanel_" + hash).load("admin-" + hash + ".do", function () {
                admin.register[hash].init.call(admin.register[hash].obj, page);
                
                if (hash === "article" && admin.article.status.id) {
                    admin.article.getAndSet();
                    return;
                }
                if (action) {
                    action();
                }
            });
        } else if (admin.register[hash].obj.hash) {
            // plugin 已经存在，不需 load HTML
            if (!admin.register[hash].obj.isInit) {
                admin.register[hash].init.call(admin.register[hash].obj, page);
                admin.register[hash].obj.isInit = true;
            } else {
                if (admin.register[hash].refresh) {
                    admin.register[hash].refresh.call(admin.register[hash].obj, page);
                }
            }
        } else{
            // 已加载过 HTML
            if (hash === "article" && admin.article.status.id) {
                admin.article.getAndSet();
                return;
            }
            if (admin.register[hash].refresh) {
                admin.register[hash].refresh.call(admin.register[hash].obj, page);
            }
        }  
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
     * 根据当前 hash 设置当前 tab
     */
    setCurByHash: function () {
        // 根据 hash 设置当前 tab
        var hash = window.location.hash;
        var tag = hash.substr(1, hash.length - 1);
        var tagList = tag.split("/");
        var tab = "",
        subTab = "",
        page = 1;
        for (var i = 0; i < tagList.length; i++) {
            if (i === 0) {
                tab = tagList[i];
            } else {
                if (/^\d+$/.test(tagList[i])) {
                    page = tagList[i];
                } else {
                    subTab = tagList[i];
                }
            }
        }
        if (tab !== "") {
            if (subTab) {
                this.tabsAction(tab, function () {
                    $("#tabPreference_" + subTab).click();
                });
            } else {
                this.tabsAction(tab, undefined, page);
            }
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
                if (window.location.hash.indexOf(this.tools[j]) > -1) {
                    $("#tabs>ul>li>div")[2].click();
                }
            }
        }
        this.setCurByHash();
    }
});
var admin = new Admin();