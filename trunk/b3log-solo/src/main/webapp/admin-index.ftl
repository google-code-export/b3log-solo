<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css"/>
        <link type="text/css" rel="stylesheet" href="/css/default-admin${miniPostfix}.css"/>
        <link type="text/css" rel="stylesheet" href="/css/default-bowknot${miniPostfix}.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
        <script type="text/javascript" src="/js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="/js/lib/jquery/jquery.bowknot.min.js"></script>
        <script type="text/javascript" src="/js/lib/tiny_mce/tiny_mce.js"></script>
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
                        <ul class="collapsed">
                            <li>
                                <div id="tabs_article">
                                    <a href="#article" onclick="admin.article.prePost()">${postArticleLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_article-list">
                                    <a href="#article-list">${articleListLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_draft-list">
                                    <a href="#draft-list">${draftListLabel}</a>
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
                        <div onclick="admin.collapseNav(this)">
                            <span class="left preferenceIcon"></span>
                            Tool
                            <span class="ico-arrow-down"></span>
                        </div>
                        <ul class="none">
                            <li>
                                <div id="tabs_file-list">
                                    <a href="#file-list">${fileListLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_page-list">
                                    <a href="#page-list">${pageMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_link-list">
                                    <a href="#link-list">${linkManagementLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_preference">
                                    <a href="#preference">${preferenceLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_user-list">
                                    <a href="#user-list">${userManageLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_plugin-list">
                                    <a href="#plugin-list">${pluginMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tabs_others">
                                    <a href="#others">${othersLabel}</a>
                                </div>
                            </li>   
                        </ul>
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
                <div id="tabsPanel_file-list" class="none"></div>
                <div id="tabsPanel_others" class="none"></div>
                <div id="tabsPanel_user-list" class="none"></div>
                <div id="tabsPanel_comment-list" class="none"></div>
                <div id="tabsPanel_plugin-list" class="none"></div>
            </div>
            <div class="footer">
                Powered by
                <a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
                    <span style="color: orange;">B</span>
                    <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                    <span style="color: green;">L</span>
                    <span style="color: red;">O</span>
                    <span style="color: blue;">G</span>&nbsp;
                    <span style="color: orangered; font-weight: bold;">Solo</span></a>, ver ${version}
            </div>
        </div>
        <script type="text/javascript" src="js/common.js"></script>
        <script type="text/javascript" src="js/admin/admin.js"></script>
        <script type="text/javascript" src="js/admin/tablePaginate.js"></script>
        <script type="text/javascript" src="js/admin/article.js"></script>
        <script type="text/javascript" src="js/admin/comment.js"></script>
        <script type="text/javascript" src="js/admin/articleList.js"></script>
        <script type="text/javascript" src="js/admin/draftList.js"></script>
        <script type="text/javascript" src="js/admin/fileList.js"></script>
        <script type="text/javascript" src="js/admin/pageList.js"></script>
        <script type="text/javascript" src="js/admin/others.js"></script>
        <script type="text/javascript" src="js/admin/linkList.js"></script>
        <script type="text/javascript" src="js/admin/preference.js"></script>
        <script type="text/javascript" src="js/admin/pluginList.js"></script>
        <script type="text/javascript" src="js/admin/userList.js"></script>
        <script type="text/javascript" src="js/admin/commentList.js"></script>
        <script type="text/javascript" src="js/admin/plugin.js"></script>
        <script type="text/javascript" src="js/admin/main.js"></script>
        <#include "admin-label.ftl">
        ${plugins}
        <script type="text/javascript">
            admin.inited();
        </script>
    </body>
</html>