<div style="color: red;">
    <#list pages as page>
    <a target="_blank" href="${page.link?substring(5)}">${page.cachedTitle}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${page.cachedType}<br/>
    </#list>
</div>
<script type="text/javascript">    
    //alert(1);
</script>