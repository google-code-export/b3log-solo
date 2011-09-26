<div>
    <div id="pageTable">
    </div>
    <div id="pagePagination" class="margin12 right">
    </div>
    <div class="clear"></div>
</div>
<div class="form">
    <div>
        <label>${title1Label}</label>

        <input id="pageTitle" type="text"/>
    </div>
    <div>
        <label>${content1Label}</label>
        <textarea id="pageContent" style="height: 430px;width: 100%;" name="pageContent"></textarea>
    </div>
    <div>
        <label>${permalink1Label}</label>
        <input id="pagePermalink" type="text"/>
    </div>
    <div class="right">
        <button onclick="admin.pageList.submit();">${saveLabel}</button>
    </div>
    <div class="clear"></div>
</div>
<div id="pageComments" class="none"></div>
<div class="clear"></div>
${plugins}
