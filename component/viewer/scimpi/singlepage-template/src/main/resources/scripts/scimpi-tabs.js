/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, scimpi */

/*
 * Specialised container that has a series of Tab headers. Selecting a Tab shows the content for 
 * that tab and hide all the conent for the other tabs.
 */
scimpi.tabs = (function () {
	
	var tabNo = 1;
	
	/*
	 * Adorns each of the 
	 */
	var initialiseContainer = function(xxxxforSegment, container) {
		if (container.children('.scimpi-ui-navigation').length > 0) {
			return;
		}
		var children = container.children();
		container.empty();
		container.append('<div class="scimpi-ui-navigation"></div>');
		container.append($('<div class="scimpi-ui-content">').append(children));
		container.append('</div>');
		buildNavigation(container);
	};

	var updateContent = function(segment) {
		buildNavigation(segment);
	};
	
	var showContentIfExists = function(container, resource) {
		var navigation = container.find('.scimpi-ui-navigation ul:first');
		var toMatch = resource;
		var link = navigation.find("[data-resource^='" + toMatch + "']");
		//link.find('a').click();

		if (link.length == 0) {
			return false;
		} else {
			link.parents('div.scimpi-ui-navigation').find('li.selected').removeClass('selected');
			link.addClass('selected');

			var contents = container.children('div.scimpi-ui-content');
			contents.children('div.scimpi-ui-segment').hide();
			contents.find("[data-content^='" + toMatch + "']").show();

			return true;
		}
	}
	
	var removeViewsForObject = function(container, segment) {
		var adjustVisibleSegment = segment.css('display') != 'none';
		var id = segment.attr('id');
		container.find('#link-' + id).remove();
		segment.remove();
		if (adjustVisibleSegment) {
			// return to first segment only if the current visible segment was removed
			var firstSegment = container.find('.scimpi-ui-content .scimpi-ui-segment').first();
			id = firstSegment.attr('id');
			firstSegment.show();
			highlightAsSelected(id);
		}
	}
	
	function addContent(newContent, segmentContainer, resource) {
		var segmentContents = segmentContainer.children('.scimpi-ui-content');
		var segment = $('<div class="scimpi-ui-segment" style="display: none;"></div>');
		segmentContents.append(segment);
		var newSection = $(newContent).appendTo(segment);
		segment.attr('data-content', resource);
		segmentContents.children().hide();		
		segment.show();
		var title = newContent.filter('div').data('label');
		var id = "tab" + tabNo++;
		segment.attr("id", id);
		var navigation = segmentContainer.find('.scimpi-ui-navigation ul:first');
		var link = $('<li id="link-' + id + '" class="scimpi-ui-tab closable" data-resource="' + resource 
				+ '"><a href="#" onclick="scimpi.shell.show(\'' + id + '\'); return false;">'
				+ title + '</a>'
				+ '<a href="#" onclick="scimpi.shell.remove(\'' + id + '\')">X</a>' + '</li>');
		link.appendTo(navigation);
		return segment;
	}

	function highlightAsSelected(id) {
		var link = $("#link-" + id);
		link.parents('div.scimpi-ui-navigation').find('li.selected').removeClass('selected');
		link.addClass('selected');
	}

	/*
	 * Build a navigation list (UL) within the div.scimpi-ui-navigation element. Each div.scimpi-ui-segment 
	 * element get a LI element created for it.
	 * 
	 * If the content classes contains .scimpi-ui-tabs-split then a new list (UL) is started, allowing multiple 
	 * lines or left/rights sections to be easily achieved. 
	 */
	var buildNavigation = function(segment) {
		var history = segment.find('div.scimpi-ui-history').first();
		if (history.size() > 0) {
			$('<ul></ul').appendTo(history);
		}
		var container = segment.find('div.scimpi-ui-navigation').first();
		if (container.size() > 0) {
			navigation = $('<ul></ul').appendTo(container);
			var content = segment.children('div.scimpi-ui-content');
			content.children('div[class~=scimpi-ui-segment]').each(function() {
				
				if ($(this).hasClass('scimpi-ui-tabs-split')) {
					navigation = $('<ul></ul').appendTo(container);					
				}
				
				var linkId = $(this).attr('id');
				if (!linkId) {
					linkId = "tab" + tabNo++;
					$(this).attr('id', linkId);
					$(this).hide();
				}
				var label = $(this).data('label');
				var type = $(this).data('type');
				var classes = 'class="scimpi-ui-tab ' + type + '"';
				var link = $('<a class="scimpi-ui-navigation" >'  + label + '</a>');
				link.click(function() {
					scimpi.shell.show(linkId);
				});
				navigation.append($('<li id="link-' + linkId + '" ' + classes + '></li>').append(link));
			});
			navigation.append('<li class="break"></li>');
			navigation.children().first().addClass('selected');
			content.children().hide();
			var firstSegment = content.children('.scimpi-ui-segment').first();
			firstSegment.show();
			if (firstSegment.children().size() == 0) {
				scimpi.shell.processSegment(firstSegment)
			}
		}
	};
	
	var setChildClass = function(parent, child) {
		var position = 'unknown';
		if (parent.hasClass('scimpi-ui-tabs-root')) {
			position = 'scimpi-ui-tabs-top';
		} else if (parent.hasClass('scimpi-ui-tabs-top')) {
			position = 'scimpi-ui-tabs-left';
		} else if (parent.hasClass('scimpi-ui-tabs-left')) {
			position = 'scimpi-ui-tabs-inner';
		} else if (parent.hasClass('scimpi-ui-tabs-inner')) {
			position = 'scimpi-ui-tabs-subinner';
		}
		child.addClass(position);
	}

	var initModule = function( $container ) {
	};

	return { 
		type: "tabs",
		initModule: initModule,
		initialiseContainer: initialiseContainer,
		updateContent: updateContent,
		addContent: addContent,
		highlightAsSelected: highlightAsSelected,
		showContentIfExists: showContentIfExists,
		removeViewsForObject : removeViewsForObject,
		setChildClass : setChildClass,
	};

}());
