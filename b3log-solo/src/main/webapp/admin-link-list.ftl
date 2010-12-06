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
                    <input id="linkTitleUpdate"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${url1Label}
                </th>
                <td>
                    <input id="linkAddressUpdate"/>
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
    var linkListCurrentPage = 1,
    linkListPageCount = 1,
    linksLength = 1;
    
    var saveLinkOrder = function (order, status) {
        $("#loadMsg").text("${loadingLabel}");
        var tableData = $("#linkList").table("option", "data"),
        srcOrder = order;
        if (status === "up") {
            srcOrder -= 1;
        } else {
            srcOrder += 1;
        }

jsonRpc.linkService.changeOrder(function (result, error) {
            if (result) {
                var tmp = tableData[order].linkOrder;
                tableData[order].linkOrder = tableData[srcOrder].linkOrder;
                tableData[srcOrder].linkOrder = tmp;
                $("#linkList").table("changeOrder", status, order);
            } else {
                $("#tipMsg").text("${updateFailLabel}");
            }
            $("#loadMsg").text("");
        }, tableData[order].id, tableData[srcOrder].linkOrder);
    }

    var validateLink = function (status) {
        if (!status) {
            status = "";
        }
        if ($("#linkTitle" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#linkTitle" + status).focus().val("");
        } else if ($("#linkAddress" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${addressEmptyLabel}");
            $("#linkAddress" + status).focus().val("");
        } else {
            return true;
        }
        return false;
    }

    var getLinkList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        linkListCurrentPage = pageNum;
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
                        linkData[i].linkAddress = "<a target='_blank' class='noUnderline' href='" + links[i].linkAddress + "'>"
                            + links[i].linkAddress + "</a>";
                        linkData[i].update = "<div class='updateIcon'></div>";
                        linkData[i].deleted = "<div class='deleteIcon'></div>";
                        linkData[i].id = links[i].oId;
                        linkData[i].linkOrder = links[i].linkOrder;
                    }

                    $("#linkList").table("update",{
                        data: linkData
                    });

                    if (result.pagination.paginationPageCount === 0) {
                        linkListPageCount = 1;
                    } else {
                        linkListPageCount = result.pagination.paginationPageCount;
                    }

                    $("#linkPagination").paginate({
                        update: {
                            currentPage: pageNum,
                            pageCount: linkListPageCount
                        }
                    });
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }

    var updateLink = function () {
        if (validateLink("Update")) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitleUpdate").val(),
                    "oId": $("#linkTitleUpdate").data("oId"),
                    "linkAddress": $("#linkAddressUpdate").val()
                }
            };
            jsonRpc.linkService.updateLink(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_LINK_SUCC":
                        $("#updateLink").dialog("close");
                        getLinkList(linkListCurrentPage);
                        $("#tipMsg").text("${updateSuccLabel}");
                        break;
                    case "UPDATE_LINK_FAIL_":
                        $("#updateLink").dialog("close");
                        $("#tipMsg").text("${updateFailLabel}");
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
            $("#tipMsg").text("");
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
                            linkListPageCount++;
                        }
                        getLinkList(linkListPageCount);
                        $("#tipMsg").text("${addSuccLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var initLink = function () {
        $("#linkList").table({
            orderActionName: "saveLinkOrder",
            colModel: [{
                    name: "",
                    inputType: "order",
                    index: "linkOrder",
                    width: 60
                },{
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
                            'action': function (event) {
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
                                            $("#linkTitleUpdate").val(result.link.linkTitle).data('oId', event.data.id[0]);
                                            $("#linkAddressUpdate").val(result.link.linkAddress);
                                            break;
                                        case "GET_LINK_FAIL_":
                                            break;
                                        default:
                                            break;
                                    }
                                    $("#loadMsg").text("");
                                }, requestJSONObject);
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    name: "${removeLabel}",
                    index: "deleted",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                var isDelete = confirm("${confirmRemoveLabel}");
                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
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
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#linkPagination").paginate({
            bindEvent: "getLinkList",
            pageCount: 1,
            windowSize: WINDOW_SIZE,
            currentPage: 1,
            style: "google",
            isGoTo: false,
            lastPage: "${lastPageLabel}",
            nextPage: "${nextPagePabel}",
            previousPage: "${previousPageLabel}",
            firstPage: "${firstPageLabel}"
        });

        getLinkList(1);
    }
    initLink();
</script>
