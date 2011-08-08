<div id="symphonyNewsGetter"></div>
<script type="text/javascript">
    plugins.symphonyNewsGetter = {
        hash: "",
        getNews: function () {
            $.ajax({
                url: "http://symphony.b3log.org:80/get-news",
                type: "GET",
                dataType:"jsonp",
                jsonp: "callback",
                error: function(){
                    alert("Error loading B3log Announcement error!");
                },
                success: function(data, textStatus){
                    var articles = data.articles;
                    if (0 === articles.length) {
                        return;
                    }
            
                    var listHTML = "<ul>";
                    for (var i = 0; i < articles.length; i++) {
                        var article = articles[i];
                        var title = article.articleTitle;
                        var articleLiHtml = "<li>"
                            + "<a target='_blank' href='" + article.articlePermalink + "'>"
                            +  title + "</a></li>"
                        listHTML += articleLiHtml
                    }
                    listHTML += "</ul>";
                    
                    $("#symphonyNewsGetter").html(listHTML);
                }
            });
        }
    };
    
    /*
     * 注册到 admin 进行管理 
     */
    admin.register["symphony-news-getter"] =  {
        "obj": plugins.symphonyNewsGetter,
        "init": plugins.symphonyNewsGetter.getNews
    };
    
    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "symphony-news-getter",
        "text": "",
        "path": "/",
        "content": $("#symphonyNewsGetter").html()
    });
    
    // 移除现有内容
    $("#cachePlugin").remove();
</script>