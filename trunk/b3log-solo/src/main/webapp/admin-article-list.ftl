<div id="articleList">
</div>
<div id="articlePagination" class="right margin12">
</div>
<div id="comments" class="none">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var currentPage = 1;
    $("#articleList").table({
        resizable: true,
        colModel: [{
                name: "${titleLabel}",
                index: "title",
                width: 460,
                style: "padding-left: 6px;"
            }, {
                name: "${tagsLabel}",
                index: "tags",
                minWidth: 380,
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
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'getUpdateArticle'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            }, {
                textAlign: "center",
                name: "${removeLabel}",
                index: "remove",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'deleteArticle'
                    }],
                style: "cursor:pointer; margin-left:22px;"
            },  {
                textAlign: "center",
                name: "${putTopLabel}",
                index: "topArticle",
                width: 56,
                bindEvent: [{
                        'eventName': 'click',
                        'functionName': 'topArticle'
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
                name: "${viewLabel}",
                width: 66,
                index: "articleViewCount",
                style: "margin-left:16px;"
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

    var getUpdateArticle = function (event) {
        $("#loadMsg").text("${loadingLabel}");
        $("#articlePanel").show();
        $("#articleTab").addClass("selected");
        $("#article-listPanel").hide();
        $("#article-listTab").removeClass("selected");
        $("#articlePanel").load("admin-article.do", function () {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };
            jsonRpc.articleService.getArticle(function (result, error) {
                switch (result.sc) {
                    case "GET_ARTICLE_SUCC":
                        // set default value for article.
                        $("#title").val(result.article.articleTitle).data('oId', event.data.id[0]);
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
                        break;
                    case "GET_ARTICLE_FAIL_":
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        });
    }
    
    var deleteArticle = function (event) {
        var isDelete = confirm("${confirmRemoveLabel}");
        
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            var requestJSONObject = {
                "oId": event.data.id[0]
            };
            
            jsonRpc.articleService.removeArticle(function (result, error) {
                switch (result.status.code) {
                    case "REMOVE_ARTICLE_SUCC":
                        var events = result.status.events;
                        if (events) {
                            var msg = "${removeSuccLabel}";
                            if ("BLOG_SYNC_REMOVE_CSDN_BLOG_FAIL" === events.blogSyncCSDNBlog.code) {
                                msg += ", ${syncCSDNBlogFailLabel}";
                            }

                            if ("BLOG_SYNC_REMOVE_CNBLOGS_FAIL" === events.blogSyncCnBlogs.code) {
                                msg += ", ${syncCnBlogsFailLabel}";
                            }

                            if ("BLOG_SYNC_REMOVE_BLOGJAVA_FAIL" === events.blogSyncBlogJava.code) {
                                msg += ", ${syncBlogJavaFailLabel}";
                            }
                            getArticleList(1);
                            $("#tipMsg").text(msg);

                            if ("BLOG_SYNC_REMOVE_CSDN_BLOG_SUCC" === events.blogSyncCSDNBlog.code
                                && "BLOG_SYNC_REMOVE_CNBLOGS_SUCC" === events.blogSyncCnBlogs.code
                                && "BLOG_SYNC_REMOVE_BLOGJAVA_SUCC" === events.blogSyncBlogJava.code) {
                                getArticleList(1);
                                $("#tipMsg").text("${removeSuccLabel}");
                            }
                            return;
                        }
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
                $("#loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var topArticle = function (event) {
        $("#loadMsg").text("${loadingLabel}");

        var requestJSONObject = {
            "oId": event.data.id[0]
        };
        if ($(this).find("div").hasClass("notPutTopIcon")) {
            jsonRpc.articleService.putTopArticle(function (result, error) {
                switch (result.sc) {
                    case "PUT_TOP_ARTICLE_SUCC":
                        getArticleList(1);
                        $("#tipMsg").text("${putTopSuccLabel}");
                        break;
                    case "PUT_TOP_ARTICLE_FAIL_":
                        $("#tipMsg").text("${putTopFailLabel}");
                        break;
                    default:
                        $("#tipMsg").text("");
                        break;
                }
                $("#loadMsg").text("");
            }, requestJSONObject);
        } else {
            jsonRpc.articleService.cancelTopArticle(function (result, error) {
                switch (result.sc) {
                    case "CANCEL_TOP_ARTICLE_SUCC":
                        getArticleList(1);
                        $("#tipMsg").text("${cancelTopSuccLabel}");
                        break;
                    case "CANCEL_TOP_ARTICLE_FAIL_":
                        $("#tipMsg").text("${cancelTopFailLabel}");
                        break;
                    default:
                        $("#tipMsg").text("");
                        break;
                }
                $("#loadMsg").text("");
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
        $("#loadMsg").text("${loadingLabel}");
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
                        articleData[i].remove = "<div class='deleteIcon'></div>";
                        var topArticleHtml = articles[i].articlePutTop ?
                            "<div class='putTopIcon'></div>" : "<div class='notPutTopIcon'></div>"
                        articleData[i].topArticle = topArticleHtml;
                        articleData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                            + articles[i].articleCommentCount + "</div>";
                        articleData[i].articleViewCount = "<a href='article-detail.do?oId="
                            + articles[i].oId + "' target='_blank' title='" + articles[i].articleTitle
                            + "' class='noUnderline'><div class='left browserIcon'></div><div class='left' style='margin-left:6px;'>"
                            + articles[i].articleViewCount + "</div></a>";;
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
    getArticleList(1);

    var getComment = function () {
        $("#loadMsg").text("${loadingLabel}");
        jsonRpc.commentService.getCommentsOfArticle(function (result, error) {
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
                        commentsHTML += "</span><span class='right deleteIcon' onclick=\"deleteComment('" + comments[i].oId
                            + "')\"></span><span class='right'>" + $.bowknot.getDate(comments[i].commentDate.time, 1)
                            + "&nbsp;</span><div class='clear'></div></div><div class='comment-body'>" + comments[i].commentContent + "</div>";
                    }
                    if ("" === commentsHTML) {
                        commentsHTML = "${noCommentLabel}"
                    }
                    $("#comments").html(commentsHTML);
                    break;
                default:
                    break;
            };
            $("#loadMsg").text("");
        }, {"oId": $("#comments").data("oId")});
    }

    var deleteComment = function (id) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            jsonRpc.commentService.removeCommentOfArticle(function (result, error) {
                switch (result.sc) {
                    case "REMOVE_COMMENT_SUCC":
                        getComment();
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
