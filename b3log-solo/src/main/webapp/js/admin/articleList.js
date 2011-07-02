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
 * article list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">LiYuan Li</a>
 * @version 1.0.0.3, Jun 30, 2011
 */

/* article-list 相关操作 */
admin.articleList = {
    tablePagination:  new TablePaginate("article"),
    
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
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        var requestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": Label.PAGE_SIZE,
            "paginationWindowSize": Label.WINDOW_SIZE,
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
                            articleData[i].tags = articles[i].articleTags;
                            articleData[i].title = "<a href='" + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                            + articles[i].articleTitle + "</a>";
                            articleData[i].date = $.bowknot.getDate(articles[i].articleCreateDate.time, 1);
                            articleData[i].comments = articles[i].articleCommentCount;
                            articleData[i].articleViewCount = articles[i].articleViewCount;
                            articleData[i].author = articles[i].authorName;
                            
                            var topClass = articles[i].articlePutTop ? Label.cancelPutTopLabel : Label.putTopLabel;
                            articleData[i].expendRow = "<a target='_blank' href='" + articles[i].articlePermalink + "'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.get('" + articles[i].oId + "', true)\">" + Label.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.del('" + articles[i].oId + "', 'article')\">" + Label.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.articleList.popTop(this, '" + articles[i].oId + "')\">" + topClass + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + articles[i].oId + "', 'article')\">" + Label.commentLabel + "</a>";
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
    },

    /* 
     * 制定或者取消置顶 
     * @it 触发事件的元素本身
     * @id 草稿 id
     */
    popTop: function (it, id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var requestJSONObject = {
            "oId": id
        };
        var $it = $(it);
        if ($it.html() === Label.putTopLabel) {
            jsonRpc.articleService.putTopArticle(function (result, error) {
                try {
                    switch (result.sc) {
                        case "PUT_TOP_ARTICLE_SUCC":
                            $it.html(Label.cancelPutTopLabel);
                            $("#tipMsg").text(Label.putTopSuccLabel);
                            break;
                        case "PUT_TOP_ARTICLE_FAIL_":
                            $("#tipMsg").text(Label.putTopFailLabel);
                            break;
                        case "PUT_TOP_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
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
                            $it.html(Label.putTopLabel);
                            $("#tipMsg").text(Label.cancelTopSuccLabel);
                            break;
                        case "CANCEL_TOP_ARTICLE_FAIL_":
                            $("#tipMsg").text(Label.cancelTopFailLabel);
                            break;
                        case "CANCEL_TOP_ARTICLE_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
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
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["article-list"] =  {
    "obj": admin.articleList,
    "init": admin.articleList.init,
    "refresh": admin.articleList.getList
}