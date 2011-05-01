<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <#include "common-head.ftl">
        <title>${allTagsLabel} - ${blogTitle}</title>
    </head>
    <body>
        <div id="header">
            <div class="inner">
                <div class="content">
                    <#include "header.ftl">
                </div>
            </div>
        </div>
        <div id="container">
            <div id="content">
                <div id="main">
                    <div id="tagsPanel"></div>
                    <script type="text/javascript">
                        var util=new Util();
                        util.setTagsPanel([
                            <#list tags as tag>
                            {
                                tagNameURLEncoded: "${tag.tagTitle?url('UTF-8')}",
                                tagName: "${tag.tagTitle}",
                                tagCount: '${tag.tagPublishedRefCount}',
                                tagId: '${tag.oId}'
                            }
                            <#if tag_has_next>,</#if>
                            </#list>
                        ]);
                    </script>
                </div>
                <!-- sidebar START -->
                <div id="sidebar">
                    <#include "sidebar.ftl">
                </div>
                <!-- sidebar END -->
                <div class="fixed"></div>
                <div id="bottom">
                    <div class="postnav">
                        <span class="alignleft"></span>
                        <span class="alignright"></span>
                        <div class="fixed"></div>
                    </div>
                    <div class="anchor">
                        <span><a href="#" onclick="MGJS.goTop();return false;">置顶</a></span>
                    </div>
                    <div class="fixed"></div>
                </div>
                <div class="fixed"></div>
            </div>
        </div>
        <div id="footer">
            <div class="inner">
                <div class="content">
                    <#include "footer.ftl">
                </div>
            </div>
        </div>
    </body>
</html>
