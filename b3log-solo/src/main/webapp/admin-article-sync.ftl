<div class="tabPanel">
    <div id="tabsarticlesync">
        <ul>
            <li>
                <div data-index="syncSetting">
                    <a href="#article-sync/syncSetting">${blogSyncMgmtLabel}</a>
                </div>
            </li>
            <li>
                <div data-index="sync">
                    <a href="#article-sync/sync">${blogArticleImportLabel}</a>
                </div>
            </li>
        </ul>
        <div id="syncBlogType">
            ${chooseBlog1Label}
            <select id="blogType" onchange="changeBlogType();">
                <option value="">&nbsp;</option>
                <option value="blogSyncCSDNBlog">${CSDNBlogLabel}</option>
                <option value="blogSyncBlogJava">${BlogJavaLabel}</option>
                <option value="blogSyncCnBlogs">${CnBlogsLabel}</option>
            </select>
            <span class="error-msg" id="blogSyncTip">
                ${blogEmptyLabel}
            </span>
        </div>
    </div>
    <div class="tabMain" id="tabsarticlesyncContent">
        <div id="tabsarticlesync_syncSetting">
            <fieldset>
                <legend>
                    ${syncMgmtLabel}
                </legend>
                <table class="form" cellpadding="12px" cellspacing="12px;" width="100%">
                    <tbody>
                        <tr>
                            <th width="58px">
                                ${userName1Label}
                            </th>
                            <td colspan="5">
                                <input id="magName" type="text"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                ${userPassword1Label}
                            </th>
                            <td colspan="5">
                                <input type="password" id="magPassword"/>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <input type="checkbox" id="addSync" class="normalInput"/>
                            </th>
                            <td>
                                <label for="addSync">
                                    <b>${syncPostLabel}</b>
                                </label>
                            </td>
                            <th>
                                <input type="checkbox" id="updateSync"/>
                            </th>
                            <td>
                                <label for="updateSync">
                                    <b>${syncUpdateLabel}</b>
                                </label>
                            </td>
                            <th>
                                <input type="checkbox" id="deleteSync"/>
                            </th>
                            <td>
                                <label for="deleteSync">
                                    <b>${syncRemoveLabel}</b>
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <th colspan="6">
                                <button onclick="syncSetting();">${updateLabel}</button>
                            </th>
                        </tr>
                    </tbody>
                </table>
            </fieldset>
        </div>
        <div id="tabsarticlesync_sync">
            <table id="archiveDatePanel" class="form none" cellpadding="0" cellspacing="9px">
                <tbody>
                    <tr>
                        <td>
                            ${selectDate1Label}
                        </td>
                        <td>
                            <select id="archiveDate">
                                <option>${selectDateLabel}</option>
                            </select>
                        </td>
                        <td>
                            <button onclick="getBlogArticlesByArchiveDate();">
                                ${getArticleLabel}
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
            <button id="getDateButton" class="none" onclick="getBlogArticleArchiveDate();">${getDateLabel}</button>
            &nbsp;
            <div id="articlesPanel" class="none">
                <button onclick="sync();">${importLabel}</button>
                <span id="articlesCount" class="red"></span>
                <div class="clear"></div>
                <div id="articleSyncList" class="paddingTop12 paddingBottom12"></div>
                <button onclick="sync();">${importLabel}</button>
            </div>
        </div>
    </div>
</div>
${plugins}
