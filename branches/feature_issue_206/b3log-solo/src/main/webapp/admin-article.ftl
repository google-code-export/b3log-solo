<table width="100%" cellpadding="0" cellspacing="9px" class="form">
    <tr>
        <th width="123">
            ${title1Label}
        </th>
        <td>
            <input id="title" type="text"/>
        </td>
    </tr>
    <tr>
        <th>
            ${content1Label}
        </th>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td colspan="2">
            <textarea id="articleContent" name="articleContent"
                      style="height: 500px;width:100%;"></textarea>
        </td>
    </tr>
    <tr>
        <th>
            ${tags1Label}
        </th>
        <td>
            <input id="tag" type="text"/>
        </td>
    </tr>
    <tr>
        <th valign="top">
            ${abstract1Label}
        </th>
        <td>
            <textarea id="abstract" style="height: 200px;width: 100%;" name="abstract"></textarea>
        </td>
    </tr>
    <tr>
        <th>
            ${permalink1Label}
        </th>
        <td>
            <input id="permalink" type="text"/>
        </td>
    </tr>
    <tr>
        <th>
            ${sign1Label}
        </th>
        <td class="signs">
            <button style="margin-left: 0px;" id="articleSign1">${signLabel} 1</button>
            <button id="articleSign2">${signLabel} 2</button>
            <button id="articleSign3">${signLabel} 3</button>
            <button id="articleSign0">${noSignLabel}</button>
        </td>
    </tr>
    <tr id="postToCommunityTR">
        <th>
            ${postToCommunityLabel}
        </th> 
        <td>
            <input id="postToCommunity" type="checkbox" checked="checked"/>
        </td>
    </tr>
    <tr>
        <th colspan="2">
            <button class="marginRight12" id="saveArticle">${saveLabel}</button>
            <button id="submitArticle">${publishLabel}</button>
            <button id="unSubmitArticle" class="none" onclick="admin.article.unPublish();">${unPublishLabel}</button>
        </th>
    </tr>
</table>
${plugins}
