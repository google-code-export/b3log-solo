<script type="text/javascript" src="/plugins/fancybox/jquery.fancybox-1.3.4/jquery.easing-1.3.pack.js"></script>
<script type="text/javascript" src="/plugins/fancybox/jquery.fancybox-1.3.4/jquery.mousewheel-3.0.4.pack.js"></script>
<script type="text/javascript" src="/plugins/fancybox/jquery.fancybox-1.3.4/jquery.fancybox-1.3.4.pack.js"></script>
<link rel="stylesheet" href="/plugins/fancybox/jquery.fancybox-1.3.4/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />

<script type="text/javascript">
    $("a[rel=group]").fancybox({
        'transitionIn'		: 'elastic',
        'transitionOut'		: 'elastic',
        'titlePosition' 	: 'over',
        'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
            return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
        }
    });

</script>
