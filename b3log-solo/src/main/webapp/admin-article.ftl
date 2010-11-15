<div class="form paddingTop12">
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
    <div class="marginBottom12 marginLeft12">
        <textarea id="articleContent" name="articleContent"
                  style="height: 500px;width: 99%;"></textarea>
    </div>
    <div class="left label">${tags1Label}</div>
    <div class="left input">
        <input id="tag" type="text"/>
    </div>
    <div class="clear"></div>
    <div class="left label">${abstract1Label}</div>
    <div class="left input">
        <textarea id="abstract" style="height: 200px;width: 100%;" name="abstract"></textarea>
    </div>
    <div class="clear"></div>
    <div class="left label">${permalink1Label}</div>
    <div class="left input">
        <input id="permalink" type="text"/>
    </div>
    <div class="clear"></div>
    <div class="right label">
        <button id="submitArticle">${postLabel}</button>
    </div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    var initArticle = function () {
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
                    height: 160,
                    data: tags
                });
            }
        });

        // editor
        tinyMCE.init({
            // General options
            mode : "exact",
            elements : "articleContent, abstract",
            theme : "advanced",
            plugins : "style,advhr,advimage,advlink,preview,media,paste,fullscreen,syntaxhl",

            // Theme options
            theme_advanced_buttons1 : "forecolor,backcolor,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons2 : "bullist,numlist,outdent,indent,|,undo,redo,|,sub,sup,blockquote,charmap,image,iespell,media,|,advhr,link,unlink,anchor,cleanup,|,pastetext,pasteword,code,preview,fullscreen,syntaxhl",
            theme_advanced_buttons3 : "",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,

            extended_valid_elements: "pre[name|class]",

            relative_urls: false,
            remove_script_host: false
        });

        $("#loadMsg").text("");
    }
    initArticle();


    var validateArticle = function () {
        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${titleEmptyLabel}");
            $("#title").focus().val("");
        } else if (tinyMCE.get('articleContent').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${contentEmptyLabel}");
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${tagsEmptyLabel}");
            $("#tag").focus().val("");
        } else if(tinyMCE.get('abstract').getContent().replace(/\s/g, "") === "") {
            $("#tipMsg").text("${abstractEmptyLabel}");
        } else {
            return true;
        }
        return false;
    }

    var addArticle = function () {
        if (validateArticle()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(",");
            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": $.bowknot.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val()
                }
            };

            jsonRpc.articleService.addArticle(function (result, error) {
                switch (result.status.code) {
                    case "ADD_ARTICLE_FAIL_DUPLICATED_PERMALINK":
                        var msg = "${addFailLabel}, ${duplicatedPermalinkLabel}";
                        $("#tipMsg").text(msg);
                        break;
                    case "ADD_ARTICLE_SUCC":
                        var events = result.status.events;
                        if (events) {
                            var msg = "${addSuccLabel}";
                            if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                msg += ", ${syncCSDNBlogFailLabel}: "
                                    + events.blogSyncCSDNBlog.msg;
                            }

                            if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                msg += ", ${syncCnBlogsFailLabel}: "
                                    + events.blogSyncCnBlogs.msg;
                            }

                            if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                msg += ", ${syncBlogJavaFailLabel}: "
                                    + events.blogSyncBlogJava.msg;
                            }

                            if ("POST_TO_BUZZ_FAIL" === events.postToGoogleBuzz.code) {
                                msg += ", ${postToBuzzFailLabel}";
                            }
                            $("#tipMsg").text(msg);
                            $("#article-listTab").click();
                        }
                        break;
                    default:
                        $("#tipMsg").text("${addFailLabel}");
                        break;
                }
                $("loadMsg").text("");
            }, requestJSONObject);
        }
    }

    var updateArticle = function () {
        if (validateArticle()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(",");

            var requestJSONObject = {
                "article": {
                    "oId": $("#title").data("oId"),
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": $.bowknot.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val()
                }
            };

            jsonRpc.articleService.updateArticle(function (result, error) {
                switch (result.status.code) {
                    case "UPDATE_ARTICLE_FAIL_DUPLICATED_PERMALINK":
                        var msg = "${addFailLabel}, ${duplicatedPermalinkLabel}";
                        $("#tipMsg").text(msg);
                        break;
                    case "UPDATE_ARTICLE_SUCC":
                        var events = result.status.events;
                        if (events) {
                            var msg = "${updateSuccLabel}";
                            if ("BLOG_SYNC_FAIL" === events.blogSyncCSDNBlog.code) {
                                msg += ", ${syncCSDNBlogFailLabel}: "
                                    + events.blogSyncCSDNBlog.msg;
                            }

                            if ("BLOG_SYNC_FAIL" === events.blogSyncCnBlogs.code) {
                                msg += ", ${syncCnBlogsFailLabel}: "
                                    + events.blogSyncCnBlogs.msg;
                            }

                            if ("BLOG_SYNC_FAIL" === events.blogSyncBlogJava.code) {
                                msg += ", ${syncBlogJavaFailLabel}: "
                                    + events.blogSyncBlogJava.msg;
                            }
                            
                            $("#tipMsg").text(msg);
                            $("#article-listTab").click();
                        }
                        break;
                    default:
                        $("#tipMsg").text("${updateFailLabel}");
                        break;
                }
                $("loadMsg").text("");
            }, requestJSONObject);
        }
    }
</script>
