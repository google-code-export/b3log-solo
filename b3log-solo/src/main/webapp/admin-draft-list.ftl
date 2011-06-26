<div id="draftList">
</div>
<div id="draftPagination" class="right margin12">
</div>
<div class="clear"></div>
<div id="draftListComments" class="none">
</div>
<script type="text/javascript">
    var draftListCurrentPage = 1;

    var loadDraftList = function () {
        $("#draftList").table({
            resizable: true,
            colModel: [{
                    text: "${titleLabel}",
                    index: "title",
                    minWidth: 110,
                    style: "padding-left: 6px;font-size:16px;"
                }, {
                    text: "${tagsLabel}",
                    index: "tags",
                    width: 386,
                    style: "padding-left: 6px;"
                }, {
                    text: "${authorLabel}",
                    index: "author",
                    width: 130,
                    style: "padding-left: 6px;"
                }, {
                    text: "${createDateLabel}",
                    index: "date",
                    width: 130,
                    style: "padding-left: 6px;"
                }, {
                    text: "${commentLabel}",
                    index: "comments",
                    width: 65,
                    style: "padding-left: 6px;"
                }, {
                    text: "${viewLabel}",
                    width: 36,
                    index: "articleViewCount",
                    style: "padding-left: 6px;"
                }],
            expendRow: {
                index: "expendRow"
            }
        });

        $("#draftPagination").paginate({
           "bind": function(currentPage) {
                adminUtil.getArticleList(currentPage, "draft");
                draftListCurrentPage = currentPage;
                return true;
            },
            "currentPage": 1,
            "errorMessage": "${inputErrorLabel}",
            "nextPageText": "${nextPagePabel}",
            "previousPageText": "${previousPageLabel}",
            "goText": "${gotoLabel}"
        });
        
        adminUtil.getArticleList(1, "draft");
        
        $("#draftListComments").dialog({
            width: 700,
            height:500,
            "modal": true,
            "hideFooter": true,
            "close": function () {
                adminUtil.getArticleList(draftListCurrentPage, "draft");
                return true;
            }
        });
    }
    loadDraftList();
</script>
${plugins}
