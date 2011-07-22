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
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.4, July 17, 2011
 */

var Admin = function () {
    this.register = {};
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
    tabsAction: function (hash, action) {
        console.log(hash);
        
        if (!$("#tab_" + hash + " a").hasClass("tab-current")) {
            $("#tab_" + hash).click();
        }
        if ($("#tabs_" + hash).html().replace(/\s/g, "") === "") {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tabs_" + hash).load("admin-" + hash + ".do", function () {
                admin.register[hash].init.call(admin.register[hash].obj);
                
                if (hash === "article" && admin.article.status.id) {
                    admin.article.getAndSet();
                    return;
                }
                if (action) {
                    action();
                }
            });
        } else if (admin.register[hash].obj.type) {
            if (!admin.register[hash].obj.isInit) {
                admin.register[hash].init.call(admin.register[hash].obj);
                admin.register[hash].obj.isInit = true;
            } else {
                if (admin.register[hash].refresh) {
                    admin.register[hash].refresh.call(admin.register[hash].obj, 1);
                }
            }
        } else{
            if (hash === "article" && admin.article.status.id) {
                admin.article.getAndSet();
                return;
            }
            if (admin.register[hash].refresh) {
                admin.register[hash].refresh.call(admin.register[hash].obj, 1);
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
        if (tab !== "") {
            if (subTab) {
                this.tabsAction(tab, function () {
                    $("#tab_" + subTab).click();
                });
            } else {
                this.tabsAction(tab);
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
        
        // Removes functions with the current user role
        if (Label.userRole !== "adminRole") {
            var unUsed = ['link-list', 'preference', 'file-list', 'page', 'others', 'user-list'];
            for (var i = 0; i < unUsed.length; i++) {
                $("#tab").tabs("remove", unUsed[i]);
            }
        }
        
        // 当前 tab 属于 Tools 时，设其展开
        // TODO: 插件
        var tools = ['#page-list', '#file-list', '#link-list', '#preference', 
            '#user-list', '#plugin-list', '#cache-list', '#others'];
        for (var j = 0; j < tools.length; j++) {
            if (window.location.hash.indexOf(tools[j]) > -1) {
                $("#tabs>ul>li>div")[2].click();
            }
        }
        
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
    }
});
var admin = new Admin();
var plugins = {};