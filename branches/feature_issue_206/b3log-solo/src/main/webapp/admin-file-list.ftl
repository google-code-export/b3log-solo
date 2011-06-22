<form id="uploadForm" action="/datastore-file-access.do" method="POST"
      enctype="multipart/form-data" target="formActionHidden">
    <table class="form" cellpadding="0" cellspacing="9" width="494">
        <tbody>
            <tr>
                <td id="uploadFile">
                    <input type='file' name='myFile' size='45' style="width: 356px;" />
                </td>
                <td>
                    <button type="submit">${submitUploadLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</form>
<div id="fileList">
</div>
<div id="filePagination" class="right margin12">
</div>
<iframe class="none" id="formActionHidden" name="formActionHidden"></iframe>
<script type="text/javascript">    
    var fileListCurrentPage = 1,
    fileListPageCount = 1,
    fileListLength = 1;
    
    var getFileList = function (pageNum) {
        fileListCurrentPage = pageNum;
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE
        };
        var result = jsonRpc.fileService.getFiles(requestJSONObject);
        switch (result.sc) {
            case "GET_FILES_SUCC":
                var files = result.files;
                var fileData = [];
                fileListLength = files.length;
                for (var i = 0; i < files.length; i++) {
                    fileData[i] = {};
                    fileData[i].name = "<a href='" + files[i].fileDownloadURL + "'>"
                        + files[i].fileName + "</a>";
                    fileData[i].uploadDate = $.bowknot.getDate(files[i].fileUploadDate.time, 1);
                    fileData[i].downloadCnt = files[i].fileDownloadCount;
                    fileData[i].size = files[i].fileSize;
                    fileData[i].remove = "<div class='deleteIcon'></div>";
                    fileData[i].id = files[i].oId;
                }

                $("#fileList").table("update",{
                    data: [{
                            groupName: "all",
                            groupData: fileData
                        }]
                });
                if (result.pagination.paginationPageCount === 0) {
                    fileListPageCount = 1;
                } else {
                    fileListPageCount = result.pagination.paginationPageCount;
                }
                
                $("#filePagination").paginate({
                    update: {
                        currentPage: pageNum,
                        pageCount: fileListPageCount
                    }
                });
                break;
            default:
                break;
        }
        $("#loadMsg").text("");
    }
    
    var initFile = function () {
        $("#fileList").table({
            resizable: true,
            colModel: [{
                    style: "padding-left: 6px;",
                    text: "${fileNameLabel}",
                    index: "name",
                    minWidth: 260
                }, {
                    textAlign: "center",
                    text: "${uploadDateLabel}",
                    index: "uploadDate",
                    width: 200
                }, {
                    textAlign: "center",
                    text: "${sizeLabel} (Bytes)",
                    index: "size",
                    width: 150
                }, {
                    textAlign: "center",
                    text: "${downloadCountLabel}",
                    index: "downloadCnt",
                    width: 80
                }, {
                    textAlign: "center",
                    text: "${removeLabel}",
                    index: "remove",
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

                                    jsonRpc.fileService.removeFile(function (result, error) {
                                        try {
                                            switch (result.sc) {
                                                case "REMOVE_FILE_SUCC":
                                                    var pageNum = fileListCurrentPage;
                                                    if (fileListLength === 1 && fileListPageCount !== 1 &&
                                                        fileListCurrentPage === fileListPageCount) {
                                                        fileListPageCount--;
                                                        pageNum = fileListPageCount;
                                                    }
                                                    getFileList(pageNum);
                                                    $("#tipMsg").text("${removeSuccLabel}");
                                                    break;
                                                case "REMOVE_FILE_FAIL_":
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

        $("#filePagination").paginate({
            bindEvent: "getfileList",
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
        
        getFileList(1);

        $("#formActionHidden").load(function () {
            getFileList(1);
            var $iframe = $("#formActionHidden").contents();
            if ($iframe.find("pre").length === 1) {
                $("#tipMsg").html($iframe.find("pre").html());
            } else if ($iframe.find("#loadMsg").length === 1) {
                $("#tipMsg").html("${addSuccLabel}");
                $("#uploadFile").html("<input type='file' name='myFile' size='45'>");
            }
        });
    }
    initFile();
</script>
${plugins}
