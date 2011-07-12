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
 *  util and every page should be userd.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.2, July 12, 2011
 */

var Util = {
    killIE: function () {
        if ($.browser.msie) {
            if ($.browser.version === "6.0" || $.browser.version === "7.0") {
                $("body").html("Let's kill IE 6 and IE 7!");
            }
        }
    },

    getCursorEndPosition: function (textarea) {
        textarea.focus();
        if (textarea.setSelectionRange) { // W3C
            return textarea.selectionEnd;
        } else if (document.selection) { // IE
            var i = 0,
            oS = document.selection.createRange(),
            oR = document.body.createTextRange(); 
            oR.moveToElementText(textarea);
            oS.getBookmark();
            for (i = 0; oR.compareEndPoints('StartToStart', oS) < 0 && oS.moveStart("character", -1) !== 0; i ++) {
                if (textarea.value.charAt(i) == '\n') {
                    i ++;
                }
            }
            return i;
        }
    }
};

var Common = function (tips) {
    this.tips = tips;
}

$.extend(Common.prototype, {
    goTop: function () {
        window.scrollTo(0, 0);
    },
    
    goBottom: function () {
        switch (this.tips.skinDirName) {
            case "tree-house":
                window.scrollTo(0, $(window).height() - 350);
                break;
            default :
                window.scrollTo(0, $(window).height());
                break;
        }
    },
    
    init: function () {
        Util.killIE();
       
        var tips = this.tips;

        // common-top
        $.ajax({
            type: "POST",
            url: "/check-login.do",
            success: function(result){
                if (result.isLoggedIn) {
                    var loginHTML = "<div class='left'>" + result.userName + "&nbsp;| &nbsp;</div>";

                    if (result.isAdmin) {
                        loginHTML += "<span class='left' onclick=\"common.clearCache('all');\">"
                        + tips.clearAllCacheLabel + "&nbsp;|&nbsp;</span>"
                        + "<span class='left' onclick='common.clearCache();'>"
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
                console.error(event);
            }
        });
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

    replaceSideEm: function (comments) {
        for (var i = 0; i < comments.length; i++) {
            var $comment = $(comments[i]);
            var commentSplited = $comment.html().split("[em");
            var replacedStr = commentSplited[0];
            for (var j = 1; j < commentSplited.length; j++) {
                var key = commentSplited[j].substr(0, 2);
                replacedStr += "[" + this.tips["em" + key + "Label"]  +
                    "]" + commentSplited[j].slice(3);
            }
            $comment.html(replacedStr);
        }
    }
});