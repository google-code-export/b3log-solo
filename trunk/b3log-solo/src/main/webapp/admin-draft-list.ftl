<div id="draftList">
</div>
<div id="draftPagination" class="right margin12">
</div>
<div class="clear"></div>
<div id="draftListComments" class="none">
</div>
<script type="text/javascript">
    var draftListCurrentPage = 1;
    var getDraftList = function (pageNum) {
        $("#loadMsg").text("${loadingLabel}");
        draftListCurrentPage = pageNum;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE,
            "articleIsPublished": false
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
                            articleData[i].title = articles[i].articleTitle;
                            articleData[i].remove = "<div class='deleteIcon'></div>";
                            articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                            articleData[i].update = "<div class='updateIcon'></div>";
                            articleData[i].comments = "<div class='commentIcon left'></div><div class='left' style='margin-left:6px;'>"
                                + articles[i].articleCommentCount + "</div>";
                            articleData[i].articleViewCount = articles[i].articleViewCount;
                            articleData[i].id = articles[i].oId;
                            articleData[i].author = articles[i].authorName;
                        }
                        $("#draftList").table("update",{
                            data: [{
                                    groupName: "all",
                                    groupData: articleData
                                }]
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
            } catch (e) {}
        }, requestJSONObject);
    }

    var loadDraftList = function () {
        $("#draftList").table({
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
                    text: "${commentNameLabel}",
                    index: "author",
                    width: 100,
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
                                adminUtil.updateArticle(data, false);
                            }
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }, {
                    textAlign: "center",
                    text: "${removeLabel}",
                    index: "remove",
                    width: 53,
                    bind: [{
                            'type': 'click',
                            'action':  function (event, data) {
                                var isDelete = confirm("${confirmRemoveLabel}");

                                if (isDelete) {
                                    $("#loadMsg").text("${loadingLabel}");
                                    $("#tipMsg").text("");
                                    var requestJSONObject = {
                                        "oId": data.id
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
                                $("#draftListComments").data("oId", data.id);
                                getDraftListComment();
                                $("#draftListComments").dialog({
                                    width: 700,
                                    height:500,
                                    closeEvent: "closeDraftListDialog()"
                                });
                            }
                        }],
                    style: "cursor:pointer; margin-left:16px;"
                }, {
                    text: "${viewLabel}",
                    width: 36,
                    index: "articleViewCount",
                    style: "text-align:center;"
                }]
        });

        $("#draftPagination").paginate({
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
        
        getDraftList(1);
    }
    loadDraftList();
    
    var closeDraftListDialog = function () {
        getDraftList(draftListCurrentPage);
        $("#draftListComments").dialog("close");
    }
    
    var getDraftListComment = function () {
        $("#loadMsg").text("${loadingLabel}");
        $("#draftListComments").html("");
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
                        $("#draftListComments").html(commentsHTML);
                        break;
                    default:
                        break;
                };
                $("#loadMsg").text("");
            } catch (e) {}
        }, {"oId": $("#draftListComments").data("oId")});
    }
</script>
${plugins}
