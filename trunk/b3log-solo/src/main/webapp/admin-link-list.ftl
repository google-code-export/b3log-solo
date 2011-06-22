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
                <input id="linkTitle" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                ${url1Label}
            </th>
            <td>
                <input id="linkAddress" type="text"/>
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
                    <input id="linkTitleUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${url1Label}
                </th>
                <td>
                    <input id="linkAddressUpdate" type="text"/>
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
    linkListLength = 1;
    
    var saveLinkOrder = function (id, order, status) {
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        var srcOrder = order;
        if (status === "up") {
            srcOrder -= 1;
        } else {
            srcOrder += 1;
        }

        jsonRpc.linkService.changeOrder(function (result, error) {
            try {
                if (result) {
                    getLinkList(linkListCurrentPage);
                } else {
                    $("#tipMsg").text("${updateFailLabel}");
                }
                $("#loadMsg").text("");
            } catch (e) {}
        }, id.toString(), srcOrder);
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
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE
        };
        jsonRpc.linkService.getLinks(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_LINKS_SUCC":
                        var links = result.links;
                        var linkData = [];
                        linksLength = links.length;

                        for (var i = 0; i < links.length; i++) {
                            linkData[i] = {};
                            if (i === 0) {
                                if (links.length === 1) {
                                    linkData[i].linkOrder = "";
                                } else {
                                    linkData[i].linkOrder = '<div class="table-center" style="width:16px">\
                                <span onclick="saveLinkOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                                }
                            } else if (i === links.length - 1) {
                                linkData[i].linkOrder = '<div class="table-center" style="width:16px">\
                                <span onclick="saveLinkOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                            </div>';
                            } else {
                                linkData[i].linkOrder = '<div class="table-center" style="width:38px">\
                                <span onclick="saveLinkOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                <span onclick="saveLinkOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                            }
                            linkData[i].linkTitle = links[i].linkTitle;
                            linkData[i].linkAddress = "<a target='_blank' class='no-underline' href='" + links[i].linkAddress + "'>"
                                + links[i].linkAddress + "</a>";
                            linkData[i].update = "<div class='updateIcon'></div>";
                            linkData[i].deleted = "<div class='deleteIcon'></div>";
                            linkData[i].id = links[i].oId;
                        }

                        $("#linkList").table("update",{
                            data: [{
                                    groupName: "all",
                                    groupData: linkData
                                }]
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
            } catch (e) {}
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
                try {
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
                } catch (e) {}
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
                try {
                    switch (result.sc) {
                        case "ADD_LINK_SUCC":
                            $("#linkTitle").val("");
                            $("#linkAddress").val("");
                            if (linkListLength === adminUtil.PAGE_SIZE &&
                                linkListCurrentPage === linkListPageCount) {
                                linkListPageCount++;
                            }
                            getLinkList(linkListPageCount);
                            $("#tipMsg").text("${addSuccLabel}");
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    }

    var initLink = function () {
        $("#linkList").table({
            colModel: [{
                    text: "",
                    index: "linkOrder",
                    width: 60
                },{
                    style: "padding-left: 6px;",
                    text: "${linkTitleLabel}",
                    index: "linkTitle",
                    width: 230
                }, {
                    style: "padding-left: 6px;",
                    text: "${urlLabel}",
                    index: "linkAddress",
                    minWidth: 180
                }, {
                    textAlign: "center",
                    text: "${updateLabel}",
                    index: "update",
                    width: 56,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                $("#loadMsg").text("${loadingLabel}");
                                $("#updateLink").dialog("open");
                                var requestJSONObject = {
                                    "oId": data.id
                                };

                                jsonRpc.linkService.getLink(function (result, error) {
                                    try {
                                        switch (result.sc) {
                                            case "GET_LINK_SUCC":
                                                $("#linkTitleUpdate").val(result.link.linkTitle).data('oId', data.id);
                                                $("#linkAddressUpdate").val(result.link.linkAddress);
                                                break;
                                            case "GET_LINK_FAIL_":
                                                break;
                                            default:
                                                break;
                                        }
                                        $("#loadMsg").text("");
                                    } catch (e) {}
                                }, requestJSONObject);
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    text: "${removeLabel}",
                    index: "deleted",
                    width: 56,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                var isDelete = confirm("${confirmRemoveLabel}");
                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
                                    var requestJSONObject = {
                                        "oId": data.id
                                    };

                                    jsonRpc.linkService.removeLink(function (result, error) {
                                        try {
                                            switch (result.sc) {
                                                case "REMOVE_LINK_SUCC":
                                                    var pageNum = linkListCurrentPage;
                                                    if (linkListLength === 1 && linkListPageCount !== 1 &&
                                                        linkListCurrentPage === linkListPageCount) {
                                                        linkListPageCount--;
                                                        pageNum = linkListPageCount;
                                                    }
                                                    getLinkList(pageNum);
                                                    $("#tipMsg").text("${removeSuccLabel}");
                                                    break;
                                                case "REMOVE_LINK_FAIL_":
                                                    $("#tipMsg").text("${removeFailLabel}");
                                                    break;
                                                default:
                                                    break;
                                            }
                                            $("#loadMsg").text("");
                                        } catch (e) {}
                                    }, requestJSONObject);
                                }
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }]
        });

        $("#linkPagination").paginate({
            bindEvent: "getLinkList",
            pageCount: 1,
            windowSize: adminUtil.WINDOW_SIZE,
            currentPage: 1,
            style: "google",
            isGoTo: false,
            lastPage: "${lastPageLabel}",
            nextPage: "${nextPagePabel}",
            previousPage: "${previousPageLabel}",
            firstPage: "${firstPageLabel}"
        });

        getLinkList(1);
        $("#updateLink").dialog({
            width: 700,
            height: 160,
            "modal": true,
            "hideFooter": true
        });
    }
    initLink();
</script>
${plugins}
