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
 * others for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Oct 24, 2011
 */

/* oterhs 相关操作 */
admin.others = {
    /*
     * @description 初始化
     */
    init: function () {
        $("#tabOthers").tabs();
        
        $.ajax({
            url: "/console/reply/notification/template",
            type: "GET",
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                     
                if (!result.sc) {
                    return;
                }
                
                $("#replayEmailTemplateTitle").val(result.replyNotificationTemplate.subject);
                $("#replayEmailTemplateBody").val(result.replyNotificationTemplate.body);
                
                $("#loadMsg").text("");
            }
        });        
    },
    
    /*
     * @description 移除未使用的标签。
     */
    removeUnusedTags: function () {
        $("#tipMsg").text("");
        
        $.ajax({
            url: "/console/tag/unused",
            type: "DELETE",
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);                
            }
        });
    },
    
    /*
     * 获取未使用的标签。
     */
    getUnusedTags: function () {
        $.ajax({
            url: "/console/tag/unused",
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    return;
                }
                
                var unusedTags = result.unusedTags;
                if (0 === unusedTags.length) {
                    return;
                }

            // XXX: Not used this function yet.
            }
        });
    },
    
    /*
     * @description 跟新回复提醒邮件模版
     */
    update: function () {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var requestJSONObject = {
            "replyNotificationTemplate": {
                "subject": $("#replayEmailTemplateTitle").val(),
                "body": $("#replayEmailTemplateBody").val()
            }
        };
            
        $.ajax({
            url: "/console/reply/notification/template",
            type: "PUT",
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                $("#loadMsg").text("");

                if (!result.sc) {
                    return;
                }
            }
        });     
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register.others =  {
    "obj": admin.others,
    "init":admin.others.init,
    "refresh": function () {
        $("#loadMsg").text("");
    }
}
