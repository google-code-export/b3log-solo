<div id="articleList">
</div>
<div id="articlePagination" class="right margin12">
</div>
<div id="articleListComments" class="none">
</div>
<div class="clear"></div>
<script type="text/javascript">
    var articleListCurrentPage = 1, articleDataTemp = [];
    var getArticleList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        articleListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE,
            "articleIsPublished": true
        };

        jsonRpc.articleService.getArticles(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_ARTICLES_SUCC":
                        var articles = result.articles,
                        articleData = [];

                        for (var i = 0; i < articles.length; i++) {
                            articleData[i] = {};
                            articleData[i].tags = "<div title='" + articles[i].articleTags + "'>" + articles[i].articleTags + "</div>";
                            articleData[i].title = "<a href='" + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                                + articles[i].articleTitle + "</a>";
                            articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                            articleData[i].update = "<div class='updateIcon'></div>";
                            var topArticleHtml = articles[i].articlePutTop ?
                                "<div class='putTopIcon'></div>" : "<div class='notPutTopIcon'></div>"
                            articleData[i].topArticle = topArticleHtml;
                            articleData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                                + articles[i].articleCommentCount + "</div>";
                            articleData[i].articleViewCount = "<a href='"
                                + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle
                                + "' class='no-underline'>"+ articles[i].articleViewCount + "</a>";;
                            articleData[i].id = articles[i].oId;
                            articleData[i].author = articles[i].authorName;
                            articleData[i].expendRow = "更新 <span onclick=\"removeArticle('" + articles[i].oId + "')\">删除</span> 置顶";
                        }
                        articleDataTemp = articleData;
                        $("#articleList").table("update",{
                            data: [{
                                    groupName: "all",
                                    groupData: articleData
                                }]
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
            } catch (e) {
            }
        }, requestJSONObject);
    }
    
    var removeArticle = function (id) {
        var isDelete = confirm("${confirmRemoveLabel}");

        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var requestJSONObject = {
                "oId": id
            };

            jsonRpc.articleService.removeArticle(function (result, error) {
                try {
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
                            getArticleList(1);
                            break;
                        case "REMOVE_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text("${forbiddenLabel}");
                            break;
                        case "REMOVE_ARTICLE_FAIL_":
                            $("#tipMsg").text("${removeFailLabel}");
                            break;
                        default:
                            $("#tipMsg").text("");
                            break;
                    }
                    $("#loadMsg").text("");
                } catch (e) {
                }
            }, requestJSONObject);
        }
    }


    var loadArticleList = function () {
        $("#articleList").table({
            resizable: true,
            colModel: [{
                    text: "${titleLabel}",
                    index: "title",
                    width: 286,
                    style: "padding-left: 6px;"
                }, {
                    text: "${tagsLabel}",
                    index: "tags",
                    minWidth: 110,
                    style: "padding-left: 6px; overflow: hidden;font-size:11px; "
                }, {
                    text: "${authorLabel}",
                    index: "author",
                    width: 130,
                    style: "padding-left: 6px; overflow: hidden;"
                }, {
                    textAlign: "center",
                    text: "${createDateLabel}",
                    index: "date",
                    width: 130
                }, {
                    textAlign: "center",
                    text: "${updateLabel}",
                    index: "update",
                    width: 49,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                adminUtil.updateArticle(data, true);
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    text: "${putTopLabel}",
                    index: "topArticle",
                    width: 53,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                $("#loadMsg").text("${loadingLabel}");
                                $("#tipMsg").text("");
                                var requestJSONObject = {
                                    "oId": data.id
                                };
                                if ($(this).find("div").hasClass("notPutTopIcon")) {
                                    jsonRpc.articleService.putTopArticle(function (result, error) {
                                        try {
                                            switch (result.sc) {
                                                case "PUT_TOP_ARTICLE_SUCC":
                                                    for (var i = 0; i < articleDataTemp.length; i++) {
                                                        if (data.id === articleDataTemp[i].id) {
                                                            articleDataTemp[i].topArticle = "<div class='putTopIcon'></div>";
                                                        }
                                                    }
                                                    $("#articleList").table("update",{
                                                        data: [{
                                                                groupName: "all",
                                                                groupData: articleDataTemp
                                                            }]
                                                    });
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
                                                    for (var i = 0; i < articleDataTemp.length; i++) {
                                                        if (data.id === articleDataTemp[i].id) {
                                                            articleDataTemp[i].topArticle = "<div class='notPutTopIcon'></div>";
                                                        }
                                                    }
                                                    $("#articleList").table("update",{
                                                        data: [{
                                                                groupName: "all",
                                                                groupData: articleDataTemp
                                                            }]
                                                    });
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
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    text: "${commentLabel}",
                    index: "comments",
                    width: 65,
                    bind: [{
                            'type': 'click',
                            'action': function (event, data) {
                                $("#articleListComments").data("oId", data.id);
                                getArticleListComment();
                                $("#articleListComments").dialog("open");
                            }
                        }],
                    style: "cursor:pointer; margin-left:16px;"
                }, {
                    text: "${viewLabel}",
                    width: 36,
                    index: "articleViewCount",
                    style: "text-align:center;"
                }],
            expendRow: {
                index: "expendRow"
            }
        });

        $("#articlePagination").paginate({
            bindEvent: "getArticleList",
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
        
        getArticleList(1);
        
        $("#articleListComments").dialog({
            width: 700,
            height:500,
            "modal": true,
            "hideFooter": true,
            "close": function () {
                getArticleList(articleListCurrentPage);
                return true;
            }
        });
    }
    loadArticleList();

    var getArticleListComment = function () {
        $("#loadMsg").text("${loadingLabel}");
        $("#articleListComments").html("");
        jsonRpc.commentService.getCommentsOfArticle(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_COMMENTS_SUCC":
                        var comments = result.comments,
                        commentsHTML = '';
                        for (var i = 0; i < comments.length; i++) {
                            var hrefHTML = "<a target='_blank' href='" + comments[i].commentURL + "'>",
                            content = comments[i].commentContent;
                            var ems = content.split("[em");
                            var contentHTML = ems[0];
                            for (var j = 1; j < ems.length; j++) {
                                var key = ems[j].substr(0, 2),
                                emImgHTML = "<img src='/skins/classic/emotions/em" + key
                                    + ".png'/>";
                                contentHTML += emImgHTML + ems[j].slice(3);
                            }
                        
                            if (comments[i].commentURL === "http://") {
                                hrefHTML = "<a target='_blank'>";
                            }

                            commentsHTML += "<div class='comment-title'><span class='left'>"
                                + hrefHTML + comments[i].commentName + "</a>";

                            if (comments[i].commentOriginalCommentName) {
                                commentsHTML += "@" + comments[i].commentOriginalCommentName;
                            }
                            commentsHTML += "</span><span title='${removeLabel}' class='right deleteIcon' onclick=\"deleteArticleListComment('"
                                + comments[i].oId + "')\"></span><span class='right'><a href='mailto:"
                                + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                                + $.bowknot.getDate(comments[i].commentDate.time, 1)
                                + "&nbsp;</span><div class='clear'></div></div><div class='comment-body'>"
                                + contentHTML + "</div>";
                        }
                        if ("" === commentsHTML) {
                            commentsHTML = "${noCommentLabel}"
                        }
                        $("#articleListComments").html(commentsHTML);
                        break;
                    default:
                        break;
                };
                $("#loadMsg").text("");
            } catch (e) {}
        }, {"oId": $("#articleListComments").data("oId")});
    }

    var deleteArticleListComment = function (id) {
        var isDelete = confirm("${confirmRemoveLabel}");
        if (isDelete) {
            $("#loadMsg").text("${loadingLabel}");
            jsonRpc.commentService.removeCommentOfArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_COMMENT_FAIL_FORBIDDEN":
                            $("#tipMsg").text("${forbiddenLabel}");
                            break;
                        case "REMOVE_COMMENT_SUCC":
                            getArticleListComment();
                            $("#tipMsg").text("${removeSuccLabel}");
                            break;
                        default:
                            $("#tipMsg").text("");
                            $("#loadMsg").text("");
                            break;
                    }
                } catch (e) {}
            }, {"oId": id});
        }
    }
</script>
${plugins}
