<div id="tabPreference">
    <ul>
        <li>
            <div id="tab_preferences">
                <a class="tab-current" href="#preference/preferences">${paramSettingsLabel}</a>
            </div>
        </li>
        <li>
            <div id="tab_skins">
                <a href="#preference/skins">${skinLabel}</a>
            </div>
        </li>
        <li>
            <div id="tab_signs">
                <a href="#preference/signs">${signLabel}</a>
            </div>
        </li>
        <li>
            <div id="tab_tencent">
                <a href="#preference/tencent">${tencentLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabPreferencePanel">
    <div id="tabs_preferences">
        <table class="form subTable" width="98%" cellpadding="0" cellspacing="9px">
            <tbody>
                <tr>
                    <td colspan="2" align="right">
                        <button onclick="admin.preference.update()">${updateLabel}</button>
                    </td>
                </tr>
                <tr>
                    <th width="234px">
                        ${blogTitle1Label}
                    </th>
                    <td>
                        <input id="blogTitle" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${blogSubtitle1Label}
                    </th>
                    <td>
                        <input id="blogSubtitle" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${blogHost1Label}
                    </th>
                    <td>
                        <input id="blogHost" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${metaKeywords1Label}
                    </th>
                    <td>
                        <input id="metaKeywords" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${metaDescription1Label}
                    </th>
                    <td>
                        <input id="metaDescription" type="text" />
                    </td>
                </tr>
                <tr>
                    <th>
                        ${noticeBoard1Label}
                    </th>
                    <td>
                        <textarea rows="9" id="noticeBoard"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${htmlhead1Label}
                    </th>
                    <td>
                        <textarea rows="9" id="htmlHead"></textarea>
                    </td>
                </tr>
            </tbody>
        </table>
        <table class="form subTable" width="98%" cellpadding="0" cellspacing="9px">
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
                        <label for="enableArticleUpdateHint">${enableArticleUpdateHint1Label}</label>
                    </th>
                    <td>
                        <input id="enableArticleUpdateHint" type="checkbox"
                               class="normalInput"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${keyOfSolo1Label}
                    </th>
                    <td>
                        <input id="keyOfSolo" class="normalInput"/>
                    </td>
                </tr>
                <tr>
                    <th colspan="2">
                        <button onclick="admin.preference.update()">${updateLabel}</button>
                    </th>
                </tr>
            </tbody>
        </table>
    </div>
    <div id="tabs_skins" class="none">
        <button onclick="admin.preference.update()" class="right">${updateLabel}</button>
        <div class="clear"></div>
        <div id="skinMain">
        </div>
        <button onclick="admin.preference.update()" class="right">${updateLabel}</button>
        <div class="clear"></div>
    </div>
    <div id="tabs_signs" class="none">
        <table class="form subTable" width="98%" cellpadding="0" cellspacing="9px">
            <tbody>
                <tr>
                    <th colspan="2">
                        <button onclick="admin.preference.update()" class="right">${updateLabel}</button>
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
                        <button onclick="admin.preference.update()" class="right">${updateLabel}</button>
                    </th>
                </tr>
            </tbody>
        </table>
    </div>
    <!--        <div id="preferences_syncGoogle" class="none">
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
                                <button onclick="admin.preference.update()">${saveLabel}</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>-->
    <div id="tabs_tencent" class="none">
        <table class="form" width="98%" cellpadding="0" cellspacing="9px">
            <tbody>
                <tr>
                    <th width="160">
                        ${appKey1Label}
                    </th>
                    <td colspan="3">
                        <input id="tencentMicroblogAppKey" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${appSecret1Label}
                    </th>
                    <td colspan="3">
                        <input id="tencentMicroblogAppSecret" type="text"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${authorizeTencentMicroblog1Label}
                    </th>
                    <td width="20%">
                        <img class="pointer" src="images/tencent-microblog.png"
                             onclick="admin.preference.oauthTencent();" alt="${authorizeTencentMicroblog1Label}"/>
                    </td>
                    <th width="20%">
                        <label for="postToTencentMicroblog">${postToTencentMicroblogWhilePublishArticleLabel}</label>
                    </th>
                    <td>
                        <input id="postToTencentMicroblog" type="checkbox" class="normalInput"/>
                    </td>
                </tr>
                <tr>
                    <th colspan="4">
                        <button onclick="admin.preference.update()">${saveLabel}</button>
                    </th>
                </tr>
            </tbody>
        </table>
    </div>
</div>
${plugins}
