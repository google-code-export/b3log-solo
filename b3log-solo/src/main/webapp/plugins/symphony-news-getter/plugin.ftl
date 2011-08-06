<script type="text/javascript">
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
            
            var listHtml = "<ul>";
            for (var i = 0; i < articles.length; i++) {
                var article = articles[i];
                var title = article.articleTitle;
                var articleLiHtml = "<li>"
                    + "<a target='_blank' href='" + article.articlePermalink + "'>"
                    +  title + "</a></li>"
                listHtml += articleLiHtml
            }
            listHtml += "</ul>";
            
            /*
             * 添加插件
             */
            admin.plugin.add({
                "id": "symphony-news-getter",
                "text": "",
                "path": "/",
                "content": listHtml
            });
        }
    });
    
</script>