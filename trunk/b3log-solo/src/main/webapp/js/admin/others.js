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
 * others for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, July 3, 2011
 */

/* oterhs 相关操作 */
admin.others = {
    /*
     * 移除未使用的 tag
     */
    removeUnusedTags: function () {
        $("#tipMsg").text("");
        jsonRpc.tagService.removeUnusedTags(function (result, error) {
            try {
                if (result.sc === "REMOVE_UNUSED_TAGS_SUCC") {
                    $("#tipMsg").text(Label.removeSuccLabel);
                } else {
                    $("#tipMsg").text(Label.removeFailLabel);
                }
            } catch (e) {
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register.others =  {
    "obj": admin.others,
    "init": function () {
        $("#loadMsg").text("");
    }
}
