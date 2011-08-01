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
 * @version 1.0.0.4, July 26, 2011
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
    },
    
    replaceEmString: function (str) {
        var commentSplited = str.split("[em");
        if (commentSplited.length === 1) {
            return str;
        }
        str = "<span class='em-span'>" + commentSplited[0] + "</span>";
        if ($.trim(commentSplited[0]) === "") {
            str = "";
        }
        for (var j = 1; j < commentSplited.length; j++) {
            var key = commentSplited[j].substr(0, 2);
            str += "<span class='em" + key + "'></span>" + "<span class='em-span'>" +  commentSplited[j].slice(3) + "</span>";
        }
        return str + "<div class='clear'></div>";
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
                window.scrollTo(0, $("body").height() - $(window).height() - 700);
                break;
            default :
                window.scrollTo(0, $("body").height() - $(window).height());
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
                    var loginHTML = "<a href='#'>" + result.userName + "</a>";

                    if (result.isAdmin) {
                        loginHTML += "<a href=\"javascript:common.clearCache('all');\">"
                        + tips.clearAllCacheLabel + "</a>"
                        + "<a href='javascript:common.clearCache();'>"
                        + tips.clearCacheLabel + "</a>";
                    }
                    
                    loginHTML += "<a href='/admin-index.do' title='"
                    + tips.adminLabel + "'>" + tips.adminLabel + "</a>"
                    + "<a href='" + result.logoutURL + "' title='" + 
                    tips.logoutLabel+ "'>" + tips.logoutLabel+ "</a>";
                
                    $("#admin").append(loginHTML);
                } else {
                    $("#admin").append("<a href='"
                        + result.loginURL + "' title='" + tips.loginLabel + "'>" + tips.loginLabel + "</a>");
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
            contentType: "application/json",
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
    },
    
    buildTags: function () {
        // 按字母进行排序
        $("#tags").html($("#tags li").get().sort(function(a, b) {
            var valA = $(a).find("span").text().toLowerCase();
            var valB = $(b).find("span").text().toLowerCase();
            return (valA < valB) ? -1 : (valA > valB) ? 1 : 0;		
        }));
        
        // 按引用次数进行排序
        var aList = $("#tags a").get();
        aList.sort(function(a, b) {
            var valA = parseInt($(a).find("b").text());
            var valB = parseInt($(b).find("b").text());
            return (valA < valB) ? -1 : (valA > valB) ? 1 : 0;		
        });
                
        // 根据引用次数添加样式，产生云效果
        var aLength = aList.length,
        classes = ["tags1", "tags2", "tags3", "tags4", "tags5"];
        var arr = Math.round(aLength / classes.length);
        for (var i = 0, c = 0; i < aLength; i++) {
            if (c < classes.length - 1) {
                for (var j = 0; j < arr; j++) {
                    aList[i++].className = classes[c];
                }
                c++;
                i--;
            } else {
                aList[i].className = classes[c];
            }
        }
    }
});