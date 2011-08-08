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
 *  plugin manager for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.4, Aug 8, 2011
 */
var plugins = {};
admin.plugin = {
    plugins: [],
    add: function (data) {
        // 添加所有插件
        data.isInit = false;
        this.plugins.push(data);
        
        var pathList = this._analysePath(data.path);
        // 添加一二级 Tab
        if (data.hash && pathList.length === 1) {
            this._addNew(data, pathList);
        }
    },
    
    setCurByHash: function (tags) {
        var pluginList = this.plugins;
        for (var i = 0; i < pluginList.length; i++) {
            var data = pluginList[i];
            var pathList = this._analysePath(data.path),
            isCurrentPlugin = false;
            
            if (data.hash) {
                if ("#" + data.hash === window.location.hash ||
                (pathList[0] === "tools" && pathList.length === 2)) {
                    isCurrentPlugin = true;
                }
            }else {
                if(data.path.replace("/", "#").indexOf(window.location.hash) > -1) {
                    isCurrentPlugin = true;
                }
            }
            
            if (isCurrentPlugin) {
                if (data.isInit) {
                    if (data.hash) {
                        if ("#" + data.hash === window.location.hash) {
                            plugins[data.id].refresh(tags.page);   
                        }
                    } else {
                        if (plugins[data.id].refresh) {
                            plugins[data.id].refresh(tags.page);   
                        }
                    }
                } else {
                    if (data.hash && pathList.length === 2) {
                        this._addNew(data, pathList);
                    } 
                    if (!data.hash){
                        this._addToExist(data, pathList);
                    }
                    plugins[data.id].init(tags.page);
                    data.isInit = true;
                }
            }
        }  
    },
    
    _analysePath: function (path) {
        var paths = path.split("/");
        paths.splice(0, 1);
        return paths;
    },
    
    _addNew: function (data, pathList) {
        if (pathList.length === 2) {
            data.target = $("#tabPreference li").get(data.index - 1);
            $("#tabPreference").tabs("add", data);
            return;
        } else if (pathList.length === 0) {
            data.target = $("#tabs>ul>li").get(data.index - 1);
        } else if (pathList[0] === "article") {
            data.target = $("#tabArticleMgt>li").get(data.index - 1);
        } else if (pathList[0] === "tools") {
            admin.tools.push("#" + data.hash.split("/")[1]);
            data.target = $("#tabTools>li").get(data.index - 1);
        }
        $("#tabs").tabs("add", data);
    },
    
    _addToExist: function (data, pathList) {
        switch (pathList[0]) {
            case "main":
                $("#mainPanel" + pathList[1].charAt(5)).append(data.content);
                break;
            case "tools":
            case "article":
                if (pathList.length === 2) {
                    $("#tabsPanel_" + pathList[1]).append(data.content);
                } else {
                    $("#tabPreferencePanel_" + pathList[2]).append(data.content);
                }
                break;
            case "comment-list":
                $("#tabsPanel_comment-list").append(data.content);
                break;
        }
    }
};