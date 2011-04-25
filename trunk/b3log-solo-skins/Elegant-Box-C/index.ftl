<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <#include "common-head.ftl">
        <title>${blogTitle}</title>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
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
                    <#include "main.ftl">
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
                        <script type="text/javascript">
                            var currentPageNum = window.location.getParameter('paginationCurrentPageNum');
                            if(!currentPageNum){
                                currentPageNum=1;
                            }
                        </script>
                        <ul class="pager">
                            <li class="page previous">
                                <a id="previousPage" href="${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={previousPageNum}">&nbsp;&lt;&nbsp;</a>
                                <script type="text/javascript">
                                    if(!currentPageNum||currentPageNum==1){
                                        $('#previousPage').hide();
                                    }else if(currentPageNum){
                                        var href=$('#previousPage').attr('href');
                                        $('#previousPage').attr('href',href.replace('{previousPageNum}',parseInt(currentPageNum)-1));
                                    }
                                </script>
                            </li>
                            <#list paginationPageNums as paginationPageNum>
                            <li class="page"><a href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum=${paginationPageNum}">${paginationPageNum}</a></li>
                            </#list>
                            <li class="page next">
                                <a id="nextPage" href="/${actionName}.do?<#if oId??>oId=${oId}&</#if>paginationCurrentPageNum={nextPageCount}">&nbsp;&gt;&nbsp;</a>
                                <script type="text/javascript">
                                    var lastPageNum='${paginationPageNums?last}';
                                    if(currentPageNum!=lastPageNum){
                                        var href=$('#nextPage').attr('href');
                                        $('#nextPage').attr('href',href.replace('{nextPageCount}',parseInt(currentPageNum)+1));
                                    }else{
                                        $('#nextPage').hide();
                                    }
                                </script>
                            </li>
                        </ul>
                        <script type="text/javascript">
                            $('.page a').each(function(i){
                                if(currentPageNum==$(this).text()){
                                    $(this).parent().addClass('current');
                                }
                            });
                        </script>
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
