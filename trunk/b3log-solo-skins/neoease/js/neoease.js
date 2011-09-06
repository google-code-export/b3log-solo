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
var Util = {
    error: function () {
        alert("Error: " + arguments[0] +
            "\nFile: " + arguments[1] + "\nLine: " + arguments[2] +
            "\nplease report this issue on http://code.google.com/p/b3log-solo/issues/list");
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
        //window.onerror = Util.error;
        Util.killIE();
       
        var tips = this.tips;

        // common-top
        $.ajax({
            type: "POST",
            url: "/check-login.do",
            success: function(result){
                if (result.isLoggedIn) {
                    var loginHTML = "<span>" + result.userName + "</span>";

                    if (result.isAdmin) {
                        loginHTML += "<a href=\"javascript:common.clearCache('all');\">"
                        + tips.clearAllCacheLabel + "</a>"
                        + "<a href='javascript:common.clearCache();'>"
                        + tips.clearCacheLabel + "</a>";
                    }
                    
                    loginHTML += "<a href='/admin-index.do#main' title='"
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
    
    buildTags: function (id) {
        id = id || "tags";
        
        // 按字母进行排序
        $("#" + id).html($("#" + id + " li").get().sort(function(a, b) {
            var valA = $(a).find("span").text().toLowerCase();
            var valB = $(b).find("span").text().toLowerCase();
            return (valA < valB) ? -1 : (valA > valB) ? 1 : 0;		
        }));
        
        // 按引用次数进行排序
        var aList = $("#" + id + " a").get();
        aList.sort(function(a, b) {
            var valA = parseInt($(a).data("count"));
            var valB = parseInt($(b).data("count"));
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

/**
 * @fileoverview neoease js.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, Sep 6, 2011
 */
var goTop = function (acceleration) {
    acceleration = acceleration || 0.1;

    var y = $(window).scrollTop();
    var speed = 1 + acceleration;
    window.scrollTo(0, Math.floor(y / speed));

    if (y > 0) {
        var invokeFunction = "goTop(" + acceleration + ")";
        window.setTimeout(invokeFunction, 16);
    }
};

var collapseArchive = function (it, year) {
    var tag = true;
    if (it.className === "collapse-ico") {
        it.className = "expand-ico";
        tag = false;
    } else {
        it.className = "collapse-ico";
    }
    
    $("#archiveSide li").each(function () {
        var $this = $(this);
        // hide other year month archives
        if ($this.data("year") === year) {
            if (tag) {
                $(this).show();
            } else {
                $(this).hide();
            }
        }
    });
};

var getArticle = function (it, id) {
    var $abstract = $("#abstract" + id),
    $content = $("#content" + id);
    
    if ($content.html() === "") {
        $.ajax({
            url: "/get-article-content?id=" + id,
            type: "GET",
            dataType: "html",
            beforeSend: function () {
                $abstract.css("background",
                    "url(/skins/neoease/images/ajax-loader.gif) no-repeat scroll center center transparent");
            },
            success: function(result, textStatus){
                it.className = "collapse-ico";
                $content.html(result);
                $abstract.hide().css("background", "none");
                $content.fadeIn("slow");
            }
        });
    } else {
        if (it.className === "expand-ico") {
            $abstract.hide();
            $content.fadeIn();
            it.className = "collapse-ico";
        } else {
            $content.hide();
            $abstract.fadeIn();
            it.className = "expand-ico";
        }
    }
    
    return false;
};

var goTranslate = function () {
  window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);  
};
    
(function () {
    // go top icon show or hide
    $(window).scroll(function () {
        var y = $(window).scrollTop();

        if (y > 182) {
            var top =  y + window.innerHeight - 21;
            if ($("body").height() - 58 <= y + window.innerHeight) {
                top = $(".footer").offset().top - 21; 
            }
            $("#goTop").fadeIn("slow").css("top", top);
        } else {
            $("#goTop").hide();
        }
    });
    
    
    // archive
    var currentYear = (new Date()).getFullYear(),
    year = currentYear;
    $("#archiveSide li").each(function (i) {
        var $this = $(this);
        
        // hide other year month archives
        if ($this.data("year") !== currentYear) {
            $(this).hide()
        }
        
        // append year archive
        if (year !== $this.data("year")) {
            year = $this.data("year");
            $this.before("<li><div onclick='collapseArchive(this, " + year + ")' class='expand-ico'>" + year + "</div></li>");
        }
    });
    
    // recent comment mouse event
    $(".recent-comments li").mouseenter(function () {
        var $ico = $(this).find("div>span");
        if ($ico[0].style.display === "none" || $ico[0].style.display === "") {
            if ($(this).find(".recent-comments-content>a").height() > 30) {
                $ico.show();
            }
        }
    }).mouseleave(function () {
        var $ico = $(this).find("div>span");
        if ($ico[0].style.display === "inline") {
            $ico.hide();
        }
    });
    
    // recent comment mouse click
    $(".recent-comments .expand-ico").click(function () {
        if (this.className === "expand-ico") {
            $(this).parent().next().css("height", "auto");
            this.className = "collapse-ico";
        } else {
            $(this).parent().next().animate({
                "height": "16px"
            });
            this.className = "expand-ico";
        }
    });
    
    // nav current
    $(".nav ul a").each(function () {
        var $this = $(this);
        if ($this.attr("href") === location.pathname) {
            $this.parent().addClass("current");
        } else if (/\/[0-9]+$/.test(location.pathname)) {
            $(".nav ul li")[0].className = "current";
        }
    })
})();