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
 * article list for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 29, 2011
 */

/* article-list 相关操作 */
admin.articleList = {
    tablePagination:  new TablePaginate("article"),
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: Label.titleLabel,
            index: "title",
            minWidth: 110,
            style: "padding-left: 12px;font-size:14px;"
        }, {
            text: Label.authorLabel,
            index: "author",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }, {
            text: Label.viewLabel,
            width: 60,
            index: "articleViewCount",
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 90,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(page);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        
        $.ajax({
            url: "/console/articles/status/published/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    $("#tipMsg").text(result.msg);
                    
                    return;
                }
                
                var articles = result.articles,
                articleData = [];
                for (var i = 0; i < articles.length; i++) {
                    articleData[i] = {};
                    articleData[i].title = "<a href='" + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                    + articles[i].articleTitle + "</a><span class='table-tag'>" + articles[i].articleTags + "</span>";
                    articleData[i].date = $.bowknot.getDate(articles[i].articleCreateTime);
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
                    
                that.tablePagination.updateTablePagination(articleData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },

    /* 
     * 制定或者取消置顶 
     * @it 触发事件的元素本身
     * @id 草稿 id
     */
    popTop: function (it, id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var $it = $(it);
        
        if ($it.html() === Label.putTopLabel) {
            $.ajax({
                url: "/console/article/puttop/" + id,
                type: "PUT",
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    $it.html(Label.cancelPutTopLabel);
                }
            });
        } else {
            $.ajax({
                url: "/console/article/canceltop/" + id,
                type: "PUT",
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    $it.html(Label.putTopLabel);
                }
            });
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