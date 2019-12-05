/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, spa */

spa.clients2 = (function () {
	var showFirst;
	
	function selectClient(event) {
		var link = $(this);
		var page = link.attr('href');
		var target = link.closest('.ibexis-ui-segment');

		
		// LOAD code is common - see spa-shell.js
		$.ajax({url:page, dataType: 'html'})
		.done (function(data) {
			data = '<div class="ibexis-ui-control"><a href="#" onclick="spa.shell.refresh($(this))">refresh</a></div>' + data;
			var content = $(data);
			var expired = content.filter('div.ibexis-expired');
			if (expired.size() == 1) {
				spa.login.showLogin();
				
			} else {
				content.appendTo(target);
				/*
				if (target.find('.navigation')) {
					buildNavigation(target);
				}
				*/
			}
		}).fail (function(data) {
			target.append(data.responseText);
		});

		
		
		
		return false;
	}
	
	function openAccount() {
		var link = $(this);
		var page = link.attr('href');
		alert(page);
		return false;
	}
	
	initModule = function ( $container ) {
		$container.on('click', 'div.ibexis-ui-segment.clients li > a', selectClient);
		$container.on('click', 'a.open', openAccount);
		
		/*
		$('.ibexis-ui-segment.tree-pane').on('click', 'li > a', selectClient);
		$('.ibexis-ui-segment.tree-pane').on('click', 'p > a', openAccount);
		*/
	};
  
  return {
	  initModule: initModule
  };
}());