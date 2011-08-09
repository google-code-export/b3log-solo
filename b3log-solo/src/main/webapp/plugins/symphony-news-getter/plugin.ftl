<link type="text/css" rel="stylesheet" href="/plugins/symphony-news-getter/style.css"/>
<div id="symphonyNewsGetterPanel">
    <div class="module-panel">
        <div class="module-header">
            <h2><a href="http://symphony.b3log.org" target="_blank">Symphony</a> ${CommunityAnnounceLabel}</h2>
        </div>
        <div class="module-body padding12">
            <div id="symphonyNewsGetter">
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    plugins.symphonyNewsGetter = {
        init: function () {
            $("#loadMsg").text("${loadingLabel}");
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
                    $("#loadMsg").text("");
                }
            });
        }
    };
    
    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "symphonyNewsGetter",
        "path": "/main/panel1 ",
        "content": $("#symphonyNewsGetterPanel").html()
    });
    
    // 移除现有内容
    $("#symphonyNewsGetterPanel").remove();
</script>