<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta name="robots" content="none" />
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link type="text/css" rel="stylesheet" href="/css/default-admin${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link rel="icon" type="image/png" href="/favicon.png" />
        <script type="text/javascript" src="/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/jquery/jquery.bowknot.min.js?${staticResourceVersion}" charset="utf-8"></script>
        <script type="text/javascript" src="/js/lib/tiny_mce/tiny_mce.js" charset="utf-8"></script>
    </head>
    <body onhashchange="admin.setCurByHash();">
        <div id="loadMsg">${loadingLabel}</div>
        <div id="tipMsg"></div>
        <div id="allPanel">
            <div id="top">
                <a href="http://b3log-solo.googlecode.com" target="_blank" class="hover">
                    B3log Solo
                </a>
                <span class="right">
                    <span>${userName}</span><a href="/" title='${indexLabel}'>${indexLabel}</a><a href='javascript:admin.logout();' title='${logoutLabel}'>${logoutLabel}</a>
                </span>
                <div class="clear"></div>
            </div>
            <div id="tabs">
                <ul>
                    <li>
                        <div id="tabs_main">
                            <a href="#main">
                                <span class="left usersIcon"></span>${adminIndexLabel}
                            </a>
                        </div>
                    </li>
                    <li>
                        <div onclick="admin.collapseNav(this)">
                            <span class="left postIcon"></span>
                            ${articleLabel}
                            <span class="ico-arrow-up"></span>
                        </div>
                        <ul id="tabArticleMgt">
                            <li>
                                <div id="tabs_article">
                                    <a href="#article/article" onclick="admin.article.prePost()">${postArticleLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_article-list">
                                    <a href="#article/article-list">${articleListLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_draft-list">
                                    <a href="#article/draft-list">${draftListLabel}</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div id="tabs_comment-list">
                            <a href="#comment-list">
                                <span class="left commentIcon"></span>${commentListLabel}
                            </a>
                        </div>
                    </li>
                    <li>
                        <div id="tabToolsTitle" onclick="admin.collapseNav(this)">
                            <span class="left preferenceIcon"></span>
                            ${ToolLabel}
                            <span class="ico-arrow-down"></span>
                        </div>
                        <ul class="none" id="tabTools">
                            <li>
                                <div id="tabs_preference">
                                    <a href="#tools/preference">${preferenceLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_page-list">
                                    <a href="#tools/page-list">${navMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_link-list">
                                    <a href="#tools/link-list">${linkManagementLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_user-list">
                                    <a href="#tools/user-list">${userManageLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_plugin-list">
                                    <a href="#tools/plugin-list">${pluginMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_others">
                                    <a href="#tools/others">${othersLabel}</a>
                                </div>
                            </li>  
                        </ul>
                    </li>
                    <li>
                        <div id="tabs_about">
                            <a href="#about">
                                <span class="left aboutIcon"></span>${aboutLabel}
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
            <div id="tabsPanel">
                <div id="tabsPanel_main" class="none"></div>
                <div id="tabsPanel_article" class="none"></div>
                <div id="tabsPanel_article-list" class="none"></div>
                <div id="tabsPanel_draft-list" class="none"></div>
                <div id="tabsPanel_link-list" class="none"></div>
                <div id="tabsPanel_preference" class="none"></div>
                <div id="tabsPanel_page-list" class="none"></div>
                <div id="tabsPanel_others" class="none"></div>
                <div id="tabsPanel_user-list" class="none"></div>
                <div id="tabsPanel_comment-list" class="none"></div>
                <div id="tabsPanel_plugin-list" class="none"></div>
                <div id="tabsPanel_about" class="none"></div>
            </div>
            <div class="footer">
                Powered by
                <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
                    ${b3logLabel}&nbsp;
                    <span style="color: orangered; font-weight: bold;">Solo</span></a>, ver ${version}
            </div>
        </div>
        <script type="text/javascript" src="/js/common.js" charset="utf-8"></script>
        <#if "" == miniPostfix>
        <script type="text/javascript" src="/js/admin/admin.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/editor.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/editorTinyMCE.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/tablePaginate.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/article.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/comment.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/articleList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/draftList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/pageList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/others.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/linkList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/preference.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/pluginList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/userList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/commentList.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/plugin.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/main.js" charset="utf-8"></script>
        <script type="text/javascript" src="/js/admin/about.js" charset="utf-8"></script>
        <#else>
        <script type="text/javascript" src="/js/admin/latkeAdmin${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
        </#if>
        <#include "admin-label.ftl">
        ${plugins}
        <script type="text/javascript">
            admin.inited();
        </script>
    </body>
</html>