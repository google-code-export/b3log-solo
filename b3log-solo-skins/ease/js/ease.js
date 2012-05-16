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
 * @fileoverview neoease js.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, May 16, 2012
 */
var getArticle = function (it, id) {
    var $abstract = $("#abstract" + id),
    $content = $("#content" + id),
    $it = $(it);
    
    if ($content.html() === "") {
        $.ajax({
            url: "/get-article-content?id=" + id,
            type: "GET",
            dataType: "html",
            beforeSend: function () {
                $abstract.css("background",
                    "url(" + latkeConfig.staticServePath + "/skins/neoease/images/ajax-loader.gif) no-repeat scroll center center transparent");
            },
            success: function(result, textStatus){
                $it.text(Label.abstractLabel);
                $content.html(result);
                $abstract.hide().css("background", "none");
                $content.fadeIn("slow");
            }
        });
    } else {
        if ($it.text() === Label.contentLabel) {
            $abstract.hide();
            $content.fadeIn();
            $it.text(Label.abstractLabel);
        } else {
            $content.hide();
            $abstract.fadeIn();
            $it.text(Label.contentLabel);
        }
    }
    
    return false;
};

var goTranslate = function () {
    window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);  
};
    
(function () {
    var $header = $(".header"),
    $banner = $header.find(".banner"),
    headerH = $header.height(),
    $body = $(".body"),
    $nav = $(".nav");
    $(window).scroll(function () {
        // go top icon show or hide
        var y = $(window).scrollTop();

        if (y > headerH) {
            var bodyH = $(window).height();
            var top =  y + bodyH - 21;
            if ($("body").height() - 58 <= y + bodyH) {
                top = $(".footer").offset().top - 21; 
            }
            $("#goTop").fadeIn("slow").css("top", top);
        } else {
            $("#goTop").hide();
        }
        
        // header event
        if (y > headerH && $banner.css("display") !== "none") {
            $header.css("top", "0");
            $banner.css("display", "none");
            $body.css("paddingTop", $nav.height() + "px");
        }
        
        if (y < headerH && $banner.css("display") !== "block") {
            $header.css("top", "auto");
            $banner.css("display", "block");
            $body.css("paddingTop", headerH + "px");
        }
    });
    
    $body.css("paddingTop", headerH + "px");
    
    // nav current
    $(".nav ul a").each(function () {
        var $this = $(this);
        if ($this.attr("href") === latkeConfig.servePath + location.pathname) {
            $this.addClass("current");
        } else if (/\/[0-9]+$/.test(location.pathname)) {
            $(".nav ul li")[0].className = "current";
        }
    });
    
    Util.init();
    Util.replaceSideEm($(".recent-comments-content"));
    Util.buildTags("tagsSide");
})();