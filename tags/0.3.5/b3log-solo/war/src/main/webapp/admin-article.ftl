<div class="form">
    <div>
        <label>${title1Label}</label>
        <input id="title" type="text"/>
    </div>
    <div>
        <textarea id="articleContent" name="articleContent"
                  style="height: 500px;width:100%;"></textarea>
    </div>
    <div>
        <label>${tags1WithTips1Label}</label>
        <input id="tag" type="text"/>
    </div>
    <div>
        <textarea id="abstract" style="height: 200px;width: 100%;" name="abstract"></textarea>
    </div>
    <div>
        <label>${permalink1Label}</label>
        <input id="permalink" type="text"/>
    </div>
    <div>
        <span class="signs">
            <label>${sign1Label}</label>
            <button style="margin-left: 0px;" id="articleSign1">${signLabel} 1</button>
            <button id="articleSign2">${signLabel} 2</button>
            <button id="articleSign3">${signLabel} 3</button>
            <button id="articleSign0">${noSignLabel}</button>
        </span>
        <div class="right">
            <label for="postToCommunity">${postToCommunityLabel}</label>
            <input id="postToCommunity" type="checkbox" checked="checked"/>
        </div>
        <div class="clear"></div>
    </div>
    <div class="right">
        <button class="marginRight12" id="saveArticle">${saveLabel}</button>
        <button id="submitArticle">${publishLabel}</button>
        <button id="unSubmitArticle" class="none" onclick="admin.article.unPublish();">${unPublishLabel}</button>
    </div>
    <div class="clear"></div>
</div>
${plugins}