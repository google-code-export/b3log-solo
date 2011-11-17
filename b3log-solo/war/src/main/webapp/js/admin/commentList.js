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
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Nov 17, 2011
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
    init: function (page) {
        this.tablePagination.buildTable([ {
            text: "",
            index: "title",
            width: 300,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentContentLabel,
            index: "content",
            minWidth: 300,
            style: "padding-left: 12px;"
        }], true);
        this.tablePagination.initPagination();
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
            url: "/console/comments/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            success: function(result, textStatus){
                if (!result.sc) {
                    $("#tipMsg").text(result.msg);
                    
                    return;
                }
                
                that.pageInfo.currentPage = pageNum;
                var comments = result.comments,
                commentsData = [];
                for (var i = 0; i < comments.length; i++) {
                    commentsData[i] = {};
                    
                    commentsData[i].content = Util.replaceEmString(comments[i].commentContent);
                            
                    commentsData[i].title = "<div style='margin:9px 0'><a href='" + comments[i].commentSharpURL + 
                    "' target='_blank'>" + comments[i].commentTitle +
                    "</a></div>";
                
                    commentsData[i].title += "<div class='left small-head'><img src='" + comments[i].commentThumbnailURL + "'/><br/>";
                    if ("http://" === comments[i].commentURL) {
                        commentsData[i].title += comments[i].commentName;
                    } else {
                        commentsData[i].title += "<a href='" + comments[i].commentURL +
                        "' target='_blank' class='no-underline'>" + comments[i].commentName + 
                        "</a>";
                    }
                    
                    commentsData[i].title += "</div><div class='left'>";
                            
                    commentsData[i].title += Label.commentEmailLabel + ": <a href='mailto:" + comments[i].commentEmail +
                    "'>" + comments[i].commentEmail + "</a><br/>";
                        
                    commentsData[i].title += Label.createDateLabel + ": " +$.bowknot.getDate(comments[i].commentTime, 1);
                            
                    var type = "Article"
                    if (comments[i].type === "pageComment") {
                        type = "Page"
                    }
                    commentsData[i].title += "<br/><a class='action' href='javascript:void(0)' onclick=\"admin.commentList.del('" +
                    comments[i].oId + "', '" + type + "')\">" + Label.removeLabel + "</a>";
                
                    commentsData[i].title += "</div>";
                }
                
                that.tablePagination.updateTablePagination(commentsData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /* 
     * 删除评论
     * @id 评论 id 
     * @type 评论类型：文章/自定义页面
     */
    del: function (id, type) {
        if (confirm(Label.confirmRemoveLabel)) {
            $("#loadMsg").text(Label.loadingLabel);
            
            $.ajax({
                url: "/console/" + type.toLowerCase() + "/comment/" + id,
                type: "DELETE",
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                     
                    if (!result.sc) {
                        return;
                    }
                    
                    admin.commentList.getList(admin.commentList.pageInfo.currentPage);
                    
                    $("#loadMsg").text("");
                }
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