/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.display = (function() {
	
	var l10n = ibexis.localization;

	var descriptors = {};
	var nextId = 0;

	var initModule = function($container) {
		ibexis.shell.registerTemplateProcessor('apps', function (element) {
			var descriptor;
			var i;
			var accountDescriptors = element.data('account-descriptor');
			for (i = 0; i < accountDescriptors.length; i++) {
				descriptor = accountDescriptors[i];
				descriptors[descriptor.id] = descriptor;
			}
			
			var userDescriptors = element.data('user-descriptor');
			for (i = 0; i < userDescriptors.length; i++) {
				descriptor = userDescriptors[i];
				descriptors[descriptor.id] = descriptor;
			}
			var applicationId = element.data('application');
			if (!applicationId || applicationId === 'default' || applicationId === '') {
				applicationId = element.data('default-application');
				if (!applicationId || applicationId === '') {
					applicationId = 'default';
				}
			}
			descriptor = descriptors['app-' + applicationId];
			if (!descriptor) {
				return $('<div class="ibexis-ui-segment" data-label="Unknown App"></div>')
			}
			
			var account = element.data('account');
			var divs = [];
			for (i = 0; i < descriptor.apps.length; i++) {
				var name = descriptor.apps[i].name;
				var id = descriptor.apps[i].descriptor;	
				var content = descriptor.apps[i].content;
				var div = $('<div>');
				div.addClass('ibexis-ui-segment');
				div.attr('data-label', name);
				if (id) {
					div.attr('data-descriptor', id);
				}
				if (content) {
					var pos = content.indexOf("$account");
					if (pos > 0) {
						content = content.substring(0, pos) + account + content.substring(pos + 8);
					}
					div.attr('data-content', content);
				}
				div.attr('data-account', account);
				divs.push(div);
			}
			return divs;
		});

		ibexis.shell.registerSegmentProcessor('descriptor', function (segment, id, showContext) {
			var descriptor = ibexis.display.descriptor(id);
			if (descriptor) {

				displaySegment(segment, descriptor, true);
				showContext(segment);
				
				var refresh = segment.data('refresh-on');
				if (refresh == 'True') {
					var interval = segment.data('refresh-interval');
					if (interval == 0) {
						interval = 600;
					}
					setInterval(displaySegment, interval * 1000, segment, descriptor, false);
				}
			} else {
				segment.append('<div>No descriptor for "' + id + '"</div>');
			}
		});
		

		ibexis.shell.registerLinkProcessor('descriptor', function (container, id, attributes) {
			var descriptor = ibexis.display.descriptor(id);
			if (descriptor) {
				var dummyContent = $('<div>');
				dummyContent.data('label', attributes['data-title'].value);
				var segment = ibexis.shell.insertNewSegment(dummyContent, container, null);
				segment.data('account', attributes['data-account'].value);
				segment.data('station', attributes['data-station'].value);
				displaySegment(segment, descriptor, true);
			} else {
				segment.append('<div>No descriptor for "' + id + '"</div>');
			}
		});

	};
	
	var displaySegment = function(segment, descriptor, always) {
		// var container = segment.closest('.ibexis-ui-container');
		var content;
		if (descriptor.apps) {
			content = createAppsGroup(segment, descriptor);
			
		} else if (descriptor.sections) {
			content = createSections(segment, descriptor);

		} else if (descriptor.content) {
			var c = descriptor.content;
			var pos = c.indexOf("$account");
			if (pos > 0) {
				c = c.substring(0, pos) + account + c.substring(pos + 8);
			}
			div.attr('data-content', c);
			
		} else if (descriptor.data) {
			if(always || segment.is(':visible')) {
				segment.empty();
				var view = createDataView(segment, descriptor);
				view.addAccountOid(segment.data('account'))
				view.addDeviceOid(segment.data('device'));
				view.addPeripheralOid(segment.data('peripheral'));
				view.addStationOid(segment.data('station'));
				content = view.init();
				
				if (descriptor.refresh == 'True') {
					var interval = $('div.ibexis-ui-refresh').data('refresh-interval');
					if (interval == 0) {
						interval = 600;
					}
					setInterval(view.refresh, interval * 1000, segment, descriptor, false);
				}
			}
		}
		if (content) {
			ibexis.shell.insertSegmentContents(content, segment);
		} else {
			console.error('Could not display descriptor ' + descriptor.id);
		}
		return content;
	}	
	
	function createAppsGroup(segment, descriptor) {
		var tabContents = $('	<div class="ibexis-ui-container ibexis-ui-tabs" data-target="reveal" ' +
				'data-context="descriptor derived content: ' +descriptor + '">');
		var sections =descriptor.apps;
		var account = segment.data('account');
		for (var i = 0; i < sections.length; i++) {
			var c = $('<div class="ibexis-ui-segment data" data-label="' + sections[i].name + '" ></div>');
			var insertionContent = sections[i].content;
			var insertDescriptor = sections[i].descriptor;
			if (insertionContent) {
				var pos = insertionContent.indexOf("$account");
				if (pos > 0) {
					insertionContent = insertionContent.substring(0, pos) + account + insertionContent.substring(pos + 8);
				}
				c.attr('data-content', insertionContent);
			} else if (insertDescriptor) {
				c.attr('data-descriptor', insertDescriptor);
				c.attr('data-account', account);
			}
			tabContents.append(c);
		}
		return tabContents;
	}
	
	function createSections(segment, descriptor) {
		var account = segment.data('account');
		//var container = segment.closest('.ibexis-ui-container');
		var tabContents = $('	<div class="ibexis-ui-container ibexis-ui-tabs" data-target="reveal" ' +
				'data-context="descriptor derived content: ' + descriptor.id + '">');
		var sections = descriptor.sections;
		for (var i = 0; i < sections.length; i++) {
			var c = $('<div class="ibexis-ui-segment data" data-label="' + sections[i].name + '" ></div>');
			if (sections[i].template) {
				var template = sections[i].template;
				template = 'application/__' + template + '.shtml?account=' + account + '&descriptor=' + sections[i].action;
				c.attr('data-content', template);

			} else if (sections[i].content) {
				var content = sections[i].content;
				var pos = content.indexOf("$account");
				if (pos > 0) {
					content = content.substring(0, pos) + account + content.substring(pos + 8);
				}
				c.attr('data-content', content);

			} else if (sections[i].descriptor) {
				var insertDescriptor = sections[i].descriptor;
				c.attr('data-descriptor', insertDescriptor);
				var ref = '';
				if (sections[i].data === 'account') {
					ref = segment.data('accont');
				} else if (sections[i].data === 'station') {
					ref = segment.data('station');
				} else if (sections[i].data === 'device') {
					ref = segment.data('device');
				}
				c.attr('data-' + sections[i].data, ref);
			} else if (sections[i].selector) {
				var selector = sections[i].selector;
				var settings = sections[i];
				//var settings = descriptor.stationSelector;
				
				content = $('<div class="the-real-contents"></div>');
	
				/*
				container.append(content);
				container = content;
				*/
				
				file = ibexis.data.stationList(account);
				console.log("requesting station list " + file);
				file.load("??", function(name, results) {
					console.log("received " + results.length + " rows");
					
					var table = $('<table class="table table-striped table-sorter"></table>');
					if (settings.title) {
						table.append('<caption>' + settings.title + '</caption>');
					}
					
					var i;
					var columns = [];
					var head = $('<thead>');
					table.append(head);
					var row = $('<tr></tr>');
					if (results.length > 0) {
						if (!settings.fields || settings.fields.length == 0) {
							row.append('<th>Name</th>');
							columns.push('name');
							row.append('<th>Device</th>');
							columns.push('device');
							row.append('<th>Latitude</th>');
							columns.push('latitude');
							row.append('<th>Longitude</th>');
							columns.push('longitude');
						} else {
							for (i = 0; i < settings.fields.length; i++) {
								var field = settings.fields[i];
								columns.push(field);
								//row.append('<th>' + descriptor.metadata.sensorTitle(field) + '</th>');
								row.append('<th>!!!' + field.charAt(0).toUpperCase() + field.substring(1) + '</th>');
							}
						}
					}
					head.append(row);
					
					var body = $('<tbody>');
					table.append(body);
					for (i = 0; i < results.length; i++) {
						row = $('<tr></tr>');
						var entry = results[i];
						var oid = entry['oid'];
						var name = entry['name'];
						for (j = 0; j < columns.length; j++) {
						//	row.append('<td>' + descriptor.metadata.value(columns[j], entry[columns[j]]) + '</td>');
							var link = $('<a class="ibexis-ui-link">');
							link.attr('data-station-oid', oid);
							link.bind('click', {'oid' : oid, 'title' : name}, function(event) {
								var container = link.closest('.ibexis-ui-container');
								var id = settings.action;
								
								//var segment = $('<div data-test="added" class="ibexis-ui-segment">');
								//segment.attr('data-label', settings.name + ' ' +  event.data.oid);
								//segment.attr('data-station', event.data.oid);
								//container.append(segment);
									
								var dummy = $('<div>');
								dummy.attr('data-label', event.data.title);
								var segment = ibexis.shell.insertNewSegment(dummy, container, id);
								segment.attr('data-station', event.data.oid);
								
								// REVIEW: this is code from the top of this file - refactor out
								var newDescriptor =  ibexis.display.descriptor(id);
								if (newDescriptor) {
									displaySegment(segment, newDescriptor, true);
									// showContext(segment); --> needs to call indicateSegmentShowing in shell
									
									var refresh = segment.data('refresh-on');
									if (refresh == 'True') {
										var interval = segment.data('refresh-interval');
										if (interval == 0) {
											interval = 600;
										}
										setInterval(displaySegment, interval * 1000, segment, newDescriptor, false);
									}
								} else {
									segment.append('<div>No descriptor for "' + id + '"</div>');
								}
								return false;
							});
							var cell = $('<td>')
							link.append(entry[columns[j]]);
							cell.append(link);
							row.append(cell);
						}
						body.append(row);
					}
					content.append(table);
					c.append(content);
				});
	
			}
			tabContents.append(c);
		}
		return tabContents
	}
	
	var createDataView = function(container, descriptor) {
		var station;
		var clear, refresh;
		var dateRange;
		var account;
		var device;
		var peripheral;
		var content;
	
		var init = function() {
			return initData(descriptor.data);
		}
		
		function initData(data) {
			var timeframe = data.timeframe;
			var context = $('<div class="the-real-contents" data-target="reveal" ></div>');
			content = context;
			
		//	container.append(content);
		//	container = content;

			if (timeframe.type === "Last4Weeks") {
				dateRange = ibexis.date.createDateRange(24 * 7 * 4);
				refresh();
			} else if (timeframe.type === "Last2Weeks") {
				dateRange = ibexis.date.createDateRange(24 * 7 * 2);
				refresh();
			} else if (timeframe.type === "Last7Days") {
				dateRange = ibexis.date.createDateRange(24 * 7);
				refresh();
			} else if (timeframe.type === "Last5Days") {
				dateRange = ibexis.date.createDateRange(24 * 5);
				refresh();
			} else if (timeframe.type === "Last3Days") {
				dateRange = ibexis.date.createDateRange(24 * 3);
				refresh();
			} else if (timeframe.type === "Last2Days") {
				dateRange = ibexis.date.createDateRange(24 * 2);
				refresh();
			} else if (timeframe.type === "Last24Hours") {
				dateRange = ibexis.date.createDateRange(24);
				refresh();
			} else if (timeframe.type === "Last6Hours") {
				dateRange = ibexis.date.createDateRange(6);
				refresh();
			} else if (timeframe.type === "Last3Hours") {
				dateRange = ibexis.date.createDateRange(3);
				refresh();
			} else if (timeframe.type === "LastHour") {
				dateRange = ibexis.date.createDateRange(1);
				refresh();

			} else if (timeframe.type === "Period") {
				var period = (timeframe.weeks * 7 + timeframe.days) * 24 + timeframe.hours;
				dateRange = ibexis.date.createDateRange(period);
				refresh();

			} else if (timeframe.type === "TimeframeOptions") {
				context.addClass('ibexis-date-viewer');
				content = $('<div></div>');
				content.addClass('ibexis-date-view');
				context.append(content);
				ibexis.date.addPeriodOptions(context, timeframe.period, function(e, view, startDate, period) {
					setDateRange(ibexis.date.createDateRange(period));
					refresh();
				});

			} else if (timeframe.type === "DateSelector") {
				context.addClass('ibexis-date-viewer');
				content = $('<div></div>');
				content.addClass('ibexis-date-view');
				context.append(content);
				ibexis.date.addDateSelector(context, function(e, view, startDate, period) {
					setDateRange(ibexis.date.createDateRange(period * 24, startDate));
					refresh();
				});

			} else if (timeframe.type === "TimeframeSelector") {
				context.addClass('ibexis-date-viewer');
				content = $('<div></div>');
				content.addClass('ibexis-date-view');
				context.append(content);
				ibexis.date.addDatePeriodSelector(context, timeframe.period, function(e, view, startDate, period) {
					setDateRange(ibexis.date.createDateRange(period * 24, startDate));
					refresh();
				});
			}
			
			return context;
		}

		var setDateRange = function(range) {
			dateRange = range;
		}
		
		var setAccountOid = function(accountOid) {
			account = accountOid; 
		}
		
		var setDeviceOid = function(deviceOid) {
			device = deviceOid; 
		}
		
		var setPeripheralOid = function(peripheralOid) {
			peripheral = peripheralOid; 
		}
		
		var setStationOid = function(stationOid) {
			station = stationOid; 
		}
		
		clear = function() {
			content.empty();
			content.append('<div class="widget-id">' + descriptor.id + '/' + descriptor.name + '</div>');
		}
		
		/*
		 * Load data from server and add all the components specified by the descriptor.
		 */
		refresh = function() {
			clear();
			if (descriptor.data) {
				ibexis.charting.resetColors(descriptor.data.colour);
				var dataSegment = descriptor.data;
				var metadata;
				metadata = ibexis.data.createMetadata();
				for (var i = 0 ; i < dataSegment.dataSources.length ; i++) {
					var source = dataSegment.dataSources[i];
					if (source.type === "Samples" || source.type === "Data" || source.type === "Events") {
						metadata.downloadApplicationData(station, function() {
							loadData(dataSegment, metadata);							
						});
						return;
					}
				}
				loadData(dataSegment, metadata);
			}
		}
		
		
	function loadData(dataSegment, metadata) {
		/*
		 * This loop loads up all the files and when all loaded calls the addComponents method.
		 */
		var data = {};
		var file;
			var i;
			var count = dataSegment.dataSources.length;
			for (i = 0 ; i < dataSegment.dataSources.length ; i++) {
				var source = dataSegment.dataSources[i];
				if (source.type === "Log") {
					file = ibexis.data.logData(device, source.log, dateRange);
					console.log("requesting log entries " + file.source);
					var log = source.log;
					file.load(source, function(source, results) {
						data[source.name] = results;
						console.log("received " + results.length + " log rows");
						count--;
						metadata.addForLog(source.name, source.log);
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
					
				} else if (source.type === "Status") {
				//		count++;
						file = ibexis.data.statusData(device, source.log, dateRange);
						console.log("requesting status entries " + file.source);
						file.load(source.name, function(name, results) {
							data[name] = results;
							console.log("received " + results.length + " rows");
							count--;
							//metadata[name] = ibexis.data.statusMetadata();
							metadata.addForLog(source.name, "status");
							if (count === 0) {
								processData(content, dataSegment, data, dateRange, metadata);
							}
						});
						
				} else if (source.type === "Peripheral_Log") {
				//	count++;
					file = ibexis.data.peripheralData(peripheral, dataType, dateRange);
					console.log("requesting peripheral log entries " + file.source);
					file.load(source.name, function(name, results) {
						data[name] = results;
						console.log("received " + results.length + " peripheral log ows");
						count--;
						// metadata[name] = ibexis.data.peripheralMetadata();
						// metadata.addForPeripheral(source.name);
						metadata.addForLog(source.name, "peripheral");
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
					
				} else if (source.type === "Samples") {
				//	count++;
					var dataType = "samples"
						/*
					if (source.data) {
						dataType = source.data 
					}
					*/
						
						
						/*
					console.log("requesting data entries " + file.source);
					ibexis.data.stationData(station, dataType, dateRange, metadata, function(source, results) {
						data[source.name] = results;
						console.log("received " + results.length + " sample rows");
						count--;
						//metadata[name] = ibexis.data.stationMetadata(station);
						metadata.mapDataSource(source.name, "sensors");
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
						*/
						
					file = ibexis.data.stationData(station, dataType, dateRange, metadata);
					console.log("requesting data entries " + file.source);
					file.load(source, function(source, results) {
						data[source.name] = results;
						console.log("received " + results.length + " sample rows");
						count--;
						//metadata[name] = ibexis.data.stationMetadata(station);
						metadata.mapDataSource(source.name, "sensors");
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
					
				} else if (source.type === "Data") {
				//	count++;
					var dataType = "data"
					if (source.data) {
						dataType = source.data 
					}
					file = ibexis.data.stationData(station, dataType, dateRange, metadata);
					console.log("requesting other data entries " + file.source);
					file.load(source, function(source, results) {
						data[source.name] = results;
						console.log("received " + results.length + " manual data rows");
						count--;
						//metadata[name] = ibexis.data.stationMetadata(station);
						metadata.mapDataSource(source.name, source.data);
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
					
				} else if (source.type === "Events") {
				//	count++;
					var dataType = "events"
					if (source.data) {
						dataType = source.data 
					}
					file = ibexis.data.stationData(station, dataType, dateRange, metadata, false);
					console.log("requesting event entries " + file.source);
					file.load(source, function(source, results) {
						data[source.name] = results;
						console.log("received " + results.length + " event rows");
						count--;
						//metadata[name] = ibexis.data.stationMetadata(station);
						metadata.mapDataSource(source.name, "sensors");
						metadata.fields(source.name).add([
							{
								id: 'type',
								name: 'Event Type',
							},
							{
								id: 'id',
								name: 'Event ID',
							}
						]); 
						if (count === 0) {
							processData(content, dataSegment, data, dateRange, metadata);
						}
					});
				}
			}
		}
		
		
		return {
			init : init,
			addAccountOid : setAccountOid,
			addDeviceOid : setDeviceOid,
			addPeripheralOid : setPeripheralOid,
			addStationOid : setStationOid,
			setDateRange : setDateRange,
			clear : clear,
			refresh : refresh,
		};
	}
	
	function processData(content, descriptor, data, dateRange, metadata) {
		var trackers = []
		/*
		trackers[0] = {
				enter: function() {
					console.log("move in ");
				},
				
				exit: function() {
					console.log("move out ");					
				},
				
				move: function(time, xx, element) {
					console.log("move to " + moment(time).format() + " " + element + " " + xx);
				}
		};
		*/
	//	trackers.push(addMouseStatus(content, descriptor));
		
		
	//	var metadata =  descriptor.metadata;
		if (descriptor.processes) {
			var processes = descriptor.processes;
			
			var i, process;
			for (i = 0 ; i < processes.length ; i++) {
				process = processes[i];
				
				if (process.type === 'Interval') {
					var results = data[process.source];
					if (!results) {
						ibexis.shell.showErrorMessage("There is no results for processor " + process.source + ' so nothing has been applied');						
					} else if (results.length > 1) {
						var magnitude = 'seconds';
						var j;
						var last = results[0]['date'];
						if (process.magnitude) {
							magnitude = process.magnitude;
						}
						for (j = 1; j < results.length; j++) {
							var current = results[j]['date'];
							var difference = current.diff(last, magnitude);
							results[j-1]['interval'] = -difference;
							last = current;
						}
						
						metadata.fields(process.source).add([
							{ 
								id: "interval",
								name: "Interval",
								type: "INTEGER"
							},
						]);
					}
				}
				
				if (process.type === 'Transmissions') {
					var results = data[process.source];
					if (!results) {
						ibexis.shell.showErrorMessage("There is no results for processor " + process.source + ' so nothing has been applied');						
					} else if (results.length > 1) {
						var j;
						var last = results[0]['transmission count'];
						for (j = 1; j < results.length; j++) {
							var current = results[j]['transmission count'];
							var difference = current - last;
							results[j-1]['transmissions'] = -difference;
							last = current;
						}
					}
					
					metadata.fields(process.source).add([
						{ 
							id: "transmissions",
							name: "Transmissions",
							type: "INTEGER"
						},
					]);
				}
				
				if (process.type === 'BasicStats') {
					var source = data[process.source];
					var sum = {};
					var count = {};
					for (j = 0; j < source.length; j++) {
						for (field in results[j]) {
							if (field !== "date" && source[j][field] != undefined) {
								if (!sum[field]) {
									sum[field] = 0;
									count[field] = 0;
								}
								sum[field] += source[j][field];
								count[field]++;
							}
						}
					}
					
					var results = [{ date : source[0]['date']}];
					for (field in sum) {
						results[0][field ] = sum[field] / count[field];
					}
					data[process.name] = results;
				}
			}
		}
		if (descriptor.components) {
			var components = descriptor.components;
			addComponents(content, components, data, metadata, dateRange, trackers);
		}
	}
	
	function addComponents(parent, components, data, metadata, dateRange, trackers) {				
		var i;
		for (i = 0 ; i < components.length ; i++) {
			var component = components[i];
			if ("show" in component && component.show == false) {
				continue;
			}
			
			if (component.splitLine) {
				// block.addClass('block');
				parent.append('<br />');
			}
			var block = $('<div class="ibexis-ui-widget">');
			parent.append(block);

			
			
//			component.metadata = metadata[component.source];
			
			var componentMetadata = metadata;
			
			if (component.type === 'Table') {
				addTable(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'FormEntry') {
				addFormEntry(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'EventGrid') {
				addEventGrid(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'AlarmGrid') {
				addAlarmGrid(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'AlarmDashboard') {
				addAlarmDashboard(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'AlarmHistory') {
				addAlarmHistory(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'Tooltip') {
				trackers.push(addMouseStatus(block, componentMetadata, component));
			} else if (component.type === 'Graph') {
				addGraph(block, data, componentMetadata, component, dateRange, trackers);
			} else if (component.type === 'SparkLine') {
				addSparkline(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'Value') {
				addValue(block, data, componentMetadata, component, dateRange);
			} else if (component.type === 'DateRangeHeading') {
				addDateRangeHeading(block, data, component, dateRange);
			} else if (component.type === 'DateTimeRangeHeading') {
				addDateTimeRangeHeading(block, data, component, dateRange);
			} else if (component.type === 'Header') {
				addHeader(block, component);
			} else if (component.type === 'EmptyHeading') {
				var source = component.source;
				if (!source) {
					source = 'log';
				}
				if (data[source].length == 0) {
					var text = 'No entries';
					if (component.text) {
						text = component.text;
					}
					block.append('<div class="empty">' + text + '</div>');
				}
			}
		}
	};

	function addMouseStatus(parent, metadata, descriptor) {
		var status = $('<div class="cursor-table hide">');
		parent.append(status);
		var table = $('<table>');
		table.append($('<tr data-id="date"><td></td><td></td></tr>'));
		status.append(table);
		
		var fields = metadata.fields(descriptor.source);
		var names = fields.ids();
		for (i = 0; i < names.length; i++) {
			var field = names[i];
			var type = fields.type(field);
			var title = fields.title(field);
			var unit = fields.unit(field);
			
			var row = $('<tr>');
			row.attr('data-id', field);
			table.append(row);
			row.append($('<td>' + title +  '</td>'));
			row.append($('<td>'));
		}
			
		return {
			enter: function() {
				status.removeClass('hide')
			},
			
			exit: function() {
				status.addClass('hide')
			},
			
			move: function(element, time, data) {
				for (var p in data) {
					var row = table.find('tr[data-id=' + p +']');
					if (row.length == 1) {
						var cell = row.children(':last-child');
						var value;
						if (p === 'date') {
							value = l10n.formatDateTime(moment(time));
						} else {
							value = fields.valueWithUnit(p, data[p]);
						}
						cell.empty();
						cell.append(value);
					}
				}
			}
		}
	}

	function addDateRangeHeading(parent, data, descriptor, dateRange) {
		var start = l10n.formatDate(dateRange.start);
		var end = l10n.formatDate(ibexis.date.endDate(dateRange));
		var size = descriptor.size;
		header(parent, size, start + ' ~ ' + end);
	}

	function addDateTimeRangeHeading(parent, data, descriptor, dateRange) {
		var start = l10n.formatDateTime(dateRange.start);
		var end = l10n.formatDateTime(ibexis.date.endDate(dateRange));
		var size = descriptor.size;
		header(parent, size, start + ' ~ ' + end);
	}

	function addHeader(parent, descriptor) {
		var text = descriptor.text;
		var size = descriptor.size;
		header(parent, size, text)
	}

	function header(parent, size, text) {
		if (!size) {
			size = 3;
		}
		parent.append('<h' + size + '>' + text);
	}

	function addValue(parent, data, metadata, descriptor) {
		var results = data[descriptor.source];
		var reading = ibexis.charting.reading(parent, results, metadata, descriptor);
		reading.render();	
	}

	function addSparkline(parent, data, metadata, descriptor) {
		var results = data[descriptor.source];

		var line = ibexis.charting.sparkLine(parent, data[descriptor.source], metadata, [descriptor.field], descriptor);
		var range = minAndMaxValues(descriptor.min, descriptor.max, [descriptor.field], results);
		line.yScale(d3.scale.linear().domain([ range.min, range.max ]));
		line.render();	
	}

	function addGraph(parent, data, metadata, descriptor, dateRange, trackers) {
		var i;
		var results = data[descriptor.source];
		var range = dateRange;
		if (descriptor.timeLimit) {
			range = ibexis.date.createDateRange(descriptor.timeLimit);
			var start = range.start;
			var limited = [];
			for (i = 0; i < results.length; i++) {
				if (results[i].date.isAfter(start)) {
					limited.push(results[i]);
				}
			}
			results = limited;
		}
	
		var allFields = [];
		
		if (descriptor.fields) { 
			// TODO pass in the DOM element, not the SVG one
			var columns = [];
			for (i = 0; i < descriptor.fields.length; i++) {
				columns.push(descriptor.fields[i]);
			}				
			
			var format = descriptor.format;
			if (!format) {
				format = "line";
			}
			
			var valueRange = minAndMaxValues(descriptor.min, descriptor.max, descriptor.fields, results);
			var scale = d3.scale.linear().domain([ valueRange.min, valueRange.max ]);
			var chart2 = ibexis.charting.lineChart(parent, results, metadata, columns, range, descriptor, format.toUpperCase());
			for (i = 0; i < trackers.length; i++) {
				chart2.addMouseTracker(trackers[i]);
			}
			chart2.yScale(scale);		
			// TODO indicate how to render the data sets - line, bar, dots etc
			chart2.render();
		} else {
			var stateFields = [];
			var fields = metadata.fields(descriptor.source);
			var fieldNames = fields.ids();
			for (i = 0; i < fieldNames.length; i++) {
				var field = fieldNames[i];
				var type = fields.type(field);
				if (type === 'STATE') {
					stateFields.push(field);
					
				} else {
					descriptor.title = fields.title(field);
					var unit = fields.unit(field);
					if (unit) {
						descriptor.title += ' (' + unit + ')';
					}
					var columns = [];
					columns.push(field);
					
					
					var chart2 = ibexis.charting.lineChart(parent, results, metadata, columns, range, descriptor);
					var valueRange = minAndMaxValues(descriptor.min, descriptor.max, [field], results);
					var scale = d3.scale.linear().domain([ valueRange.min, valueRange.max ]);
					chart2.yScale(scale);
					for (var j = 0; j < trackers.length; j++) {
						chart2.addMouseTracker(trackers[j]);
					}
					// TODO indicate how to render the data sets - line, bar, dots etc
					chart2.render();
				}
			}
			if (stateFields.length > 0) {
				descriptor.title = stateFields.length == 1 ?fields.title(stateFields[0]) : "";
				descriptor.fields = stateFields;
				var chart3 = ibexis.charting.lineChart(parent, results, metadata, stateFields, range, descriptor, "STATE");
				var scale = d3.scale.linear().domain([ 0, 1 ]);
				chart3.yScale(scale);
				for (var j = 0; j < trackers.length; j++) {
					chart3.addMouseTracker(trackers[j]);
				}
				chart3.render();
				
				// this hack reverts the descriptor to its (almost) original state so that the next time it is used there are not a set of field in it 
				descriptor.fields = null;
			}
		}
	}
	
	function minAndMaxValues(minVal, maxVal, fields, results) {
		var minAuto, maxAuto;
		var min, max;
		var i;
		
		if (minVal === "Auto") {
			min = Number.MAX_VALUE;
			minAuto = true;
		} else {
			min = minVal ? parseFloat(minVal) : 0;
		}
		
		if (maxVal === "Auto") {
			max = 0;
			maxAuto = true;
		} else {
			max = maxVal ? parseFloat(maxVal) : 100; 
		}
		

		for (i = 0; i < fields.length; i++) {
			var field = fields[i];
			if (maxAuto) {
				var setMax = d3.max(results, function(v) { return v[field]; });
				if (!max || setMax > max) {
					max = setMax;
				}
			}
			if (minAuto) {
				var setMin = d3.min(results, function(v) { return v[field]; });
				if (!min || setMin > min) {
					min = setMin;
				}
			}
		}
		if (maxAuto) {
			if (max == 0) {
				max = 1;
			} else {
				max = max * 1.05;
			}
		}
		if (minAuto) {
			if (min == Number.MAX_VALUE) {
				min = 0;
			} else { 
				min = min * 0.95;
			}
		}

		return {min: min, max: max};
	}
	
	function addFormEntry(parent, data, metadata, descriptor) {
		var format = ibexis.date.dateTimePattern();
		var line;
		var includeFields = descriptor.fields;
		var results = data[descriptor.source];
		var field;
		var form = $('<div class="ibexis-summary"></div>');
		if (descriptor.label) {
			form.append('<div class="title">' + descriptor.label + '</div>');
		}
		for (field in results[0]) {
			line = $('<div class="field"></div>');
			line.append('<span class="label">' + field + '</div>');
			line.append('<span class="value">' + results[0][field] + '</div>');
			form.append(line);
		}
		parent.append(form);
	}
	
	
	function addTable(parent, data, metadata, descriptor) {
		var columns = [];
		var table, row, entry, i, j;
		var includeFields = descriptor.fields;
		var results = data[descriptor.source];
		
		if (!results) {
			parent.append('<div class="error">No data source "' + descriptor.source + '"</div>');
			return;
		}
		
		
		columns.push('date');
		var fieldData = metadata.fields(descriptor.source);
		var fields;
		if (!descriptor.fields || descriptor.fields.length == 0) {
			fields = fieldData.ids();
		} else {
			fields = descriptor.fields;
		}
		for (i = 0; i < fields.length; i++) {
			var field = fields[i];
			columns.push(field);
		}

		
		
		table = $('<table class="table table-striped table-sorter"></table>');
		if (descriptor.title) {
			table.append('<caption>' + descriptor.title + '</caption>');
		}
		
		var head = $('<thead>');
		table.append(head);
		row = $('<tr></tr>');
		if (results.length > 0) {
			for (j = 0; j < columns.length; j++) {
				if (columns[j] != 'date') {
					row.append('<th>' + fieldData.title(columns[j]) + '</th>');
				} else {
					row.append('<th>Date</th>');
				}
			}
		}
		head.append(row);
		
		var body = $('<tbody>');
		table.append(body);
		var last;
		if (descriptor.limit) {
			last = Math.min(descriptor.limit, results.length);
		} else {
			last = results.length;
		}
		if (descriptor.reversed) {
			results.reverse();
		}
		for (i = 0; i < last; i++) {
			row = $('<tr></tr>');
			entry = results[i];
			row.append('<td>' + l10n.formatLongDateTime(entry['date']) + '</td>');
			for (j = 0; j < columns.length; j++) {
				if (columns[j] != 'date') {
					row.append('<td>' + fieldData.valueWithUnit(columns[j], entry[columns[j]]) + '</td>');
//					row.append('<td>' + entry[columns[j]] + '</td>');
				}
			}
			body.append(row);
		}
		parent.append(table);
	}

	function addAlarmGrid(parent, data, metadata, descriptor) {
		addEventGrid(parent, data, metadata, descriptor);
	}
	
	function addEventGrid(parent, data, metadata, descriptor) {
		var results = data[descriptor.source];
		
		if (!results) {
			parent.append('<div class="error">No data source "' + descriptor.source + '"</div>');
			return;
		}
		
		var ids = [];
		var columns = [];
		var latest = [];
		var status = [];
		
		// Determine all the event IDs
		var fields = [];
		var index = 0;
		for (var i = 0; i < results.length; i++) {
			var row = results[i];
			var id = row['id'];
			if (fields[id] === undefined) {
				fields[id] = '';
				ids[index++] = id;
			}
		}

		// Determine the value columns
		for (p in results[0]) {
			if (p === 'date' || p === 'type' || p === 'id') {
				continue;
			}
			columns.push(p);
		}
		
		// Find the most recent states
		for (var i = 0; i < ids.length; i++) {
			for (var j = 0; j < results.length; j++) {
				var row = results[j];
				 if (row['id'] == ids[i]) {
					 for(var k = 0; k < columns.length; k++) {
						 var value = row[columns[k]];
						 if (value != '') {
							 latest.push(value);
						 }
					 }
				 
					 status.push(row['type'][2] === 'R' ? 'alarm' : 'ok');
					 break;
				 }
			}
		}
		
		var now = moment().utc();

		// Create grid
		var grid = $('<div class="alarms"></div>');
		for (var i = 0; i < ids.length; i++) {
			var cell = $('<div class="peripheral ' + status[i] + '"></div>');
			var header = $('<div class="header"><div class="indicator"></div>');
			var detail = $('<div class="details"></div>');
			detail.append('<p><span class="name">' + ids[i] + '</span></p>');
			detail.append('<p class="latest-value"><span class="value">' + latest[i] + '</span></p>');
	
			grid.append(cell);
			cell.append(header);
			header.append(detail);
			
			
			var history = $('<div class="history">');
			var j;
			var rows = 0;
			for (j = 0; j < results.length && rows < 10; j++) {
				 var row = results[j];
				 if (row['id'] == ids[i]) {
				 	var isRaised = row['type'][2] === 'R';
					 var action = isRaised ?  'Alarm raised' : 'Back to normal';
					 var type = isRaised ?  'alarm' : 'ok';
					 var value = row['id'] + ' ' + row[p];
					 var time = row['date'];
					 var date = l10n.formatLongDateTime(time);

					var minutes = now.diff(time, 'minutes');
					var hours = now.diff(time, 'hours');
					if (minutes < 1) {
						date = '< 1 minute';
					} else if (minutes < 120) {
						date = '+' + minutes + ' minutes';
					} else if (hours < 36) {
						date = '+' + hours + ' hours';
					} else {
						date = '+' + now.diff(time, 'days') + ' days';
					}
					date = date + ', at ' + l10n.formatTime(time);
					 // <span class="value">' + action + 					 '</span> (<span class="value">' + value + '</span>) at 
					 history.append('<p title="' + l10n.formatLongDateTime(time) + ', ' + action + 
					 	' (' + value + ')"><span class="status ' + type + '">*</span><span class="value">' + date + '</span></p>');
					 rows++;
				 }
			}

			cell.append(history);
		}		
		
		
		/*
		if (results.length > 0) {
			if (!descriptor.fields || descriptor.fields.length == 0) {
				row.append('<th>Date</th>');
				for (field in results[0]) {
					if (field != 'date') {
						columns.push(field);
						row.append('<th>' + field + '</th>');
					}
				}
			} else {
				row.append('<th>Date</th>');
				columns.push('date');
				for (i = 0; i < descriptor.fields.length; i++) {
					field = descriptor.fields[i];
					columns.push(field);
					row.append('<th>' + field + '</th>');
				}
			}
		}
		table.append(row);
		
		for (i = 0; i < results.length; i++) {
			row = $('<tr></tr>');
			entry = results[i];
			row.append('<td>' + l10n.formatLongDateTime(entry['date']) + '</td>');
			for (j = 0; j < columns.length; j++) {
				if (columns[j] != 'date') {
					row.append('<td>' + entry[columns[j]] + '</td>');
				}
			}
			table.append(row);
		}
		*/
		parent.append(grid);
	}
	
	function addAlarmDashboard(parent, data, metadata, descriptor) {
		
		var content = parent;
		var block1 = $('<div class="ibexis-ui-block">');
		content.append(block1);
		var alarmsBlock = $('<div>');
		alarmsBlock.addClass('alarms');
		var station = metadata.target.stationNo;
		alarmsBlock.attr('data-station', station);
		content.append(alarmsBlock);

		var addRow = function(row) {
			 var isRaised = row['type'][2] === 'R';
			 var action = ''; 
			 var type = '';
			 if (row['type'][1] === 'A') {
				 action = isRaised ?  'Alarm raised' : 'Back to normal';
				 type = isRaised ?  'alarm' : 'ok';
			 } else if (row['type'][1] === 'W') { 
				 action = isRaised ?  'Warning' : 'Back to normal';
				 type = isRaised ?  'warning' : 'ok';
			 } else if (row['type'][1] === 'I') { 
				 type = 'notice';									 
			 }
			 
			 var entry = $('<p>');
			 var time = row['date'];
			 var value = row['id'] + ' ' + row[p];
			 var title = l10n.formatLongDateTime(time) + ', ' + action + ' (' + value + ')'
			 entry.attr('title', title);
			 entry.append('<span class="status ' + type + '">*</span>');
			 entry.append('<span class="value"></span>');
			 return {
				 type : type,
				 action : action,
				 entry : entry
			 }
		}

		var limit;
		var alarms = metadata.alarms();
		var results = data[descriptor.source];
		var ids = alarms.ids();
		for (i = 0; i < ids.length; i++) {
			var id = ids[i];
			var alarmBlock = $('<div class="sensor">');
			alarmsBlock.append(alarmBlock);

			var headerBlock = $('<div class="header"><div class="indicator"></div><div class="details"><p><span class="name"></span></p><p class="latest-value">	<span class="value"></span></p></div></div>');
			alarmBlock.append(headerBlock);
		//	headerBlock.find('span.name').append(metadata.alarmTitle(id));
			headerBlock.find('span.name').append(alarms.title(id));
			
			var historyBlock = $('<div class="history">');
			alarmBlock.append(historyBlock);
			
			var j;
			var rows = 0;
			for (j = 0; j < results.length && (!limit || rows < limit); j++) {
				 var row = results[j];
				 if (row['id'].toLowerCase() == id) {
					 var entry = addRow(row);
					 historyBlock.append(entry.entry);
					 rows++;
					 var headerValue = headerBlock.find('p.latest-value span.value');
					 if (headerValue.html() === "") {
						 alarmBlock.addClass(entry.type);
						 headerValue.append(entry.action);
					 }
				 }
			}
		}
		
		var processor = function(notification) {
			if (station == notification.event.station) {
				var event = notification.event;
				var row = {
					date: moment.utc(event.date),
					type: event.type,
					id: event.exposition,
				};
				results.splice(0, 0, row);
				
				var sensor = alarmsBlock.children().first();
				for (i = 0; i < ids.length; i++) {
					var id = ids[i];
					 if (row['id'].toLowerCase() == id) {
						 var entry = addRow(row);
						 sensor.find('div.history').prepend(entry.entry);
						 sensor.attr('class', 'sensor ' + entry.type);
						 sensor.find('div.header p.latest-value span.value').replaceWith(entry.action);
						 updateAlarmDashboard(ids, results, alarmsBlock, limit);
						 break;
					 }
					sensor = sensor.next();
				}
			}
		}
		ibexis.alarms.addEventProcessor(processor);
		
		updateAlarmDashboard(ids, results, alarmsBlock, limit);
		
		var interval = $('div.ibexis-ui-refresh').data('refresh-interval');
		if (interval == 0) {
			interval = 30;
		}
		setInterval(updateAlarmDashboard, interval * 1000, ids, results, alarmsBlock, limit);
	}
		
	
	function updateAlarmDashboard(alarmsIds, results, alarmBlocks, limit) {
		var now = moment().utc();

		var sensor = alarmBlocks.children().first();
		for (i = 0; i < alarmsIds.length; i++) {
			var id = alarmsIds[i];
			var entry = sensor.find('div.history').children().first(); 
			
			var j;
			var rows = 0;
			for (j = 0; j < results.length && (!limit || rows < limit); j++) {
				 var row = results[j];
				 if (row['id'].toLowerCase() == id) {
					 var time = row['date'];
					 var date = l10n.formatLongDateTime(time);

					var minutes = now.diff(time, 'minutes');
					var hours = now.diff(time, 'hours');
					if (minutes < 1) {
						date = '< 1 minute';
					} else if (minutes < 120) {
						date = '+' + minutes + ' minutes';
					} else if (hours < 36) {
						date = '+' + hours + ' hours';
					} else {
						date = '+' + now.diff(time, 'days') + ' days';
					}
					date = date + ', at ' + l10n.formatTime(time);
					var span = entry.find('span.value');
					if (date != span.html()) {
						span.empty();
						span.append(date);
					}
					entry = entry.next();
					rows++;
				 }
			}
			
			sensor = sensor.next();
		}
	}

	function addAlarmHistory(parent, data, metadata, descriptor) {
		var columns = [];
		var p, j;
		var includeFields = descriptor.fields;
		var results = data[descriptor.source];
		var table = $('<table  class="table table-striped table-sorter alarms">');
		if (descriptor.title) {
			table.append('<caption>' + descriptor.title + '</caption>');
		}
		var alarms = metadata.alarms();
		var validAlarms = {};
		for (j = 0; j < alarms.ids().length; j++) {
			validAlarms[alarms.ids()[j]] = true;
		}
		var body = $('<tbody>');
		table.append(body);
		for (j = 0; j < results.length; j++) {
			var row2 = results[j];

			var id = row2['id'];
			if (!validAlarms[id]) {
				continue;
			}
			
			var date = row2['date'];
			var type = row2['type'];		
			var row = $('<tr>');
			body.append(row);
			
			row.append('<td>' + l10n.formatDateTime(date));
			
			var isRaised = type[2] === 'R';
			var action = type;
			if (type[1] === 'A') {
				action = isRaised ?  'Alarm raised' : 'Back to normal';
				type = isRaised ?  'alarm' : 'ok';
			} else if (type[1] === 'W') { 
				action = isRaised ?  'Warning' : 'Back to normal';
				type = isRaised ?  'warning' : 'ok';
			} else if (type[1] === 'I') { 
				action = 'Notice';
				type = 'notice';									 
			}
			
			 
			row.append('<td><span class="indicator ' + type + '">*</span>' + action + '</td>');
			
			var cell = $('<td>');
			row.append(cell);
			var id = results[j]['id'];
		//	cell.append(descriptor.metadata.alarmTitle(id));
			cell.append(alarms.title(id));

			var state = "";
			for (p in results[j]) {
				if (p == 'date' || p == 'type' || p == 'id') {
					continue;
				}
				var value = results[j][p];
				if (value != '' || Number.isInteger(value)) {
					var field = metadata.fields(descriptor.source);
					state = state +  field.title(p) + ": " +  field.valueWithUnit(p, value) + "\n";
				}
			}
			row.append('<td>' + state); 

		}
		parent.append(table);
	}

	
	var stationTable = function(div, stationOid) {
		var content = div.find('.ibexis-date-view');
		ibexis.date.addDatePeriodSelector(div, 1, function(e, view, startDate, period) {
			var data = {};
			var file = ibexis.data.stationData(stationOid, null, {start: startDate, period: period * 24});
			file.load('station', function(name, results) {
				data[name] = results;
				console.log("received " + results.length + " rows");
				content.empty();
				addTable(content, data, { source: 'station'});
			});
		});
	}
	
	var stationCharts = function(div, stationOid) {
		var content = div.find('.ibexis-date-view');
		ibexis.date.addDatePeriodSelector(div, 1, function(e, view, startDate, period) {
			var data = {};
			var file = ibexis.data.stationData(stationOid, null, {start: startDate, period: period * 24});
			file.load('station', function(name, results) {
				data[name] = results;
				console.log("received " + results.length + " rows");
				content.empty();
				var pre = $('<pre></pre>');
				pre.append(results.length);
				var i;
				for (i = 0; i < results.length; i++) {
					pre.append(results[i]);
				}
				content.append(pre);
			});
		});
	}
	
	/*
	 * Add a Descriptor to the lookup using it's 'id' field.
	 */
	var addDescriptor = function(descriptor) {
		descriptor.index = nextId++;
		descriptors[descriptor.id] = descriptor;
	}
	
	/*
	 * Find the descriptor by its id.
	 */
	var descriptor = function(id) {
		return descriptors[id];
	}
	
	var displayDescriptors = function(selector) {
		var container = $(selector);
		var table = $('<table class="table table-striped table-sorter"><tr><th></th><th>ID</th><th>Name</th><th>Description</th></tr></table>');
		var row, cell;
		
		var body = $('<tbody>');
		table.append(body);
		for (index in descriptors) {
			var descriptor = descriptors[index];
			row = $('<tr></tr>');
			cell = $('<td></td>');
			cell.append(descriptor.index);
			row.append(cell);
			cell = $('<td></td>');
			cell.append($('<a href="#" onclick="ibexis.display.editDescriptor(\'' + descriptor.id + '\')">' + descriptor.id + '</a>'));
			row.append(cell);
			cell = $('<td></td>');
			cell.append(descriptor.name);
			row.append(cell);
			cell = $('<td></td>');
			cell.append(descriptor.description);
			row.append(cell);
			body.append(row);
		}
		container.empty();
		container.append(table);
	}
	
	var editDescriptor = function(index) {
		// TODO replace the null with an ID for this segment 
		ibexis.dialog.openResource('_edit-descriptor.shtml', null, function(dialog) {
			var descriptor = descriptors[index];
			dialog.find('.name').append(descriptor.name);
			dialog.find('textarea.descriptor').append(JSON.stringify(descriptor, null, 4));
			dialog.find('button.save').click(function() {
				var text = dialog.find('textarea.descriptor').val();
				var newDescriptor = JSON.parse(text);
				descriptors[index] = newDescriptor;
				ibexis.dialog.close();
			});
		});
		/*
		var content = $('<form class="ibexis-descriptor"></form>');
		var field = $('<textarea name="descriptor"></textarea>');
		content.append(field);
		ibexis.dialog.openWithContent(content, function() {
			
		});
		*/
	}

	return {
		initModule : initModule,
//		create : create,
		displayDescriptors : displayDescriptors,
		editDescriptor : editDescriptor,
		addDescriptor : addDescriptor,
		descriptor : descriptor,
		stationTable : stationTable,
		stationCharts : stationCharts,
	}

}());
