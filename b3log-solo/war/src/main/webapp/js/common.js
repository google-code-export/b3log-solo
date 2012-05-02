/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
 * @fileoverview util and every page should be userd.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.1.6, Apri 21, 2012
 */
var Util = {
    error: function () {
        $("#tipMsg").text("Error: " + arguments[0] +
            " File: " + arguments[1] + "\nLine: " + arguments[2] +
            " please report this issue on http://code.google.com/p/b3log-solo/issues/list");
        $("#loadMsg").text("");
    },
        
    killIE: function () {
        if ($.browser.msie) {
            // kill IE6 and IE7
            if ($.browser.version === "6.0" || $.browser.version === "7.0") {
                window.location = "/kill-browser.html";
                return;
            }
            
            // 后台页面 kill 360 
            if (window.external && window.external.twGetRunPath) {
                var path = external.twGetRunPath();
                if(path && path.toLowerCase().indexOf("360se") > -1 && 
                    window.location.href.indexOf("admin-index") > -1) {
                    window.location = "/kill-browser.html";
                    return; 
                }
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
    
    _processEm: function (str) {
        if (str.replace(/\s/g, "") === "") {
            return "";
        }
        
        var strList = [], 
        resultStr = "",
        brList = ["<br>", "<br/>", "<BR>", "<BR/>"];
        for (var j = 0; j < brList.length; j++) {
            if (str.indexOf(brList[j]) > -1) {
                strList = str.split(brList[j]);
            }
        }
        
        if (strList.length === 0) {
            return "<span class='em-span'>" + str + "</span>";
        }
        
        for (var i = 0; i < strList.length; i++) {
            resultStr += "<span class='em-span'>" + strList[i] + "</span>";
            if (i !== strList.length - 1) {
                resultStr +="<br class='em-br'>";
            }
        }
        return resultStr;
    },
    
    replaceEmString: function (str) {
        var commentSplited = str.split("[em");
        if (commentSplited.length === 1) {
            return str;
        }
        str = this._processEm(commentSplited[0]);
        if ($.trim(commentSplited[0]) === "") {
            str = "";
        }
        for (var j = 1; j < commentSplited.length; j++) {
            var key = commentSplited[j].substr(0, 2);
            str += "<span class='em" + key + "'></span>" + this._processEm(commentSplited[j].slice(3));
        }
        return str + "<div class='clear'></div>";
    },
    
    proessURL: function (url) {
        if (!/^\w+:\/\//.test(url)) {
            url = "http://" + url;
        }
        return url;
    },
    
    switchMobile: function (skin) {
        Cookie.createCookie("btouch_switch_toggle", skin, 365);
        setTimeout(function () {
            location.reload();
        }, 1250); 
    },
    
    setTopBar: function () {
        var $top = $("#top");
        if ($top.length === 1) {
            var $showTop = $("#showTop");
            
            $showTop.click(function () {
                $top.slideDown();
                $showTop.hide();
            })
            
            $("#hideTop").click(function () {
                $top.slideUp();
                $showTop.show();
            });
        }
    } 
};

var Common = function (tips) {
    this.tips = tips;
};

$.extend(Common.prototype, {
    goTop: function () {
        window.scrollTo(0, 0);
    },
    
    goBottom: function (bottom) {
        if (!bottom) {
            bottom = 0;
        }
        window.scrollTo(0, $("body").height() - $(window).height() - bottom);
    },
    
    init: function () {
        //window.onerror = Util.error;
        Util.killIE();
        Util.setTopBar();
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
            url: "/blog/clear-cache.do",
            cache: false,
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
    
    buildTags: function (id) {
        id = id || "tags";
        
        // 根据引用次数添加样式，产生云效果
        var classes = ["tags1", "tags2", "tags3", "tags4", "tags5"],
        bList = $("#" + id + " b").get();
        var max = parseInt($("#" + id + " b").last().text());
        var distance = Math.ceil(max / classes.length);

        for (var i = 0; i < bList.length; i++) {
            var num = parseInt(bList[i].innerHTML);
            // 算出当前 tag 数目所在的区间，加上 class
            for (var j = 0; j < classes.length; j++) {
                if (num > j * distance && num <= (j + 1) * distance) {
                    bList[i].parentNode.className = classes[j];
                    break;
                }
            }
        }
        
        // 按字母或者中文拼音进行排序
        $("#" + id).html($("#" + id + " li").get().sort(function(a, b) {
            var valA = $(a).find("span").text().toLowerCase();
            var valB = $(b).find("span").text().toLowerCase();
            // 对中英文排序的处理
            return valA.localeCompare(valB);
        }));
    }
});

if (!Cookie) {
    var Cookie = {
        readCookie: function (name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for(var i=0;i < ca.length;i++) {
                var c = ca[i];
                while (c.charAt(0)==' ') c = c.substring(1,c.length);
                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
            }
            return "";
        },

        eraseCookie: function (name) {
            this.createCookie(name,"",-1);
        },

        createCookie: function (name,value,days) {
            var expires = "";
            if (days) {
                var date = new Date();
                date.setTime(date.getTime()+(days*24*60*60*1000));
                expires = "; expires="+date.toGMTString();
            }
            document.cookie = name+"="+value+expires+"; path=/";
        }
    };
}
