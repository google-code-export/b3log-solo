/*

	jQuery Tools 1.0
	Created by Andrea Cima Serniotti
	This is a list of useful jQuery functions that might improve the usability and accessiblity of your site

*/


if($.cookie("patternC") && $.cookie("switcherBgC")) { 
	var patternValue = $.cookie("patternC");
	var switcherValue = $.cookie("switcherBgC");
	var skinCss = "body{background-image:" + patternValue + "} #switcher{background-image:" + switcherValue;
	if($('style').length != 0){ // if there's a tag style in the head then...
		$('style').append(skinCss);
	}
	if($('style').length == 0){ // if there isn't a tag style in the head then...
		$('<style type="text/css">' + skinCss + '</style>').appendTo('head');
	}
}


$(document).ready(function () {

		$('textarea').parent('p').addClass('textarea');
	
	// ---- Enhanced Stylesheet Switcher -------------------------------------------------------------------------------------------
	
		$('<ul id="switcher" class="grey"> \
        <li id="skin1"><a href="#" title="grey">Grey</a></li> \
        <li id="skin2"><a href="#" title="brown">Brown</a></li> \
        <li id="skin3"><a href="#" title="blue">Blue</a></li> \
		</ul>').insertAfter('#service-menu'); // Insert the switcher where you want it to be
		$("#switcher li a").click(function() { // When you click one of the links in the switcher
			var pattern = 'url(' + currentSkinRoot + 'images/pattern-' + $(this).attr("title") + '.jpg)';
			var switcherBg = 'url('+ currentSkinRoot + 'images/switcher-' + $(this).attr("title") + '.png)';
			$('body').css('background-image',pattern);
			$("#switcher").css('background-image',switcherBg);
			$.cookie("patternC",pattern, {path: '/', expires: 365}); // Add cookie		
			$.cookie("switcherBgC",switcherBg, {path: '/', expires: 365}); // Add cookie
			return false;
		});
		

	// ---- Flickr ----------------------------------------------------------------------------------------------------
	
	 
	
	 $('#flickr').find('a').wrap("<p></p>");
	 $('#flickr p:nth-child(2n+1)').addClass('nomargin');
	

	// ---- Tabs ----------------------------------------------------------------------------------------------------------
	
		$(".tab-content").hide(); // Hide all content
		$("ul.tabs li:first").addClass("active").show(); // Activate first tab
		$(".tab-content:first").show(); // Show first tab content
		$("ul.tabs li").click(function() { // onClick
			$("ul.tabs li").removeClass("active"); // Remove any "active" class
			$(this).addClass("active"); // Add "active" class to selected tab
			$(".tab-content").hide(); // Hide all tab content
			var activeTab = $(this).find("a").attr("href"); // Find the href attribute value to identify the active tab + content
			$(activeTab).fadeIn(); // Fade in the active content
			return false; // Prevent the default link behaviour
		});

	// ---- First Paragraph and Drop Letter ----------------------------------------------------------------------------------------------------
	    $('.post p').eq(2).addClass('intro');
		$('.page p').eq(2).addClass('intro');
		$('.work .intro,.goodies .intro,.newsletter .intro').removeClass('intro');
		$('.post-excerpt').each(function () { $(this).find('p').eq(0).addClass('intro'); });
		$('.intro').each(function() {
			  var text = $(this).html();
			  var first = $('<span>'+text.charAt(0)+'</span>').addClass('drop');
			  $(this).html(text.substring(1)).prepend(first);
		   });

    // ---- Lightbox ----------------------------------------------------------------------------------------------------

	$('a[href$=.jpg]').lightBox({fixedNavigation:true});	
	$('#screenshots-thumbs a:eq(3)').css('margin-right','0');
	
	
	



	// ---- External Links ----------------------------------------------------------------------------------------------------
	
		//$("a[href*='http://'],[href*='https://']:not([href*='"+location.hostname+"'])").attr("target","_blank").attr("title","Opens new window").addClass("external"); // Detect all the external links and add target blank to open them in a new window. It also adds a title and a class="external" to the link.
	
	
	// ---- Documents Icons ---------------------------------------------------------------------------------------------------
	
		$("a[href$='.pdf']").attr("title","This PDF document opens a new window").addClass("pdf"); // Detect all pdfs, add a title and a class on them
		$("a[href$='.ppt']").attr("title","This Power Point document opens a new window").addClass("ppt"); // Detect all ppts, add a title and a class on them
		$("a[href$='.doc']").attr("title","This Word document opens a new window").addClass("word"); // Detect all words, add a title and a class on them
		$("a[href$='.xls']").attr("title","This Excel document opens a new window").addClass("xls"); // Detect all xlss, add a title and a class on them

		$("a[href$='.pdf'],a[href$='.ppt'],a[href$='.doc'],a[href$='.xls']").each(function () { 
			var parent = $(this).parent('li');
			var node = $(parent).contents().filter(function(){return this.nodeType != 1; }); // Check if the parent LI has some text before the link
			if(!$(node).length) {$(parent).addClass('no-bullet');} // If there is no text then add class "no-bullet" to the LI
		});
	
	
	// ---- Print this page ---------------------------------------------------------------------------------------------------

		/*$('<p id="actions"></p>').appendTo('#wrapper'); // Generate a container for all the actions right before the #wrapper closing tag
		$('<a id="print" href="#" title="Print this page">Print this page</a>').click(function(){window.print();}).appendTo('#actions'); // Add the "Print Page" link to #actions
	 */

	// ---- Email a friend ----------------------------------------------------------------------------------------------------
	
		$('<a id="email-a-friend" href="#" title="Opens your email client with pre-filled details of this page">Email a friend</a>').click(function(){mailpage();}).appendTo('#actions');  // Add the "Email a friend" link to #actions. Make use you have in this file the code that generates the #actions paragraph.
	
		function mailpage(){
			mail_str = "mailto:?subject=Check out the " + document.title;
			mail_str += "&body=I thought you might be interested in the " + document.title;
			mail_str += ". You can view it at, " + location.href;
			location.href = mail_str;
		};
	
	
	// ---- Add to favourites -------------------------------------------------------------------------------------------------

		$('<a id="fav" href="#" title="Opens a pop up that lets you add this page to your bookmarks">Add to favourites</a>').click(function(){addFav();}).appendTo('#actions');
		
		function addFav(){
		if (window.sidebar) { // firefox
			window.sidebar.addPanel(document.title, document.location.href,"");
		} else if( document.all ) { //MSIE
			window.external.AddFavorite( document.location.href, document.title);
		} else {
			alert("Sorry, your browser doesn't support this feature");
		}
		return false;
		};
	
	
	// ---- FAQs ---------------------------------------------------------------------------------------------------------------

		$('.faqs dd').hide(); // Hide all DDs inside .faqs
		$('.faqs dt').hover(function(){$(this).addClass('hover')},function(){$(this).removeClass('hover')}).click(function(){ // Add class "hover" on dt when hover
			$(this).next().slideToggle('normal'); // Toggle dd when the respective dt is clicked
		});
		
		
	// ---- Expandable boxes ----------------------------------------------------------------------------------------------------------

		$(".clickable").each(function() { // Hide the box content if .clickable has also class "hidden". Remove class hidden from the html code if you want the box to be displayed by default.
			if($(this).hasClass('hidden')){
					$(this).parents(".expandable").find('.expandable-cont').hide();
				}
		});	
		
		$('.expandable-cont').each(function() { // Generate the collapse and expand buttons
				if($(this).is(':hidden')){
					$(this).parents(".expandable").find('.clickable').addClass("hidden").append('<a href="#" class="collapse hidden" title="Expand the content of this box">[Expand]</a>');
				}
				else{	
					$(this).parents(".expandable").find('.clickable').addClass("visible").append('<a href="#" class="collapse visible" title="Collapse the content of this box">[Collapse]</a>');
				}					   
		});							
	
		$(".clickable").click(function(){ // Update the collapse and expand buttons a toggle the box content
			$(this).parents(".expandable").find(".expandable-cont").slideToggle("slow", function () {
			if($(this).is(':hidden')){
				$(this).parents(".expandable").find(".collapse").removeClass("visible").addClass("hidden").attr("title","Expand the content of this box").html("[Expand]");
			}
			else{
				$(this).parents(".expandable").find(".collapse").removeClass("hidden").addClass("visible").attr("title","Collapse the content of this box").html("[Collapse]");;
			}
			});	
			return false;
		});
	
	
	// ---- Stripes in tables ----------------------------------------------------------------------------------------------------------

		$("table tr:nth-child(even)").addClass("striped"); // Add class "striped" to all the even TRs

	
	// ---- Form focus highlight ----------------------------------------------------------------------------------------------------------
	
	$("input,select,textarea").focus(function() { // Set the class curFocus on focus on inputs and selects p parents
			$(this).parent("p").addClass("curFocus");
		});
	$(".submit input").focus(function() { // Remove the class curFocus on focus on submit button p parent
			$(this).parent("p").removeClass("curFocus");
		});
	$("input,select,textarea").blur(function() { // Remove the class curFocus on blur
		$(this).parent("p").removeClass("curFocus")		
	});
	
    // ---- Textarea bubble ----------------------------------------------------------------------------------------------------------

	$('<span class="bubble"></span>').insertAfter('textarea');
	$("textarea").focus(function() { // Set the class curFocus on focus on inputs and selects p parents
			$(this).next(".bubble").addClass("focus");
		});
	$("textarea").blur(function() { // Remove the class curFocus on blur
		$(this).next(".bubble").removeClass("focus")		
	});
	
	
    // ---- Service Menu Animation ----------------------------------------------------------------------------------------------------------
	
	$('#service-menu a').hover(function(){
		$(this)
		.stop(true)
		.animate(
				 {width: '75px' },
				 {duration:500,speed:'normal'}
		);
										
	},function(){
	
		$(this)
		.stop(true)
		.animate(
				 {width: '30px' },
				 {duration:600,speed:'normal'}
		);
	});
	
	
	// ---- Form hints ----------------------------------------------------------------------------------------------------------

	$('#lang_sel').prependTo('#main');
	$('#lang_sel span,lang-switcher-cont').remove();



});
