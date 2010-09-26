<div id="linkList">
</div>
<div id="linkPagination" class="margin12 right">
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
                width: 230
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
        isGoTo: false,
        lastPage: "${lastPageLabel}",
        nextPage: "${nextPagePabel}",
        previousPage: "${previousPageLabel}",
        firstPage: "${firstPageLabel}"
    });

    var validateUpdateLink = function () {
        if ($("#updateLinkTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#updateLinkTitle").focus().val("");
        } else if ($("#updateLinkAddress").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${addressEmptyLabel}");
            $("#updateLinkAddress").focus().val("");
        } else {
            return true;
        }
        return false;
    }

    var validateLink = function () {
        if ($("#linkTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#linkTitle").focus().val("");
        } else if ($("#linkAddress").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${addressEmptyLabel}");
            $("#linkAddress").focus().val("");
        } else {
            return true;
        }
        return false;
    }

    var popUpdateLink = function (event) {
        $("#loadMsg").text("${loadingLabel}");
        $("#updateLink").dialog({
            width: 700,
            height:200
        });
        var requestJSONObject = {
            "oId": event.data.id[0]
        };

        jsonRpc.linkService.getLink(function (result, error) {
            switch (result.sc) {
                case "GET_LINK_SUCC":
                    $("#updateLinkTitle").val(result.link.linkTitle).data('oId', event.data.id[0]);
                    $("#updateLinkAddress").val(result.link.linkAddress);
                    break;
                case "GET_LINK_FAIL_":
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }

    var deleteLink = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            jsonRpc.linkService.removeLink(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_LINK_SUCC":
                        getLinkList(1);
                        $("#tipMsg").text("${removeSuccLabel}");
                        break;
                    case "REMOVE_LINK_FAIL_":
                        $("#tipMsg").text("${removeFailLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var getLinkList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        currentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        jsonRpc.linkService.getLinks(function (result, error) {
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
            $("#loadMsg").text("");
        }, requestJSONObject);
    }
    getLinkList(1);

    var updateLink = function () {
        if (validateUpdateLink()) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#updateLinkTitle").val(),
                    "oId": $("#updateLinkTitle").data("oId"),
                    "linkAddress": $("#updateLinkAddress").val()
                }
            };
            jsonRpc.linkService.updateLink(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_LINK_SUCC":
                        $("#updateLink").dialog("close");
                        getLinkList(currentPage);
                        $("#tipMsg").text("${updateSuccLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var submitLink = function () {
        if (validateLink()) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitle").val(),
                    "linkAddress": $("#linkAddress").val()
                }
            };
            jsonRpc.linkService.addLink(function (result, error) {
                switch (result.sc) {
                    case "ADD_LINK_SUCC":
                        $("#linkTitle").val("");
                        $("#linkAddress").val("");
                        if (linksLength === PAGE_SIZE) {
                            pageCount++;
                        }
                        getLinkList(pageCount);
                        $("#tipMsg").text("${addSuccLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }
</script>
