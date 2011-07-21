<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <link type="text/css" rel="stylesheet" href="/css/default-base.css"/>
        <link type="text/css" rel="stylesheet" href="/css/default-admin.css"/>
        <link type="text/css" rel="stylesheet" href="/css/default-bowknot.css"/>
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
                    <a href="#">${userName}</a><a href="/" title='${indexLabel}'>${indexLabel}</a><a href='javascript:admin.logout();' title='${logoutLabel}'>${logoutLabel}</a>
                </span>
                <div class="clear"></div>
            </div>
            <div id="tabs">
                <ul>
                    <li>
                        <div onclick="admin.collapseNav(this)">
                            <span class="left articlesIcon"></span>
                            文章
                            <span class="ico-arrow-up"></span>
                        </div>
                        <ul class="collapsed">
                            <li>
                                <div id="tab_article">
                                    <a href="#article" onclick="admin.article.clear()">${postArticleLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_article-list">
                                    <a href="#article-list">${articleListLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_draft-list">
                                    <a href="#draft-list">${draftListLabel}</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div id="tab_comment-list">
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
                                <div id="tab_file-list">
                                    <a href="#file-list">${fileListLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_page-list">
                                    <a href="#page-list">${pageMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_link-list">
                                    <a href="#link-list">${linkManagementLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_preference">
                                    <a href="#preference">${preferenceLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_user-list">
                                    <a href="#user-list">${userManageLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_plugin-list">
                                    <a href="#plugin-list">${pluginMgmtLabel}</a>
                                </div>
                            </li>
                            <li>
                                <div id="tab_others">
                                    <a href="#others">${othersLabel}</a>
                                </div>
                            </li>   
                        </ul>
                    </li>
                </ul>
            </div>
            <div id="main">
                <div id="tabs_article"></div>
                <div id="tabs_article-list"></div>
                <div id="tabs_draft-list"></div>
                <div id="tabs_link-list"></div>
                <div id="tabs_preference"></div>
                <div id="tabs_page-list"></div>
                <div id="tabs_file-list"></div>
                <div id="tabs_others"></div>
                <div id="tabs_user-list"></div>
                <div id="tabs_comment-list"></div>
                <div id="tabs_plugin-list"></div>
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
        <#include "admin-label.ftl">
        ${plugins}
    </body>
</html>