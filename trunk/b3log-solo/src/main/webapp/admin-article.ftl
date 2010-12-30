<table width="100%" cellpadding="0" cellspacing="9px" class="form">
    <tr>
        <th>
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
            <textarea id="articleContent" name="articleContent"
                      style="height: 500px;width:100%;"></textarea>
        </td>
    </tr>
    <tr>
        <th>
            ${tags1Label}
        </th>
        <td>
            <input id="tag" type="text"/>
        </td>
    </tr>
    <tr>
        <th valign="top">
            ${abstract1Label}
        </th>
        <td>
            <textarea id="abstract" style="height: 200px;width: 100%;" name="abstract"></textarea>
        </td>
    </tr>
    <tr>
        <th>
            ${permalink1Label}
        </th>
        <td>
            <input id="permalink" type="text"/>
        </td>
    </tr>
    <tr>
        <th>
            ${sign1Label}
        </th>
        <td class="signs">
            <button id="articleSign1">${signLabel} 1</button>
            <button id="articleSign2">${signLabel} 2</button>
            <button id="articleSign3">${signLabel} 3</button>
            <button id="articleSign0">${noSignLabel}</button>
        </td>
    </tr>
    <tr>
        <th colspan="2">
            <button class="marginRight12" id="saveArticle">${saveLabel}</button>
            <button id="submitArticle">${publishLabel}</button>
            <button id="unSubmitArticle" class="none" onclick="unPublish();">${unPublishLabel}</button>
        </th>
    </tr>
</table>
<script type="text/javascript">
    var articleStatus = {};
    
    var unPublish = function () {
        jsonRpc.articleService.cancelPublishArticle(function (result, error) {
            if (result.sc === "CANCEL_PUBLISH_ARTICLE_SUCC") {
                $("#tipMsg").text("${unPulbishSuccLabel}");
                $("#draft-listTab").click();
            } else {
                $("#tipMsg").text("${unPulbishFailLabel}");
            }
        }, {oId: articleStatus.oId});
    }
    
    var beforeInitArticle = function () {
        articleStatus = $("#title").data("articleStatus");
        // set button status
        if (articleStatus) {
            if (articleStatus.isArticle) {
                $("#unSubmitArticle").show();
                $("#submitArticle").hide();
            }
        } else {
            $("#submitArticle").show();
            $("#unSubmitArticle").hide();
        }
    }
    
    var initArticle = function () {
        beforeInitArticle();

        // tag auto completed
        // TODO: add to beforeInitArticle()
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

        // submit action
        $("#submitArticle").click(function () {
            if (articleStatus) {
                updateArticle(true);
            } else {
                addArticle(true);
            }
        });
        
        $("#saveArticle").click(function () {
            if (articleStatus) {
                updateArticle(articleStatus.isArticle);
            } else {
                addArticle(false);
            }
        });

        // editor
        var localeString = "${localeString}";
        var language = localeString.substring(0, 2);
        tinyMCE.init({
            // General options
            language: language,
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

    var addArticle = function (articleIsPublished) {
        if (validateArticle()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(","),
            signId = 0;
            $(".signs button").each(function (i) {
                if (this.className === "selected") {
                    signId = i;
                }
            });

            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": $.bowknot.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSign_oId": signId
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

                            //                            if ("POST_TO_BUZZ_FAIL" === events.postToGoogleBuzz.code) {
                            //                                msg += ", ${postToBuzzFailLabel}";
                            //                            }
                            $("#tipMsg").text(msg);
                            $("#article-listTab").click();
                        } else {
                            $("#tipMsg").text("${addSuccLabel}");
                            $("#draft-listTab").click();
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

    var updateArticle = function (tag) {
        if (validateArticle()) {
            $("#loadMsg").text("${loadingLabel}");
            $("#tipMsg").text("");
            var tagArray = $("#tag").val().split(","),
            signId = 0;
            $(".signs button").each(function (i) {
                if (this.className === "selected") {
                    signId = i;
                }
            });
            
            var requestJSONObject = {
                "article": {
                    "oId": articleStatus.oId,
                    "articleTitle": $("#title").val(),
                    "articleContent": tinyMCE.get('articleContent').getContent(),
                    "articleAbstract": tinyMCE.get('abstract').getContent(),
                    "articleTags": $.bowknot.trimUnique(tagArray).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": tag,
                    "articleSign_oId": signId
                }
            };

            jsonRpc.articleService.updateArticle(function (result, error) {
                switch (result.status.code) {
                    case "UPDATE_ARTICLE_FAIL_FORBIDDEN":
                        $("#tipMsg").text("${forbiddenLabel}");
                        break;
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
                        } else {
                            $("#tipMsg").text("${updateSuccLabel}");
                            $("#draft-listTab").click();
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
