/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, ibexis */

ibexis.clients = (function () {

	var initModule = function ( $container ) {
	};

	var initialiseContainer = function(segment, content) {
		content.appendTo(segment);
		segment.prepend("<p>insert client content</p>");
	};

	function addContent(content, container, forLink) {
		container.prepend("<p>add selected content (1)</p>");
		
		
		/*
		var element = container.children(".ibexis-ui-content");
		element.empty();
		element.append(content);
		
		var id = "split" + tabNo++;
		element.attr('id', id);
		forLink.parent().attr('id', 'link-' + id);
		return element;
		*/
	}

	var updateContent = function(segment) {
	};
	
	function highlightAsSelected(linkId) {
	}
  
	return {
		type: 'clients',
		initModule: initModule,
		initialiseContainer: initialiseContainer,
		addContent: addContent,
		updateContent: updateContent,
		highlightAsSelected: highlightAsSelected
  };
}());