<div>
    <div id="pageTable">
    </div>
    <div id="pagePagination" class="margin12 right">
    </div>
    <div class="clear"></div>
</div>
<table class="form" width="100%" cellpadding="0px" cellspacing="9px">
    <tbody>
        <tr>
            <th width="48px">
                ${title1Label}
            </th>
            <td>
                <input id="pageTitle" type="text"/>
            </td>
        </tr>
        <tr>
            <th valign="top">
                ${content1Label}
            </th>
            <td>
                <textarea id="pageContent" style="height: 430px;width: 100%;" name="pageContent"></textarea>
            </td>
        </tr>
        <tr>
            <th>
                ${permalink1Label}
            </th>
            <td>
                <input id="pagePermalink" type="text"/>
            </td>
        </tr>
        <tr>
            <th colspan="2" align="right">
                <button onclick="admin.pageList.submit();">${saveLabel}</button>
            </th>
        </tr>
    </tbody>
</table>
<div id="pageComments" class="none"></div>
<div class="clear"></div>
${plugins}
