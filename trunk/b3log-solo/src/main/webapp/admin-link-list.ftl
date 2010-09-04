<div id="linkPanel">
    <div id="linkList">
    </div>
    <div id="linkPagination">
    </div>
    <div id="comments" class="none">
    </div>
    <div class="clear"></div>
    <table class="form" width="100%" cellpadding="0px" cellspacing="9px">
        <thead>
            <tr>
                <th style="text-align: left" colspan="2">
                    ${addLinkLabel}
                </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <th width="48px">
                    ${linkTitle1Label}
                </th>
                <td>
                    <input id="linkTitle"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${url1Label}
                </th>
                <td>
                    <input id="linkAddress"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                    <button onclick="submitLink();">${saveLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
    <div id="updateLink" class="none">
        <table class="form" width="100%" cellpadding="0px" cellspacing="9px">
            <thead>
                <tr>
                    <th style="text-align: left" colspan="2">
                        ${updateLinkLabel}
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <th width="48px">
                        ${linkTitle1Label}
                    </th>
                    <td>
                        <input id="updateLinkTitle"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        ${url1Label}
                    </th>
                    <td>
                        <input id="updateLinkAddress"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="right">
                        <button onclick="updateLink();">${updateLabel}</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<script type="text/javascript">
    var currentPage = 1,
    pageCount = 1,
    linksLength = 1;
    $("#linkList").table({
        resizable: true,
        colModel: [{
                style: "padding-left: 6px;",
                name: "${linkTitleLabel}",
                index: "linkTitle",
                width: 130
            }, {
                style: "padding-left: 6px;",
                name: "${urlLabel}",
                index: "linkAddress",
                minWidth: 180
            }, {
                textAlign: "center",
                name: "${updateLabel}",
                index: "update",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'popUpdateLink'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            }, {
                textAlign: "center",
                name: "${removeLabel}",
                index: "deleted",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'deleteLink'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            }, {
                visible: false,
                index: "id"
            }]
    });

    $("#linkPagination").paginate({
        bindEvent: "getLinkList",
        pageCount: 10,
        windowSize: 5,
        currentPage: 1,
        style: "google",
        isGoTo: false
    });

    var validateUpdateLink = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        if ($("#updateLinkTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}").show();
            $("#updateLinkTitle").focus().val("");
        } else if ($("#updateLinkAddress").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${addressEmptyLabel}").show();
            $("#updateLinkAddress").focus().val("");
        } else {
            return true;
        }
        return false;
    }

    var validateLink = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        if ($("#linkTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}").show();
            $("#linkTitle").focus().val("");
        } else if ($("#linkAddress").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${addressEmptyLabel}").show();
            $("#linkAddress").focus().val("");
        } else {
            return true;
        }
        return false;
    }

    var popUpdateLink = function (event) {
        $("#updateLink").dialog({
            width: 700,
            height:200
        });
        var requestJSONObject = {
            "oId": event.data.id[0]
        };

        var result = jsonRpc.linkService.getLink(requestJSONObject);
        switch (result.sc) {
            case "GET_LINK_SUCC":
                $("#updateLinkTitle").val(result.link.linkTitle).data('oId', event.data.id[0]);
                $("#updateLinkAddress").val(result.link.linkAddress);
                $("#tipMsg").text("${updateSuccLabel}").show();
                break;
            case "GET_LINK_FAIL_":
                break;
            default:
                break;
        }
    }

    var deleteLink = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");

        if (isDelete) {
            $("#tipMsg").text("${loadingLabel}").show();
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            var result = jsonRpc.linkService.removeLink(requestJSONObject);
            switch (result.sc) {
                case "REMOVE_LINK_SUCC":
                    $("#tipMsg").text("${removeSuccLabel}").show();
                    getLinkList(1);
                    break;
                case "REMOVE_LINK_FAIL_":
                    $("#tipMsg").text("${removeFailLabel}").show();
                    break;
                default:
                    break;
            }
        }
    }

    var getLinkList = function (pageNum) {
        $("#tipMsg").text("${loadingLabel}").show();
        currentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        var result = jsonRpc.linkService.getLinks(requestJSONObject);
        switch (result.sc) {
            case "GET_LINKS_SUCC":
                var links = result.links;
                var linkData = [];
                linksLength = links.length;

                for (var i = 0; i < links.length; i++) {
                    linkData[i] = {};
                    linkData[i].linkTitle = links[i].linkTitle;
                    linkData[i].linkAddress = links[i].linkAddress;
                    linkData[i].update = "<div class='updateIcon'></div>";
                    linkData[i].deleted = "<div class='deleteIcon'></div>";
                    linkData[i].id = links[i].oId;
                }

                $("#linkList").table({
                    update:{
                        data: linkData
                    }
                });

                if (result.pagination.paginationPageCount === 0) {
                    pageCount = 1;
                } else {
                    pageCount = result.pagination.paginationPageCount;
                }
                    
                $("#linkPagination").paginate({
                    update: {
                        currentPage: pageNum,
                        pageCount: pageCount
                    }
                });
               
                break;
            default:
                break;
        }
        $("#tipMsg").text("").hide();
    }
    getLinkList(1);

    var updateLink = function () {
        if (validateUpdateLink()) {
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#updateLinkTitle").val(),
                    "oId": $("#updateLinkTitle").data("oId"),
                    "linkAddress": $("#updateLinkAddress").val()
                }
            };
            var result = jsonRpc.linkService.updateLink(requestJSONObject);
            switch (result.sc) {
                case "UPDATE_LINK_SUCC":
                    $("#updateLink").dialog("close");
                    $("#tipMsg").text("${updateSuccLabel}").show();
                    getLinkList(currentPage);
                    break;
                default:
                    break;
            }
        }
    }

    var submitLink = function () {
        if (validateLink()) {
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitle").val(),
                    "linkAddress": $("#linkAddress").val()
                }
            };
            var result = jsonRpc.linkService.addLink(requestJSONObject);
            switch (result.sc) {
                case "ADD_LINK_SUCC":
                    $("#linkTitle").val("");
                    $("#linkAddress").val("");
                    if (linksLength === PAGE_SIZE) {
                        pageCount++;
                    }

                    getLinkList(pageCount);
                    break;
                default:
                    break;
            }
        }
    }
</script>
