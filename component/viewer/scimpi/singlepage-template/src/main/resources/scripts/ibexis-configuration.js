
/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, ibexis */

ibexis.configuration = (function() {

	var configurations = {};
	var definition;
	
	var statusBits = [
        "Internal device status ports",
		"Modem statistics (old devices only)", 
		"SMS statistics (old devices only)",
		"Http statistics", 
		"USB statistics", 
		"Device requests statistics", 
		"File statistics", 
		"Memory statistics",
		"Boot statistics" ,
        null, 
        null, 
        null, 
        null, 
        null, 
        null, 
        null,
        "Module status ports",
        "Module low frequency statistics",
        "Module high frequency statistics"
	];
	
	var uploadBits = [
		'Status', 
		'Debug', 
		'Property Changes',
		'Executed Commands', 
		'Requests Received', 
		'Device State', 
		'System Events', 
		'Self Test',
		'Boot List',
		'Network Scan'
	];

	var initModule = function() {
		ibexis.shell.registerSegmentProcessor('configuration', function (target, deviceOid, showContext) {
			var parent = target.closest('.ibexis-ui-content');
			var firstPane = parent.children('.ibexis-ui-segment').first();
			if (firstPane.children().length == 0) {
				console.log('removing configuration entry for ' + deviceOid);
				delete(configurations[deviceOid]);
			}
			segment(deviceOid, target);
			showContext(target);
///			return true;
		});
	};

	var appendSections = function(div, section) {
		if (section) {
			var i;
			for (i = 0; i < section.length; i++) {
				appendFields(div, section[i]);
			}
		}
	};

	var appendFields = function(div, section) {
		if (section) {
			div.append('<h3>' + section._title + ' [<i>' + section._id + '</i>]</h3>');
			for (p in section) {
				if (p[0] != '_') {
					div.append('<div><div class="label">' + p + '</div><div class="property">' + section[p] + '</div></div>');
				}
			}
		}
	};

	var display = function(div, configuration) {
		div.append('<h1>' + configuration.title + '</h1>');
		div.append('<div>[' + configuration.date + ']</div>');
		div.append('<div>' + configuration.summary + '</div>');

		appendFields(div, configuration.system);
		appendFields(div, configuration.cellular);
		appendFields(div, configuration.ethernet);
		appendFields(div, configuration.wifi);

		appendSections(div, configuration.port);
		appendSections(div, configuration.monitor);
		appendSections(div, configuration.sampling);
		appendSections(div, configuration.status);
	};

	function createEntryMap(section) {
		var entries = {};
		var fields = section.fields;
		for (i = 0; i < fields.length; i++) {
			entries[fields[i].id] = fields[i];
		}
		return entries;
	}

	var listPorts = function(div, deviceOid, internal) {
		loadConfiguration(deviceOid, function(configuration) {
			var ports = configuration.port;
			var i,
				j,
				count = 0,
				port;
			for (i = 0; i < ports.length; i++) {
				port = ports[i];
				var fields = [ 'name', 'sensor-type', 'enabled', 'id', 'slot', 'address', 'fetch-time', 'retain-time' ];
				var id;
				for (j = 0; j < port.fields.length; j++) {
					var field = port.fields[j];
					if (field.id === 'id') {
						id = field.value;
					} else if (field.id === 'type') {
						switch (field.value) {
						case 'analog':
							fields = fields.concat([ 'unit', 'slope', 'offset', 'minimum', 'maximum' ]);
							break;
						case 'state':
							fields = fields.concat([ 'pull-up', 'polarity', 'debounce-time' ]);
							break;
						case 'counter':
							fields = fields.concat([ 'unit', 'pull-up', 'edge', 'debounce-time', 'slope' ]);
							break;
						case 'modbus':
							fields = fields.concat([ 'unit', 'slope', 'offset', 'minimum', 'maximum' ]);
							break;
						case 'spi-slave':
							fields = fields.concat([ 'unit' ]);
							break;
						}
					}
				}

				if ((internal == true && id >= 900) || (internal == false && id < 900)) {
					var entryMap = createEntryMap(port);				
					//var content = form('edit', deviceOid, 'port:' + entry.index, elements, fields, null);
					var title = entryMap.name.display + (entryMap.unit.value ? ' (' + entryMap.unit.display + ')' : '');
					var section = 'port:' + port.index;
					var description = '#' + entryMap['id'].display;
					// content = summary(deviceOid, 'edit', 'port:' + entry.index, content, entryMap, title, elements.type.display, '#' + elements['id'].display, null);
					var content = summary(deviceOid, 'edit', section, entryMap, fields, port.title, (entryMap.enabled.value === 'true'? 'Enabled' : ''), description, !internal, function(line, id, value) {});
					div.append(content);
					count++;
				}
			}
			ibexis.shell.adaptContent(div);
			
			if (count == 0) {
				var empty = $('<p>There are no ports set up</p>');
				div.append(empty);
			}
		});
	}

	var listStatus = function(div, deviceOid) {
		loadConfiguration(deviceOid, function(configuration) {
			var entries = configuration.status
			var i, entry;
			for (i = 0; i < entries.length; i++) {
				entry = entries[i];
				var entryMap = createEntryMap(entry);
			//	var content = form('edit', deviceOid, 'status:' + entry.index, elements, null, 
			//			null, );
				var label ='Stores status data every ' + entryMap.interval.display;
				var enabled = (entryMap.enabled.value ? 'Enabled' : '');
				var content = summary(deviceOid, 'edit', 'status:' + entry.index, entryMap, null, entry.title, enabled, label, true, function(line, id, value) {
					var list = $('<div class="checkbox-list" data-name="' + id + '"></div>');
					var bits = [];
					var box = 0;
					for (var j = 0; j < statusBits.length; j++) {
						var element = statusBits[j];
						if (element == null) {
							continue;
						}
						var item = $('<div></div>');
						var checkbox = $('<input type="checkbox"></input>');
						checkbox.attr('name', id);
						checkbox.attr('value', j);
						item.append(checkbox);
						item.append(element);
						list.append(item);

						bits[box] = checkbox;
						box++;
					}
					line.append(list);
					line.append('(' + value + ')');
					return bits;
				});
				div.append(content);
			}
			ibexis.shell.adaptContent(div);
			
			if (entries.length == 0) {
				var empty = $('<p>There are no status samplers set up</p>');
				div.append(empty);
			}
		});
	}

	var listSampling = function(div, deviceOid) {
		loadConfiguration(deviceOid, function(configuration) {
			var entries = configuration.sampling;
			var ports = configuration.port;
			var i, entry;
			for (i = 0; i < entries.length; i++) {
				entry = entries[i];
				var entryMap = createEntryMap(entry);
				//var content = form('edit', deviceOid, 'sampling:' + entry.index, elements, null, null, );
				var section = 'sampling:' + entry.index;
				var description = 'Logging every ' + entryMap.interval.display;
				
				//content = summary(deviceOid, 'sampling:' + entry.index, content, elements, entry.title, (elements.enabled.value === 'true' ? 'Enabled' : ''), );
				
				var content = summary(deviceOid, 'edit', section, entryMap, null, entry.title, (entryMap.enabled.value === 'true'? 'Enabled' : ''), description, true, function(line, id, value) {
					var list = $('<div class="checkbox-list" data-name="' + id + '"></div>');
					var bits = [];
					var bit = 0;
					for (var j = 0; j < ports.length; j++) {
						var element = ports[j];
						var item = $('<div></div>');
						var checkbox = $('<input type="checkbox"></input>');
						checkbox.attr('name', id);
						checkbox.attr('value', element.index - 1);
						item.append(checkbox);
						item.append(element.title);
						list.append(item);

						// bits[bit++] = checkbox;
						bits[element.index - 1] = checkbox;
					}
					line.append(list);
					line.append('(' + value + ')');
					return bits;
				});
				div.append(content);
			}
			ibexis.shell.adaptContent(div);
			
			if (entries.length == 0) {
				var empty = $('<p>There are no data samplers set up</p>');
				div.append(empty);
			}
		});
	}

	var listMonitors = function(div, deviceOid) {
		loadConfiguration(deviceOid, function(configuration) {
			var elements = configuration.monitor;
			var ports = configuration.port;
			var i, entry;
			for (i = 0; i < elements.length; i++) {
				entry = elements[i];
				var entries = createEntryMap(entry);
				//var content = form('edit', deviceOid, 'monitor:' + entry.index, elements, null, null, function(line, id, value) {});
				var section =  'monitor:' + entry.index;
				var description = 'Checking every ' + entries.interval.display;
				var content = summary(deviceOid, 'edit', section, entries, null, entry.title, (entries.enabled.value === 'true'? 'Enabled' : ''), description, true, function(line, id, value) {});
				div.append(content);
			}
			ibexis.shell.adaptContent(div);
			
			if (elements.length == 0) {
				var empty = $('<p>There are no monitors set up</p>');
				div.append(empty);
			}
		});
	}

	// TODO make this function create the form (call the form() function) itself, instead of each caller passing in the creared form.
	function summary(deviceOid, formType, sectionId, entries, include, title, enabled, description, allowRemove, createBitmapFields) {
		var section = $('<div class="ibexis-ui-collapsible-section configuraton">')
		var summary = $('<div class="summary">');
		var summaryContent = '<span class="title">' + title + '</span><span class="detail"><span>' + enabled + '</span><span>' + description + '</span>';  
		summary.append(summaryContent);
		section.append(summary);
		
		var content = $('<div class="content"></div>');
		if (allowRemove) {
			var button = $('<a class="action button" >Remove</a>');
			button.click(function() {
				remove(deviceOid, sectionId, section);
			});
			content.append('<div></div>').append(button);
		}
		var div = form(formType, deviceOid, sectionId, entries, include, null, createBitmapFields);
		content.append(div);
		section.append(content);
		return section;
	}
	
	function toggleForm() {
		var form = $(this).parent('div').sibling('form');
		form.toggle(1000);
	}

	var form = function(formType, deviceId, sectionId, entries, include, title, bitmap) {
		var form = $('<form class="' + formType + ' full" action="configuration-update.app" type="POST"></form>');
		if (title) {
			form.append('<div class="title">' + title);
		}
		var i,
			j;
		if (!include) {
			include = [];
			var k;
			for (k in entries) {
				include.push(k);
			}
		}
		form.append('<input type="hidden" name="device" value="' + deviceId + '" />');
		form.append('<input type="hidden" name="section" value="' + sectionId + '" />');

		for (i = 0; i < include.length; i++) {
			var line = $('<div class="field fld_' + include[i] + '"></field>');
			var options;
			var field = entries[include[i]];
			if (!field) {
				throw new Error('No include field with ID ' + include[i] + ' in available entries');
			}
			var value;
			if (field.display) {
				value = field.display;
			} else {
				value = "";
			}

			var definition = fieldDefinition(sectionId, include[i]);
			var id = definition.id;
			var label = definition.name;

			var classString = definition.mandatory ? 'class="required" ' : '';
			line.append('<label class="label">' + label + ':</label>');

			switch (definition.type) {
			case 'NUMBER':
				line.append('<input ' + classString + 'type="text" size="10" name="' + id + '" value="' + value + '" />');
				break;
			case 'MULTIPLE_SELECTOR':
				options = $('<select ' + classString + 'type="text" name="' + id + '">');
				for (j = 0; j < definition.options.length; j++) {
					var option = $('<option>' + definition.options[j] + '</option>');
					option.attr('value', definition.options[j]);
					if (definition.options[j] === value) {
						option.attr('selected', 'selected');
					}
					options.append(option);
				}
				line.append(options);
				break;
			case 'BOOLEAN_SELECTOR':
				var checkbox = $('<input ' + classString + 'type="checkbox"></input>');
				checkbox.attr('name', id);
				checkbox.attr('value', 'true');
				if (value === 'true') {
					checkbox.attr('checked', 'checked');
				}
				line.append(checkbox);
				break;
			case 'HEX_BITMAP':
				if (!value) {
					value = 0;
				}
				var checkboxes = bitmap(line, id, value);
				var chars,
					bits,
					b = 0;
				for (chars = value.length - 1; chars >= 0; chars--) {
					var char = parseInt(value[chars], 16);
					for (bits = 0; bits < 4; bits++) {
						if (b < checkboxes.length && checkboxes[b]) {
							if (char % 2 == 1) {
								checkboxes[b].attr('checked', 'checked');
							}
						}
						b++;
						char = Math.floor(char / 2);
					}
				}
				break;
			case 'LONG_TEXT':
				line.append('<input ' + classString + 'type="text" size="40" autocomplete="off" name="' +
						id + '" value="' + value.replace(/"/g, '&quot;') + '" />');
				break;
			default:
				line.append('<input ' + classString + 'type="text" size="15" utocomplete="off" name="' +
					id + '" value="' + value.replace(/"/g, '&quot;') + '" />');
			}
			form.append(line);
		}
		var button = $('<input class="button" type="submit" name="execute" />');
		if (formType === 'edit') {
			button.attr('value', 'Update');
		} else {
			button.attr('value', 'Add');
		}
		form.append(button);
		form.submit(form, update);
		return form;
	}

	function fieldDefinition(sectionId, fieldId) {
		var indexOf = sectionId.indexOf(':');
		if (indexOf > 0) {
			sectionId = sectionId.substr(0, indexOf);
		}
		var section = definition[sectionId];
		var i;
		for (i = 0; i < section.fields.length; i++) {
			var field = section.fields[i];
			if (field.id === fieldId) {
				return field;
			}
		}
	}

	function update(event) {
		event.preventDefault();
		ibexis.dialog.showBusy();
		var form = $(this).closest('form');
		var segment = form.closest(".ibexis-ui-segment");
		var isEdit = form.hasClass('edit');
		var action = form.attr('action');
		// find all elements except checkboxes
		var formContents = form.find(':not(input[type=checkbox])');
		var formData = formContents.serialize();
		formData += appendBitmaps(form);
		formData += appendCheckBoxes(form);
		form = $(this);
		$.ajax({url: action, type: 'GET', data: formData, dataType: 'json'})
		.done (function(data) {
			form.find('div.field').removeClass('error').find('span.error').remove();
			var hasErrors = false;
			for (item in data) {
				var field = form.find('div.field.fld_' + item);
				field.addClass('error');
				field.append('<span class="error">' + data[item] + '</span>');
				hasErrors = true;
			}
			ibexis.dialog.clearBusy();
			if (!hasErrors) {
				ibexis.dialog.close();
				ibexis.shell.feedback('Configuration changes saved and queued for upload to device');
				if (!isEdit) { // is adding new entry
					var deviceOid = form.find('input[name=device]').attr('value');
					delete configurations[deviceOid];
					loadConfiguration(deviceOid, function(configuration) {});
					var targetUpdate = ibexis.dialog.owner();
					ibexis.shell.refresh(targetUpdate);
				}
			}
		}).fail (function(data) {
			ibexis.show.showHtmlError(data);
		});
		return false;
	}

	function remove(deviceOid, group, div) {
		// event.preventDefault();
		ibexis.dialog.showBusy();

		var action = 'configuration-update.app?action=remove&device=' + deviceOid + '&group=' + group;

		$.ajax({url: action, type: 'GET', dataType: 'json'})
		.done (function(data) {
			ibexis.dialog.clearBusy();
			if (data.result && data.result === "queued") {
				ibexis.shell.feedback('Group removed from configuration and request queued for upload to device.');
				div.remove();
			} else {
				ibexis.shell.feedback('Group was not found in the configuration. Refresh the section to see the current configuration.');
				// TODO can we refresh the section automatically?
			}
		}).fail (function(data) {
			ibexis.shell.showHtmlError(data);
		});
		return false;
	}

	/*
	 * For a list of checkboxes for a bitmap this function determines the required bitmap value
	 * and replaces the set of checkboxes with an input element of the same name, whose value 
	 * is the bitmap
	 */
	function appendBitmaps(form) {
		var list = form.find('.checkbox-list');
		if (list) {
			var hexValue = '';
			list.each(function() {
				var value = 0;
				//var flag;
				// var bit = 0;
				$(this).find('input[type=checkbox]').each(function() {
					if ($(this).prop('checked')) {
						var bit = $(this).attr('value');
						value = value + Math.pow(2, bit);
					}
				});
				hexValue = value.toString(16) + hexValue;

			});
			return '&' + list.data('name') + '=' + hexValue.toUpperCase();
		} else {
			return '';
		}
	}

	/*
	 * HTML does not include a checkbox if it is not set, so this function makes all checkboxes 
	 * set and changes the value to reflect its true state - essentially if not checked 
	 * the value is set to 'false'.
	 */
	function appendCheckBoxes(form) {
		var formData = '';
		var lists = form.find('.checkbox-list');
		form.find('input[type=checkbox]').not(lists.find('input')).each(function() {
			var flag = $(this).prop('checked') ? 'true' : 'false';
			formData += '&' + $(this).prop('name') + '=' + flag;
		});
		return formData;
	}

	var addConfigurationSection = function(owner, section, fieldIds, deviceOid) {
		loadConfiguration(deviceOid, function(configuration) {
			var entries = {};
			var fields = definition[section].fields;
			var i,
				id,
				defaultValue;
			for (i = 0; i < fields.length; i++) {
				id = fields[i].id;
				defaultValue = fields[i].default ? fields[i].default : '';
				entries[id] = {
					display : defaultValue,
					id : id
				};
			}

			var div = $('<div>');
			var content = form('action', deviceOid, section + ':', entries, fieldIds);
			div.append(content);
			var name = definition[section].name;
			div.find('form input:FIRST-CHILD').before('<div class="title">Add New ' + name + '</div>');
			ibexis.dialog.openWithContent(div, owner);
		});
	}

	var editConfigurationSection = function(section, fields, id, deviceOid, title) {
		var div = $('#ibexis-device-configuration-' + section + '-' + id);
		loadConfiguration(deviceOid, function(configuration) {
			var entries = createEntryMap(configuration[section]);
			var content = form('edit', deviceOid, section, entries, fields, title);
			div.append(content);
		})
	}

	function loadConfiguration(deviceOid, result) {	
		console.log('get configuration for ' + deviceOid);
		var configuration = configurations[deviceOid];
		if (configuration) {
			//configuration.value = value;
			result(configuration);
		} else {
			var value = function(section, index, property) {
				var fields = this[section].fields;
				for (i = 0; i < fields.length; i++) {
					var id = fields[i].id;
					if (id === property) {
						return fields[i].value; 
					}
				}
				return null;
			};

			console.log('downloading configuration data for ' + deviceOid);
			$.getJSON('configuration-data.app', 'device=' + deviceOid)
				.done(function(data) {
					configurations[deviceOid] = data.configuration;
					definition = data.definition;
					data.configuration.value = value;
					result(data.configuration);
				}).fail (function(data) {
					ibexis.shell.showHtmlError(data);
				});
		}
	}
	
	var segment = function(deviceOid, toSegment) {
		var section = toSegment.data('label')
		var div = $('<div class="ibexis-ui-configuration the-real-contents">');
		
		var fields;
		switch (section) {
		case 'Communication':
			fields = ['communication-preference', 'retry-interval'];
			showSection(deviceOid, div, 'system', fields, 'Communication Preference');
			showSection(deviceOid, div, 'cellular', null, 'Cellular');
			showSection(deviceOid, div, 'ethernet', null, 'Ethernet');
			showSection(deviceOid, div, 'wifi', null, 'Wifi');
			break;
			
		case 'Operation':
			fields = ['mode', 'sleep-idle-time'];
			showSection(deviceOid, div, 'system', fields, 'Power');
			fields = ['debug-console', 'debug-usb', 'debug-file'];
			showSection(deviceOid, div, 'system', fields, 'Debug');
			break;
			
		case 'Update':
			fields = ['use-ntp', 'ntp-server', 'ntp-interval', 'firmware-server', 'firmware-port', 'firmware-prefix'];
			showSection(deviceOid, div, 'system', fields);
			break;
			
		case 'Upload':
			fields = ['device-server', 'device-path', 'device-port', 'device-upload-interval', 'separate-data-upload', 'data-server', 'data-port', 'data-path', 'data-upload-interval', 'upload-files'];
			showSection(deviceOid, div, 'system', fields, "Upload", function(line, id, value) {
					var list = $('<div class="checkbox-list" data-name="' + id + '"></div>');
					var bits = [];
					for (var j = 0; j < uploadBits.length; j++) {
						var element = uploadBits[j];
						var item = $('<div></div>');
						var checkbox = $('<input type="checkbox"></input>');
						checkbox.attr('name', id);
						checkbox.attr('value', j);
						item.append(checkbox);
						item.append(element);
						list.append(item);

						bits[j] = checkbox;
					}
					line.append(list);
					line.append('(' + value + ')');
					return bits;
				});
			break
			
		case 'Ports':
			var link = $('<a class="ibexis-ui-add button" href="#"><span class="ibexis-ui-add">Add</span></a>');
			link.on('click', function () {
				var owner = link.closest('.ibexis-ui-segment');
				addConfigurationSection(owner, 'port', ['type', 'direction', 'slot', 'address', 'name'], deviceOid);
				return false;
			});
			div.append(link);
			listPorts(div, deviceOid, false);
			break;
			
		case 'Internal':
			var link = $('<a class="ibexis-ui-add button" href="#"><span class="ibexis-ui-add">Add</span></a>');
			link.on('click', function () {
				var owner = link.closest('.ibexis-ui-segment');
				addConfigurationSection(owner, 'port', ['type', 'direction', 'slot', 'address', 'name'], deviceOid);
				return false;
			});
			div.append(link);
			listPorts(div, deviceOid, true);
			break;
			
		case 'Status':
			var link = $('<a class="ibexis-ui-add button" href="#"><span class="ibexis-ui-add">Add</span></a>');
			link.on('click', function () {
				var owner = link.closest('.ibexis-ui-segment');
				addConfigurationSection(owner, 'status', ['enabled', 'interval'], deviceOid);
				return false;
			});
			div.append(link);
			listStatus(div, deviceOid);
			break;
			
		case 'Sampling':
			var link = $('<a class="ibexis-ui-add button" href="#"><span class="ibexis-ui-add">Add</span></a>');
			link.on('click', function () {
				var owner = link.closest('.ibexis-ui-segment');
				addConfigurationSection(owner, 'sampling', ['enabled', 'interval'], deviceOid);
				return false;
			});
			div.append(link);
			listSampling(div, deviceOid);
			break;
			
		case 'Monitoring':
			var link = $('<a class="ibexis-ui-add button" href="#"><span class="ibexis-ui-add">Add</span></a>');
			link.on('click', function () {
				var owner = link.closest('.ibexis-ui-segment');
				addConfigurationSection(owner, 'monitor', ['enabled', 'interval'], deviceOid);
				return false;
			});
			div.append(link);
			listMonitors(div, deviceOid);
			break;
		}
		toSegment.append(div);
	}
	
	function showSection(deviceOid, intoContainer, section, fields, title, bitmap) {
		loadConfiguration(deviceOid, function(configuration) {
			var entries = createEntryMap(configuration[section]);
			var content = form('edit', deviceOid, section, entries, fields, title, bitmap);
			intoContainer.append(content);
		})
    }

	return {
		initModule : initModule,
		display : display,
		load : loadConfiguration,
	};

}());
