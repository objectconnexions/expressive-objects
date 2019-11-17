/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, ibexis */

ibexis.devices = (function () {		
		
	var l10n = ibexis.localization;

	var showData = function(segment, oid, type) {
		var from;
		var data = [];
		var index = 0;
		var view;
		var indexTable;
		var packetDisplay;
		
		var loadData = function(size, toId) {
			var file = ibexis.data.sourceData(oid, type, size, toId);
			file.load("", function(name, dataList) {
//				data = dataList;
				data = dataList.concat(data);
				list();
				last();
			});
		};
		
		var first = function() {
			index = 0;
			showPackets();
		};
		
		var last = function() {
			if (data.length > 0) {
				index = data.length - 1;
				showPackets();
			}
		};
		
		var next = function() {
			if (index + 1 < data.length) {
				index++;
			}
			showPackets();
		};
		
		var previous = function() {
			if (index > 0) {
				index--;
			} else {
				// TODO go get more... and add to data set
				var toId = data[index].id;
				loadData(3, toId)
			}
			showPackets();
		};
		
		var setIndex = function(e) {
			index = $(this).data('index');
			showPackets();
		};
		
		var check = function() {
			
		}
		
		var list = function() {
			var i;
			var row;
			var lastDate;
			
			view.empty();
			
			if (data.length == 0) {
				// no data to show
			} else {
				indexTable = $('<ul></ul>');
				for (i = 0; i < data.length; i++) {
					row = $('<li></li>');
					var link = $('<a href="#"></a>');
					link.attr('data-index', i);
					
					var label1, label2;
					var date = ibexis.date.parseUtcDate(data[i].date);
					if (!lastDate || date.date() != lastDate.date()) {
						label2 = l10n.formatDate(date); 
					} else {
						label2 = l10n.formatTime(date); 
					}

					label1 = data[i].id;
					var pos = label1.indexOf('_');
					if (pos > 0) {
						label1 = label1.substring(pos + 1);
					}

					link.append('<span class="date">' + label2 + '</span>' + label1);
					lastDate = date;
					row.append(link);
					indexTable.append(row);
				}
				var container = $('<div class="ibexis-packet-controller">');
				container.append(indexTable);
				view.append(container);
				indexTable.on('click', 'a', setIndex);
				
				var controls = $('<div class="ibexis-upload-control"></div>');
				controls.append($('<a class="first" href="#">First</a>'));
				controls.on('click', '.first', first);
				controls.append($('<a class="prev" href="#">Previous</a>'));
				controls.on('click', '.prev', previous);
				controls.append($('<a class="next" href="#">Next</a>'));
				controls.on('click', '.next', next);
				controls.append($('<a class="last" href="#">Last</a>'));
				controls.on('click', '.last', last);
				/*
				var selected = from.startOf('day');
				var today = ibexis.date.currentTime().startOf('day');
				if (selected.isSame(today)) {
					controls.append($('<a class="check" href="#">Check for more</a>'));
					controls.on('click', '.check', check);
				}*/
				container.append(controls);
				
				
				packetDisplay = $('<div class="ibexis-packet-display">');
				view.append(packetDisplay);
			}
		};
		
		var show = function() {
			var i;
			var row;
			//var timePattern = ibexis.date.timePattern();
			
			view.empty();
			
			if (data.length == 0) {
				// no data to show
			} else {
				indexTable = $('<ul></ul>');
				for (i = 0; i < data.length; i++) {
					row = $('<li></li>');
					var link = $('<a href="#"></a>');
					link.attr('data-index', i);
					var time = l10n.formatTime(moment(data[i].date).utc());
					link.append('#' + data[i].link + '<span class="date">' + time + '</span>');
					row.append(link);swansea.ibexis.net
					indexTable.append(row);
				}
				var container = $('<div class="ibexis-packet-controller">');
				container.append(indexTable);
				view.append(container);
				indexTable.on('click', 'a', setIndex);
				
				var controls = $('<div class="ibexis-upload-control"></div>');
				controls.append($('<a class="first" href="#">First</a>'));
				controls.on('click', '.first', first);
				controls.append($('<a class="prev" href="#">Previous</a>'));
				controls.on('click', '.prev', previous);
				controls.append($('<a class="next" href="#">Next</a>'));
				controls.on('click', '.next', next);
				controls.append($('<a class="last" href="#">Last</a>'));
				controls.on('click', '.last', last);
				var selected = from.startOf('day');
				var today = ibexis.date.currentTime().startOf('day');
				if (selected.isSame(today)) {
					controls.append($('<a class="check" href="#">Check for more</a>'));
					controls.on('click', '.check', check);
				}
				container.append(controls);
				
				
				packetDisplay = $('<div class="ibexis-packet-display">');
				view.append(packetDisplay);
			}
		};

		function showPackets () {
			var items = indexTable.find('li');
			if (items) {
				items.removeClass('selected');
				var item = items.eq(index)
				var offset = parseInt(item.offset().left) - parseInt(indexTable.offset().left) + parseInt(indexTable.scrollLeft());
				console.log('scroll to ' + offset);
				indexTable.stop().animate({
			        scrollLeft: offset - indexTable.width() / 2 + item.width() / 2
			    }, 1000);
				item.addClass('selected');
			}
			packetDisplay.empty();
			for (i = index; i <= index + 1 && i < data.length; i++) {
				if (i >= 0) {
					content = $('<pre></pre>');
					var date = ibexis.date.parseUtcDate(data[i].date);
					content.append(l10n.formatDateTime(date) + ' ');
					content.append(data[i].header + '\n\n');
					content.append(data[i].content);
					packetDisplay.append(content);
				}
			}
		}
		
		var init = function() {
			var content = $('<div class="ibexis-ui-content the-real-contents ibexis-device-data ibexis-device-upload">'); 
			segment.append(content);
			loadData(3);
			view = content;
		};
		
		return {
			init : init, 
		};
	}
		

	var showDiagnosticSummary= function (div, deviceOid) {
		
		ibexis.configuration.load(deviceOid, function(configuration) {
			var content = $('<div class="ibexis-ui-content the-real-contents">'); 
			div.append(content);
			var dateRange = ibexis.date.createDateRange(24);
			var file;
			file = ibexis.data.logData(deviceOid, 'transmission', dateRange);
			console.log("requesting log entries " + file);
			file.load('name???', function(name, results) {
				var count = 0;
				var uploads = 0;
				var sys = 0;
				var dbg = 0;
				var rsp = 0;
				content.append('<p>Latest uploads:-<br />');
				for (var i = 0; i < results.length; i++) {
					count++;
					var type = results[i]['type'];
					if (type === 'UPL') {
						uploads++;
					} else if (type === 'SYS') {
						sys++;
					} else if (type === 'DBG') {
						dbg++;
					} else if (type === 'RSP') {
						rsp++;
					}

					if (i < 5) {
						content.append(l10n.formatDateTime(results[i].date) + " " + type + " " + results[i].sections + '<br />');
					}
				}
				content.append('<p>Uploads: ' + l10n.formatFloat(uploads * 100.0 / i) + '%<br />');
				content.append('Startups: ' + sys + '<br />');
				content.append('Debug: ' + dbg + '<br />');
				content.append('Responses: ' + rsp);
			});
		});
	}
	
	var showHealth = function (div, deviceOid) {
		var file;

		ibexis.configuration.load(deviceOid, function(configuration) {
			var dateRange = ibexis.date.createDateRange(24);
			file = ibexis.data.logData(deviceOid, 'transmission', dateRange);
			console.log("requesting log entries " + file);
			var content = $('<div class="ibexis-ui-content the-real-contents">'); 
			div.append(content);
			file.load('name???', function(name, results) {
				var isUploading = showTransmissionHealth(content, configuration, results);
				
				showStatusHealth(content, isUploading, configuration, deviceOid, dateRange);
			});
		});
    }
	
	function showTransmissionHealth(content, configuration, results) {
		var isUploading = false;
		console.log("received " + results.length + " rows of transmission data");
		
		var uploads = 0;
		for (var i = 0; i < results.length; i++) {
			if (results[i]['type'] === 'UPL') {
				uploads++;
			}
		}
		
		var section = $('<div class="ibexis-ui-health">');
		content.append(section);
		
		section.append('<h2>Transmissions</h2>');
		var interval = configuration.value('system', null, 'device-upload-interval');
		
		var indicator = $('<div class="health-indicator">');
		section.append(indicator);
		if (uploads < 22 * 60 / interval) {
			indicator.addClass('health-alert');
		} else if (uploads < 24 * 60 / interval) {
			indicator.addClass('health-warning');
		} else {
			indicator.addClass('health-good');
		}

		var lastIndex = results.length - 1;
		var last = results[lastIndex];
		indicator = $('<div class="health-indicator">');
		section.append(indicator);
		if (last) {
			var nextDue = interval - (moment().utc().diff(last.date) /1000) / 60.0;
			if (nextDue < -interval * 2) {
				indicator.addClass('health-alert');
			} else if (nextDue < -interval) {
				indicator.addClass('health-warning');
				isUploading = true;
			} else {
				indicator.addClass('health-good');
				isUploading = true;
			}

			var value = $('<div class="health-value">');
			value.append(l10n.formatTime(last.date));
			section.append(value);

			var labels = $('<div class="health-description">');
			section.append(labels);
			var label;
			if (nextDue < -2) {
				label = 'Next upload is overdue by ' + l10n.formatInteger(Math.floor(-nextDue)) + ' minutes';
			} else if (nextDue < 2) {
				label = 'Next upload due imminently';
				var section = $('<div class="ibexis-ui-health">');
			} else {
				label = 'Next upload expected in, ' + l10n.formatInteger(Math.ceil(nextDue)) + ' minutes';
			}
			labels.append('<div class="label">' + label + '</div>');
			labels.append('<div class="label"> ' + uploads + ' uploads in last 24 hours, expected ' + (24 * 60 / interval)  + '</div>');
			labels.append('<div class="label"> Total transmissions was ' + results.length + '</div>');
		} else {
			indicator.addClass('health-alert');
			var value = $('<div class="health-value">');
			section.append(value);
			var labels = $('<div class="health-description">');
			section.append(labels);
			labels.append('<div class="label">No recent transmissions</div>');
		}
		return isUploading;
	}
	
	function showStatusHealth(content, isUploading, configuration, deviceOid, dateRange) {
		var file = ibexis.data.logData(deviceOid, 'status', dateRange);
		file.load('name???', function(name, results) {
			showCellularHealth(content, !isUploading, results);
			showExternalPowerHealth(content, !isUploading, results);
			showBatteryHealth(content, !isUploading, results);
			showSystemPowerHealth(content, !isUploading, results);

			var uploadFile = ibexis.data.sourceData(deviceOid, "upload", 1);
			uploadFile.load(null, function(name, results) {
				showClockHealth(content, !isUploading, results);
			});
		});
	}	

	function showCellularHealth(content, isOutOfDate, results) {
		var tooLow = true, good = true;
		var min = 100, max = 0, sum = 0;
		for (var i = 0; i < results.length; i++) {
			var line = results[i]['ss'];
			if (line == 0) {
				continue;
			}
			if (line > 15) {
				tooLow = false;
			}
			if (line < 16) {
				good = false;
			}
			min = Math.min(min, line);
			max = Math.max(max, line);
			sum += line;
		}
		
		if (min < 100 && max > 0) {
			var section = $('<div class="ibexis-ui-health">');
			if (isOutOfDate) {
				section.addClass("out-of-date")
			}
			content.append(section);

			section.append('<h2>Cellular</h2>');
			var indicator = $('<div class="health-indicator">');
			section.append(indicator);
			
			if (tooLow) {
				indicator.addClass('health-alert');
			} else if (!good) {
				indicator.addClass('health-warning');
			} else {
				indicator.addClass('health-good');
			}
	
			// repeated for smaller, most recent sample set
			var tooLow = true;
			var good = true;
			var last = results.length;
			if (last > 5) {
				last = 5;
			}
			for (var i = 0; i < last; i++) {
				var line = results[i]['ss'];
				if (line > 15) {
					tooLow = false;
				}
				if (line < 16) {
					good = false;
				}
			}
	
			indicator = $('<div class="health-indicator">');
			section.append(indicator);
			if (tooLow) {
				indicator.addClass('health-alert');
			} else if (!good) {
				indicator.addClass('health-warning');
			} else {
				indicator.addClass('health-good');
			}
	
			
			
			var lastIndex = results.length - 1;
			var last = results[lastIndex];
			var strength = last['ss'];
			
			var value = $('<div class="health-value">');
			value.append(strength);
			section.append(value);
			
			var labels = $('<div class="health-description">');
			section.append(labels);
			labels.append('<div class="label">Current signal strength is ' + strength + '</div>');
			if (min < max) {
				labels.append('<div class="label">Range between ' + min + ' and ' + max + '</div>');
			}
			labels.append('<div class="label">Average ' + l10n.formatFloat(sum / lastIndex) + '</div>');
		}
	}

	function showPowerHealth(content, isOutOfDate, results, property, title, low, good) {
		var section = $('<div class="ibexis-ui-health">');
		if (isOutOfDate) {
			section.addClass("out-of-date")
		}
		content.append(section);
		
		section.append('<h2>' + title);
		var indicator = $('<div class="health-indicator">');
		section.append(indicator);
		
		var exists = false, tooLow = true, good = true;
		var min = 100, max = 0, sum = 0;
		for (var i = 0; i < results.length; i++) {
			var line = results[i][property];
			if (line === '') {
				continue;
			}
			exists = true;
			if (line > low) {
				tooLow = false;
			}
			if (line < good) {
				good = false;
			}
			min = Math.min(min, line);
			max = Math.max(max, line);
			sum += line;
		}
		

		if (!exists) {
			indicator.addClass('health-unknown');
		} else if (tooLow) {
			indicator.addClass('health-alert');
		} else if (!good) {
			indicator.addClass('health-warning');
		} else {
			indicator.addClass('health-good');
		}

		
		var lastIndex = results.length - 1;
		var last = results[lastIndex];
		
		// repeated for smaller, most recent sample set
		exists = false;
		tooLow = true;
		var to = results.length;
		if (to > 5) {
			to = 5;
		}
		for (var i = 0; i < to; i++) {
			var line = results[i][property];
			if (line === '') {
				continue;
			}			
			exists = true;
			if (line > low) {
				tooLow = false;
			}
			if (line < good) {
				good = false;
			}
		}

		indicator = $('<div class="health-indicator">');
		section.append(indicator);
		if (!exists) {
			indicator.addClass('health-unknown');
		} else if (tooLow && last[property] < low) {
			indicator.addClass('health-alert');
		} else if (tooLow) {
			indicator.addClass('health-warning');
		} else {
			indicator.addClass('health-good');
		}

		var value = $('<div class="health-value">');
		if (exists) {
			var voltage = last[property];
			value.append(l10n.formatFloat(voltage) + 'v');
		}
		section.append(value);

		
		var labels = $('<div class="health-description">');
		section.append(labels);
		if (exists) {
			var status = 'OK';
			if (voltage < good) {
				status = 'low';
			}
			labels.append('<div class="label">Current voltage is ' + status + '</div>');
			var minv = l10n.formatFloat(min);
			var maxv = l10n.formatFloat(max);
			if (minv != maxv) {
				labels.append('<div class="label">Range between ' + minv + 'V and ' + maxv + 'V</div>');
				labels.append('<div class="label">Average ' + l10n.formatFloat(sum / results.length) + 'V</div>');
			} else {
				labels.append('<div class="label">Constant at ' + minv + 'V</div>');				
			}
		} else {
			labels.append('<div class="label">Current voltage is unknown</div>');
		}
	}
	

	function showExternalPowerHealth(content, isOutOfDate, results) {
		// TODO this check should be relative to the required power - 6V is for the standard board
		showPowerHealth(content, isOutOfDate, results, 'ev', 'External voltage', 6, 6);
	}

	function showBatteryHealth(content, isOutOfDate, results) {
		showPowerHealth(content, isOutOfDate, results, 'bv', 'Battery voltage', 3.9, 4.1);
	}
	
	function showSystemPowerHealth(content, isOutOfDate, results) {
		showPowerHealth(content, isOutOfDate, results, 'sv', 'System voltage', 4.0, 4.3);
	}

	
	function showClockHealth(content, isOutOfDate, results) {
		var section = $('<div class="ibexis-ui-health">');
		if (isOutOfDate) {
			section.addClass("out-of-date")
		}
		content.append(section);
		
		section.append('<h2>Clock</h2>');
		var indicator = $('<div class="health-indicator">');
		section.append(indicator);
		
		indicator = $('<div class="health-indicator">');
		section.append(indicator);
		
		var lastIndex = results.length - 1;
		var last = results[lastIndex];
		
		if (last) {
			var transmission = moment(last.date).utc();
			var header = last.content.substr(last.content.indexOf('\n'), last.content.indexOf(' ')); 
			var data = moment(header).utc();
			
			var diff = data.diff(transmission) / 1000;
			
			if (Math.abs(diff) > 120) {
				indicator.addClass('health-alert');
			} else if (Math.abs(diff) > 10) {
				indicator.addClass('health-warning');
			} else {
				indicator.addClass('health-good');
			}
			
			var value = '';
			if (diff > 0) {
				value = '+';
			}
			if (diff == 0) {
				value = '<1s';
			} else if (Math.abs(diff) < 120.0) {
				value += l10n.formatFloat(diff) + 's';
			} else {
				value += l10n.formatInteger(diff / 60) + 'm';
			}
			section.append($('<div class="health-value">' + value + '</div>'));
			
			var status = 'correct';
			if (diff > 1) {
				status = "running fast";
			} else if (diff < 1) {
					status = "running slow";
			}
			var labels = $('<div class="health-description">');
			section.append(labels);
			labels.append('<div class="label">The clock is ' + status + '</div>');
		}
	}


	var initModule = function() {
		ibexis.shell.registerSegmentProcessor('health', function (segment, deviceOid, showContext) {
			showHealth(segment, deviceOid);
			showContext(segment);
		});
		ibexis.shell.registerSegmentProcessor('data', function (segment, deviceOid, showContext) {
			type = segment.data('type');
			showData(segment, deviceOid, type).init();
			showContext(segment);
		});
		ibexis.shell.registerSegmentProcessor('diagnostics', function (segment, deviceOid, showContext) {
			type = segment.data('type');
			showDiagnosticSummary(segment, deviceOid);
			showContext(segment);
		});
	};
	
	return { 
		initModule: initModule,
	};

}());
