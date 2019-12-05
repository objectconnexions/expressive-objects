/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, scimpi */

scimpi.plain = (function () {

	var initModule = function($container) {
	};

	var initialiseContainer = function(segment, content) {
		//content.prepend("<p>insert content</p>");
		content.appendTo(segment);
	};

	function addContent(content, container, forLink) {
		container.append(content);

		/*
		var element = container.children(".scimpi-ui-content");
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
	
	var setChildClass = function(parent, child) {
	}

	return {
		type : 'plain',
		initModule : initModule,
		initialiseContainer : initialiseContainer,
		addContent : addContent,
		updateContent : updateContent,
		highlightAsSelected : highlightAsSelected,
		setChildClass : setChildClass,
  };
}());