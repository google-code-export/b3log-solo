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
 *  common comment for admin
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.1, Jun 28, 2011
 */

admin.comment = { 
    /*
     * 打开评论窗口
     * @id 该评论对应的 id
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    open: function (id, fromId) {
        this.getList(id, fromId);
        $("#" + fromId + "Comments").dialog("open");
    },
    
    /*
     * 获取评论列表
     * @id 该评论对应的 id
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    getList: function (id, fromId) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#" + fromId + "Comments").html("");
        
        var from = "Article";
        if (fromId === "page") {
            from = "Page";
        }
        jsonRpc.commentService["getCommentsOf" + from](function (result, error) {
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
                            commentsHTML += "</span><span title='" + Label.removeLabel + "' class='right deleteIcon' onclick=\"admin.comment.del('"
                            + comments[i].oId + "', '" + fromId + "')\"></span><span class='right'><a href='mailto:"
                            + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                            + $.bowknot.getDate(comments[i].commentDate.time, 1)
                            + "&nbsp;</span><div class='clear'></div></div><div class='margin12'>"
                            + contentHTML + "</div>";
                        }
                        if ("" === commentsHTML) {
                            commentsHTML = Label.noCommentLabel;
                        }
                        $("#" + fromId + "Comments").html(commentsHTML);
                        break;
                    default:
                        break;
                };
                $("#loadMsg").text("");
            } catch (e) {}
        }, {
            "oId": id
        });
    },
    
    /*
     * 删除评论
     * @id 评论 id
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    del: function (id, fromId) {
        var isDelete = confirm(Label.confirmRemoveLabel);
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            var from = "Article";
            if (fromId === "page") {
                from = "Page";
            }
            jsonRpc.commentService["removeCommentOf" + from](function (result, error) {
                try {
                    switch (result.sc) {
                        case "REMOVE_COMMENT_FAIL_FORBIDDEN":
                            $("#tipMsg").text(Label.forbiddenLabel);
                            break;
                        case "REMOVE_COMMENT_SUCC":
                            admin.comment.getList(id, fromId);
                            $("#tipMsg").text(Label.removeSuccLabel);
                            break;
                        default:
                            $("#tipMsg").text("");
                            $("#loadMsg").text("");
                            break;
                    }
                } catch (e) {}
            }, {
                "oId": id
            });
        }
    }
};
