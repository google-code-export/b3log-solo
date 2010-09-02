/*
 * Copyright (C) 2010, Liyuan Li
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
(function ($) {
    var Bowknot = function () {

    };

    $.extend(Bowknot.prototype, {
        _info: {
            version:"0.0.0.7",
            author: "lly219@gmail.com"
        },

        getDefaults: function (defaults, settings, key) {
            if (key === "styleClass") {
                if (settings.theme === "default" || settings.theme === undefined) {
                    return defaults.styleClass;
                }

                settings.styleClass = {};
                for (var styleName in defaults[key]) {
                    settings.styleClass[styleName] = settings.theme + "-" + defaults.styleClass[styleName];
                }
            } else if ((key === "height" && settings[key] !== "auto") || key === "width") {
                if (settings[key] === null || settings[key] === undefined) {
                    return defaults[key] + "px";
                } else {
                    return settings[key] + "px";
                }
            } else {
                if (settings[key] === null || settings[key] === undefined) {
                    return defaults[key];
                }
            }
            return settings[key];
        },

        strToInt: function (str) {
            return parseInt(str.substring(0, str.length - 2));
        },

        getDate: function(time, type) {
            var date = new Date(time);
            var year = date.getFullYear(),
            month = date.getMonth() + 1,
            day = date.getDate(),
            hours = date.getHours(),
            seconds = date.getSeconds(),
            minutes = date.getMinutes();

            if (month < 10) {
                month = "0" + month.toString();
            }

            if (day < 10) {
                day = "0" + day.toString();
            }

            if (hours < 10) {
                hours = "0" + hours.toString();
            }

            if (minutes < 10) {
                minutes = "0" + minutes.toString();
            }

            if (seconds < 10) {
                seconds = "0" + seconds.toString();
            }

            switch (type) {
                case undefined: // yyyy-MM-dd
                    return year + "-" + month + "-" + day;
                    break;
                case 1: // yyyy-MM-dd HH:mm:ss
                    return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
                    break;
                default:
                    return false;
                    break;
            }
        },

        ellipsis: function (str, strLength) {
            var length = 0,
            strTrim = str.replace(/(^\s*)|(\s*$)/g, ""),
            strArray = strTrim.split(""),
            resultStr = "";
            for (var i = 0; i < strArray.length; i++) {
                if (length < strLength) {
                    if(strArray[i]&& strArray[i].match(/[^u4E00-u9FA5]/)) {
                        length += 2;
                    } else {
                        length++;
                    }
                    resultStr += strArray[i];
                }
            }
            if (strTrim !== resultStr) {
                resultStr += "...";
            }
            return resultStr;
        }
    });

    $.bowknot = new Bowknot();
})(jQuery);