<div class="table-main">
    <div class="table-header">
        <table cellspacing="0" cellpadding="0" style="width:100%">
            <tbody>
                <tr>
                    <th>
                        ${titleLabel}
                    </th>
                    <th>
                        ${typeLabel}
                    </th>
                    <th>
                        ${cacheStatusLabel}
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
                    <td>
                        <a target="_blank" href="${page.link?substring(5)}">${page.cachedTitle}</a>
                    </td>
                    <td>
                        ${page.cachedType}
                    </td>
                    <td>
                        <button onclick="changeCacheStatus(this);">${openLabel}</button>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript">    
    var changeCacheStatus = function (it) {
       
    }
</script>