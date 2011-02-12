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

var Util = function (tips) {
    this.tips = tips;
}

$.extend(Util.prototype, {
    goTop:function (type) {
        switch (type) {
            case undefined:
                window.scrollTo(0, 0);
                break;
            default:
                alert("has no type!");
                break;
        }
    },

    goBottom: function () {
        switch (this.tips.skinDirName) {
            case undefined:
                window.scrollTo(0, 0);
                break;
            case "tree-house":
                var clientHeight  = 0, scrollHeight = 0;
                if(document.body.clientHeight && document.documentElement.clientHeight) {
                    clientHeight = (document.body.clientHeight < document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
                } else {
                    clientHeight = (document.body.clientHeight > document.documentElement.clientHeight) ? document.body.clientHeight : document.documentElement.clientHeight;
                }
                scrollHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
                window.scrollTo(0, scrollHeight - clientHeight - 350);
                break;
            default :
                alert("has no type!");
                break;
        }
    },

    init: function () {
        if ($.browser.msie) {
            if ($.browser.version === "6.0") {
                alert("Let's kill IE 6!");
                return;
            }
        }
        var tips = this.tips;

        // common-top
        $.ajax({
            type: "POST",
            url: "/check-login.do",
            success: function(result){
                if (result.isLoggedIn) {
                    var loginHTML = "<div class='left'>" + result.userName + "&nbsp;| &nbsp;</div>";

                    if (result.isAdmin) {
                        loginHTML += "<span class='left' onclick=\"util.clearCache('all');\">"
                        + tips.clearAllCacheLabel + "&nbsp;|&nbsp;</span>"
                        + "<span class='left' onclick='util.clearCache();'>"
                        + tips.clearCacheLabel + "&nbsp;|&nbsp;</span>";
                    }
                    loginHTML += "<div class='left adminIcon' onclick=\"window.location='/admin-index.do';\" title='"
                    + tips.adminLabel + "'></div>" + "<div class='left'>&nbsp;|&nbsp;</div>"
                    + "<div onclick=\"window.location.href='"
                    + result.logoutURL + "';\" class='left logoutIcon' title='" + tips.logoutLabel+ "'></div>";
                
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<div class='left loginIcon' onclick=\"window.location.href='"
                        + result.loginURL + "';\" title='" + tips.loginLabel + "'></div>");
                }
            },
            error: function (event, XMLHttpRequest, ajaxOptions, thrownError) {
                
            }
        });

        // paginate
        this.setCurrentPage();
    },

    clearCache: function (all) {
        var data = {};
        if (all === "all") {
            data = {
                "all": "all",
                "URI": ""
            };
        } else {
            data = {
                "all": "",
                "URI": window.location.pathname
            };
        }
        
        $.ajax({
            type: "POST",
            url: "/clear-cache.do",
            data: JSON.stringify(data),
            success: function(result){
                window.location.reload();
            }
        });
    },

    replaceCommentsEm: function (selector) {
        var $commentContents = $(selector);
        for (var i = 0; i < $commentContents.length; i++) {
            var str = $commentContents[i].innerHTML;
            var ems = str.split("[em");
            var content = ems[0];
            for (var j = 1; j < ems.length; j++) {
                var key = ems[j].substr(0, 2),
                emImgHTML = "<img src='/skins/" + this.tips.skinDirName + "/emotions/em" + key
                + ".png'/>";
                content += emImgHTML + ems[j].slice(3);
            }
            $commentContents[i].innerHTML = content;
        }
    },

    setCurrentPage: function () {
        if ($(".pagination").length >= 1) {
            var local = window.location.search.substring(1),
            currentPage = "1";
            var paramURL = local.split("&");
            for (var i = 0; i < paramURL.length; i++) {
                if (paramURL[i].split("=")[0] === "paginationCurrentPageNum") {
                    currentPage = paramURL[i].split("=")[1];
                }
            }

            $(".pagination a").each(function () {
                var $it = $(this);
                $it.removeClass("selected");
                if ($it.text() === currentPage) {
                    $it.addClass("selected");
                }
            });

            if ($("#nextPage").length > 0) {
                $("#nextPage").attr("href", $("#nextPage").attr("href").replace("{paginationLastPageNum}", parseInt(currentPage) + 1));
            }
            if ($("#previousPage").length > 0) {
                $("#previousPage").attr("href", $("#previousPage").attr("href").replace("{paginationFirstPageNum}", parseInt(currentPage) - 1));
            }
        }
    },

    // tags
    randomColor: function () {
        var arrHex = ["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"];
        var strHex = "#";
        var index;
        for(var i = 0; i < 6; i++) {
            index = Math.round(Math.random() * 15);
            strHex += arrHex[index];
        }
        return strHex;
    },

    getMaxCount: function (tags) {
        var maxCount = 0;
        for (var i = 0; i < tags.length; i++) {
            if (tags[i].tagCount > maxCount) {
                maxCount = tags[i].tagCount;
            }
        }

        return maxCount;
    },

    getStyle: function (maxCount, currentCount) {
        var styleHTML = {
            padding: "",
            font: "",
            color: ""
        };
        styleHTML.padding =  "padding:" + parseInt(16 * currentCount / maxCount) + "px;";

        styleHTML.color = "color:" + this.randomColor() + ";";

        var fontSize = parseInt(36 * currentCount / maxCount);
        if (fontSize < 12) {
            fontSize = 12;
        }
        styleHTML.font = "font-size:" + fontSize + "px;";
        if (maxCount === currentCount) {
            styleHTML.font += "font-weight:bold;";
        }
        return styleHTML;
    },

    setTagsPanel: function (tags) {
        var tagsHTML = "";

        if (tags.length === 0) {
            return;
        }

        var maxCount = this.getMaxCount(tags);

        for (var i = 0; i < tags.length; i++) {
            var style = this.getStyle(maxCount, tags[i].tagCount);
            tagsHTML += "<a title='" + tags[i].tagCount + "' class='tagPanel' style='"
            + style.font + style.color + style.padding + "' href='/tags/"
            + tags[i].tagNameURLEncoded +"'>" + tags[i].tagName + "</a> ";
        }
        $("#tagsPanel").html(tagsHTML + "<div class='clear'></div>");
    }
});
