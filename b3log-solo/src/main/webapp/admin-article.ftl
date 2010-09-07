<div class="form paddingTop12" id="aticltPanel">
    <div class='left label'>
        ${title1Label}
    </div>
    <div class="left input">
        <input id="title" type="text"/>
    </div>
    <div class="clear"></div>
    <div class="label">
        ${content1Label}
    </div>
    <div class="marginRBL12">
        <textarea rows="15" id="articleContent" name="articleContent" style="width: 99%;"></textarea>
    </div>
    <div class="left label">${tags1Label}</div>
    <div class="left input">
        <input id="tag" type="text"/>
    </div>
    <div class="clear"></div>
    <div class="left label">${abstract1Label}</div>
    <div class="left input">
        <textarea id="abstract" rows="3"></textarea>
    </div>
    <div class="clear"></div>
    <div class="right label">
        <button id="submitArticle">${postLabel}</button>
    </div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    var init = function () {
        $("#tipMsg").text("${loadingLabel}");
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
        $("#tipMsg").text("");
    }
    init();
    
    var validateArticle = function () {
        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#title").focus().val("");
        } else if (tinyMCE.get('articleContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${contentEmptyLabel}");
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${tagsEmptyLabel}");
            $("#tag").focus().val("");
        } else if($("#abstract").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${abstractEmptyLabel}");
            $("#abstract").focus().val("");
        } else {
            return true;
        }
        return false;
    }
    
    var addArticle = function () {
        if (validateArticle()) {
            $("#tipMsg").text("${loadingLabel}");
            var tagArray = $("#tag").val().split(","),
            tagsString = "";

            for (var i = 0; i < tagArray.length; i++) {
                tagsString += tagArray[i].replace(/(^\s*)|(\s*$)/g, "");
            }

            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": $("#abstract").val(),
                    "articleTags": $("#tag").val()
                }
            };

            jsonRpc.articleService.addArticle(function (result, error) {
                switch (result.sc) {
                    case "ADD_ARTICLE_SUCC":
                        $("#content").load("admin-article-list.do", function () {
                            $("#tipMsg").text("${addSuccLabel}");
                            setCurrentNaviStyle(1);
                        });
                        break;
                    default:
                        $("#tipMsg").text("${addFailLabel}");
                        break;
                }
            }, requestJSONObject);
        }
    }

    var updateArticle = function () {
        if (validateArticle()) {
            $("#tipMsg").text("${loadingLabel}");
            var tagArray = $("#tag").val().split(","),
            tagsString = "";

            for (var i = 0; i < tagArray.length; i++) {
                tagsString += tagArray[i].replace(/(^\s*)|(\s*$)/g, "");
            }
            
            var requestJSONObject = {
                "article": {
                    "oId": $("#title").data("oId"),
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": $("#abstract").val(),
                    "articleTags": tagsString
                }
            };
            
            jsonRpc.articleService.updateArticle(function (result, error) {
                switch (result.sc) {
                    case "UPDATE_ARTICLE_SUCC":
                        $("#content").load("admin-article-list.do", function () {
                            $("#tipMsg").text("${updateSuccLabel}");
                            setCurrentNaviStyle(1);
                        });
                        break;
                    default:
                        $("#tipMsg").text("${updateFailLabel}");
                        break;
                }
            }, requestJSONObject);
        }
    }
</script>
