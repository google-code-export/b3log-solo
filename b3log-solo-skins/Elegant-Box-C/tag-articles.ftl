<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <#include "common-head.ftl">
        <title>${tag.tagTitle} - ${blogTitle}</title>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        <link href="tag-articles-feed.do?oId=${oId}" title="ATOM" type="application/atom+xml" rel="alternate" />
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
                    <div class="post">
                        <div class="title bottom_space">
                            <h2>
                                文章标签 ‘${tag.tagTitle}’ (${tag.tagPublishedRefCount})
                            </h2>
                            <div class="fixed"></div>
                        </div><!-- title -->
                        <div class="content">
                            <#list articles as article>
                            <div class="boxcaption">
                                <h3><a rel="bookmark" href="${article.articlePermalink}">${article.articleTitle}</a></h3>
                            </div>
                            <div class="box">
                                <div class="excerpt">
                                    <p>${article.articleAbstract}</p>
                                </div>
                                <small>
                                    <#if article.hasUpdated>
                                    ${article.articleUpdateDate?string("yyyy年MM月dd日")}
                                    <#else>
                                    ${article.articleCreateDate?string("yyyy年MM月dd日")}
                                    </#if>
                                    |
                                    <#if article.articleCommentCount==0>
                                    <a href="${article.articlePermalink}#comments" title="${article.articleTitle} 上的评论">没有评论</a>
                                    <#else>
                                    <a href="${article.articlePermalink}#comments" title="${article.articleTitle} 上的评论">${article.articleCommentCount}条评论</a>
                                    </#if>
                                </small>
                                <div>
                                    <small>
                                        ${tags1Label}
                                        <#list article.articleTags?split(",") as articleTag>
                                        <a rel="tag" href="/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if>
                                        </#list>
                                    </small>
                                </div>
                            </div>
                            </#list>
                        </div> <!-- post -->
                        <div class="fixed"></div>
                    </div>
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
