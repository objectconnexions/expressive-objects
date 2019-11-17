/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, scimpi */

scimpi.shell = (function () {
/*	
	var structure = [
		{
			  "label": "Section 0",
			  "name": "Applications set #0",
			  "decription": "",
		},
		{
			  "label": "Section 1",
			  "name": "Applications set #1",
			  "decription": "",
			  "contents": [
			    {
			      "label": "Subsection 1",
			      "name" : "",
			      "description" : "",
			      "type" : "",
			      "contents" : [
			        {
			          "label": "Side pane 1",
			          "name" : "",
			          "description" : "",
			          "type" : "",
			          "contents" : [
			            {
			              "label": "Inner section 1",
			              "name" : "",
			              "description" : "",
			              "type" : "",
			              "contents" : [
			                
			              ]
			            },
			            {
			              "label": "Inner section 2",
			              "name" : "",
			              "description" : "",
			              "type" : "",
			              "contents" : [
			                
			              ]
			            },
			            {
			              "label": "Inner section 3",
			              "name" : "",
			              "description" : "",
			              "type" : "",
			              "contents" : [
			                
			              ]
			            }
			          ]
			        }
			      ]
			    },
			    {
			      "label": "Subsection 2",
			      "name" : "",
			      "description" : "",
			      "type" : "",
			      "contents" : []
			    }
			  ]
			}
			];
	
	// TODO remove when fixing the dynamic apps
	structure = [];
*/	
	
	var l10n = scimpi.localization;

	var views = {};
	var segmentProcessors = {};
	var contentProcessors = {};
	var templateProcessors = {};
	var linkProcessors = {};
	
	/*
	 * Returns the id and classes for the specified element
	 */
	function debugElementDetails(element) {
		var tagName = element.prop('tagName');
		var label = element.data('label');
		if (label && label != 'Untitled Page') {
			return tagName + '+' + label;
		} else {
			return tagName + (element.attr('id') ? "#" + element.attr('id') : "") + "." + element.attr('class');
		}
	}
	
	function submitForm() {
		closeFeedback();
		var form = $(this);
		var action = form.attr('action');
		var segment = form.closest(".scimpi-ui-segment");
		var formData = form.serialize();
		$.ajax({url: action, type: 'POST', data: formData, dataType: 'html'})
		.done (function(data) {
			var content = $(data);
			var isEdit = form.is('.edit')
			var isAction = form.is('.action')
			// TODO check for expired
			
			if (content.find('.errors').size() > 0) {
				submitForm_error(form, content);
				
			} else {
				var target = segment;
				var fromDialog = segment.attr('id') === 'scimpi-dialog';
				if (fromDialog) {
					/*
					var container = form.closest(".scimpi-ui-container");
					container = submitForm_dismissDialog();
					container = container.children(".scimpi-ui-container");
					target = container;
					*/
					target = submitForm_dismissDialog().children(".scimpi-ui-container");
				}
				
				var removed = content.filter('div.scimpi-removed');
				if (removed.size() > 0) {
					submitForm_removed(content);
				}
				
				var newContent = content.filter(':not(div.feedback)').filter(':not(div.scimpi-removed)');
				if (isAction) {
					if (newContent.size() > 0) {
						var view = form.find('input[name=_view]').attr('value');
						var remove = content.find('div[data-remove-oid]');
						var resource = view + '?_result=' + remove.data('remove-oid');

						insertSegmentContents(newContent, target, resource);
					}
				} else if (isEdit) {
					var oid = form.find('input[name=_object]').attr('value');
					console.log("updating content relating to " + oid);
					var toDealWith = $('#scimpi-content *[data-update-oid="' + oid + '"]');
					toDealWith.each(function() {
						var target = $(this).data('update-target');
						if (target) {
							target = target.toLowerCase();
						}
						if (!target || target === 'self') {
							//$(this).remove();
						} else if (target === 'parent') {
							//$(this).parent().remove();
						} else if (target == 'segment') {
							var newSegment = $(this).closest('.scimpi-ui-segment');
							if (newSegment != segment) {
								newSegment.empty();
							}
						}
					});
					
					if (!fromDialog) {	
						adaptContent(newContent);
						segment.children().replaceWith(newContent);
					}

				}

				var feedback = content.filter('div.feedback');
				if (feedback.size() > 0) {
					showFeedback(feedback);
				}

			}
		}).fail (function(data) {
			showHtmlError(data);
		});
		return false;
	}
	
	function submitForm_error(form, content) {
		/* 
		 * Replace the existing dialog content with the 
		 * updated content (including error elements) 
		 */
		form.empty();  // TODO remove
		var newForm = content.find('form');
		form.replaceWith(newForm);
		var feedback = content.filter('div.feedback');
		if (feedback) {
			showFeedback(feedback);
		}
		newForm.find('input.button[name="cancel"]').remove();
		newForm.find('input.button').after('<input class="button" type="button" value="Cancel" name="cancel"></input>');
		// TODO respond to the click of the cancel button by replacing the form with the original content
	}
	
	function submitForm_removed(content) {
		/*
		 * When an object is deleted then remove any elements 
		 * that are displaying that item.
		 */
		var div = content.filter('.scimpi-removed').first();
		var oid = div.data('object');
		console.log("removing content relating to " + oid);
		var toDealWith = $('#scimpi-content *[data-remove-oid="' + oid + '"]');
		toDealWith.each(function() {
			var target = $(this).data('remove-target');
			if (target) {
				target = target.toLowerCase();
			}
			if (!target || target === 'self') {
				$(this).remove();
			} else if (target === 'parent') {
				$(this).parent().remove();
			} else if (target == 'segment') {
				var segment = $(this).closest('.scimpi-ui-segment');
				var container = segment.closest('.scimpi-ui-container');
				var displayType = lookupView(container);
				displayType.removeViewsForObject(container, segment);
			}
		});
	} 
	
	function submitForm_dismissDialog() {
		// for a dialog find the original parent container
		var owner = scimpi.dialog.owner();
		scimpi.dialog.close();
		return owner;
	}		

/*
TODO - REMOVE - appears not to be used

	function openLink() {
		closeFeedback();
		var link = $(this);
		if (link.attr('onclick')) {
			return true;
		}

		var target = link.attr("target");
		var href = link.attr('href');
		if (target) {
			window.open(href, target);
			return true;
		}

		var container = link.closest('.scimpi-ui-container');
		if(!showLinkContentIfExists(link, href, container)) {
			var segment = link.closest('.scimpi-ui-segment');
			loadRemoteResource(href, segment);
		}
		return false;
	}
*/	

	/*
	 * Add content to current scimpi-ui-container when a link is clicked, A new segment is created and the content is loaded
	 * using the URL in the href attribute.
	 * 
	 * By default the new content is added to the parent container. If the view for that container specifies an
	 * alternate container, then that will be used instead.
	 */
/*
TODO - REMOVE - appears not to be used

	function addLinkContent(link) {
		if (!link.attr('onclick') && link.attr('href') != '#') {
			closeFeedback();
			var url = link.attr('href');
			var container = link.closest('.scimpi-ui-container');
			if(!showLinkContentIfExists(link, url, container)) {
				loadRemoteResource(url, container);
			}
			return false;
		} else {
			return true;
		}
	}
*/

	/*
	 * Determine if content exists for the specified url (has been previously loaded), if
	 * so it then displayed and return true. If it doesn't exist then false is returned.
	 * Delegates to the view manager (calling its shiwContentIfExists() method) for
	 * the current container.
	 */

/*
TODO - REMOVE - appears not to be used


	var showLinkContentIfExists = function(link, url, container) {
		var displayType = lookupView(container);
		return displayType.showContentIfExists(container, url);
	};
*/
	
	/*
	 * Initialise all the containers (marked wih scimpi-ui-container class) including those
	 * that are contained within the specified content element.
	 */
	var initialiseContainers = function(content) {
		if (content.hasClass('scimpi-ui-container')) {
			initialiseContainer(content);
		}

		var innerContainers = content.find('.scimpi-ui-container');
		innerContainers.each(function(index) {
			initialiseContainer($(this));
		});
	};
	
	/*
	 * Delegates to the the view manager for the specified container, allowing it to set
	 * its contained elements, specifically setting up the links
	 */
	var initialiseContainer = function (innerContainer) {
		var containerType = lookupView(innerContainer);
		console.log("using " + containerType.type + " for initialising container for " + debugElementDetails(innerContainer));
		var newSection = containerType.initialiseContainer(null, innerContainer);
		
		
		//var container = segment.closest('.scimpi-ui-container');
		//var containerType = lookupView(container);
		var parent = innerContainer.parent();
		if (parent) {
			var parentContainer = parent.closest('.scimpi-ui-container');
			containerType.setChildClass(parentContainer, innerContainer);
		} else {
			console.error('No parent container to use to set class for child container: ' + debugElementDetails(innerContainer))
		}
	}
	
	var insertContentIntoSegment = function(content, segment) {
		console.log("inserting content into segment " + debugElementDetails(segment));
		segment.empty();
		segment.append(content);
	}
	
	
	/*
	 * TODO update comment:
	 * 
	 * Makes an HTTP request to the server to retrieve the resource identified by the
	 * provided URL. The returned content is then added to the specified container. If the
	 * returned content contain a div with an _scimpi-expired_ class then the login dialog is
	 * shown instead.
	 */
	/*
	function loadResource(url, loaded, error) {
		console.log("loading content from " + url);
		$.ajax({url: url, dataType: 'html'})
		.done (function(data) {
			var content = $(data);
			var expired = content.filter('div.scimpi-expired');
			var rootContent = content.filter('div#scimpi-content');
			if (expired.size() == 1) {
				scimpi.login.showLogin();
			} else if (rootContent.size() == 1) {
				$('div#scimpi-content').empty();
				insertSegmentContentsUsingProcessor($('div#scimpi-content'), content.children(), null);
			} else {
				loaded(content);
			}
		}).fail (function(data) {
			error(data.responseText);
		});
	}
*/
	

	
	/*
	 * Loads the content using the specified URL and then inserts that content into the DOM
	 * at the section specified, specifically replacing the elements of class scimpi-ui-template
	 * with those having marching IDs in the loaded content.
   */
	var loadIntoTemplate = function(url, templateLocator, complete) {
		console.log("loading content from " + url + " for " + templateLocator + " (template)");
		$.ajax({url: url, dataType: 'html'})
		.done (function(data) {
			var content = $(data);
			processTemplate(content);
			var expired = content.filter('div.scimpi-expired');
			if (expired.size() == 1) {
				scimpi.login.timedOut();
			} else {
				content.attr('data-context', 'loaded '  + url + ' for template');
				var template = $(templateLocator);
				
				var holders = template.find('.scimpi-ui-template');
				if (holders.length === 0) {
					content.appendTo(template);
				} else {
					holders.each(function(index) {
						var id = '#' + $(this).attr('id');
						var replacement = content.filter(id);
						console.log("replacing " + id + " with " + replacement);
						$(this).empty();
						$(this).append(replacement.children());
				  	});
			  }
			  initialiseContainers(template);
			  if (complete) {
				  complete();
			  }
			}

			
			
			
			
			var toContainer = $('div#scimpi-content div.scimpi-ui-container');
			var containerType = lookupView(toContainer);
			console.log("using " + containerType.type + " handler for adding dynamic content to root container (" + debugElementDetails(toContainer) + ")");
			
			// TODO remove
			
			for (var i = 0; i < structure.length; i++) {
				var label = structure[i].label;
				var section = $('	<div class="scimpi-ui-tabs scimpi-ui-tabs-top" data-label="' + label + '"></div>');
				containerType.addContent($(section), toContainer, null);
				var sectionContainerType = scimpi.tabs;
				sectionContainerType.initialiseContainer(null, section);
				
				var subsections = structure[i].contents;
				if (subsections) {
					for (var j = 0; j < subsections.length; j++) {
						var sublabel = subsections[j].label;
						var link = i + '/' + j;
						var subsection = $('<div class="scimpi-ui-container scimpi-ui-tabs scimpi-ui-tabs-top"  data-label="' + 
								sublabel + '" data-structure="'+ link + '"></div>');
						sectionContainerType.addContent(subsection, section, null);
						sectionContainerType.initialiseContainer(null, subsection);
						
						var accountOid = toContainer.closest('div[data-account]').data('account');
						subsection = $('<div class="xxxxscimpi-ui-container scimpi-ui-tabs-top scimpi-ui-grid"  data-label="' + 
								sublabel + '" data-content="'+ 'old_application/_apps-station-grid-selector.shtml?view=_apps-one-station.shtml&account=' + accountOid + '"></div>');
						processSegment(subsection);
						sectionContainerType.addContent(subsection, section, null);
						//sectionContainerType.initialiseContainer(null, subsection);
					}
				}
			}
			
			
			
			/*
			
			
			var section = $('	<div class="xxxscimpi-ui-container scimpi-ui-tabs scimpi-ui-tabs-top" data-target="reveal" data-label="Test 1"></div>');
			containerType.addContent($(section), toContainer, null);

			var sectionContainerType = scimpi.tabs;
			sectionContainerType.initialiseContainer(null, section);
			sectionContainerType.addContent($('<div xxxclass="scimpi-ui-container scimpi-ui-tabs scimpi-ui-tabs-top"  data-label="Sub Test 1">Section 1</div>'), section, null);
			sectionContainerType.addContent($('<div xxxclass="scimpi-ui-container scimpi-ui-tabs scimpi-ui-tabs-top"  data-label="Sub Test 2">Section 2</div>'), section, null);
			
			containerType.addContent($('<div data-label="Test 2">testing 2</div>'), toContainer, null);
*/


			
			
		}).fail (function(data) {
			showHtmlError(data);
		});
	};

	/**
	 * Reload the content of the current (or nearest parent) scimpi-ui-segement element. If
	 * the element is hidden then the content will be removed, but it will not be reloaded
	 * (which will occur automatically when it next made visible). 
	 */
	var refreshSegment = function(target) { 
		var container = target.closest('.scimpi-ui-segment');
		container.empty();
		if (container.css('display') != 'none') {
			processSegment(container);
		}
	}
	
	/*
	 * Loads the contents for a segment. A segment being a marker element of
	 * class 'scimpi-ui-segment' that has a data-content, data-descriptor or
	 * data-action attribute. Using one of these attribute the system will load
	 * the specified content and place it within the element.
	 * 
	 * Removing all the child element and calling this method again will refresh
	 * the view.
	 */
	var processSegment = function(segment) {
		/*
		 * If class is scimpi-ui-segment (is an element that we need to process
		 * then look for data- content. descriptor, action, health, etc, where these types should have been registered 
		 * this the shell. 
		 * 
		 */
		segment.attr('data-context', 'default content within page');
		segment.css('display', 'block');
		for (p in segmentProcessors) {
			var id = segment.data(p); 
			if(id) {
				segmentProcessors[p](segment, id, indicateSegmentShowing);
				break;
			}
		}
	}
	
	var processTemplate = function(content) {	
		var templates = content.find('[data-template]');
		if (templates.length > 0) {
			templates.each(function() {			
				var id = $(this).data('template'); 
				if(id) {
					for (p in templateProcessors) {
						var replacement = templateProcessors[p]($(this));
						$(this).replaceWith(replacement);
						break;
					}
				}
			});
		}
	}
	
	function indicateSegmentShowing(segment) {
		var parentContainer = segment.parent().closest('.scimpi-ui-container');
		var displayType = lookupView(parentContainer);
		var id = segment.attr('id');
		displayType.highlightAsSelected(id);
		
		showParentContainer(segment, false);
	}
	
	/*
	 * Loads content from the server (using the specified URL) and run the specified function with the loaded contents
	*/
	function loadRemoteResource(url, fn) {
		console.log("loading resource from " + url);
		$.ajax({url: url, dataType: 'html'})
		.done (function(data) {
			var content = $(data);
			var expired = content.filter('div.scimpi-expired');
			if (expired.size() == 1) {
				scimpi.login.timedOut();
				return;
			}
				
			content.attr('data-context', 'loaded ' + url);

			var removed = content.filter('div.scimpi-removed');
			if (removed.size() > 0) {
				submitForm_removed(content);
			}

			var feedback = content.filter('div.feedback');
			if (feedback.size() > 0) {
				showFeedback(feedback);
			}
				
			var resource = content.data('resource');
			if (!resource) {
				resource = url;
			}
			/*
			insertSegmentContents(content, intoContainer, resource);

			var containerType = lookupView(intoContainer);
			containerType.setChildClass(intoContainer, content);
			*/

			// TODO should we add the content (the-real-content) class to this element?
			fn(content, resource);
		}).fail (function(data) {
			showHtmlError(data);
		});
	}

	function insertSegmentContents(content, intoSegment, resource) {
		var updateElement = intoSegment.find("div[data-update]");
		var update = updateElement.data('update');
		var targetId = content.data('target');
		if (targetId) {
			var insertContent = contentProcessors[targetId];
			if(insertContent) {
				console.log('using content processor \'' + targetId + '\' to display <' + debugElementDetails(content) + '>');
				insertContent(content, intoSegment, resource);
				adaptContent(content);
				if (update) {
					// clear existing list etc (as determined by update label)
					var targetUpdate = intoSegment.find('div[data-update="' + update + '"]');
					refreshSegment(targetUpdate);
				}
				return;
			}
		}
		console.error('failed to use content-processor \'' + targetId + '\' to display <' + content + '>');
	}

	var adaptContent = function(content) {
		//content.find('table').tablesorter();
		sortTables(content);
		initialiseButtonConfirmation();
		
		
		
		
		
		var support = content.children('div#support');
		if (support.length > 0) {
			support.find('a').attr('target', 'support');
		} else {			
			content.find('a').each(function(index) {
				var url = $(this).attr('href');
				if (url && url != '#') {
					var link = $(this);
					$(this).bind('click', function() {
						var container = link.closest('.scimpi-ui-container');
						var displayType = lookupView(container);
						if(!displayType.showContentIfExists(container, url)) {
							var segment = link.closest('.scimpi-ui-segment');
							loadRemoteResource(url, function(content, resource) {
								insertSegmentContents(content, segment, resource);
							});
						}

						/* 
						TODO - REMOVE this is the only call to showLinkContentIfExists, so can be removed

						if(!showLinkContentIfExists(link, url, container)) {
							var segment = link.closest('.scimpi-ui-segment');
							loadRemoteResource(url, container);
						}
						*/
						return false;
					});
					$(this).attr('href', null);
				} else {
					var attributes = this.attributes;
					if (attributes) {
						var i;
						for (i = 0; i < attributes.length; i++) {
							if (attributes[i].name.startsWith('data-')) {
								for (p in linkProcessors) {
									var id = $(this).data(p); 
									if(id) {
										var link = $(this);var link = $(this);
										var processor = linkProcessors[p];
										$(this).bind('click', function() {
											var container = link.closest('.scimpi-ui-container');
											/*
											var displayType = lookupView(container);
											if(!displayType.showContentIfExists(container, url)) {
												var segment = link.closest('.scimpi-ui-segment');
												loadRemoteResource(url, function(content, resource) {
													insertSegmentContents(content, segment, resource);
												});
											}
*/
											processor(container, id, attributes);
											return false;
										});
										$(this).attr('href', null);	
										break;
									}
								}
								break;
							}
						}
					}
				}
			});
		}
		
		var sections = content.find('div.scimpi-ui-collapsible-section');
		sections.each(function(index) {
			if (!$(this).hasClass('control')) {
				$(this).addClass('control');
				$(this).prepend('<p class="controller"><a href="#" onclick="$(this).parent().siblings(\'.content\').toggle(300); return false;">+</a>');
				var block = $(this).find('.content');
				block.hide();
			}
		});
		if (sections.length == 1) {
			var block = sections.last().find('.content');
			block.show();
		}

	}
/*
TODO - REMOVE - appears not to be used


	function addDynamic(segment, content) {
		var toContainer = segment.closest('.scimpi-ui-container');
		var containerType = lookupView(toContainer);
		console.log("using " + containerType.type + " handler for adding dynamic content (" + debugElementDetails(content)
				+ ")  to container (" + debugElementDetails(toContainer) + ")");
		containerType.addContent(content, toContainer, null);
	//	containerType.highlightAsSelected(content.closest('.scimpi-ui-segment').attr('id'));

	}
*/

	/*
	 * Insert a new segment into the container and place the specified content within it.
	 */
	var insertNewSegment = function(content, container, resource) {
/*
//TODO moved from here...				
		var displayType = lookupView(container);
		displayType.setChildClass(container, content);
		// intoContainer.append(contents);
*/
		/*
		 * Adds the specified content to the specified container, which was obtained using
		 * the specified resource. The work is delegated to the view handler for the container.
		 */
	 	//var toContainer = segment.closest('.scimpi-ui-container');
		
		var containerType = lookupView(container);
		console.log("using " + containerType.type + " handler for adding new content (" + debugElementDetails(content)
				+ ")  to container (" + debugElementDetails(container) + ")");
		var segment = containerType.addContent(content, container, resource);
		initialiseContainers(content);
		if (content.has('.scimpi-ui-container').length == 0) {
			processSegment(content);
		}

//to here..
		containerType.highlightAsSelected(content.closest('.scimpi-ui-segment').attr('id'));
		return segment;
	}
	
	/*
	 * Shows a segment that already exists (although the content might not be loaded yet) that has the specified 
	 * ID. If this content element is empty then try to load the
	 * content from the resource specified in the content elements data-content attribute. If the showParents
	 * parameter is specified and set to true then parent containers (marked using scimpi-ui-container) will be also
	 * be shown.
	 *
	 * (called by tabs (set up in navigation builder)
	 */
	var showSegment = function(id, showParents) {
		var linkId = '#' + id;
		var segment = $(linkId);
		
		closeFeedback();
		
		// TODO is this useful or even used?
		if (segment.size() == 0) {
			// $(this).find();
			console.log("Can't find segment to show!");
			console.log("DECIDE WHAT TO DO HERE");
		}
		//console.log("segment already showing " + segment.attr('id') + " " + segment.attr("style"));	
		if (segment.attr('style') === 'display: block;') {
			segment.empty();
		}
		
		var parentContainer = segment.parent().closest('.scimpi-ui-container');
		var displayType = lookupView(parentContainer);
		// TODO replace view if already highlighted		
		console.log("highlighting #" + id + " with " + displayType.type + " handler");
		//	displayType.highlightAsSelected(id);
		//	showParentContainer(segment, showParents);
		
		// TODO not need now context elements removed
		if (segment.children(':not(.context-info)').size() == 0) {
			processSegment(segment);
		} else { 
			console.log("segment already showing " + segment.attr('id') + " " + segment.attr("style"));	
			indicateSegmentShowing(segment);
			// if (segment.attr("style").contains("display: block")) {
		}
//		indicateSegmentShowing(segment);
	};
	
	// TODO this should be done by the handlers!  Although the showing parent part is common and should stay here.
	function showParentContainer(segment, showParents) {
		segment.siblings().hide();
		segment.show();
		if (showParents) {
			var parent = segment.parents('.scimpi-ui-segment');
			if (parent && parent != segment) {
				showParentContainer(parent, showParents);
			}
		}
	};	

	/*
	 * Clears all the contents from the main divs
	 */
	var clearMainContent = function() {
		var segment = $('#scimpi-content');
		segment.empty();
		segment = $('#scimpi-footer-help');
		segment.empty();
	};
	
	/*
TODO - REMOVE - appears not to be used

	var remove = function(link) {
		var segment = link.closest('div.scimpi-ui-control').parent();
		var id = segment.attr('id')
		removeSegment(id);
	};
*/
	
	var removeSegment = function(id) {
		var segment = $('div#' + id);
		var container = segment.parent().closest('div.scimpi-ui-container');
		segment.remove();
		
		var navigation = container.children('.scimpi-ui-navigation').find('ul');
		var navigationId = 'link-' + id;
		var navigationLink = navigation.find('li#' + navigationId);
		navigationLink.remove();
		
		var historyId = 'history-' + id;
		var history = $('div.scimpi-ui-history ul');
		var historyLink = history.find('li#' + historyId);
		historyLink.remove();
		
		container.children('.scimpi-ui-content').children().first().show();
		navigation.children().first().addClass("selected");
	};
	
	/*
	 * Adds the specified view manager, using the specified name, to the list of managers.
	 */
	var registerView = function(name, view) {
		views[name] = view;
	};
	
	var registerSegmentProcessor = function(name, processor) {
		segmentProcessors[name] = processor;
	}
	
	var registerContentProcessor = function(name, processor) {
		contentProcessors[name] = processor;
	}
	
	var registerTemplateProcessor = function(name, processor) {
		templateProcessors[name] = processor;
	}
	
	var registerLinkProcessor = function(name, processor) {
		linkProcessors[name] = processor;
	}
	
	function lookupView(forSegment) {
		var displayType;
		var div = forSegment.filter('div');
		if (div) {
			var classes = div.attr('class');
			if (classes) {
				classes = classes.match(/(scimpi-ui-[\w-]*)\b/g);
				if (classes) {
					for(var i = 0; i < classes.length; i++) {
						var type = classes[i];
						displayType = views[type];
						if (displayType) {
							break;
						}
					}
				}
			}
		}
		if (!displayType) {
			displayType = scimpi.plain;
		}
		return displayType;
	};

	var updateClock = function( ) {
		var clock = $('#scimpi-ui-clock');
		if (clock.length == 0) {
			return;
		}
		var currentTime = moment().utc();
		if (currentTime && scimpi.localization.isReady()) {
			var displayTime = l10n.formatDate(currentTime) + '<br />' + l10n.formatLongTime(currentTime);
			clock.empty();
			clock.append(displayTime);	  	
		}
	}

	function collapseSidebar() {
		var root = $('#scimpi');
		root.toggleClass('collapsed-sidebar');
		var link 	= $('#scimpi-help a.collapse');
		link.empty();
		if (root.hasClass('collapsed-sidebar')) {
			link.append('?');
		} else {
			link.append('>>');
		}
	}
	
	var showErrorMessage = function(message) {
		var content;
		content = '<div id="system-error"><div class="error-message"><h2>Whoops</h2>' +
			'<p>' + message +'</p></div>' + 
			'<div class="buttons"><button class="button" onclick="scimpi.shell.dismissSystemError()">Close</button></div></div>';

		var error = $('#system-error-overlay');
		error.removeClass('hide');
		error.removeClass('detail')
		error.empty();
		error.append(content);
		
		$('#scimpi').addClass('disable');
	}
	
	var showHtmlError = function(data) {
		var content;
		if (data.status == 404) {
			content = '<div id="system-error"><div class="error-message"><h2>Whoops</h2>' +
				'<p>Something has unexpectedly gone wrong. Our systems have logged this problem so the developers can' +
				' review it.</p><p>Please close this dialog and try again, or press the restart button to reload the ' + 
				' application.</p><p>As this application is in development mode you can also look at all the debug ' + 
				'details using the detail button.</p></div>' + 
				'<div class="buttons"><button class="button" onclick="scimpi.shell.dismissSystemError()">Close</button>' +
				'<button class="button" onclick="scimpi.shell.restartApplication()">Restart</button></div></div>';
		} else if (data.responseText) {
			content = data.responseText;				
		} else {
				content = '<div id="system-error"><div class="error-message"><h2>Whoops</h2>' +
				'<p>Something has unexpectedly gone wrong. Our systems have logged this problem so the developers can' +
				' review it.</p><p>Please close this dialog and try again, or press the restart button to reload the ' + 
				' application.</p><p>As this application is in development mode you can also look at all the debug ' + 
				'details using the detail button.</p></div>' + 
				'<div class="buttons"><button class="button" onclick="scimpi.shell.dismissSystemError()">Close</button>' +
				'<button class="button" onclick="scimpi.shell.restartApplication()">Restart</button></div></div>';

		}

		var error = $('#system-error-overlay');
		error.removeClass('hide');
		error.removeClass('detail')
		error.empty();
		error.append(content);
		
		$('#scimpi').addClass('disable');
	}
	
	var showSystemErrorDetails = function() {
		var error = $('#system-error-overlay');
		error.toggleClass('detail');
	}
	
	var dismissSystemError = function() {
		var error = $('#system-error-overlay');
		error.addClass('hide');
		error.empty();
		
		$('#scimpi').removeClass('disable');
	}
	
	var restartApplication = function() {
		//window.location.href = '/logout.app';	
		dismissSystemError();
		scimpi.login.logOut();
		window.location.href = '/index.shtml';
	}
	
	var showFeedback = function(feedback) {
		if (feedback.children() && feedback.children().length > 0) {
			addFeedback(feedback.children());
		}
	}

	var addFeedback = function(content) {
		$('div#message').append('<a class="close" href="#">X</a>');
		$('div#message').append(content);	
	}
	
	var closeFeedback = function() {
		$('div#message').empty();
	}
	
	var addFeedbackMessage = function(message) {
		addFeedback($('<div class="message">' + message +'</p>'));
	}
	
	var initModule = function( $container ) {
		$('#scimpi-help').on('click', 'a.collapse', collapseSidebar);
		$('#scimpi').on('submit', 'form.action, form.edit', submitForm);
		$('#scimpi-dialog').on('submit', 'form.action, form.edit', submitForm);
		$('#message').on('click', 'a.close', closeFeedback);
		
		// initialiseButtonConfirmation();

		
		// Content inserts the content into an existing element - a segment predefined for expected content
		registerSegmentProcessor('content', function (segment, file, showContext) {
			segment.attr('data-context', 'default content from ' + file)
			loadRemoteResource(file, function(content, resource) {
				insertSegmentContents(content, segment, resource);
				/*
				// TODO rationalise the following, and check is resource should be handled later on?
				insertSegmentContents(content, segment, resource);
				var containerType = lookupView(segment);
				containerType.setChildClass(segment, content);
				*/
			});
		});

		registerSegmentProcessor('structure', function (segment, id, showContext) {
			segment.attr('data-context', 'dynamic content')
			segment.append($('<div>DYNAMIC CONTENT</div>'));
			
			segment.append('<div class="scimpi-ui-container scimpi-ui-tabs scimpi-ui-tabs-top" data-target="reveal" data-label="Untitled Page" data-context="/old_application/_apps.shtml uses the tabs template">');
			segment.append('<div class="scimpi-ui-segment data" data-label="Exploration" data-content="old_application/_apps-data-viewer-test.shtml?account=CUS:19"></div>');
			segment.append('<div class="scimpi-ui-segment data" data-label="Export" data-content="old_application/_apps-data-export-example.shtml?account=CUS:19"></div>');
			segment.append('<div class="scimpi-ui-segment data" data-label="Monitoring" data-content="old_application/_apps-monitoring-example.shtml?account=CUS:19"></div>');
			segment.append('<div class="scimpi-ui-placeholder">This section will provide access to the dynamically created applications.</div>');
			segment.append('</div>');

			showContext(segment);
		});

		// Factory inserts the content into  new segment of an existing container
		registerSegmentProcessor('factory', function (segment, file, showContext) {
			segment.attr('data-context', 'default content from ' + file)
			//var container = segment.closest(".scimpi-ui-container");
			loadRemoteResource(file, function(content) {
				insertSegmentContents(content, segment);
			});
		});

		registerSegmentProcessor('action', function (segment, action) {
			var method = eval(action);
			if (method) {
				var target = $(linkId);
				method.apply(this, [target]);
			}
		});
		
		registerSegmentProcessor('method', function (segment, action, showContext) {
			//var container = segment.closest(".scimpi-ui-container");
			loadRemoteResource(action, function(content) {
				insertSegmentContents(content, segment);
			});
		});
		
		registerSegmentProcessor('debug', function (segment, section, showContext) {
			var debug = scimpi[section].debug();
			var div = $('<div class="scimpi-ui-content the-real-contents">');
			div.append(debug);
			segment.empty();
			segment.append(div);
			showContext(segment);
		});


		/*
		 * Root replaces the main/root content
		 */ 
		registerContentProcessor('account', function (content, toContainer, resource) {
			console.log("using root to show content (" + debugElementDetails(content) + ")");
			processTemplate(content);
			initialiseContainers(content);
			var root = $('div#scimpi-content > div#scimpi-account').parent();
			root.empty();
			root.append(content);
		});

		/*
		 * Display contents as a dialog
		 */
		registerContentProcessor('dialog', function(content, toContainer, resource) {
			console.log("using dialog to show content (" + debugElementDetails(content) + ")");
			toContainer.removeClass('hide');
			var owner = toContainer.parent().closest('.scimpi-ui-segment');
			scimpi.dialog.openWithContent(content, owner);			
		});
		
		/*
		 * Display contents as a message
		 */
		registerContentProcessor('message', function(content, toContainer, resource) {
			console.log("using message box to show content (" + debugElementDetails(content) + ")");
			closeFeedback();
			addFeedback(content);
		});
		
		registerContentProcessor('panel', function(content, segment, resource) {
			console.error("unexpected use of panel conetnt " + resource);
			segment.empty();
			segment.append(content);
			indicateSegmentShowing(segment);
		});
		
		/*
		 * Display an existing segment
		 */
		registerContentProcessor('reveal', function(content, segment, resource) {
			insertContentIntoSegment(content, segment);
			initialiseContainers(content);		
			
			/*
			var container = segment.closest('.scimpi-ui-container');
			var containerType = lookupView(container);
			containerType.insertContentIntoSegement(content);
			
			
/*

		 	var toContainer = segment.closest('.scimpi-ui-container');
			var containerType = lookupView(toContainer);
			containerType.
			
			// TODO testing moving adding content to here from ...
			containerType.setChildClass(toContainer, content);
			// .. from here
			
*/			
//			segment.append(content);
			
			indicateSegmentShowing(segment);
		});

		/*
		 * Create a new segment
		 */
		registerContentProcessor('segment', function(content, segment, resource) {
			var container = segment.closest('.scimpi-ui-container');
			insertNewSegment(content, container, resource);
		});
		
		updateClock();
		setInterval('scimpi.shell.updateClock()', 1000 );
	};


	function sortTables(content) {
		var table =  content.find('table.table-sorter');
		if (table.length > 0) {
			var reverse = table.hasClass('reverse');
			var last = $('table.table-sorter tr th').length - 1;
			var headers = {};
			headers[last] = {sorter: false};
			
			$.tablesorter.addParser({ 
				id: 'dates', 
				is: function(s) { 
					return moment(s, l10n.dateTimePattern()).isValid();        	
				}, 
				format: function(s) {
					return moment(s, l10n.dateTimePattern()).unix();
				}, 
				type: 'number' 
			});
			
		    var tt = table.tablesorter( {
		    	headers: headers,
		    	/* TODO if these sorts are used then JWebUnit will fail (for every test) as the there are 
		    	 * no parsers to be used, even though we explicitly added one in the statement above!!!!
		    	 */
			    //	sortList: [[0,0]]
			    //	sortList: [[0,reverse ? 1 : 0]],
		    } ); 
		    
		    var sorting = [[0,0],[2,0]]; 
	       // $("table").trigger("sorton",[sorting]); 
		}
	}


	function initialiseButtonConfirmation() {
		$('form.confirm input.button, a.confirm').click(function(){
			  var confirmed = confirm('Are you sure?');
			  return confirmed;
		});
		
//		$('a:contains(Remove)').click(function(){
		$('a[ref="remove"]').click(function(){
			  var confirmed = confirm('Are you sure?');
			  return confirmed;
		}); 
	}

	
	return {
		initModule : initModule,
		updateClock : updateClock,
		registerView : registerView,
		registerSegmentProcessor : registerSegmentProcessor,
		registerContentProcessor : registerContentProcessor,
		registerTemplateProcessor : registerTemplateProcessor,
		registerLinkProcessor : registerLinkProcessor,
		insertSegmentContents : insertSegmentContents,
		processSegment : processSegment,
		show : showSegment,
		feedback : addFeedbackMessage,
		refresh : refreshSegment,
		remove : removeSegment,
		adaptContent : adaptContent,

		start : loadIntoTemplate,
		clear : clearMainContent,
		
		insertNewSegment : insertNewSegment,
		
		showErrorMessage : showErrorMessage,
		showHtmlError : showHtmlError,
		dismissSystemError : dismissSystemError,
		showSystemErrorDetails : showSystemErrorDetails,
		restartApplication : restartApplication,
	};

}());
