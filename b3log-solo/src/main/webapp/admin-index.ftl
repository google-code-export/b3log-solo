<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <link type="text/css" rel="stylesheet" href="styles/${miniDir}default-admin${miniPostfix}.css"/>
        <link type="text/css" rel="stylesheet" href="styles/${miniDir}default-bowknot${miniPostfix}.css"/>
        <link type="text/css" rel="stylesheet" href="styles/${miniDir}default-base${miniPostfix}.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/lib/jquery/jquery.bowknot.min.js"></script>
        <script type="text/javascript" src="js/lib/tiny_mce/tiny_mce.js"></script>
    </head>
    <body>
        <div id="loadMsg">${loadingLabel}</div>
        <div id="tipMsg"></div>
        <div id="adminMain">
            <div id="top">
                <a href="http://b3log-solo.googlecode.com" class="logo" target="_blank">
                    <span style="color: orange;margin-left:0px;">B</span>
                    <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                    <span style="color: green;">L</span>
                    <span style="color: red;">O</span>
                    <span style="color: blue;">G</span>&nbsp;
                    <span style="color: orangered; font-weight: bold;">Solo</span>
                </a>
                <span class="right">
                    <span class="left">${userName}&nbsp;|&nbsp;</span>
                    <span class='left homeIcon' onclick="window.location='/';" title='${indexLabel}'></span>
                    <span class='left'>&nbsp;|&nbsp;</span>
                    <span onclick='adminUtil.adminLogout();' class='left logoutIcon' title='${logoutLabel}'></span>
                </span>
                <div class="clear"></div>
            </div>
            <div id="allPanel">
                <div>
                    <ul id="sideNavi">
                        <li id="articleTab" onclick="adminUtil.changeList(this);adminUtil.clearArticle();">
                            <a href="#article"><div class="left postIcon"></div>${postArticleLabel}</a>
                        </li>
                        <li id="article-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#article-list"><div class="left articlesIcon"></div>${articleListLabel}</a>
                        </li>
                        <li id="draft-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#draft-list"><div class="left draftsIcon"></div>${draftListLabel}</a>
                        </li>
                        <li id="file-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#file-list"><div class="left fileIcon"></div>${fileListLabel}</a>
                        </li>
                        <li id="page-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#page-list"><div class="left pageIcon"></div>${pageMgmtLabel}</a>
                        </li>
                        <li id="link-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#link-list"><div class="left linkIcon"></div>${linkManagementLabel}</a>
                        </li>
                        <li id="article-syncTab" onclick="adminUtil.changeList(this);">
                            <a href="#article-sync"><div class="left blogSyncIcon"></div>${blogSyncLabel}</a>
                        </li>
                        <li id="preferenceTab" onclick="adminUtil.changeList(this);">
                            <a href="#preference"><div class="left preferenceIcon"></div>${preferenceLabel}</a>
                        </li>
                        <li id="user-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#user-list"><div class="left usersIcon"></div>${userManageLabel}</a>
                        </li>
                        <li id="cache-listTab" onclick="adminUtil.changeList(this);">
                            <a href="#cache-list"><div class="left cacheIcon"></div>${cacheMgmtLabel}</a>
                        </li>
                        <li id="othersTab" onclick="adminUtil.changeList(this);">
                            <a href="#others"><div class="left othersIcon"></div>${othersLabel}</a>
                        </li>
                    </ul>
                </div>
                <div id="main">
                    <div class="content">
                        <div id="articlePanel" class="none">
                        </div>
                        <div id="article-listPanel" class="none">
                        </div>
                        <div id="draft-listPanel" class="none">
                        </div>
                        <div id="link-listPanel" class="none">
                        </div>
                        <div id="preferencePanel" class="none">
                        </div>
                        <div id="article-syncPanel" class="none">
                        </div>
                        <div id="page-listPanel" class="none">
                        </div>
                        <div id="file-listPanel" class="none">
                        </div>
                        <div id="cache-listPanel" class="none">
                        </div>
                        <div id="othersPanel" class="none">
                        </div>
                        <div id="user-listPanel" class="none">
                        </div>
                    </div>
                </div>
                <div class="footer">
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        <span style="color: orange;">B</span>
                        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
                        <span style="color: green;">L</span>
                        <span style="color: red;">O</span>
                        <span style="color: blue;">G</span>&nbsp;
                        <span style="color: orangered; font-weight: bold;">Solo</span>,
                    </a>ver ${version}
                </div>
            </div>
        </div>
        <script type="text/javascript" src="js/${miniDir}adminUtil${miniPostfix}.js"></script>
        <script type="text/javascript" src="js/${miniDir}util${miniPostfix}.js"></script>
        <script type="text/javascript">
            var adminUtil = new AdminUtil({
                "userRole": "${userRole}",
                "loadingLabel": "${loadingLabel}",
                "removeSuccLabel": "${removeSuccLabel}",
                "removeFailLabel": "${removeFailLabel}",
                "getSuccLabel": "${getSuccLabel}"
            });

            adminUtil.init();
            // Removes functions if enabled multiple users support
                <#if enabledMultipleUserSupport>
                var unUsed = ['article-sync'];
            for (var i = 0; i < unUsed.length; i++) {
                $("#" + unUsed[i] + "Tab").remove();
                $("#" + unUsed[i] + "Panel").remove();
            }
                </#if>
        </script>
    </body>
</html>
