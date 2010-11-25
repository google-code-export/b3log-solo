<form id="uploadForm" action="/datastore-file-access.do" method="POST"
      enctype="multipart/form-data" target="formActionHidden">
    <table class="form" cellpadding="0" cellspacing="9">
        <tbody>
            <tr>
                <td id="uploadFile">
                    <input type='file' name='myFile' size='45'>
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
                    fileData[i].remove = "<div class='deleteIcon'></div>";
                    fileData[i].id = files[i].oId;
                }

                $("#fileList").table({
                    update:{
                        data: fileData
                    }
                });

                if (0 === result.pagination.paginationPageCount) {
                    result.pagination.paginationPageCount = 1;
                }
                
                $("#filePagination").paginate({
                    update: {
                        currentPage: pageNum,
                        pageCount: result.pagination.paginationPageCount
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
                    name: "${fileNameLabel}",
                    index: "name",
                    minWidth: 260
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
                    width: 80
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
                    visible: false,
                    index: "id"
                }]
        });

        $("#filePagination").paginate({
            bindEvent: "getfileList",
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
