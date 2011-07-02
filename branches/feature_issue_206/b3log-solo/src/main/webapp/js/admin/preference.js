/*
 * Copyright (c) 2011, B3log Team
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
 * preference for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.2, Jun 30, 2011
 */

/* preference 相关操作 */
admin.preference = {
    locale: "",
    
    /*
     * 初始化
     */
    init: function () {
        $("#loadMsg").text(Label.loadingLabel);
        
        $("#tabspreference").tabs();
         
        jsonRpc.preferenceService.getPreference(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_PREFERENCE_SUCC":
                        // preference
                        var preference = result.preference;
                        $("#metaKeywords").val(preference.metaKeywords),
                        $("#metaDescription").val(preference.metaDescription),
                        $("#blogTitle").val(preference.blogTitle),
                        $("#blogSubtitle").val(preference.blogSubtitle),
                        $("#mostCommentArticleDisplayCount").val(preference.mostCommentArticleDisplayCount);
                        $("#mostViewArticleDisplayCount").val(preference.mostViewArticleDisplayCount),
                        $("#recentCommentDisplayCount").val(preference.recentCommentDisplayCount);
                        $("#mostUsedTagDisplayCount").val(preference.mostUsedTagDisplayCount);
                        $("#articleListDisplayCount").val(preference.articleListDisplayCount);
                        $("#articleListPaginationWindowSize").val(preference.articleListPaginationWindowSize);
                        $("#blogHost").val(preference.blogHost);
                        $("#localeString").val(preference.localeString);
                        $("#timeZoneId").val(preference.timeZoneId);
                        $("#noticeBoard").val(preference.noticeBoard);
                        $("#htmlHead").val(preference.htmlHead);
                        $("#secret").val(preference.googleOAuthConsumerSecret);
                        $("#externalRelevantArticlesDisplayCount").val(preference.externalRelevantArticlesDisplayCount);
                        $("#relevantArticlesDisplayCount").val(preference.relevantArticlesDisplayCount);
                        $("#randomArticlesDisplayCount").val(preference.randomArticlesDisplayCount);
                        $("#keyOfSolo").val(preference.keyOfSolo);
                        preference.enableArticleUpdateHint ? $("#enableArticleUpdateHint").attr("checked", "checked") : $("#enableArticleUpdateHint").removeAttr("checked");
                        preference.enablePostToBuzz ? $("#syncBuzz").attr("checked", "checked") : $("#syncBuzz").removeAttr("checked");

                        // Tencent micro blog settings
                        preference.enablePostToTencentMicroblog ? $("#postToTencentMicroblog").attr("checked", "checked") : $("#postToTencentMicroblog").removeAttr("checked");
                        $("#tencentMicroblogAppKey").val(preference.tencentMicroblogAppKey);
                        $("#tencentMicroblogAppSecret").val(preference.tencentMicroblogAppSecret);

                        admin.preference.locale = preference.localeString;

                        // skin
                        $("#skinMain").data("skinDirName", preference.skinDirName);
                        var skins = eval('(' + preference.skins + ')');
                        var skinsHTML = "";
                        for (var i = 0; i < skins.length; i++) {
                            if (skins[i].skinName === preference.skinName
                                && skins[i].skinDirName === preference.skinDirName ) {
                                skinsHTML += "<div title='" + skins[i].skinDirName
                                + "' class='left skinItem selected'><img class='skinPreview' src='skins/"
                                + skins[i].skinDirName + "/preview.png'/><div>" + skins[i].skinName + "</div></div>"
                            } else {
                                skinsHTML += "<div title='" + skins[i].skinDirName
                                + "' class='left skinItem'><img class='skinPreview' src='skins/"
                                + skins[i].skinDirName + "/preview.png'/><div>" + skins[i].skinName + "</div></div>"
                            }
                        }
                        $("#skinMain").append(skinsHTML + "<div class='clear'></div>");

                        $(".skinItem").click(function () {
                            $(".skinItem").removeClass("selected");
                            $(this).addClass("selected");
                            $("#skinMain").data("skinDirName", this.title);
                        });

                        // sign
                        var signs = eval('(' + preference.signs + ')');
                        for (var i = 0; i < signs.length; i++) {
                            var oId = parseInt(signs[i].oId);
                            if (oId !== 0) {
                                $("#preferenceSign" + oId).val(signs[i].signHTML);
                            }
                        }
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        });
    },
    
    /*
     * 更新
     */
    update: function () {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        if ($("#syncGoogle").hasClass("selected")) {
            if ("" === $("#secret").val().replace(/\s/g, "")) {
                $("#tipMsg").text(Label.contentEmptyLabel);
                return;
            }
        }

        var signs = [{
            "oId": 0,
            "signHTML": ""
        }, {
            "oId": 1,
            "signHTML": $("#preferenceSign1").val()
        }, {
            "oId": 2,
            "signHTML": $("#preferenceSign2").val()
        }, {
            "oId": 3,
            "signHTML": $("#preferenceSign3").val()
        }];
        
        var requestJSONObject = {
            "preference": {
                "metaKeywords": $("#metaKeywords").val(),
                "metaDescription": $("#metaDescription").val(),
                "blogTitle": $("#blogTitle").val(),
                "blogSubtitle": $("#blogSubtitle").val(),
                "mostCommentArticleDisplayCount": $("#mostCommentArticleDisplayCount").val(),
                "mostViewArticleDisplayCount": $("#mostViewArticleDisplayCount").val(),
                "recentArticleDisplayCount": 10, // XXX: remove recentArticleDisplayCount
                "recentCommentDisplayCount": $("#recentCommentDisplayCount").val(),
                "mostUsedTagDisplayCount": $("#mostUsedTagDisplayCount").val(),
                "articleListDisplayCount": $("#articleListDisplayCount").val(),
                "articleListPaginationWindowSize": $("#articleListPaginationWindowSize").val(),
                "skinDirName": $("#skinMain").data("skinDirName"),
                "blogHost": $("#blogHost").val(),
                "localeString": $("#localeString").val(),
                "timeZoneId": $("#timeZoneId").val(),
                "noticeBoard": $("#noticeBoard").val(),
                "htmlHead": $("#htmlHead").val(),
                "googleOAuthConsumerSecret": ""/*$("#secret").val()*/,
                "externalRelevantArticlesDisplayCount": $("#externalRelevantArticlesDisplayCount").val(),
                "relevantArticlesDisplayCount": $("#relevantArticlesDisplayCount").val(),
                "randomArticlesDisplayCount": $("#randomArticlesDisplayCount").val(),
                "enablePostToBuzz": false /*$("#syncBuzz").attr("checked")*/,
                "enableArticleUpdateHint": $("#enableArticleUpdateHint").attr("checked"),
                "signs": signs,
                "tencentMicroblogAppKey": $("#tencentMicroblogAppKey").val(),
                "tencentMicroblogAppSecret": $("#tencentMicroblogAppSecret").val(),
                "enablePostToTencentMicroblog": $("#postToTencentMicroblog").attr("checked"),
                "keyOfSolo": $("#keyOfSolo").val()
            }
        }

        jsonRpc.preferenceService.updatePreference(function (result, error) {
            try {
                switch (result.sc) {
                    case "UPDATE_PREFERENCE_SUCC":
                        $("#tipMsg").text(Label.updateSuccLabel);
                        if ($("#localeString").val() !== admin.preference.locale) {
                            window.location.reload();
                        }
                        
                        // update article signs
                        for (var i = 1; i < signs.length; i++) {
                            $("#articleSign" + signs[i].oId).tip("option", "content", signs[i].signHTML === "" ? "该签名档为空" : signs[i].signHTML);
                        }
                        break;
                    case "UPDATE_PREFERENCE_FAIL_":
                        $("#tipMsg").text(Label.updatePreferenceFailLabel);
                        break;
                    case "UPDATE_PREFERENCE_FAIL_CANNT_BE_LOCALHOST":
                        $("#tipMsg").text(Label.canntBeLocalhostOnProductionLabel);
                        break;
                    case "UPDATE_PREFERENCE_FAIL_NEED_MUL_USERS":
                        $("#tipMsg").text(Label.updatePreferenceFailNeedMulUsersLabel);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    },
    
    /*
     * 腾讯微博认证
     */
    oauthTencent: function () {
        if ("" === $("#tencentMicroblogAppKey").val().replace(/\s/g, "")) {
            $("#tipMsg").text(Label.contentEmptyLabel);
            return;
        }

        if ("" === $("#tencentMicroblogAppSecret").val().replace(/\s/g, "")) {
            $("#tipMsg").text(Label.contentEmptyLabel);
            return;
        }

        $("#loadMsg").text(Label.loadingLabel);
        window.location = "tencent-microblog-oauth-authorize-token.do?appKey="
        + $("#tencentMicroblogAppKey").val()
        + "&appSecret=" + $("#tencentMicroblogAppSecret").val();

    }
    
/*
     * Buzz 认证
    oauthBuzz: function () {
        if ("" === $("#secret").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${contentEmptyLabel}");
            return;
        }
        $("#loadMsg").text(Label.loadingLabel);
        window.location = "buzz-oauth.do?googleOAuthConsumerSecret=" + encodeURIComponent($("#secret").val());
    }
     */
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["preference"] =  {
    "obj": admin.preference,
    "init": admin.preference.init
}