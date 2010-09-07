<div id="articlePanel">
    <div id="articleList">
    </div>
    <div id="articlePagination">
    </div>
    <div id="comments" class="none">
    </div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    var currentPage = 1;
    $("#articleList").table({
        resizable: true,
        colModel: [{
                name: "${titleLabel}",
                index: "title",
                minWidth: 180,
                style: "padding-left: 6px;"
            }, {
                name: "${tagsLabel}",
                index: "tags",
                width: 380,
                style: "padding-left: 6px; white-space: nowrap; overflow: hidden; "
            }, {
                textAlign: "center",
                name: "${createDateLabel}",
                index: "date",
                width: 130
            }, {
                textAlign: "center",
                name: "${updateLabel}",
                index: "update",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'updateArticle'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            }, {
                textAlign: "center",
                name: "${removeLabel}",
                index: "deleted",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'deleteArticle'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            }, {
                textAlign: "center",
                name: "${commentLabel}",
                index: "comments",
                width: 66,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'popComments'
                    }],
                style: "cursor:pointer; margin-left:16px;"
            }, {
                visible: false,
                index: "id"
            }]
    });

    $("#articlePagination").paginate({
        bindEvent: "getArticleList",
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

    var updateArticle = function (event) {
        $("#content").load("admin-article.do", '', function () {
            $("#tipMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };

            jsonRpc.articleService.getArticle(function (result, error) {
                switch (result.sc) {
                    case "GET_ARTICLE_SUCC":
                        setCurrentNaviStyle(0);
                        // set default value for article.
                        $("#title").val(result.article.articleTitle).data('oId', event.data.id[0]);
                        $('#articleContent').val(result.article.articleContent);
                        $("#abstract").val(result.article.articleAbstract);

                        var tags = result.article.articleTags,
                        tagsString = '';
                        for (var i = 0; i < tags.length; i++) {
                            if (0 === i) {
                                tagsString = tags[i].tagTitle;
                            } else {
                                tagsString += "," + tags[i].tagTitle;
                            }
                        }
                        $("#tag").val(tagsString);

                        break;
                    case "GET_ARTICLE_FAIL_":
                        break;
                    default:
                        break;
                }
                $("#tipMsg").text("");
            }, requestJSONObject);
        });
    }
    
    var deleteArticle = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");
        
        if (isDelete) {
            $("#tipMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };
            
            jsonRpc.articleService.removeArticle(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_ARTICLE_SUCC":
                        getArticleList(1);
                        $("#tipMsg").text("${removeSuccLabel}");
                        break;
                    case "REMOVE_ARTICLE_FAIL_":
                        $("#tipMsg").text("${removeFailLabel}");
                        break;
                    default:
                        $("#tipMsg").text("");
                        break;
                }
            }, requestJSONObject);
        }
    }

    var closeDialog = function () {
        getArticleList(currentPage);
        $("#comments").dialog("close");
    }

    var popComments = function (event) {
        $("#comments").data("oId", event.data.id[0]);
        getComment();
        $("#comments").dialog({
            width: 700,
            height:500,
            closeEvent: "closeDialog()"
        });
    }
    
    var getArticleList = function (pageNum) {
        $("#tipMsg").text("${loadingLabel}");
        currentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE
        };
        
        jsonRpc.articleService.getArticles(function (result, error) {
            switch (result.sc) {
                case "GET_ARTICLES_SUCC":
                    var articles = result.articles,
                    articleData = [];

                    for (var i = 0; i < articles.length; i++) {
                        articleData[i] = {};
                        articleData[i].tags = "<div title='" + articles[i].articleTags + "'>" + articles[i].articleTags + "</div>";
                        articleData[i].title = "<a href='article-detail.do?oId=" + articles[i].oId + "' target='_blank' title='" + articles[i].articleTitle + "' class='noUnderline'>"
                            + articles[i].articleTitle + "</a>";
                        articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                        articleData[i].update = "<div class='updateIcon'></div>";
                        articleData[i].deleted = "<div class='deleteIcon'></div>";
                        articleData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                            + articles[i].articleCommentCount + "</div>";
                        articleData[i].id = articles[i].oId;
                    }

                    $("#articleList").table({
                        update:{
                            data: articleData
                        }
                    });

                    if (0 === result.pagination.paginationPageCount) {
                        result.pagination.paginationPageCount = 1;
                    }

                    $("#articlePagination").paginate({
                        update: {
                            pageCount: result.pagination.paginationPageCount
                        }
                    });
                    break;
                default:
                    break;
            }
            $("#tipMsg").text("");
        }, requestJSONObject);
    }
    getArticleList(1);

    var getComment = function () {
        $("#tipMsg").text("${loadingLabel}");
        jsonRpc.commentService.getComments(function (result, error) {
            switch (result.sc) {
                case "GET_COMMENTS_SUCC":
                    var comments = result.comments,
                    commentsHTML = '';
                    for (var i = 0; i < comments.length; i++) {
                        commentsHTML += "<div class='comment-title'><span class='left'>" + comments[i].commentDate
                            + "</span><span class='right deleteIcon' onclick=\"deleteComment('" + comments[i].oId
                            + "')\"></span><div class='clear'></div></div><div class='comment-body'>" + comments[i].commentContent + "</div>";
                    }
                    if ("" === commentsHTML) {
                        commentsHTML = "${noCommentLabel}"
                    }
                    $("#comments").html(commentsHTML);
                    break;
                default:
                    break;
            };
            $("#tipMsg").text("");
        }, {"oId": $("#comments").data("oId")});
        
    }

    var deleteComment = function (id) {
        $("#tipMsg").text("${loadingLabel}");
        jsonRpc.commentService.removeComment(function (result, error) {
            switch (result.sc) {
                case "REMOVE_COMMENT_SUCC":
                    getComment();
                    $("#tipMsg").text("${removeSuccLabel}");
                    break;
                default:
                    $("#tipMsg").text("");
                    break;
            }
        }, {"oId": id});
    }
</script>
