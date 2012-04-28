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
 * @fileoverview markdowm WMD editor 
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.1, Apr 28, 2012
 */
admin.editors.WMD = {
    /*
     * @description 初始化编辑器
     * @param conf 编辑器初始化参数
     * @param conf.kind 编辑器类型
     * @param conf.id 编辑器渲染元素 id
     * @param conf.fun 编辑器首次加载完成后回调函数
     */
    init: function (conf) {
        var $textarea = $("#" + conf.id);
        
        if (conf.kind === "simple") {
            $textarea.width("99%");
            return;
        }
        
        /***** Make sure WMD has finished loading *****/
        if (!Attacklab || !Attacklab.wmd) {
            alert("WMD hasn't finished loading!");
            return;
        }
        
        // build the dom elementsx
        var previewHTML = "<div class='wmd-preivew'>" 
                            + "<h1>Preview</h1>"
                            + "<div class='wmd-preivew-main' id='" + conf.id + "Preview'></div>" 
                            + "</div>"
                        + "<div class='clear'></div>"
        $textarea.css({
            "width": "50%",
            "float": "left"
        }).after(previewHTML);
        
        /***** build the preview manager *****/
        var panes = {
            input: $textarea[0], 
            preview: $("#" + conf.id + "Preview")[0], 
            output: null
        };
        
        var previewManager = new Attacklab.previewManager(panes);
        new Attacklab.editor($textarea[0], previewManager.refresh);
        
        if (typeof(conf.fun) === "function") {
            conf.fun();
        }
    },
    
    /*
     * @description 获取编辑器值
     * @param {string} id 编辑器id
     * @returns {string} 编辑器值
     */
    getContent: function (id) {
        return $("#" + id).val();
    },
    
    /*
     * @description 设置编辑器值
     * @param {string} id 编辑器 id
     * @param {string} content 设置编辑器值
     */
    setContent: function (id, content) {
        $("#" + id).val(content);
    }
};