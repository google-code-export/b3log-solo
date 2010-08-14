/*
 * Copyright (C) 2009, 2010, B3log Team
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

var getArg = function(key) {
    var items = window.location.search.substr(1, window.location.search.length).split("&");
    for (var i = 0; i < items.length; i++) {
        var item = items[i].split("=");
        var itemKey = item[0],
        itemValue = item[1];
        if (key === itemKey) {
            return itemValue;
        }
    }
}