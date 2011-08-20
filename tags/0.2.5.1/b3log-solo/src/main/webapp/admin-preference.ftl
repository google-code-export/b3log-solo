<div class="tabPanel">
    <div class="tabs">
        <span class="selected" id="preferences" onclick="changePreferenceTab(this);">
            ${paramSettingsLabel}
        </span>
        <span id="skins" onclick="changePreferenceTab(this);">
            ${skinLabel}
        </span>
        <span id="signs" onclick="changePreferenceTab(this);">
            ${signLabel}
        </span>
        <span id="tencent" onclick="changePreferenceTab(this);">
            ${tencentLabel}
        </span>
        <!--
        <span id="syncGoogle" onclick="changePreferenceTab(this);">
            ${googleLabel}
        </span>
        -->
    </div>
    <div class="tabMain">
        <div id="preferencesPanel">
            <table class="form subTable" width="99%" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <td colspan="2" align="right">
                            <button onclick="changePreference();">${updateLabel}</button>
                        </td>
                    </tr>
                    <tr>
                        <th width="234px">
                            ${blogTitle1Label}
                        </th>
                        <td>
                            <input id="blogTitle"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${blogSubtitle1Label}
                        </th>
                        <td>
                            <input id="blogSubtitle"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${blogHost1Label}
                        </th>
                        <td>
                            <input id="blogHost"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${metaKeywords1Label}
                        </th>
                        <th>
                            <input id="metaKeywords" />
                        </th>
                    </tr>
                    <tr>
                        <th>
                            ${metaDescription1Label}
                        </th>
                        <th>
                            <input id="metaDescription" />
                        </th>
                    </tr>
                    <tr>
                        <th>
                            ${noticeBoard1Label}
                        </th>
                        <th>
                            <textarea rows="9" id="noticeBoard"></textarea>
                        </th>
                    </tr>
                    <tr>
                        <th>
                            ${htmlhead1Label}
                        </th>
                        <th>
                            <textarea rows="9" id="htmlHead"></textarea>
                        </th>
                    </tr>
                </tbody>
            </table>
            <table class="form subTable" width="99%" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th width="234px">
                            ${localeString1Label}
                        </th>
                        <td>
                            <select id="localeString">
                                <option value="zh_CN">简体中文</option>
                                <option value="en_US">Englisth(US)</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th width="234px">
                            ${timeZoneId1Label}
                        </th>
                        <td>
                            <select id="timeZoneId">
                                ${timeZoneIdOptions}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexTagDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="mostUsedTagDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexRecentCommentDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="recentCommentDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexMostCommentArticleDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="mostCommentArticleDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexMostViewArticleDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="mostViewArticleDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${pageSize1Label}
                        </th>
                        <td>
                            <input id="articleListDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${windowSize1Label}
                        </th>
                        <td>
                            <input id="articleListPaginationWindowSize" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${randomArticlesDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="randomArticlesDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${relevantArticlesDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="relevantArticlesDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${externalRelevantArticlesDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="externalRelevantArticlesDisplayCount" class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${enableArticleUpdateHint1Label}
                        </th>
                        <td>
                            <input id="enableArticleUpdateHint" type="checkbox"
                                   class="normalInput"/>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <button onclick="changePreference();">${updateLabel}</button>
                        </th>
                    </tr>
                </tbody>
            </table>
        </div>
        <div id="skinsPanel" class="none">
            <button onclick="changePreference();" class="right">${updateLabel}</button>
            <div class="clear"></div>
            <div id="skinMain">
            </div>
            <button onclick="changePreference();" class="right">${updateLabel}</button>
            <div class="clear"></div>
        </div>
        <div id="signsPanel" class="none">
            <table class="form subTable" width="99%" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th colspan="2">
                            <button onclick="changePreference();" class="right">${updateLabel}</button>
                        </th>
                    </tr>
                    <tr>
                        <th valign="top" width="80">
                            ${signLabel}1:
                        </th>
                        <td>
                            <textarea rows="8" id="preferenceSign1"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th valign="top">
                            ${signLabel}2:
                        </th>
                        <td>
                            <textarea rows="8" id="preferenceSign2"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th valign="top">
                            ${signLabel}3:
                        </th>
                        <td>
                            <textarea rows="8" id="preferenceSign3"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <button onclick="changePreference();" class="right">${updateLabel}</button>
                        </th>
                    </tr>
                </tbody>
            </table>
        </div>
        <!--        <div id="syncGooglePanel" class="none">
                    <table class="form" width="99%" cellpadding="0" cellspacing="9px">
                        <tbody>
                            <tr>
                                <th width="260">
                                    ${OAuthConsumerSecret1Label}
                                </th>
                                <td>
                                    <input id="secret"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    ${authorizeGoogleBuzz1Label}
                                </th>
                                <td>
                                    <img class="pointer" src="images/buzz.png"
                                         onclick="oauthBuzz();" alt="${authorizeGoogleBuzz1Label}"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    ${postToBuzzWhilePublishArticleLabel}
                                </th>
                                <td align="left">
                                    <input type="checkbox" class="normalInput" id="syncBuzz"/>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="right">
                                    <button onclick="changePreference();">${saveLabel}</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>-->
        <div id="tencentPanel" class="none">
            <table class="form" width="99%" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th width="260">
                            ${appKey1Label}
                        </th>
                        <td>
                            <input id="tencentMicroblogAppKey"/>
                        </td>
                    </tr>
                    <tr>
                        <th width="260">
                            ${appSecret1Label}
                        </th>
                        <td>
                            <input id="tencentMicroblogAppSecret"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${authorizeTencentMicroblog1Label}
                        </th>
                        <td>
                            <img class="pointer" src="images/tencent-microblog.png"
                                 onclick="oauthTencentMicroblog();" alt="${authorizeTencentMicroblog1Label}"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${postToTencentMicroblogWhilePublishArticleLabel}
                        </th>
                        <td align="left">
                            <input type="checkbox" class="normalInput" id="postToTencentMicroblog"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" align="right">
                            <button onclick="changePreference();">${saveLabel}</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var localeString = "";
    var getPreference = function () {
        $("#loadMsg").text("${loadingLabel}");
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
                        preference.enableArticleUpdateHint ? $("#enableArticleUpdateHint").attr("checked", "checked") : $("#enableArticleUpdateHint").removeAttr("checked");
                        preference.enablePostToBuzz ? $("#syncBuzz").attr("checked", "checked") : $("#syncBuzz").removeAttr("checked");

                        // Tencent micro blog settings
                        preference.enablePostToTencentMicroblog ? $("#postToTencentMicroblog").attr("checked", "checked") : $("#postToTencentMicroblog").removeAttr("checked");
                        $("#tencentMicroblogAppKey").val(preference.tencentMicroblogAppKey);
                        $("#tencentMicroblogAppSecret").val(preference.tencentMicroblogAppSecret);

                        localeString = preference.localeString;

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
            } catch (e) {}
        });
    }
    
    getPreference();
    
    var changePreferenceTab = function (it) {
        var tabs = ['preferences', 'skins', 'signs', 'syncGoogle', 'tencent'];
        for (var i = 0; i < tabs.length; i++) {
            if (it.id === tabs[i]) {
                $("#" + tabs[i] + "Panel").show();
                $("#" + tabs[i]).addClass("selected");
            } else {
                $("#" + tabs[i] + "Panel").hide();
                $("#" + tabs[i]).removeClass("selected");
            }
        }
    }
    
    var changePreference = function () {
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        if ($("#syncGoogle").hasClass("selected")) {
            if ("" === $("#secret").val().replace(/\s/g, "")) {
                $("#tipMsg").text("${contentEmptyLabel}");
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
                "enablePostToTencentMicroblog": $("#postToTencentMicroblog").attr("checked")
            }
        }

        jsonRpc.preferenceService.updatePreference(function (result, error) {
            try {
                switch (result.sc) {
                    case "UPDATE_PREFERENCE_SUCC":
                        $("#tipMsg").text("${updateSuccLabel}");
                        if ($("#localeString").val() !== localeString) {
                            window.location.reload();
                        }

                        // update article signs
                        for (var i = 1; i < signs.length; i++) {
                            $("#articleSign" + signs[i].oId).tip("option", "content", signs[i].signHTML);
                        }
                        break;
                    case "UPDATE_PREFERENCE_FAIL_":
                        $("#tipMsg").text("${updatePreferenceFailLabel}");
                        break;
                    case "UPDATE_PREFERENCE_FAIL_CANNT_BE_LOCALHOST":
                        $("#tipMsg").text("${canntBeLocalhostOnProductionLabel}");
                        break;
                    case "UPDATE_PREFERENCE_FAIL_NEED_MUL_USERS":
                        $("#tipMsg").text("${updatePreferenceFailNeedMulUsersLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, requestJSONObject);
    }

    var oauthBuzz = function () {
        if ("" === $("#secret").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${contentEmptyLabel}");
            return;
        }
        $("#loadMsg").text("${loadingLabel}");
        window.location = "buzz-oauth.do?googleOAuthConsumerSecret=" + encodeURIComponent($("#secret").val());
    }

    var oauthTencentMicroblog = function () {
        if ("" === $("#tencentMicroblogAppKey").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${contentEmptyLabel}");
            return;
        }

        if ("" === $("#tencentMicroblogAppSecret").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${contentEmptyLabel}");
            return;
        }

        $("#loadMsg").text("${loadingLabel}");
        window.location = "tencent-microblog-oauth-authorize-token.do?appKey="
            + $("#tencentMicroblogAppKey").val()
            + "&appSecret=" + $("#tencentMicroblogAppSecret").val();

    }
</script>