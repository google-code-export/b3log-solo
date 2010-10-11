<div class="left">
    <div id="pageList">
    </div>
    <div id="pagePagination" class="margin12 right">
    </div>
</div>
<table class="form right" width="76%" cellpadding="0px" cellspacing="9px">
    <tbody>
        <tr>
            <th width="48px">
                ${title1Label}
            </th>
            <td>
                <input id="pageTitle"/>
            </td>
        </tr>
        <tr>
            <th valign="top">
                ${content1Label}
            </th>
            <td>
                <input id="pageContent" style="height: 400px;width: 100%;" name="pageContent"/>
            </td>
        </tr>
        <tr>
            <th>
                ${order1Label}
            </th>
            <td>
                <input id="pageOrder" style="width: 30px;" name="pageOrder"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <button onclick="submitPage();">${saveLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
<div class="clear"></div>
<script type="text/javascript">
    var currentPage = 1,
    pageCount = 1,
    pagesLength = 1;
    
    var getPageList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        currentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        jsonRpc.pageService.getPages(function (result, error) {
            switch (result.sc) {
                case "GET_PAGES_SUCC":
                    var pages = result.pages;
                    var pageData = [];
                    pagesLength = pages.length;

                    for (var i = 0; i < pages.length; i++) {
                        pageData[i] = {};
                        pageData[i].pageTitle = pages[i].pageTitle;
                        pageData[i].update = "<div class='updateIcon'></div>";
                        pageData[i].deleted = "<div class='deleteIcon'></div>";
                        pageData[i].id = pages[i].oId;
                    }

                    $("#pageList").table({
                        update:{
                            data: pageData
                        }
                    });

                    if (result.pagination.paginationPageCount === 0) {
                        pageCount = 1;
                    } else {
                        pageCount = result.pagination.paginationPageCount;
                    }

                    $("#pagePagination").paginate({
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

    var initPage = function () {
        $("#pageList").table({
            resizable: true,
            colModel: [{
                    style: "padding-left: 6px;",
                    name: "${titleLabel}",
                    index: "pageTitle",
                    width: 124
                }, {
                    textAlign: "center",
                    name: "${updateLabel}",
                    index: "update",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'functionName': 'getUpdatePage'
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    name: "${removeLabel}",
                    index: "deleted",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'functionName': 'deletePage'
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#pagePagination").paginate({
            bindEvent: "getPageList",
            pageCount: 1,
            windowSize: 1,
            currentPage: 1,
            style: "google",
            isGoTo: false,
            lastPage: "${lastPageLabel}",
            nextPage: "${nextPagePabel}",
            previousPage: "${previousPageLabel}",
            firstPage: "${firstPageLabel}"
        });

        // editor
        tinyMCE.init({
            // General options
            mode : "exact",
            elements : "pageContent",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class]",

            relative_urls: false,
            remove_script_host: false
        });

        getPageList(1);
        $("#loadMsg").text("");
    }

    initPage();

    var validatePage = function () {
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#pageTitle").focus();
        } else if (tinyMCE.get('pageContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${contentEmptyLabel}");
        } else if ("" === $("#pageOrder").val().replace(/\s/g, "")) {
            $("#tipMsg").text("${orderEmptyLabel}");
            $("#pageOrder").focus();
        } else {
            return true;
        }
        return false;
    }

    var getUpdatePage = function (event) {
        $("#loadMsg").text("${loadingLabel}");
        var requestJSONObject = {
            "oId": event.data.id[0]
        };

        jsonRpc.pageService.getPage(function (result, error) {
            switch (result.sc) {
                case "GET_PAGE_SUCC":
                    $("#pageTitle").val(result.page.pageTitle).data('oId', event.data.id[0]);
                    tinyMCE.get('pageContent').setContent(result.page.pageContent);
                    $("#pageOrder").val(result.page.pageOrder);
                    break;
                case "GET_LINK_FAIL_":
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }

    var deletePage = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            jsonRpc.pageService.removePage(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_PAGE_SUCC":
                        getPageList(1);
                        $("#tipMsg").text("${removeSuccLabel}");
                        break;
                    case "REMOVE_PAGE_FAIL_":
                        $("#tipMsg").text("${removeFailLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var updatePage = function () {
        if (validatePage()) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": $("#pageTitle").data("oId"),
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pageOrder": $("#pageOrder").val()
                }
            };
            jsonRpc.pageService.updatePage(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_PAGE_SUCC":
                        getPageList(currentPage);
                        $("#pageTitle").removeData("oId").val("");
                        $("#tipMsg").text("${updateSuccLabel}");
                        tinyMCE.get('pageContent').setContent("");
                        $("#pageOrder").val("");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var addPage = function () {
        if (validatePage()) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pageOrder": $("#pageOrder").val()
                }
            };
            jsonRpc.pageService.addPage(function (result, error) {
                switch (result.sc) {
                    case "ADD_PAGE_SUCC":
                        $("#pageTitle").val("").removeData("oId");
                        $("#pageOrder").val("");
                        if (tinyMCE.get("pageContent")) {
                            tinyMCE.get('pageContent').setContent("");
                        } else {
                            $("#pageContent").val("");
                        }
                        if (pagesLength === PAGE_SIZE) {
                            pageCount++;
                        }
                        getPageList(pageCount);
                        $("#tipMsg").text("${addSuccLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var submitPage = function () {
        if ($("#pageTitle").data("oId")) {
            updatePage();
        } else {
            addPage();
        }
    }
</script>
