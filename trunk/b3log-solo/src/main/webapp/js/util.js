/*
 * Copyright (c) 2009, 2010, B3log Team
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

var Util = function (tip) {
    this.tip = tip;
}

$.extend(Util.prototype, {
    goTop:function (type) {
        switch (type) {
            case "simple":
                window.scrollTo(0, 0);
                break;
            default:
                alert("has no type!");
                break;
        }
    },

    init: function () {
        var tip = this.tip;
        $.ajax({
            type: "POST",
            url: "/check-login.do",
            success: function(result){
                if (result.isLoggedIn) {
                    var loginHTML = "<span class='left'>" + result.userName + "&nbsp;| &nbsp;</span>";

                    if (result.isAdmin) {
                        loginHTML += "<span class='left' onclick='util.clearAllCache();'>"
                        + tip.clearAllCacheLabel + "&nbsp;|&nbsp;</span>"
                        + "<span class='left' onclick='util.clearCache();'>"
                        + tip.clearCacheLabel + "&nbsp;|&nbsp;</span>";
                    }
                    loginHTML += "<div class='left adminIcon' onclick=\"window.location='/admin-index.do';\" title='"
                    + tip.adminLabel + "'></div>" + "<div class='left'>&nbsp;|&nbsp;</div>"
                    + "<div onclick='util.adminLogout();' class='left logoutIcon' title='" + tip.logoutLabel+ "'></div>";
                
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<div class='left loginIcon' onclick='util.adminLogin();' title='" + tip.loginLabel + "'></div>");
                }
            },
            error: function (event, XMLHttpRequest, ajaxOptions, thrownError) {
                
            }
        });
    },

    clearCache: function () {
        jsonRpc.adminService.clearPageCache(window.location.pathname);
        window.location.reload();
    },

    clearAllCache: function () {
        jsonRpc.adminService.clearAllPageCache();
        window.location.reload();
    },

    adminLogin: function () {
        var loginURL = jsonRpc.adminService.getLoginURL("/admin-index.do");
        window.location.href = loginURL;
    },

    adminLogout: function () {
        var logoutURL = jsonRpc.adminService.getLogoutURL();
        window.location.href = logoutURL;
    }
});
