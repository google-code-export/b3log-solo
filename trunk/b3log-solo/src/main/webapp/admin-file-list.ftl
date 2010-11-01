<form id="uploadForm" action="/datastore-file-access.do" method="POST"
      enctype="multipart/form-data">
    <table class="form" width="40%" cellpadding="0" cellspacing="9">
        <tbody>
            <tr>
                <td>
                    <input type="file" name="myFile" size="50">
                </td>
                <td>
                    <input type="submit" value="Submit" class="button" style="height: 28px;">
                </td>
            </tr>
        </tbody>
    </table>
</form>
<div id="fileList">
</div>
<div id="filePagination" class="right margin12">
</div>
<script type="text/javascript">
    // variable
    var fileListCurrentPage = 1,
    fileListPageCount = 1,
    filesLength = 1;

    var initFile = function () {
        // init file list
        $("#fileList").table({
            resizable: true,
            colModel: [{
                    style: "padding-left: 6px;",
                    name: "${fileNameLabel}",
                    index: "name",
                    width: 260
                }, {
                    textAlign: "center",
                    name: "${uploadDateLabel}",
                    index: "uploadDate",
                    width: 200
                }, {
                    textAlign: "center",
                    name: "${sizeLabel} (Bytes)",
                    index: "size",
                    width: 200
                }, {
                    textAlign: "center",
                    name: "${downloadCountLabel}",
                    index: "downloadCnt",
                    width: 56
                }, {
                    textAlign: "center",
                    name: "${removeLabel}",
                    index: "remove",
                    width: 56,
                    bindEvent: [{
                            'eventName': 'click',
                            'functionName': 'removeFile'
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    name: "${updateLabel}",
                    index: "update",
                    width: 56,
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#filePagination").paginate({
            bindEvent: "getfileList",
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
    }
    initFile();

    var deleteFile = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");

        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            var result = jsonRpc.fileService.removeFile(requestJSONObject);
            switch (result.sc) {
                case "REMOVE_FILE_SUCC":
                    getFileList(1);
                    $("#tipMsg").text("${removeSuccLabel}");
                    break;
                case "REMOVE_FILE_FAIL_":
                    $("#tipMsg").text("${removeFailLabel}");
                    break;
                default:
                    break;
            }
        }
    }

    var removeFile = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            jsonRpc.fileService.removeFile(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_FILE_SUCC":
                        getFileList(1);
                        $("#tipMsg").text("${removeSuccLabel}");
                        break;
                    case "REMOVE_FILE_FAIL_":
                        $("#tipMsg").text("${removeFailLabel}");
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var getFileList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        fileListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        var result = jsonRpc.fileService.getFiles(requestJSONObject);
        switch (result.sc) {
            case "GET_FILES_SUCC":
                var files = result.files;
                var fileData = [];

                for (var i = 0; i < files.length; i++) {
                    fileData[i] = {};
                    fileData[i].name = "<a href='" + files[i].fileDownloadURL + "'>"
                        + files[i].fileName + "</a>";
                    fileData[i].uploadDate = $.bowknot.getDate(files[i].fileUploadDate.time, 1);
                    fileData[i].downloadCnt = files[i].fileDownloadCount;
                    fileData[i].size = files[i].fileSize;

                    fileData[i].update = "<div class='updateIcon'></div>";
                    fileData[i].remove = "<div class='deleteIcon'></div>";
                    fileData[i].id = files[i].oId;
                }

                $("#fileList").table({
                    update:{
                        data: fileData
                    }
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
        $("#tipMsg").text("");
    }

    getFileList(1);
</script>
