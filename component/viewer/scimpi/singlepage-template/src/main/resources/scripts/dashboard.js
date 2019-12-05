var downloadedData;

function loadData(config) {
	var query = "stationProvider=" + config.stationProvider + "&date=" + config.dataFrom + "&duration=" + config.duration + "&samples=" + config.resolution;
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
		
		var format = {};
//		formatting.number = new Intl.NumberFormat(config.language, {maximumSignificantDigits: 3, maximumFractionDigits: 1});
		format.number = {};
		format.number.format = function(d) {
			return numberFormat(d);
		}
		config.format = format;
		
		console.log(map);
		console.log(config);

		render(map, config);
	});

}

// ------------------------------------------------------------------------------------------

function initDashboard(config) {
	config.dataFrom = +new Date() - 24 * 60 * 60 * 1000;
	config.duration = 24;
	config.resolution = 200;
	
	loadData(config);
}



function render(stations, config) {
	d3.selectAll('#dashboard div.station').each(function(d, i) {
		var id = $(this).attr("id");
		var element = d3.select(this).select('.content');
		var station = stations[id];
		
		if (!station.configuration) {
			station.configuration = {};
		}
		var layout = station.configuration.layout;
		if (!layout) {
			layout = {};
			if (station.configuration.components) {
				layout.sections = [{components: station.configuration.components}];
			}
		}

		
		var sections = [];
		var userSections = layout.sections;
		if (userSections) {
			for (var i = 0; i < userSections.length; i++) {
				var userSection = userSections[i];
				if(userSection.use) {
					sections = sections.concat(findSection(userSection.use));
				} else {
					sections = sections.concat(userSection);
				}
			}
		}
		if (!layout.defaults || layout.defaults != "none") {
			sections.unshift(findSection("sample-summary"));
			sections.unshift(findSection("status-overview"));
			sections.unshift(findSection("alarm-summary"));
		}

			
		
		for (var i = 0; i < sections.length; i++) {
			var section = sections[i];
			if (section) {
				renderSection(element, id, station, section, config.format);
			}
		}
	});
}

function findSection(name) {
	for (var i = 0; i < defaultSections.length; i++) {
		if (defaultSections[i].name == name) {
			return defaultSections[i];
		}
	}
	return undefined;
}

function renderSection(container, id, station, section, format) {
	var element = container.append('div').attr("class", "section");
	if (section.type == "summary") {
		renderSummary(element, station, section, format);
	} else {
		renderComponents(element, id, station, section.components, format);
	}
}

function renderComponents(container, id, station, components, format) {
	for (var i = 0; i < components.length; i++) {
		var component = components[i];
		if (!component.height) component.height = 28;
		if (!component.width) component.width = 75;
		if (!component.margin) component.margin = 0;
		if (!component.max) component.max = 100;
		if (!component.min) component.min = 0;
		
		var widget = container.append('div').attr('class', "widget");
		if (component.type == "graph") {
			renderGraph(widget, station, component, format);
		} else if (component.type == "sparkline") {
			renderSparkline(widget, station, component, format);
		} else if (component.type == "reading") {
			renderReading(widget, station, component, format);
		} else if (component.type == "alarm") {
			renderAlarm(widget, station, component, format);
		}
	}
}


function renderAlarm(element, station, component) {
	var alarms = station.alarms.current;
	for(var i = 0; i < alarms.length; i++) {
		var status = alarms[i].status;
		var port = alarms[i].port;
		var sensor = station.sensors[port];
		var name = sensor ? sensor.name : "unknown";
		element.append('div').attr("class", "alarm " + status).text(status + " " + name);
	}
}

function renderReading(element, station, component, format) {
	element.attr("class", "widget reading");
	var port = component.port;
	var type = component.data;
	var name;
	if (type == "sample") {
		var sensor = station.sensors[port];
		name = sensor.name;
	} else {
		name = statusName(port);
	}
	element.append('span').attr('class', "value-label").text(name);
	renderValue(element, station, type, port, format);
}

function renderValue(container, station, type, port, format) {
	var numberFormat = format.number;
	
	if (type == "status") {
		var status = station.status;
		var lastStatus = status.current;
		if (lastStatus) {
			var unit = "";
			if (port == "bt" || port == "ct") unit = "\xb0C";
			if (port == "bv" || port == "ev") unit = "V";
			if (port == "dv") unit = "kB";
			container.append('span').attr('class', "value-value").text(numberFormat.format(lastStatus[port]));
			container.append('span').attr('class', "value-unit").text(unit);
		} else {
			container.text("None");
		}
		
	} else if (type == "sample") {
		var lastSample = station.samples.current;
		if (lastSample) {
			var port = port;
			var sensor = station.sensors[port];
			if (sensor) {
				container.append('span').attr('class', "value-value").text(numberFormat.format(lastSample[port]));
				container.append('span').attr('class', "value-unit").text(sensor.unit);
			}
		} else {
			container.text("None");
		}
		
	} else if (type == "alarm") {
		var lastSample = station.samples.current;
		if (lastSample) {
			var port = port;
			var sensor = station.sensors[port];
			if (sensor) {
				container.append('span').attr('class', "value-value").text(numberFormat.format(lastSample[port]));
				container.append('span').attr('class', "value-unit").text(sensor.unit);
			}
		} else {
			container.text("None");
		}
	}
}

function renderSummary(element, station, section, format) {
	if (section.data == "samples") {
		var lastSample = station.samples.current;
		for(port in lastSample) {
			var widget = element.append('div').attr('class', "widget reading");
			var sensor = station.sensors[port];
			if (sensor) {
				widget.append('span').attr('class', "value-label").text(sensor.name);
			}
			renderValue(widget, station, "sample", port, format);
		}
		
	} else if (section.data == "status") {
		var status = station.status.current;
		var ports = ["bv", "ev", "ss", "sq", "bt", "ct", "dv"];
		for(p in ports) {
			var port = ports[p];
			var name = statusName(port);
			var widget = element.append('div').attr('class', "widget reading");
			widget.append('span').attr('class', "value-label").text(name);
			renderValue(widget, station, "status", port, format);
		}
		
	} else if (section.data == "alarms") {
//		element.attr("class", "section alarm");
		var alarms = station.alarms.current;
		for (var i = 0; i < alarms.length; i++) {
			var alarm = alarms[i];
			var widget = element.append('div').attr('class', "widget reading alarm " + alarm.status);
			var port = alarm.port;
			var sensor = station.sensors[port];
//			widget.append('span').attr('class', "value").text(alarm.status);
			if (sensor) {
				widget.append('span').attr('class', "value-label").text(sensor.name);
			}
			renderValue(widget, station, "sample", port, format);
		}
	}

	
	// TODO do same for status values
}

function renderGraph(element, station, component) {
	element.attr("class", "widget graph");
	renderGraphLine(element, station, component);
}
	
function renderGraphLine(element, station, component) {
	var from = new Date(station.start);
	var to = new Date(station.end);
	
	var chart = lineChart(element, component.width, component.height, component.margin, component.xaxis, component.yaxis, true)
		.xScale(d3.time.scale().domain([from, to]))
		.yScale(d3.scale.linear().domain([component.min, component.max]));
	var data = [];
	if (component.data == "status") {
		var history = station.status.history;
		for(var i = 0; history && i < history.length; i++) {
			var point = history[i];
			data.push({x: new Date(point['date']), y: point[component.port]});
		}
	} else if (component.data == "sample") {
		var history = station.samples.history;
		for(var i = 0; history && i < history.length; i++) {
			var point = history[i];
			var y = point[component.port];
			if (y) {
				data.push({x: new Date(point['date']), y: y});
			}
		}
	} else {
	//	element.append('p').text("Unknown");
	}
	chart.addSeries(data);
	chart.render();
	
}

function renderSparkline(box, station, component, format) {
	//box.classed("sparkline");
	box.attr("class", "widget sparkline");
	var port = component.port;
	var type = component.data;
	var name;
	if (type == "status") {
		name = statusName(port);
	} else {
		var sensor = station.sensors[port];
		name = sensor.name;
	}
	box.append('span').attr('class', "value-label").text(name);
	renderGraphLine(box, station, component);
	renderValue(box, station, type, port, format);
}


function statusName(port) {
	if (port == "bt") return"Board Temp"
	else if (port == "ct") return "CPU Temp"
	else if (port == "bv") return "Battery Voltage"
	else if (port == "ev") return "External Voltage";
	else if (port == "dv") return "Data Volume";
	else if (port == "ss") return "Signal Strength";
	else if (port == "sq") return "Signal Quality";
	else if (port == "date") return "Date";

	return "unknown"
}





/*	
config.defaults = {};
config.defaults.components = [
                              {
                            	  type: "sparkline",
                            	  data: "status",
                            	  port: "bv",
                            	  min: 2.5,
                            	  max: 5
                              },
                              {
                            	  type: "sparkline",
                            	  data: "status",
                            	  port: "ev",
                            	  min: 0,
                            	  max: 25
                              },
                              {
                            	  type: "sparkline",
                            	  data: "status",
                            	  port: "ss",
                            	  min: 0,
                            	  max: 30
                              },
                              {
                            	  type: "sparkline",
                            	  data: "status",
                            	  port: "sq",
                            	  min: 0,
                            	  max: 5
                              },
                              {
                            	  type: "sparkline",
                            	  data: "status",
                            	  port: "bt",
                            	  min: -10,
                            	  max: 30,
                              }	                              
                              ];
*/	                              
/*
config.stations = [
                   {
                	   number: "4",
                	   components: [
                	                {
                	                	type: "summary",
                	                	data: "sample",
                	                },
                	                ]
                   },
                   {
                	   number: "2",
                	   components: [
                	                {
                	                	type: "graph",
                	                	data: "sample",
                	                	port: "64",
                	                	height: 120,
                	                	width: 500,
                	                	xaxis: true,
                	                	yaxis: true,
                	                	min: 10,
                	                	max: 30,
                	                	margin: 5
                	                },
                	                {
                	                	type: "reading",
                	                	data: "sample",
                	                	port: "64"
                	                },
                	                {
                	                	type: "graph",
                	                	data: "status",
                	                	port: "bt",
                	                	height: 120,config.layout
                	                	width: 500,
                	                	xaxis: true,
                	                	yaxis: true,
                	                	min: 10,
                	                	max: 30,
                	                	margin: 5
                	                },
                	                {
                	                	type: "reading",
                	                	data: "status",
                	                	port: "bt"
                	                }
                	                ]
                   }
                   ];

*/	



var defaultSections = [
		           {
		        	   name: "status-overview",
		        	   components: 
		        		   [{
		        			   type: "sparkline",
		        			   data: "status",
		        			   port: "bv",
		        			   min: 2.5,
		        			   max: 5
		        		   },
		        		   {
		        			   type: "sparkline",
		        			   data: "status",
		        			   port: "ev",
		        			   min: 0,
		        			   max: 25
		        		   },
		        		   {
		        			   type: "sparkline",
		        			   data: "status",
		        			   port: "ss",
		        			   min: 0,
		        			   max: 31
		        		   },
		        		   {
		        			   type: "sparkline",
		        			   data: "status",
		        			   port: "sq",
		        			   min: 0,
		        			   max: 5
		        		   },
		        		   {
		        			   type: "sparkline",
		        			   data: "status",
		        			   port: "bt",
		        			   min: -10,
		        			   max: 30,
		        		   }	                              
		        		   ]
		           },
		           {
		        	   name: "sample-summary",
		        	   type: "summary",
		        	   data: "samples"
		           },
		           {
		        	   name: "status-summary",
		        	   type: "summary",
		        	   data: "status"
		           },
		           {
		        	   name: "alarm-summary",
		        	   type: "summary",
		        	   data: "alarms"
		           },
		           {
		        	   name: "battery",
		        	   components: [
		        	                {
                	                	type: "graph",
                	                	data: "status",
                	                	port: "bv",
                	                	height: 120,
                	                	width: 500,
                	                	xaxis: true,
                	                	yaxis: true,
                	                	min: 0,
                	                	max: 5,
                	                	margin: 5
		        	                },
		        	                ]
		           }
	           ];
