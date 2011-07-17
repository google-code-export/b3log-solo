<div id="commentTable">
</div>
<div id="commentPagination" class="right margin12">
</div>
<div class="clear"></div>

<script type="text/javascript">
    
    var commentListCurrentPage = 1;	
    
    var getCommentList = function (pageNum){
    	
        commentListCurrentPage = pageNum;
    
        var commentRequestJSONObject = {
            "paginationCurrentPageNum": pageNum,
            "paginationPageSize": adminUtil.PAGE_SIZE,
            "paginationWindowSize": adminUtil.WINDOW_SIZE           			 
        };
     	 
     	 	
    
        jsonRpc.commentService.getComments(function (result, error) {
	  		
            try {
                switch (result.sc) {
                    case "GET_COMMENTS_SUCC":
                        var comments = result.comments,
                        commentsData = [];
                        for (var i = 0; i < comments.length; i++) {
                            commentsData[i] = {};
                            commentsData[i].commentContent = comments[i].commentContent;
                            commentsData[i].commentArticleTitle = comments[i].commentArticleTitle;
                            commentsData[i].date = $.bowknot.getDate(comments[i].commentDate.time, 1);
                            commentsData[i].remove = "<div class='deleteIcon'></div>";
                            commentsData[i].id = comments[i].oId;
                            //commentsData[i].commentContent = comments[i].commentContent;
                        }
                         
                         
                        $("#commentList").table("update",{
                            data: [{
                                    groupName: "all",
                                    groupData: commentsData
                                }]
                        });
                        
	  	 			
	  	 			     
                        if (0 === result.pagination.paginationPageCount) {
                            result.pagination.paginationPageCount = 1;
                        }

                        $("#commentsPagination").paginate({
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
                //alert(e);
            }
        },commentRequestJSONObject); 
    }
	
    var loadArticleList = function () {
	 
        $("#commentList").table({
            resizable: true,
            colModel: [
            	
            	
                {
                    text: "${commentContentLabel}",
                    index: "commentContent",
                    width: 286,
                    style: "padding-left: 6px;"
                }
                ,
                {
                    text: "${titleLabel}",
                    index: "commentArticleTitle",
                    width: 286,
                    style: "padding-left: 6px;"
                }
                ,
                {
                    text: "${createDateLabel}",
                    index: "date",
                    width: 286,
                    style: "padding-left: 6px;"
                },
                {
                    textAlign: "center",
                    text: "${removeLabel}",
                    index: "remove",
                    width: 53,
                    bind: [{
                            'type': 'click',
                            'action': function (event,data) {
                                var id  =data.id;
                                //alert(data.id);
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
                                                    getCommentList(commentListCurrentPage);
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
                        }],
                    style: "cursor:pointer; margin-left:22px;"
                }
                
            ]   
        });
	 	
        $("#commentsPagination").paginate({
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
        
        getCommentList(1);
	 
    }
       
</script>
${plugins}