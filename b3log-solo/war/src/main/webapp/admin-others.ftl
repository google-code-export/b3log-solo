<div id="tabOthers" class="sub-tabs">
    <ul>
        <li>
            <div id="tabOthers_email">
                <a class="tab-current" href="#tools/others/email">Email</a>
            </div>
        </li>
        <li>
            <div id="tabOthers_other">
                <a href="#tools/others/other">${othersLabel}</a>
            </div>
        </li>
    </ul>
</div>
<div id="tabOthersPanel" class="sub-tabs-main">
    <div id="tabOthersPanel_email">
        <table class="form" width="98%" cellpadding="0" cellspacing="9px">
            <tbody>
                <tr>
                    <th width="234px" valign="top">
                        <label for="replayEmailTemplateTitle">${replayEmailTemplate1Label}</label>
                    </th>
                    <td>
                        <input id="replayEmailTemplateTitle" type="text" />
                    </td>
                </tr>
                <tr>
                    <th valign="top">
                        <label for="replayEmailTemplateBody">${replayEmailTemplate1Label}</label>
                    </th>
                    <td>
                        <textarea rows="9" id="replayEmailTemplateBody"></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="right">
                        <button onclick="admin.others.update()">${updateLabel}</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div id="tabOthersPanel_other" class="none">
        <button class="margin12" onclick="admin.others.removeUnusedTags();">${removeUnusedTagsLabel}</button>
    </div>
</div>
${plugins}
