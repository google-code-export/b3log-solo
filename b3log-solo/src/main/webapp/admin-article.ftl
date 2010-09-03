<table class="form" width="99%" cellpadding="0px" cellspacing="9px">
    <tbody>
        <tr>
            <th width="40px">
                ${title1Label}
            </th>
            <td>
                <input id="title" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                ${content1Label}
            </th>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2">
                <textarea rows="15" id="articleContent" name="articleContent"></textarea>
            </td>
        </tr>
        <tr>
            <th>${tag1Label}</th>
            <td>
                <input id="tag" type="text"/>
            </td>
        </tr>
        <tr>
            <th valign="top">${abstract1Label}</th>
            <td>
                <textarea id="abstract" rows="3"></textarea>
            </td>
        </tr>
        <tr>
            <th colspan="2">
                <button id="submitArticle">${postLabel}</button>
            </th>
        </tr>
    </tbody>
</table>
<script type="text/javascript">
    var init = function () {
        // submit action
        $("#submitArticle").click(function () {
            if ($("#title").data("oId")) {
                updateArticle();
            } else {
                addArticle();
            }
        });

        // tag auto completed
        jsonRpc.tagService.getTags(function (result, error) {
            if (result.length > 0) {
                var tags = [];
                for (var i = 0; i < result.length; i++) {
                    tags.push(result[i].tagTitle);
                }
                $("#tag").completed({
                    data: tags
                });
            }
        })

        // editor
        tinyMCE.init({
            // General options
            mode : "exact",
            elements : "articleContent",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class]"
        });
    }

    init();
    
    var addArticle = function () {
        var requestJSONObject = {
            "article": {
                "articleTitle": $("#title").val(),
                "articleContent": tinyMCE.get('articleContent').getContent(),
                "articleAbstract": $("#abstract").val(),
                "articleTags": $("#tag").val()
            }
        };
        var result = jsonRpc.articleService.addArticle(requestJSONObject);
        switch (result.sc) {
            case "ADD_ARTICLE_SUCC":
                setCurrentNaviStyle(1);
                $("#content").load("admin-article-list.do", function () {
                    //KE.remove("articleContent");
                });
                $("#tipMsg").text("${addSuccLabel}").show();
                break;
            default:
                break;
        }
    }

    var updateArticle = function () {
        var requestJSONObject = {
            "article": {
                "oId": $("#title").data("oId"),
                "articleTitle": $("#title").val(),
                "articleContent": tinyMCE.get('articleContent').getContent(),
                "articleAbstract": $("#abstract").val(),
                "articleTags": $("#tag").val()
            }
        };
        var result = jsonRpc.articleService.updateArticle(requestJSONObject);
        switch (result.sc) {
            case "UPDATE_ARTICLE_SUCC":
                setCurrentNaviStyle(1);
                $("#content").load("admin-article-list.do", function () {
                    //KE.remove("articleContent");
                });
                $("#tipMsg").text("${updateSuccLabel}").show();
                break;
            default:
                break;
        }
    }
</script>
