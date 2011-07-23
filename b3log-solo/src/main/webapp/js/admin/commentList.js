/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * comment list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, July 18, 2011
 */

/* comment-list 相关操作 */
admin.commentList = {
    tablePagination:  new TablePaginate("comment"),
    pageInfo: {
        currentPage: 1
    },
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function () {
        this.tablePagination.buildTable([{
            text: Label.commentContentLabel,
            index: "content",
            minWidth: 300,
            style: "padding-left: 12px;"
        }, {
            text: Label.titleLabel,
            index: "title",
            width: 300,
            style: "padding-left: 12px;"
        }, {
            text: Label.authorLabel,
            index: "userName",
            width: 100,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentEmailLabel,
            index: "userEmail",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 150,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.getList(1);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE
        };
        jsonRpc.commentService.getComments(function (result, error) {
            try {
                switch (result.sc) {
                    case "GET_COMMENTS_SUCC":
                        that.pageInfo.currentPage = pageNum;
                        var comments = result.comments,
                        commentsData = [];
                        for (var i = 0; i < comments.length; i++) {
                            commentsData[i] = {};
                            commentsData[i].content = Util.replaceEmString(comments[i].commentContent);
                            commentsData[i].title = "<a href='" + comments[i].commentSharpURL + 
                            "' target='_blank'>" + comments[i].commentTitle +
                            "</a>";
                            if ("http://" === comments[i].commentURL) {
                                commentsData[i].userName =comments[i].commentName;
                            } else {
                                commentsData[i].userName = "<a href='" + comments[i].commentURL +
                                "' target='_blank'>" + comments[i].commentName + 
                                "</a>";
                            }
                            commentsData[i].userEmail = "<a href='mailto:" + comments[i].commentEmail +
                            "'>" + comments[i].commentEmail + "</a>";
                            commentsData[i].date = $.bowknot.getDate(comments[i].commentDate.time, 1);
                            commentsData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.commentList.del('" + comments[i].oId + "', 'article')\">" + Label.removeLabel + "</a>";
                        }
                        that.tablePagination.updateTablePagination(commentsData, pageNum, result.pagination);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        }, requestJSONObject);
    },
    
    /* 
     * 删除评论
     * @id 评论 id 
     */
    del: function (id) {
        if (confirm(Label.confirmRemoveLabel)) {
            $("#loadMsg").text(Label.loadingLabel);
            jsonRpc.commentService.removeCommentOfArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_COMMENT_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
                            break;
                        case "REMOVE_COMMENT_SUCC":
                            admin.commentList.getList(admin.commentList.pageInfo.currentPage);
                            $("#tipMsg").text(Label.removeSuccLabel);
                            break;
                        case "REMOVE_COMMENT_FAIL_":
                            $("#tipMsg").text(Label.removeFailLabel);
                            break;
                        default:
                            break;
                    }
                    $("#loadMsg").text(Label.loadingLabel);
                } catch (e) {
                    console.error(e);
                }
            }, {
                "oId": id
            });   
        }
    }
};

/*
     * 注册到 admin 进行管理 
     */
admin.register["comment-list"] =  {
    "obj": admin.commentList,
    "init": admin.commentList.init,
    "refresh": admin.commentList.getList
}