<div class="side-tool">
    <ul>
        <li>
            Page
            <ul>
                <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}">
                        ${page.pageTitle}
                    </a>
                </li>
                </#list>
            </ul>
        </li>
        <li id="changeBG">
            <a title="grey" id="greyBG" class="selected"></a>
            <a title="brown" id="brownBG"></a>
            <a title="blue" id="blueBG"></a>
        </li>
        <li id="goTop">
            <span onclick="util.goTop();">${goTopLabel}</span>
        </li>
    </ul>
</div>