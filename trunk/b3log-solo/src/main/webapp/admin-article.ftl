<div class="form" widdiv="99%" cellpadding="0px" cellspacing="9px">
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
    <div>
        <textarea rows="15" id="articleContent" style="width: 99%;" name="articleContent"></textarea>
    </div>
    <div class="left label">${tag1Label}</div>
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
</div>
<script type="text/javascript">
    var init = function () {
        $("#tipMsg").text("${loadingLabel}").show();
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
        $("#tipMsg").text("").hide();
    }

    init();
    
    var validateArticle = function () {
        $("#tipMsg").text("${loadingLabel}").show();
        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}").show();
            $("#title").focus().val("");
        } else if (tinyMCE.get('articleContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${contentEmptyLabel}").show();
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${tagsEmptyLabel}").show();
            $("#tag").focus().val("");
        } else if($("#abstract").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${abstractEmptyLabel}").show();
            $("#abstract").focus().val("");
        } else {
            return true;
        }
        return false;
    }
    
    var addArticle = function () {
        if (validateArticle()) {
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
            var result = jsonRpc.articleService.addArticle(requestJSONObject);
            switch (result.sc) {
                case "ADD_ARTICLE_SUCC":
                    $("#tipMsg").text("${addSuccLabel}").show();
                    $("#content").load("admin-article-list.do");
                    setCurrentNaviStyle(1);
                    break;
                default:
                    break;
            }
        }
    }

    var updateArticle = function () {
        if (validateArticle()) {
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
            var result = jsonRpc.articleService.updateArticle(requestJSONObject);
            switch (result.sc) {
                case "UPDATE_ARTICLE_SUCC":
                    $("#tipMsg").text("${updateSuccLabel}").show();
                    $("#content").load("admin-article-list.do");
                    setCurrentNaviStyle(1);
                    break;
                default:
                    break;
            }
        }
    }
</script>
