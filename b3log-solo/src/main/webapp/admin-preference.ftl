<div>
    <div class="tabs">
        <ul>
            <li>
                <span class="selected" id="preferences" onclick="changeTab(this);">
                    ${paramSettingsLabel}
                </span>
            </li>
            <li>
                <span id="skins" onclick="changeTab(this);">
                    ${skinLabel}
                </span>
            </li>
        </ul>
        <div class="clear"></div>
    </div>
    <div class="tabPanels">
        <div id="preferencesPanel">
            <table class="form" width="99%" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <th width="146px">
                            ${indexTagDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="mostUsedTagDisplayCount"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexRecentCommentDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="recentCommentDisplayCount"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${indexMostCommentArticleDisplayCnt1Label}
                        </th>
                        <td>
                            <input id="mostCommentArticleDisplayCount"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${pageSize1Label}
                        </th>
                        <td>
                            <input id="articleListDisplayCount"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            ${windowSize1Label}
                        </th>
                        <td>
                            <input id="articleListPaginationWindowSize"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
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
                            ${adminGmail1Label}
                        </th>
                        <td>
                            <input id="adminGmail"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
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
                        <th colspan="2">
                            <button onclick="changePreference();">${updateLabel}</button>
                        </th>
                    </tr>
                </tbody>
            </table>
        </div>
        <div id="skinsPanel" class="none">
            <div id="skinMain">
            </div>
            <button onclick="changePreference();">${saveLabel}</button>
        </div>
    </div>
</div>

<div class="clear"></div>
<script type="text/javascript">
    var localeString = "";
    var getPreference = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        var result = jsonRpc.preferenceService.getPreference();
        switch (result.sc) {
            case "GET_PREFERENCE_SUCC":
                var preference = result.preference;
                // preference
                $("#blogTitle").val(preference.blogTitle),
                $("#blogSubtitle").val(preference.blogSubtitle),
                $("#mostCommentArticleDisplayCount").val(preference.mostCommentArticleDisplayCount);
                $("#recentCommentDisplayCount").val(preference.recentCommentDisplayCount);
                $("#mostUsedTagDisplayCount").val(preference.mostUsedTagDisplayCount);
                $("#articleListDisplayCount").val(preference.articleListDisplayCount);
                $("#articleListPaginationWindowSize").val(preference.articleListPaginationWindowSize);
                $("#blogHost").val(preference.blogHost);
                $("#adminGmail").val(preference.adminGmail);
                $("#localeString").val(preference.localeString);
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
                            + skins[i].skinDirName + "/preview.png'/><span>" + skins[i].skinName + "</span></div>"
                    } else {
                        skinsHTML += "<div title='" + skins[i].skinDirName
                            + "' class='left skinItem'><img class='skinPreview' src='skins/"
                            + skins[i].skinDirName + "/preview.png'/><span>" + skins[i].skinName + "</span></div>"
                    }
                }
                $("#skinMain").append(skinsHTML + "<div class='clear'></div>");

                $(".skinItem").click(function () {
                    $(".skinItem").removeClass("selected");
                    $(this).addClass("selected");
                    $("#skinMain").data("skinDirName", this.title);
                });
                break;
            default:
                break;
        }
        $("#tipMsg").text("").hide();
    }
    getPreference();
    
    var changeTab = function (it) {
        var tabs = ['preferences', 'skins'];
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
        $("#tipMsg").text("${loadingLabel}").show();
        var requestJSONObject = {
            "preference": {
                "blogTitle": $("#blogTitle").val(),
                "blogSubtitle": $("#blogSubtitle").val(),
                "mostCommentArticleDisplayCount": $("#mostCommentArticleDisplayCount").val(),
                "recentArticleDisplayCount": 10, // XXX: remove recentArticleDisplayCount
                "recentCommentDisplayCount": $("#recentCommentDisplayCount").val(),
                "mostUsedTagDisplayCount": $("#mostUsedTagDisplayCount").val(),
                "articleListDisplayCount": $("#articleListDisplayCount").val(),
                "articleListPaginationWindowSize": $("#articleListPaginationWindowSize").val(),
                "skinDirName": $("#skinMain").data("skinDirName"),
                "blogHost": $("#blogHost").val(),
                "adminGmail": $("#adminGmail").val(),
                "localeString": $("#localeString").val()
            }
        }

        var result = jsonRpc.preferenceService.updatePreference(requestJSONObject);
        switch (result.sc) {
            case "UPDATE_PREFERENCE_SUCC":
                $("#tipMsg").text("${updateSuccLabel}").show();
                if ($("#localeString").val() !== localeString) {
                    window.location.reload();
                }
                break;
            case "GET_ARTICLE_FAIL_":
                break;
            default:
                break;
        }
    }
</script>
