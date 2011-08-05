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
 * @version 1.0.0.7, July 30, 2011
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
        tags.page = 1;
        for (var i = 0; i < tagList.length; i++) {
            if (i === 0) {
                tags.tab = tagList[i];
            } else {
                if (/^\d+$/.test(tagList[i])) {
                    tags.page = tagList[i];
                } else {
                    tags.subTab = tagList[i];
                }
            }
        }
        return tags;
    },
    
    /*
     * 根据当前 hash 设置当前 tab
     */
    setCurByHash: function () {
        var tags = this.analyseHash();
        
        if (!tags.tab) {
            return;
        }
        
        if (tinyMCE) {
            if (tinyMCE.get('articleContent')) {
                if (tags.tab !== "article" && admin.article.isConfirm &&
                    tinyMCE.get('articleContent').getContent().replace(/\s/g, '') !== "") {
                    if (!confirm(Label.editorLeaveLabel)) {
                        window.location.hash = "#article";
                        return;
                    }
                }
                if (tags.tab === "article" && admin.article.isConfirm &&
                    tinyMCE.get('articleContent').getContent().replace(/\s/g, '') !== "") {
                    return;
                }
            }
        }
        
        //console.log(tags.tab);
        $("#tabs").tabs("setCurrent", tags.tab); 
        
        if ($("#tabsPanel_" + tags.tab).html().replace(/\s/g, "") === "") {
            // 还未加载 HTML
            $("#loadMsg").text(Label.loadingLabel);
            $("#tabsPanel_" + tags.tab).load("admin-" + tags.tab + ".do", function () {
                // 回调加载页面初始化函数
                if (tags.tab === "article" && admin.article.status.id) {
                    admin.article.getAndSet();
                }
                
                admin.register[tags.tab].init.call(admin.register[tags.tab].obj, tags.page);
                
                if (tags.subTab) {
                    $("#tabPreference").tabs("setCurrent", tags.subTab);
                }
            });
        } else if (admin.register[tags.tab].obj.hash) {
            // plugin 已经存在
            if (!admin.register[tags.tab].obj.isInit) {
                admin.register[tags.tab].init.call(admin.register[tags.tab].obj, tags.page);
                admin.register[tags.tab].obj.isInit = true;
            } else {
                if (admin.register[tags.tab].refresh) {
                    admin.register[tags.tab].refresh.call(admin.register[tags.tab].obj, tags.page);
                }
            }
        } else{
            if (tags.tab === "article" && admin.article.status.id) {
                admin.article.getAndSet();
                return;
            }
            
            // 已加载过 HTML
            if (admin.register[tags.tab].refresh) {
                admin.register[tags.tab].refresh.call(admin.register[tags.tab].obj, tags.page);
            }
            
            if (tags.subTab) {
                $("#tabPreference").tabs("setCurrent", tags.subTab);
            }
        }  
        
        // clear article 
        if (tags.tab !== "article") {
            admin.article.clear();
        }
        admin.article.isConfirm = true;
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