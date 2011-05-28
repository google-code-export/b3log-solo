<p id="about">
    ${Elegant_Box_C_copyrightLabel} &copy; ${year} ${blogTitle} | ${Elegant_Box_C_poweredByLabel} 
    <a href="http://b3log-solo.googlecode.com/" target="_blank" style="text-decoration: none;">
        <span style="color: orange;margin-left:0px;">B</span>
        <span style="font-size: 9px; color: blue;"><sup>3</sup></span>
        <span style="color: green;">L</span>
        <span style="color: red;">O</span>
        <span style="color: blue;">G</span>&nbsp;
        <span style="color: orangered; font-weight: bold;">Solo</span>
    </a>
    ,${version} | ${Elegant_Box_C_themeByLabel} 
    <a href="http://www.neoease.com/" target="_blank">NeoEase</a> & 
    <a href="http://www.oncereply.me/" target="_blank">oncereply</a> ${Elegant_Box_C_themeByEndLabel}			</p>

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
                $('#admin').append('<li><a href="javascript:clearPageCache(\'all\');">${clearAllCacheLabel}</a></li>')
                    .append('<li><a href="javascript:clearPageCache();">${clearCacheLabel}</a></li>')
                    .append('<li><a href="http://${blogHost}/admin-index.do">${adminConsoleLabel}</a></li>')
                    .append('<li><a href="javascript:void(0);" onclick="window.location.href=\''+result.logoutURL+'\'">${logoutLabel}</a></li>');
            }else{
                $('#admin').empty().append('<li><a href="javascript:void(0);" onclick="window.location.href=\''+result.loginURL+'\'">${loginLabel}</a></li>');
            }
        }
    });
</script>

<div class="fixed"></div>
