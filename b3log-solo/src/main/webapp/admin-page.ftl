<div>
    <div id="pageList">
    </div>
    <div id="pagePagination" class="margin12 right">
    </div>
    <div class="clear"></div>
</div>
<table class="form" width="100%" cellpadding="0px" cellspacing="9px">
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
                <textarea id="pageContent" style="height: 430px;width: 100%;" name="pageContent"></textarea>
            </td>
        </tr>
        <tr>
            <th>
                ${permalink1Label}
            </th>
            <td>
                <input id="pagePermalink" type="text"/>
            </td>
        </tr>
        <tr>
            <th colspan="2" align="right">
                <button onclick="submitPage();">${saveLabel}</button>
            </th>
        </tr>
    </tbody>
</table>
<div id="pageComments" class="none">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var pageListCurrentPage = 1,
    pageListPageCount = 1,
    pagePagesLength = 1;
    
    var getPageList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        pageListCurrentPage = pageNum;
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
                    pagePagesLength = pages.length;

                    for (var i = 0; i < pages.length; i++) {
                        pageData[i] = {};
                        pageData[i].pageTitle = pages[i].pageTitle;
                        pageData[i].pageOrder = "";
                        pageData[i].pagePermalink = "<a class='noUnderline' href='" + pages[i].pagePermalink + "' target='_blank'>"
                            + pages[i].pagePermalink + "</a>";
                        pageData[i].update = "<div class='updateIcon'></div>";
                        pageData[i].deleted = "<div class='deleteIcon'></div>";
                        pageData[i].id = pages[i].oId;
                        pageData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                            + pages[i].pageCommentCount + "</div>";
                    }

                    $("#pageList").table({
                        update:{
                            data: pageData
                        }
                    });

                    if (result.pagination.paginationPageCount === 0) {
                        pageListPageCount = 1;
                    } else {
                        pageListPageCount = result.pagination.paginationPageCount;
                    }

                    $("#pagePagination").paginate({
                        update: {
                            currentPage: pageNum,
                            pageCount: pageListPageCount
                        }
                    });
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }

    var savePageOrder = function (order, status) {
        var tableData = $("#pageList").table("option", "data");
        if (status === "up") {
            alert(tableData[order].pageTitle);
            alert(tableData[order - 1].pageTitle);
        } else {
            alert(tableData[order].pageTitle);
            alert(tableData[order + 1].pageTitle);
        }
    }

    var initPage = function () {
        $("#pageList").table({
            orderActionName: "savePageOrder",
            colModel: [ {
                    name: "",
                    inputType: "order",
                    index: "pageOrder",
                    width: 60
                }, {
                    style: "padding-left: 6px;",
                    name: "${titleLabel}",
                    index: "pageTitle",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    name: "${permalinkLabel}",
                    index: "pagePermalink",
                    minWidth: 300
                }, {
                    textAlign: "center",
                    name: "${updateLabel}",
                    index: "update",
                    width: 49,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                $("#loadMsg").text("${loadingLabel}");
                                var requestJSONObject = {
                                    "oId": event.data.id[0]
                                };

                                jsonRpc.pageService.getPage(function (result, error) {
                                    switch (result.sc) {
                                        case "GET_PAGE_SUCC":
                                            $("#pageTitle").val(result.page.pageTitle).data('oId', event.data.id[0]);
                                            tinyMCE.get('pageContent').setContent(result.page.pageContent);
                                            $("#pagePermalink").val(result.page.pagePermalink);
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
                    width: 53,
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
                                        $("#pageTitle").val("").removeData("oId");
                                        $("#pagePermalink").val("");
                                        if (tinyMCE.get("pageContent")) {
                                            tinyMCE.get('pageContent').setContent("");
                                        } else {
                                            $("#pageContent").val("");
                                        }

                                        $("#loadMsg").text("");
                                    }, requestJSONObject);
                                }
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    name: "${commentLabel}",
                    index: "comments",
                    width: 65,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                $("#pageComments").data("oId", event.data.id[0]);
                                getPageComment();
                                $("#pageComments").dialog({
                                    width: 700,
                                    height:500,
                                    closeEvent: "closePageDialog()"
                                });
                            }
                        }],
                    style: "cursor:pointer; margin-left:16px;"
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#pagePagination").paginate({
            bindEvent: "getPageList",
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

        // editor
        var localeString = "${localeString}";
        var language = localeString.substring(0, 2);
        tinyMCE.init({
            // General options
            language: language,
            mode : "exact",
            elements : "pageContent",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen,syntaxhl",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen,syntaxhl",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class]",

            relative_urls: false,
            remove_script_host: false
        });

        getPageList(1);
    }

    initPage();

    var validatePage = function () {
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#pageTitle").focus();
        } else if (tinyMCE.get('pageContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${contentEmptyLabel}");
        } else {
            return true;
        }
        return false;
    }

    var updatePage = function () {
        if (validatePage()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": $("#pageTitle").data("oId"),
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            jsonRpc.pageService.updatePage(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_PAGE_FAIL_DUPLICATED_PERMALINK":
                        var msg = "${addFailLabel}, ${duplicatedPermalinkLabel}";
                        $("#tipMsg").text(msg);
                        break;
                    case "UPDATE_PAGE_SUCC":
                        getPageList(pageListCurrentPage);
                        $("#pageTitle").removeData("oId").val("");
                        $("#tipMsg").text("${updateSuccLabel}");
                        tinyMCE.get('pageContent').setContent("");
                        $("#pagePermalink").val("");
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
            $("#tipMsg").text("");
            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pageContent": tinyMCE.get('pageContent').getContent(),
                    "pagePermalink": $("#pagePermalink").val()
                }
            };
            jsonRpc.pageService.addPage(function (result, error) {
                switch (result.sc) {
                    case "ADD_PAGE_FAIL_DUPLICATED_PERMALINK":
                        var msg = "${addFailLabel}, ${duplicatedPermalinkLabel}";
                        $("#tipMsg").text(msg);
                        break;
                    case "ADD_PAGE_SUCC":
                        $("#pageTitle").val("").removeData("oId");
                        $("#pagePermalink").val("");
                        if (tinyMCE.get("pageContent")) {
                            tinyMCE.get('pageContent').setContent("");
                        } else {
                            $("#pageContent").val("");
                        }
                        if (pagePagesLength === PAGE_SIZE) {
                            pageListPageCount++;
                        }
                        getPageList(pageListPageCount);
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

    var getPageComment = function () {
        $("#loadMsg").text("${loadingLabel}");
        $("#pageComments").html("");
        jsonRpc.commentService.getCommentsOfPage(function (result, error) {
            switch (result.sc) {
                case "GET_COMMENTS_SUCC":
                    var comments = result.comments,
                    commentsHTML = '';
                    for (var i = 0; i < comments.length; i++) {
                        var hrefHTML = "<a target='_blank' href='" + comments[i].commentURL + "'>";

                        if (comments[i].commentURL === "http://") {
                            hrefHTML = "<a target='_blank'>";
                        }

                        commentsHTML += "<div class='comment-title'><span class='left'>"
                            + hrefHTML + comments[i].commentName + "</a>";

                        if (comments[i].commentOriginalCommentName) {
                            commentsHTML += "@" + comments[i].commentOriginalCommentName;
                        }
                        commentsHTML += "</span><span class='right deleteIcon' onclick=\"deletePageComment('" + comments[i].oId
                            + "')\"></span><span class='right'><a href='mailto:"
                            + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                            + $.bowknot.getDate(comments[i].commentDate.time, 1)
                            + "&nbsp;</span><div class='clear'></div></div><div class='comment-body'>" + comments[i].commentContent + "</div>";
                    }
                    if ("" === commentsHTML) {
                        commentsHTML = "${noCommentLabel}"
                    }
                    $("#pageComments").html(commentsHTML);
                    break;
                default:
                    break;
            };
            $("#loadMsg").text("");
        }, {"oId": $("#pageComments").data("oId")});
    }
    
    var closePageDialog = function () {
        getPageList(pageListCurrentPage);
        $("#pageComments").dialog("close");
    }

    var deletePageComment = function (id) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            jsonRpc.commentService.removeCommentOfPage(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_COMMENT_SUCC":
                        getPageComment();
                        $("#tipMsg").text("${removeSuccLabel}");
                        break;
                    default:
                        $("#tipMsg").text("");
                        $("#loadMsg").text("");
                        break;
                }
            }, {"oId": id});
        }
    }
</script>
