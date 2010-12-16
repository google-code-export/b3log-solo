<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="robots" content="none"/>
        <title>${blogTitle} - ${adminConsoleLabel}</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="js/lib/jsonrpc.min.js"></script>
        <script type="text/javascript" src="js/lib/jquery/jquery.bowknot.min.js"></script>
        <script type="text/javascript" src="js/lib/tiny_mce/tiny_mce.js"></script>
        <link type="text/css" rel="stylesheet" href="styles/default-admin.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-bowknot.css"/>
        <link type="text/css" rel="stylesheet" href="styles/default-base.css"/>
        <link rel="icon" type="image/png" href="/favicon.png"/>
        ${htmlHead}
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
                <div class="left side">
                    <ul id="sideNavi">
                        <li id="articleTab" onclick="adminUtil.changeList(this);adminUtil.clearAtricle();">
                            <div class="left postIcon"></div>
                            <span>&nbsp;${postArticleLabel}</span>
                        </li>
                        <li id="article-listTab" onclick="adminUtil.changeList(this);">
                            <div class="left articlesIcon"></div>
                            <span>&nbsp;${articleListLabel}</span>
                        </li>
                        <li id="draft-listTab" onclick="adminUtil.changeList(this);">
                            <div class="left draftsIcon"></div>
                            <span>&nbsp;${draftListLabel}</span>
                        </li>
                        <li id="file-listTab" onclick="adminUtil.changeList(this);">
                            <div class="left fileIcon"></div>
                            <span>&nbsp;${fileListLabel}</span>
                        </li>
                        <li id="pageTab" onclick="adminUtil.changeList(this);">
                            <div class="left pageIcon"></div>
                            <span>&nbsp;${pageMgmtLabel}</span>
                        </li>
                        <li id="link-listTab" onclick="adminUtil.changeList(this);">
                            <div class="left linkIcon"></div>
                            <span>&nbsp;${linkManagementLabel}</span>
                        </li>
                        <li id="article-syncTab" onclick="adminUtil.changeList(this);">
                            <div class="left blogSyncIcon"></div>
                            <span>&nbsp;${blogSyncLabel}</span>
                        </li>
                        <li id="preferenceTab" onclick="adminUtil.changeList(this);">
                            <div class="left preferenceIcon"></div>
                            <span>&nbsp;${preferenceLabel}</span>
                        </li>
                        <li id="user-listTab" onclick="adminUtil.changeList(this);">
                            <div class="left usersIcon"></div>
                            <span>&nbsp;${userManageLabel}</span>
                        </li>
                        <li id="othersTab" onclick="adminUtil.changeList(this);">
                            <div class="left othersIcon"></div>
                            <span>&nbsp;${othersLabel}</span>
                        </li>
                    </ul>
                </div>
                <div class="left" id="main">
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
                        <div id="pagePanel" class="none">
                        </div>
                        <div id="file-listPanel" class="none">
                        </div>
                        <div id="othersPanel" class="none">
                        </div>
                        <div id="user-listPanel" class="none">
                        </div>
                    </div>
                </div>
                <div class="clear"></div>
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
        <script type="text/javascript" src="js/adminUtil.js"></script>
        <script type="text/javascript">
            var adminUtil = new AdminUtil({
                "userRole": "${userRole}",
                "loadingLabel": "${loadingLabel}"
            });

            adminUtil.init();
        </script>
    </body>
</html>
