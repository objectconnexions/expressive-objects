/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, ibexis */

// TODO no longer used?
ibexis.xxxxstationdata = (function () {
	var downloadedData; // Is this useful?
	var stationData;
	var format;
	
	initModule = function ( $container ) {
	};
	
	function loadStationData(config) {
		var from = config.from ? "&date=" + moment(config.from, datePattern) : "";
		var query = "stationProvider=" + config.stationProvider + "&stations=" + config.stations + "&types=samples"  + from + "&duration=" + config.duration + "&samples=" + config.resolution + "&adjust=true";
		$.get("station-data.app", query, function(data) {
			downloadedData = data;
			console.log(data);
			
			var map = {}
			for(var i = 0; i < data.length; i++) {
				map[data[i].number] = data[i]; 
			}
			
			var locale = d3.locale({
				"decimal": config.formatting.decimalSeparator,
				"thousands": config.formatting.groupSeparator,
				"grouping": [3],
				"currency": ["$", ""],
				"dateTime": "%a %b %e %X %Y",
				"date": "%m/%d/%Y",
				"time": "%H:%M:%S",
				"periods": ["AM", "PM"],
				"days": ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
				"shortDays": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
				"months": ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
				"shortMonths": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
			});
			var numberFormat = locale.numberFormat(".3r");
			
			format = {};
	//		formatting.number = new Intl.NumberFormat(config.language, {maximumSignificantDigits: 3, maximumFractionDigits: 1});
			format.number = {};
			format.number.format = function(d) {
				return numberFormat(d);
			}
			config.format = format;
			
			console.log(map);
			console.log(config);
	
			stationData = map;
			dataReady(map, config);
		});
	
	}
	
	// ------------------------------------------------------------------------------------------
	
	function load(config) {
		loadDateFormatData();
	
	//	config.from = +new Date() - 24 * 60 * 60 * 1000;
		config.duration = config.duration ? config.duration * 24 : 24;
		config.resolution = 200;
		
		
		d3.select("#chart")
			.append("div")
			.attr("id", "progress")
			.attr("class", "progress progress-striped active")
			.append('div')
			.attr("class", "bar")
			.attr("style", "width: 100%")
			.text("Download in progress...");
	
		
		loadStationData(config);
	}
	
	function loadDateFormatData() {
		var dateFormat = $('#date-format');
		datePattern = dateFormat.data('date-pattern');
		dateTimePattern = dateFormat.data('date-time-pattern');
		var language = dateFormat.data('lang');
		var i18n = dateFormat.data('i18n');

		if (i18n) {
			moment.lang(language, {
			    months : i18n.monthsShort,
			    monthsShort : i18n.monthsShort,
			    weekdays : i18n.weekdays,
			    weekdaysShort : i18n.weekdaysShort,
			    weekdaysMin : i18n.weekdaysShort
			});
		}
	}

	
	function dataReady(stations, config) {
		// Data loaded
		$('div#progress').remove();
	
		/*
		var json = JSON.stringify(stations, null, " ");
	//	console.log(json);
		$('#explore .data').append(json);
		*/
//		doSummary();
		
//		displayData();
	}
	
	function doSummary() {
		var station = stationData["1"];
		
		$('#explore .summary').prepend("<div class='list'></div>")
		
		$('#explore .summary').append("Name: " + station.name);
		$('#explore .summary').append("<br />Start: " + moment(station.start).format(dateTimePattern));
		$('#explore .summary').append("<br />End: " + moment(station.end).format(dateTimePattern));
		
		var ports = Object.keys(station.sensors);
		
		// there are not component defaults yet - so each parameter is specified here
		var component = {
				height: 200,
				width: 500,
				margin: 30,
				min: 15,
				max: 25, 
				data: "sample",
				
				xaxis: true,
				yaxis: true,
				port: ports[0],
		};
		
		var summary = [];
		for( var i = 0; i < ports.length; i++) {
			var port = ports[i];
			var item = {};
			item.port = port;
			
			var count = 0;
			var total = 0;
			var min = 100000;
			var max = -199999;
			var history = station.samples.history;
			for( var k = 0; k < history.length; k++) {
				var value = history[k][port];
				if (value) {
					total += value;
					count++;
					if (value > max) {
						max = value;
					}
					if (value < min) {
						min = value;
					}
				}
			}
			item.samples = count;
			item.min = min;
			item.max = max;
			item.mean = total / count;
	
			$('#explore .summary').append("<br />" + station.sensors[port].name + ": " + format.number.format(item.min) + "~" + format.number.format(item.max) + " " + format.number.format(item.mean));
		}
	
		
		var element = d3.select('#explore .summary');
		renderGraph(element, station, component);
	}

	function raw(target) {
		var station = stationData["1"];
		var json = JSON.stringify(station, null, " ");
		target.append(json);
	}
	
	function summary(target) {
		var station = stationData["1"];
		target.prepend("<div class='list'></div>")
		target.append("Name: " + station.name);
		target.append("<br />Start: " + moment(station.start).format(dateTimePattern));
		target.append("<br />End: " + moment(station.end).format(dateTimePattern));
		
		var ports = Object.keys(station.sensors);		
		var summary = [];
		for( var i = 0; i < ports.length; i++) {
			var port = ports[i];
			var item = {};
			item.port = port;
			
			var count = 0;
			var total = 0;
			var min = 100000;
			var max = -199999;
			var history = station.samples.history;
			for( var k = 0; k < history.length; k++) {
				var value = history[k][port];
				if (value) {
					total += value;
					count++;
					if (value > max) {
						max = value;
					}
					if (value < min) {
						min = value;
					}
				}
			}
			item.samples = count;
			item.min = min;
			item.max = max;
			item.mean = total / count;
	
			target.append("<br />" + station.sensors[port].name + ": " +  format.number.format(item.min) + "~" + format.number.format(item.max) + " " + format.number.format(item.mean));
		}
	}

	function table(target) {
		var station = stationData["1"];
		var content = [];
		var ports = Object.keys(station.sensors);		
		var history = station.samples.history;
		content.push('<tr>');
		for( var i = 0; i < ports.length; i++) {
			var port = ports[i];
			var sensor = station.sensors[port];
			content.push('<th>' + sensor.name);
			content.push(' (' + sensor.unit + ')');
			content.push('</th>');
		}
		content.push('</tr>');
		for( var k = 0; k < history.length; k++) {
			var sample = history[k];
			content.push('<tr>');
			content.push('<td>' + moment(sample['date']).format(dateTimePattern) + '</td>');
			for( var i = 0; i < ports.length; i++) {
				var port = ports[i];
				var value = sample[port];
				if (value) {
						var port = ports[i];
						content.push('<td>' + value + '</td>');
				}
			}
			content.push('</tr>');
		}

		target.append('<table>' + content.join('') + '</table>');
	}

	function charts(target) {
		target.append("<div>CHART DATA HERE</div>");
		
		var station = stationData["1"];
		var ports = Object.keys(station.sensors);		

		target.append('	<div id="explore" xxxstyle="display: none">	<div id="dashboard">		<h4>Exploration</h4>		<div class="summary"></div> 	<div class="data" style="white-space: pre;"></div>	</div>');
		var element = d3.select('#explore .summary');

		for( var i = 0; i < ports.length; i++) {
			// there are not component defaults yet - so each parameter is specified here
			var component = {
					height: 200,
					width: 500,
					margin: 30,
					min: 15,
					max: 125, 
					data: "sample",
					
					xaxis: true,
					yaxis: true,
					port: ports[i],
			};

			renderGraph(element, station, component);
		}
	}
	  
	return {
		initModule: initModule,
		load: load,
		raw: raw,
		table: table,
		summary: summary,
		charts: charts
	};

}());