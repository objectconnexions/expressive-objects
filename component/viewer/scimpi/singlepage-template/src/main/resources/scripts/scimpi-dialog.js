/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, scimpi */

scimpi.dialog = (function () {
	
	var container;
	var nextId = 1;

	var initialiseContainer = function(segment, container) {
		this.container = container;
		container.removeClass("hide");
	};

	function addContent(content, container, forLink) {
		container.append(content);
	}

	var updateContent = function(segment) {
		alert("update - does it need to do something?");
	};
	
	function highlightAsSelected(linkId) {
		var link = $('#link-' + linkId);
		link.siblings().removeClass('selected');
		link.addClass('selected');
	}
	
	var openWithContent = function(content, owner, fn) {
		var dialog = revealDialog();
		showContent(dialog, content, owner, fn);
	}
	
	var openResource = function(resource, owner, fn) {
		var dialog = revealDialog();
		$.ajax({url: resource, dataType: 'html'})
		.done (function(data) {
			showContent(dialog, data, owner, fn);
		}).fail (function(data) {
			// TODO allow to cancel
			dialog.html(data.responseText)
		});	
	}
	
	function revealDialog() {
		var overlay = $('#scimpi-dialog-overlay');
		overlay.removeClass('hide');
		$('#scimpi').addClass('disable');
		var dialog = $('#scimpi-dialog');
		dialog.hide();
		return dialog;
	}

	function showContent(dialog, content, owner, fn) {
		var dialog = $('#scimpi-dialog');
		if (owner) {
			var id;
			id = owner.attr('id');
			if (!id) {
				id = nextId++;
			}
			dialog.attr('data-owner', '#' + id);
		}
		dialog.append(content);
		var form = dialog.find('form');
		var cancel = form.find('input[name=cancel]');
		if (cancel.length == 0 && !form.hasClass('login')) {
			cancel = $('<input class="button" type="button" value="Cancel" name="cancel"></input>');
			form.append(cancel);
		}
		cancel.click(close);
		// TODO reveal with style...
		dialog.show();
		dialog.find('input[type=text]').first().focus();
		if(fn) {
			fn(dialog);
		}
	}

	var showBusy = function() {
		var page = $('#scimpi-dialog-overlay');
		page.addClass("busy");
		page.css('cursor', 'wait');
	}
	
	var clearBusy = function() {
		var page = $('#scimpi-dialog-overlay');
		page.removeClass("busy");
		page.css('cursor', 'default');
	}
	
	var close = function() {
		var overlay = $('#scimpi-dialog-overlay');
		overlay.addClass('hide');
		$('#scimpi').removeClass('disable');
		
		var dialog = $('#scimpi-dialog');
		dialog.empty();
		return false;
	}

	var owner = function() {
		var dialog = $('#scimpi-dialog');
		var ownerId = dialog.attr('data-owner');
		if (ownerId) {
			return $('body').find(ownerId);
		}
		return null;
    }
	
	var setChildClass = function(parent, child) {
	}

	var initModule = function( $container ) {
		$('#scimpi-dialog').on('click', 'form.action input.button[name="cancel"]', close);
		$('#scimpi-dialog').on('click', 'form.edit input.button[name="cancel"]', close);
	};

	return { 
		type: 'dialog',
		initModule: initModule,
		initialiseContainer: initialiseContainer,
		updateContent: updateContent,
		addContent: addContent,
		highlightAsSelected: highlightAsSelected,
		setChildClass : setChildClass,

		
		owner : owner,
		openResource : openResource,
		openWithContent : openWithContent,
		showBusy : showBusy,
		clearBusy : clearBusy,
		close : close,
	};

}());
