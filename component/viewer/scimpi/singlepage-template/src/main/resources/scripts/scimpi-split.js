/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, scimpi */

scimpi.split = (function () {
	
	var tabNo = 1;

	var initialiseContainer = function(segment, container) {
		/*
		content.appendTo(segment);
		// return segment.find('.scimpi-ui-container');
		return segment;
		*/
		
		// content.append("<p>Split init DONE</p>");
	};
	
	function showContentIfExists(container, resource) {
		/*
		var navigation = container.parent().find('.scimpi-ui-navigation ul:first');
		var link = navigation.find("[data-resource='" + resource + "']");
		link.find('a').click();
		return link.length > 0;
		*/
		return false;
	}

	function addContent(content, container, forLink) {
		var element = container.children(".scimpi-ui-content");
		element.empty();
		element.append(content);
		
		var id = "split" + tabNo++;
		element.attr('id', id);
		forLink.parent().attr('id', 'link-' + id);
	}

	var updateContent = function(segment) {
		alert("update - does it need to do something?");
	};
	
	function highlightAsSelected(linkId) {
		var link = $('#link-' + linkId);
		link.siblings().removeClass('selected');
		link.addClass('selected');
	}
	
	var setChildClass = function(parent, child) {
	}

	var initModule = function( $container ) {
	};

	return { 
		type: 'split',
		initModule: initModule,
		initialiseContainer: initialiseContainer,
		updateContent: updateContent,
		addContent: addContent,
		highlightAsSelected: highlightAsSelected,
		showContentIfExists: showContentIfExists,
		setChildClass : setChildClass,
	};

}());
