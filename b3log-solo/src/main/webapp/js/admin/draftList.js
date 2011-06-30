/*
 * Copyright (c) 2011, B3log Team
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
 * draft list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.3, Jun 30, 2011
 */

/* draft-list 相关操作 */
admin.draftList = {
    tablePagination:  new TablePaginate("draft"),
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function () {
        this.tablePagination.buildTable([{
            text: Label.titleLabel,
            index: "title",
            minWidth: 110,
            style: "padding-left: 12px;font-size:16px;"
        }, {
            text: Label.tagsLabel,
            index: "tags",
            width: 400,
            style: "padding-left: 12px;"
        }, {
            text: Label.authorLabel,
            index: "author",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }, {
            text: Label.viewLabel,
            width: 80,
            index: "articleViewCount",
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(1);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        var that = this;
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE,
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
                            articleData[i].tags = articles[i].articleTags;
                            articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                            articleData[i].comments = articles[i].articleCommentCount;
                            articleData[i].articleViewCount = articles[i].articleViewCount;
                            articleData[i].author = articles[i].authorName;
                            articleData[i].title = articles[i].articleTitle;
                            articleData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.article.get('" + articles[i].oId + "', false);\">" + Label.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.del('" + articles[i].oId + "', 'draft'\">" + Label.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + articles[i].oId + "', 'draft')\">" + Label.commentLabel + "</a>";
                        }
                    
                        that.tablePagination.updateTablePagination(articleData, pageNum, result.pagination.paginationPageCount);
                        break;
                    default:
                        break;
                }
                $("#loadMsg").text("");
            } catch (e) {
                console.error(e);
            }
        }, requestJSONObject);
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["draft-list"] =  {
    "obj": admin.draftList,
    "init": admin.draftList.init,
    "refresh": admin.draftList.getList
}