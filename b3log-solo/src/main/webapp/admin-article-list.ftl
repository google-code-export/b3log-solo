<div id="articleList">
</div>
<div id="articlePagination" class="right margin12">
</div>
<div id="articleListComments" class="none">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var articleListCurrentPage = 1;
    
    var popTop = function (it, id) {
        $("#loadMsg").text("${loadingLabel}");
        $("#tipMsg").text("");
        var requestJSONObject = {
            "oId": id
        };
        var $it = $(it);
        if ($it.html() === "${putTopLabel}") {
            jsonRpc.articleService.putTopArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "PUT_TOP_ARTICLE_SUCC":
                            $it.html("${cancelPutTopLabel}");
                            $("#tipMsg").text("${putTopSuccLabel}");
                            break;
                        case "PUT_TOP_ARTICLE_FAIL_":
                            $("#tipMsg").text("${putTopFailLabel}");
                            break;
                        case "PUT_TOP_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text("${forbiddenLabel}");
                            break;
                        default:
                            $("#tipMsg").text("");
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        } else {
            jsonRpc.articleService.cancelTopArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "CANCEL_TOP_ARTICLE_SUCC":
                            $it.html("${putTopLabel}");
                            $("#tipMsg").text("${cancelTopSuccLabel}");
                            break;
                        case "CANCEL_TOP_ARTICLE_FAIL_":
                            $("#tipMsg").text("${cancelTopFailLabel}");
                            break;
                        case "CANCEL_TOP_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text("${forbiddenLabel}");
                            break;
                        default:
                            $("#tipMsg").text("");
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {}
            }, requestJSONObject);
        }
    }
    
    var loadArticleList = function () {
        $("#articleList").table({
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
        
        $("#articlePagination").paginate({
            "bind": function(currentPage) {
                adminUtil.getArticleList(currentPage, "article");
                articleListCurrentPage = currentPage;
                return true;
            },
            "currentPage": 1,
            "errorMessage": "${inputErrorLabel}",
            "nextPageText": "${nextPagePabel}",
            "previousPageText": "${previousPageLabel}",
            "goText": "${gotoLabel}"
        });
        
        adminUtil.getArticleList(1, "article");
        
        $("#articleListComments").dialog({
            width: 700,
            height:500,
            "modal": true,
            "hideFooter": true,
            "close": function () {
                adminUtil.getArticleList(articleListCurrentPage, "article");
                return true;
            }
        });
    }
    loadArticleList();
</script>
${plugins}
