<p id="about">
				版权所有 &copy; ${year} ${blogTitle} | 基于 <a href="http://b3log-solo.googlecode.com/">B3log</a>,${version} | 主题由 <a href="http://www.neoease.com/">NeoEase</a> & <a href="http://www.oncereply.me/">oncereply</a> 提供			</p>

<ul id="admin">
</ul>

<script type="text/javascript">
    var clearPageCache=function(all){
        var data={"all":'',"URI": window.location.pathname};
        if(all){
            data.all='all';
            data.URI='';
        }
        $.ajax({
            type: "POST",
            url: "/clear-cache.do",
            data: JSON.stringify(data),
            success: function(result){
                window.location.reload();
            }
        });
    };
    $.ajax({
        type:'post',
        url:'/check-login.do',
        success:function(result){
            if (result.isLoggedIn) {
                $('#admin').append('<li><a href="javascript:clearPageCache(\'all\');">清除所有缓存</a></li>')
                    .append('<li><a href="javascript:clearPageCache();">清除本页缓存</a></li>')
                    .append('<li><a href="http://${blogHost}/admin-index.do">站点管理</a></li>')
                    .append('<li><a href="javascript:void(0);" onclick="window.location.href=\''+result.logoutURL+'\'">退出</a></li>');
            }else{
                $('#admin').empty().append('<li><a href="javascript:void(0);" onclick="window.location.href=\''+result.loginURL+'\'">登录</a></li>');
            }
        }
    });
</script>

<div class="fixed"></div>
