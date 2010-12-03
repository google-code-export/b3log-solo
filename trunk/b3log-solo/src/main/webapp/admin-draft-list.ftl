<div id="draftList">
</div>
<div id="draftPagination" class="right margin12">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var draftListCurrentPage = 1;
    var getDraftList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        draftListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": PAGE_SIZE,
            "paginationWindowSize": WINDOW_SIZE,
            "articleIsPublished": false
        };

        jsonRpc.articleService.getArticles(function (result, error) {
            switch (result.sc) {
                case "GET_ARTICLES_SUCC":
                    var articles = result.articles,
                    articleData = [];

                    for (var i = 0; i < articles.length; i++) {
                        articleData[i] = {};
                        articleData[i].tags = "<div title='" + articles[i].articleTags + "'>" + articles[i].articleTags + "</div>";
                        articleData[i].title = articles[i].articleTitle;
                        articleData[i].remove = "<div class='deleteIcon'></div>";
                        articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                        articleData[i].update = "<div class='updateIcon'></div>";
                        articleData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                            + articles[i].articleCommentCount + "</div>";
                        articleData[i].articleViewCount = articles[i].articleViewCount;
                        articleData[i].id = articles[i].oId;
                    }
                    $("#draftList").table({
                        update:{
                            data: articleData
                        }
                    });

                    if (0 === result.pagination.paginationPageCount) {
                        result.pagination.paginationPageCount = 1;
                    }

                    $("#draftPagination").paginate({
                        update: {
                            pageCount: result.pagination.paginationPageCount,
                            currentPage: pageNum
                        }
                    });
                    break;
                default:
                    break;
            }
            $("#loadMsg").text("");
        }, requestJSONObject);
    }

    var loadDraftList = function () {
        $("#draftList").table({
            resizable: true,
            colModel: [{
                    name: "${titleLabel}",
                    index: "title",
                    width: 286,
                    style: "padding-left: 6px;"
                }, {
                    name: "${tagsLabel}",
                    index: "tags",
                    minWidth: 110,
                    style: "padding-left: 6px; overflow: hidden;font-size:11px; "
                }, {
                    textAlign: "center",
                    name: "${createDateLabel}",
                    index: "date",
                    width: 130
                }, {
                    textAlign: "center",
                    name: "${updateLabel}",
                    index: "update",
                    width: 49,
                    bindEvent: [{
                            'eventName': 'click',
                            'action': function (event) {
                                $("#loadMsg").text("${loadingLabel}");
                                $("#articleTab").click();
                                var requestJSONObject = {
                                    "oId": event.data.id[0]
                                };
                                jsonRpc.articleService.getArticle(function (result, error) {
                                    switch (result.sc) {
                                        case "GET_ARTICLE_SUCC":
                                            // set default value for article.
                                            $("#title").val(result.article.articleTitle).data("articleStatus", {
                                                "isArticle": false,
                                                'oId': event.data.id[0]
                                            });
                                            if (tinyMCE.get('articleContent')) {
                                                tinyMCE.get('articleContent').setContent(result.article.articleContent);
                                            } else {
                                                $("#articleContent").val(result.article.articleContent);
                                            }
                                            if (tinyMCE.get('abstract')) {
                                                tinyMCE.get('abstract').setContent(result.article.articleAbstract);
                                            } else {
                                                $("#abstract").val(result.article.articleAbstract);
                                            }

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
                                            $("#permalink").val(result.article.articlePermalink);
                                            beforeInitArticle();
                                            $("#tipMsg").text("${getSuccLabel}");
                                            break;
                                        case "GET_ARTICLE_FAIL_":
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
                    index: "remove",
                    width: 53,
                    bindEvent: [{
                            'eventName': 'click',
                            'action':  function (event) {
                                var isDelete = confirm("${confirmRemoveLabel}");

                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
                                    var requestJSONObject = {
                                        "oId": event.data.id[0]
                                    };

                                    jsonRpc.articleService.removeArticle(function (result, error) {
                                        switch (result.status.code) {
                                            case "REMOVE_ARTICLE_SUCC":
                                                var events = result.status.events,
                                                msg = "${removeSuccLabel}";
                                                if (events) {
                                                    if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                                        msg += ", ${syncCSDNBlogFailLabel}: "
                                                            + events.blogSyncCSDNBlog.msg;
                                                    }

                                                    if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                                        msg += ", ${syncCnBlogsFailLabel}: "
                                                            + events.blogSyncCnBlogs.msg;
                                                    }

                                                    if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                                        msg += ", ${syncBlogJavaFailLabel}: "
                                                            + events.blogSyncBlogJava.msg;
                                                    }
                                                }
                                                $("#tipMsg").text(msg);
                                                getDraftList(1);
                                                break;
                                            case "REMOVE_ARTICLE_FAIL_":
                                                $("#tipMsg").text("${removeFailLabel}");
                                                break;
                                            default:
                                                $("#tipMsg").text("");
                                                break;
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
                                $("#articleListComments").data("oId", event.data.id[0]);
                                getArticleListComment();
                                $("#articleListComments").dialog({
                                    width: 700,
                                    height:500,
                                    closeEvent: "closeArticleListDialog()"
                                });
                            }
                        }],
                    style: "cursor:pointer; margin-left:16px;"
                }, {
                    name: "${viewLabel}",
                    width: 36,
                    index: "articleViewCount",
                    style: "text-align:center;"
                }, {
                    visible: false,
                    index: "id"
                }]
        });

        $("#draftPagination").paginate({
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
        
        getDraftList(1);
    }
    loadDraftList();
</script>
