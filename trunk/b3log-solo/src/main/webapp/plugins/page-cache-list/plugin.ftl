<div class="table-main">
    <div class="table-header">
        <table cellspacing="0" cellpadding="0" style="width:100%">
            <tbody>
                <tr>
                    <th width="120">
                        ${typeLabel}
                    </th>
                    <th>
                        ${titleLabel}
                    </th>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="table-body">
        <table cellspacing="0" cellpadding="0" style="width:100%">
            <tbody>
                <#list pages as page>
                <tr class="table-oddRow">
                    <td style="text-align: center;width: 120px">
                        ${page.cachedType}
                    </td>
                    <td style="padding-left: 6px;">
                        <a target="_blank" href="${page.link?substring(5)}">${page.cachedTitle}</a>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
    </div>
</div>
