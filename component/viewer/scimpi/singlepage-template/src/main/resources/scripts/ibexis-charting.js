/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.charting = (function() {

	var l10n = ibexis.localization;
	var customTimeFormat;
	var nextId = 0;
	var nextColor = 0;
	var pallete = d3.scale.category20().domain(d3.range(0,20)); 


	var initModule = function($container) {
		prepareFormats();
	};

	function prepareFormats() {
		customTimeFormat = timeFormat(
		[
		 	[
		 	 	function(d) { return moment(d).format("YYYY");}, 
		 	 	function(d) { return true; }
		 	], [
		 	    function(d) { return moment(d).format("YYYY"); }, 
		 	    function(d) { return d instanceof Date; }
		 	], [
		 	    function(d) { return moment(d).format("MMM"); },
		 	    function(d) { return d instanceof Date && d.getMonth();  }
	 	    ], [
	 	        // TODO localize this and the following times/dates
	 	        function(d) { return moment(d).format("D MMM"); },
	 	        function(d) { return d instanceof Date && d.getDate() != 1; }
 	        ], [
 	            function(d) { return moment(d).format("ddd D"); }, 
 	            function(d) { return d instanceof Date && d.getDay() && d.getDate() != 1; }
            ], [
            	function(d) { return l10n.formatTime(moment(d)); },
                //function(d) { return moment(d).format(l10n.dateTimePattern()); }, //.indexOf('a') > 0 ? "ha" : "H:mm");},
                function(d) { return d instanceof Date && d.getHours(); }
            ],
		]);
	};
	
	var timeFormat = function(formats) {
		return function(date) {
			var i = formats.length - 1, f = formats[i];
			while (!f[1](date))
				f = formats[--i];
			return f[0](date);
		};
	};

	var resetColors = function(index) {
		if (index) {
			nextColor = index;
		} else {
			nextColor = 0;
		}
	}
	
	var lineChart = function(parent, data, metadata, columns, dateRange, settings, type, tracker) {	
		settings.showXAxis = settings.showXAxis ? settings.showXAxis : true; 
		settings.showYAxis = settings.showYAxis ? settings.showYAxis : true; 
		settings.margin = settings.margin ? settings.margin : 30;
		settings.width = settings.width ? settings.width : 500;
		settings.height = settings.height ?  settings.height : 300;

		settings.showGrid = settings.showGrid ? settings.showGrid : true; 
		settings.showLine = settings.showLine == undefined ? true : settings.showLine;
		settings.showPoints = settings.showPoints == undefined ? false : settings.showPoints;
		settings.showArea = settings.showArea ? settings.showArea : false;

		settings.showTimeCursor = settings.showTimeCursor == undefined ? true : settings.showTimeCursor;
		settings.showTimeLabel = settings.showTimeLabel == undefined ? true : settings.showTimeLabel;

		return chart(parent, data, metadata, columns, dateRange, settings, type, tracker);
	}	
	
	function chart(widget, data, metadata, columns, dateRange, settings, type) {	
		var fields = metadata.fields(settings.source);
		var _showXAxis = settings.showXAxis; 
		var _showYAxis = settings.showYAxis; 
		var margin = settings.margin ? settings.margin : 20;
		var left = (_showYAxis ? 50 : 0) + margin;
		var bottom = (_showXAxis ? 16 : 0) + margin;

		var _showKey = false;
		if (settings.key) {
			_showKey = setting.key;
		} else if (settings.fields && settings.fields.length > 1) {
			_showKey = true;
		}
		var top = (_showKey ? 18 : 0) + margin;

		var fieldCount = 1;
		if (settings.fields) {
			fieldCount = settings.fields.length;
		}

		var _width = settings.width;
		var _height;
		if (type == 'STATE') {
			_height = 10 + 20 * fieldCount;
		} else {
			_height = settings.height;
		}
		_height = top + _height + bottom;
		_width = left + _width + margin;
		var _margins = {
				top : top,
				left : left,
				right : margin,
				bottom : bottom
			}, 
			
			_showGrid, 
			_showLine,
			_showPoints,
			_showArea,
			_showState = false,
			
			_xScale, 
			_yScale, 
			// TODO allow colour to be passed in in settings
			_colors = [],
			_svg, 
			_body, 
			_overlay,
			_line;

		var _trackers = [];
		
		var id = nextId++;

		var _chart = {};
		
		if (type == 'STATE') {
			_showYAxis = false;
			_showGrid = false; 
			_showLine = false;
			_showPoints = false;
			_showArea = false;
			_showState = true;
		} else {
			_showGrid = settings.showGrid;
			_showLine = settings.showLine;
			_showPoints = settings.showPoints;
			settings.showArea = settings.showArea;
		}

		var c = nextColor;
		var index;
		if (settings.colours) {
			for (var i = 0; i < settings.fields.length; i++) {
				if(i >= settings.colours.length) {
					index = c++ % 20;
					_colors[i] = pallete(index);
				} else if (Number.isInteger(settings.colours[i])) {
					c = Math.max(c, settings.colours[i]);
					_colors[i] = pallete(Number(settings.colours[i]));
				} else {
					_colors[i] = settings.colours[i];
				}
			}
		} else {
			for (var i = 0; i < fieldCount; i++) {
				index = c++ % 20;
				_colors[i] = pallete(index);
			}
		}
		nextColor = c % 20;
		
		var from = dateRange.start;
		var to = ibexis.date.endDate(dateRange);
		console.log("scale from " + from + " to " + to);
		_xScale = d3.time.scale().domain([from, to]);
		
		
		_chart.render = function() {
			if (!_svg) {
				var parent = d3.select(widget.get(0));
				if (settings.title) {
					var _title = parent.append("h4");
					_title.text(settings.title);
				}
				_svg = parent.append("svg")
					.attr("class", "chart" + id)
					.attr("height", _height)
					.attr("width", _width);
				renderKey(_svg);
				renderAxes(_svg);
				defineBodyClip(_svg);				
			}
			renderBody(_svg);
			renderOverlay(_svg);
			trackMouse(data, _svg, _width, _height, _margins.left, _margins.bottom, _xScale, _trackers, _chart);
		};

		function renderKey(svg) {
			if (_showKey) {
				var key = svg.append("g", ":first-child").attr("class", "key");
				//var sets = data.data();
				var x = _margins.left;
				var y = -1;
				var j;
				for ( j = 0; j < settings.fields.length; j++) {
					var fieldId = settings.fields[j];
					var fieldLabel = fields.title(fieldId);
					key.append("svg:rect")
						.attr("x", x)
						.attr("y", y + 1)
						.attr("width", 16)
						.attr("height", 12)
						.attr("rx", 4)
						.attr("ry", 4)
						.attr("fill", _colors[j]);
					
					key.append("svg:text")
						.attr("x", x + 24)
						.attr("y", y + 12)
						.text(fieldLabel);

					x += 160;
				}
			}
		}
		
		function renderAxes(svg) {
			var axes = svg.append("g").attr("class", "axes");
			if (_xScale) {
				renderXAxis(axes);
			}
			if (_yScale) {
				renderYAxis(axes);
			}
		}

		function renderXAxis(axes) {
			_xScale.range([ 0, chartWidth() ]);
			var xAxis = d3.svg.axis()
				.scale(_xScale)
				.tickSubdivide(2)
		
				.tickSize(6, 4, 8)
				.ticks(chartWidth() / 100)
				.tickFormat(customTimeFormat)
				.orient("bottom");
			if (_showXAxis) {
				axes.append("g")
					.attr("class", "x axis")
					.attr("transform", function() { return "translate(" + xStart() + "," + yStart() + ")"; })
					.call(xAxis);
			}
			if (_showGrid) {
				d3.selectAll(".chart" + id + " g.x g.tick")
					.append("line")
					.classed("grid-line", true)
					.attr("x1", 0)
					.attr("y1", 0)
					.attr("x2", 0)
					.attr("y2", -chartHeight());
				d3.selectAll(".chart" + id + " g.x")
					.append("line")
					.classed("grid-edge", true)
					.attr("x1", chartWidth())
					.attr("y1", 0)
					.attr("x2", chartWidth())
					.attr("y2", -chartHeight());
			}
		}

		function renderYAxis(axes) {
			_yScale.range([ chartHeight(), 0 ]);
			if (_showYAxis) {
				var yAxis = d3.svg.axis()
					.scale(_yScale)
					.ticks(5)
					.orient("left");
				axes.append("g")
					.attr("class", "y axis")
					.attr("transform", function() {
						return "translate(" + xStart() + "," + yEnd() + ")";
					})
					.call(yAxis);
			}
			if (_showGrid) {
				d3.selectAll(".chart" + id + " g.y g.tick")
					.append("line")
					.classed("grid-line", true)
					.attr("x1", 0)
					.attr("y1", 0)
					.attr("x2", chartWidth())
					.attr("y2", 0);
				d3.selectAll(".chart" + id + " g.y")
					.append("line")
					.classed("grid-edge", true)
					.attr("x1", 0)
					.attr("y1", 0)
					.attr("x2", chartWidth())
					.attr("y2", 0);
			}
		}

		function defineBodyClip(svg) {
			var padding = 5;

			svg.append("defs")
				.append("clipPath")
				.attr("id", "body-clip")
				.append("rect")
				.attr("x", 0 - padding)
				.attr("y", 0)
				.attr("width", chartWidth() + 2 * padding)
				.attr("height", chartHeight());
		}

		function renderBody(svg) {
			if (!_body) {
				_body = svg.append("g", "g.axes")
					.attr("class", "body")
					.attr("transform", "translate(" + xStart() + "," + yEnd() + ")")
					.attr("clip-path", "url(#body-clip)");
			}
			if (_showLine) {
				drawGraphLine(data);
			}
			if (_showPoints) {
				drawDots(data);
			}
			if (_showState) {
				drawState(data);
			}
		}

		function renderOverlay(svg) {
			if (!_overlay) {
				_overlay = svg.append("g")
					.attr("class", "overlay");
			}
			if (settings.showTimeLabel == true) {
				_trackers.push(timeLabel());
			}
			if (settings.showValueLabels == true) {
				_trackers.push(valueLabels());
			}
			if (settings.showTimeCursor == true) {
				_trackers.push(timeCursor());
			}
			if (settings.showValueCursors == true) {
				_trackers.push(valueCursors());
			}
		}

		function drawGraphLine(data) {
			  var lines = columns.map(function(id) {
			    return {
			      id: id,
			      values: data.map(function(d) {
			    	  if (d[id] == "" || isNaN(d[id])) {
			    		  return null;
			    	  } else {
			    		  return {date: d.date, y: d[id]};
			    	  }
			      }).filter(function(d) { return d != null })
			    };
			  });
			
			var line = d3.svg.line()
				.x(function(d) { 
					return _xScale(d.date); 
				})
				.y(function(d) { 
					return _yScale(d.y);
				});

			_body.selectAll("path.line")
				.data(lines)
				.enter()	
				.append("path")
				.style("stroke", function(d, i) { return _colors[i]; })
				.attr("class", "line");

			_body.selectAll("path.line")
				.data(lines)
				/*
				 , function(d, i) {
					console.log(i + " -> ");
					console.log(d);
					//console.log(data[i]);
					return d;
				})
				*/
				.transition()
				.attr("d", function(d) { return line(d.values); });
		}	

		// TODO to incorporate
		function drawStepLine(graph, x, y, scaleTime, scaleY, timeData, yData, color) {
			var line = d3.svg.line().x(function(d) {
				return scaleTime(d.time);
			}).y(function(d) {
				return scaleY(d.y);
			});
			
			var points = [];
			var last = null;
			for ( var i = 0; i < yData.length; i++) {
				var value = yData[i];
				if (isNaN(value)) {
					continue;
				}
				if (last == null) {
					points.push({
						time : new Date(timeData[i]),
						y : value
					});
					last = value;
				} else if (value != last || i == yData.length - 1) {
					points.push({
						time : new Date(timeData[i]),
						y : last
					});
					points.push({
						time : new Date(timeData[i]),
						y : value
					});
					last = value;
				}
			}
			points.push({
				time : scaleTime.invert(scaleTime(new Date(timeData[i - 1])) + 10),
				y : last
			});
			
			var path = graph.append("svg:path");
			path.attr("transform", "translate(" + x + "," + y + ")");
			path.attr("fill", "transparent");
			path.attr("stroke", color);
			path.attr("d", line(points));
			path.attr("class", "line");
		}

		function drawDots(data) {
			var dots = columns.map(function(id) {
				return {
					id: id,
					values: data.map(function(d) {
						return {date: d.date, y: d[id]};
					})
				};
			});

			dots.forEach(function(list, i) {
				_body.selectAll("circle._" + i)
					.data(list.values)
					.enter()
					.append("circle")
					.attr("class", "dot _" + i);

				_body.selectAll("circle._" + i)
					.data(list.values)
					.style("stroke", function(d) { return _colors[i]; })
					.transition()
					.attr("cx", function(d) { return _xScale(d.date); })
					.attr("cy", function(d) { return _yScale(d.y); })
					.attr("r", 4.5);
			});
		}
		
		function drawState(data) {
			var set = columns.map(function(id) {
				return {
					id: id,
					values: data.map(function(d) {
						return {date: d.date, y: d[id]};
					})
				};
			});

			var x = 1;
			var y = 1;
			var j,i;
			for ( j = 0; j < set.length; j++) {
				var data = set[j].values;
				_body.append("svg:rect")
				.attr("x", x - 1)
				.attr("y", y - 1)
				.attr("width", _xScale.range()[1])
				.attr("height", 16)
				.attr("stroke", "#666")
				.attr("stroke-width", 0.25)
				.attr("fill", "#f8f8f8");

				var state = 0;
				var start = _xScale.range()[0];
				var currentState = null;
				for ( i = data.length - 1 ; i >= 0; i--) {
					state = data[i].y;
					if (isNaN(state)) {
						continue;
					}
					if (currentState == undefined) {
						currentState = state;
					}
					if (currentState != state) {
						var end = _xScale(data[i].date);
						var width = Math.abs(start - end);
						if (width <= 1) {
							width = 1;
						}
						if (state == 0) {
							_body.append("svg:rect")
							.attr("x", x + start)
							.attr("y", y)
							.attr("width", width)
							.attr("height", 14)
							.attr("fill", _colors[j]);
						} else {
							start = end;
						}
						currentState = state;
					}
				}
			  
				if (state == 1) {
				  _body.append("svg:rect")
				  	.attr("x", x + start)
				  	.attr("y", y)
				  	.attr("width",  _xScale.range()[1] - start)
				  	.attr("height", 14)
				  	.attr("fill", _colors[j]);
			  }
			  
			  y += 20;
			}
		}

		// TODO to incorporate
		function drawBars(graph, x, y, scaleTime, scaleY, timeData, yData, color) {
			var bars = graph.append("svg:g");
			var maxHeight = scaleY.range()[0];
			var baseline = y + maxHeight;
			var graphWidth = scaleTime.range()[1];
			var lastWidth = 10;
			for ( var i = 0; i < timeData.length; i++) {
				  var value = yData[i];
				  if (isNaN(value)) {
					  continue;
				  }
			
				  var top = y + scaleY(value);
				  var left = x + scaleTime(timeData[i]);
				  var width = i + 1 >= timeData.length ? lastWidth  : (scaleTime(timeData[i + 1]) - scaleTime(timeData[i]));
				  var gap = width * 0.1;
				  gap = gap < 1 ? 1 : gap;
				  width = width <= gap ? width : width - gap;
				  var height = baseline - top;
				  bars.append("svg:rect")
				  	.attr("x", left)
				  	.attr("y", top)
				  	.attr("width", width)
				  	.attr("height", height)
				  	.attr("fill", color);
				  lastWidth = width;
			}
		}
		
		
		function trackMouse(data) {
			_svg.on("mouseover", function(e) {
				for (var i = 0; i < _trackers.length; i++) {
					_trackers[i].enter();
				}
			});		
			_svg.on("mouseout", function(e) {
				for (var i = 0; i < _trackers.length; i++) {
					_trackers[i].exit();
				}
			});
			_svg.on("mousemove", function(e) {
				if (data[0]== undefined) {
					return;
				}
				
				var boundary =  _xScale(data[0].data) + 1;
				var axisOffset = _margins.left;
			
				var mouse = d3.mouse(this);
				var x = mouse[0];
//				var y = mouse[1];		
				x = Math.max(axisOffset, x);
//				x = Math.min(axisOffset + boundary, x);
				x -= axisOffset;

				var last = new Date(data[0].date).getTime();
				var cursorTime = new Date(_xScale.invert(x));
				var time = cursorTime.getTime();
				var element = 1;
				while (element < data.length) {
					var next = new Date(data[element].date).getTime();
					var checkpoint = last + (next - last) / 2;
					if (time < checkpoint) {
						break;
					}
					element++;
					last = next;
				}
				element--;
				var selectedTime = data[element].date;
				for (var i = 0; i < _trackers.length; i++) {
					_trackers[i].move(element, selectedTime, data[element], this);
				}
			});
		}
		
		var timeCursor = function() {
			var vline = _overlay.insert("svg:line", ":first-child")
				.attr("y1", _margins.top)
				.attr("y2", _height - _margins.bottom);
			
			return {
				enter: function() {
					vline
					.style("stroke", "black")
					.style("stroke-opacity", "0.4")
					.attr("x1", -1)
					.attr("x2", -1);
				},
				
				exit: function() {
					vline.style("stroke", "transparent");
				},
				
				move: function(element, time, data) {
					var cursorAt = _margins.left + _xScale(time);
					vline
					.attr("x1", cursorAt)
					.attr("x2", cursorAt);
				}
			}
		}
		
		var valueCursors = function() {
			var hline = [];
			for ( var p in columns) {
				var line = _overlay.insert("svg:line", ":first-child")
					.attr("x1", _margins.left)
					.attr("x2", _width - _margins.right);
				hline.push(line);
			}
			
			return {
				enter: function() {
					var color = 0;
					for ( var p in columns) {
						hline[p].
						style("stroke", _colors[color++]).
						style("stroke-opacity", "0.5")
						.attr("y1", -1)
						.attr("y2", -1);
					}
				},
				
				exit: function() {
					for ( var p in columns) {
						hline[p]
						.style("stroke", "transparent");
					}	
				},
				
				move: function(element, time, data) {
					for ( var p in columns) {
						var  y = data[columns[p]];
						if (y == undefined) {
							y = -1;
						} else {
							y = _margins.top + _yScale(y);
						}
						hline[p]
						.attr("y1", y)
						.attr("y2", y);
					}
				}
			}
		}

		var timeLabel = function() {
			var width = 150;
			var background = _overlay.insert("svg:rect")
				.style("fill", "transparent")
				.attr("width", width)
				.attr("height", "20")
				.attr("rx", "4")
				.attr("ry", "4")
				.attr("y", _margins.top - 12);
			var text = _overlay.insert("svg:text")
				.style("fill", "transparent")
				.style("text-anchor", "middle")
				.attr("y", _margins.top + 3);
			
			return {
				enter: function() {
					background
					.style("fill", "#e8e8e8")
					.style("fill-opacity", "0.8")
					.attr("x", -width);
					text
					.style("fill", "black")
					.attr("x", -width);
				},
				
				exit: function() {
					background.style("fill", "transparent");
					text.style("fill", "transparent");
				},
				
				move: function(element, time, data) {
					text.text(l10n.formatDateTime(time));

					var cursorAt = _margins.left + _xScale(time);
					if (cursorAt - width / 2 < _margins.left + 4) {
						cursorAt = _margins.left + width /2  + 4;
					} else if (cursorAt + width / 2 > _width - _margins.right - 4) {
						cursorAt = _width - _margins.right - width / 2 - 4;
					}
					background.attr("x", cursorAt - width / 2);
					text.attr("x", cursorAt);
				}
			}
		}

		var valueLabels = function() {
			var width = 50;
			var backgrounds = [];
			var labels = [];
			
			for ( var p in columns) {
				var background = _overlay.insert("svg:rect")
				.style("fill", "transparent")
				.style("stroke", "transparent")
				.style("sroke-width", "1")
				.attr("width", width)
				.attr("height", "16")
				.attr("rx", "4")
				.attr("ry", "4");
				backgrounds.push(background);

				var label = _overlay.insert("svg:text")
				.style("fill", "transparent")
				labels.push(label);
			}
			
			return {
				enter: function() {
					for ( var p in columns) {
						backgrounds[p]
						.style("fill", "#ffffff")
						.style("stroke", _colors[p])
						.style("fill-opacity", "0.45")
						.attr("x", -width);
						labels[p]
						.style("fill", "#000")
						.attr("x", -width);
					}
				},
				
				exit: function() {
					for ( var p in columns) {
						backgrounds[p].style("fill", "transparent")
						.style("stroke", "transparent");
						labels[p].style("fill", "transparent");
					}
				},
				
				move: function(element, time, data) {
					var cursorAt = _margins.left + _xScale(time);
					if (cursorAt + width > _width - _margins.left) {
						cursorAt -= width + 18;
					}
					for ( var p in columns) {
						var value = data[columns[p]];
						var valueAt = -10;
						if (value != undefined) {
							var valueAt = _margins.top + _yScale(value);
							value = fields.valueWithUnit(columns[p], value);
							labels[p].text(value);
							backgrounds[p].attr('width', value.length * 10);
						}
						backgrounds[p].attr("x", cursorAt + 9);
						backgrounds[p].attr("y", valueAt - 8);
						labels[p].attr("x", cursorAt + 12);
						labels[p].attr("y", valueAt + 5);
					}
				}
			}
		}


		function xStart() {
			return _margins.left;
		}

		function yStart() {
			return _height - _margins.bottom;
		}

		function xEnd() {
			return _width - _margins.right;
		}

		function yEnd() {
			return _margins.top;
		}

		function chartWidth() {
			return _width - _margins.left - _margins.right;
		}

		function chartHeight() {
			return _height - _margins.top - _margins.bottom;
		}

		_chart.width = function(w) {
			if (!arguments.length)
				return _width;
			_width = w;
			return _chart;
		};

		_chart.height = function(h) {
			if (!arguments.length)
				return _height;
			_height = h;
			return _chart;
		};

		_chart.margins = function(m) {
			if (!arguments.length)
				return _margins;
			_margins = m;
			return _chart;
		};

		_chart.colors = function(c) {
			if (!arguments.length)
				return _colors;
			_colors = c;
			return _chart;
		};

		_chart.xScale = function(x) {
			if (!arguments.length)
				return _xScale;
			_xScale = x;
			return _chart;
		};

		_chart.yScale = function(y) {
			if (!arguments.length)
				return _yScale;	
			_yScale = y;
			return _chart;
		};
		
		_chart.addMouseTracker = function(tracker) {
			_trackers.push(tracker);
		}
		
		return _chart;
	};


/*
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
*/
	// TODO convert
	function renderAlarm(element, station, component) {
		var alarms = station.alarms.current;
		for(var i = 0; i < alarms.length; i++) {
			var status = alarms[i].status;
			var port = alarms[i].port;
			var sensor = station.sensors[port];
			var name = sensor ? sensor.name : "unknown";
			element.append('div').attr("class", "alarm " + 	
					/*	
					function renderGraphLine(element, data1, station, component) {
						var from = data1.data[0].date;
						var to = data1.data[data1.data.length - 1].date;
						
						var chart = lineChart(element, component.width, component.height, component.margin, component.xaxis, component.yaxis, true)
							.xScale(d3.time.scale().domain([from, to]))
							.yScale(d3.scale.linear().domain([component.min, component.max]));
						var data = [];

							for(var i = 0; data1 && i < fata1.length; i++) {
								var point = data[i];
								var y = point[component.port];
								if (y) {
									data.push({x: new Date(point['date']), y: y});
								}
							}
						
						chart.addSeries(data);
						chart.render();
						
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
								container.append('span').attr('class', "value").text(numberFormat.format(lastStatus[port]));
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
									container.append('span').attr('class', "value").text(numberFormat.format(lastSample[port]));
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
									container.append('span').attr('class', "value").text(numberFormat.format(lastSample[port]));
									container.append('span').attr('class', "value-unit").text(sensor.unit);
								}
							} else {
								container.text("None");
							}
						}
					}
				*/status).text(status + " " + name);
		}
	}
/*
	// TODO convert
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
*/
	

	var reading = function(widget, results, metadata, descriptor) {
		widget.addClass('reading');
		
		var render = function() {
			var index = results.length - 1;
			var value;
			while (index > 0) {
				value = results[index][descriptor.field];
				if (value) {
					var fields = metadata.fields(descriptor.source);
					var label = $('<span class="value-label">');
					widget.append(label);
					if (descriptor.title) {
						if (descriptor.title != '-') {
							label.append(descriptor.title);
						} 
					} else {
						label.append(fields.title(descriptor.field));
					}
					widget.append('<span class="value-value">' + fields.value(descriptor.field, value));
					var unit = descriptor.unit ? descriptor.unit : fields.unit(descriptor.field);
					widget.append('<span class="value-unit">' + unit);
					return;
				}
				index--;
			}			
		}
		
		return {
			render: render,
		}
	}

	var sparkLine = function (widget, data, metadata, column, descriptor) {
		var fields = metadata.fields(descriptor.source);
		var _yScale;
		var _svg;
		var _body;
		var chartWidth = 50;
		var padding = 3;
		var chartHeight = 30;
		var id = "xxx";
		var _colors = d3.scale.category10();
		
		var _width = 100;
		var _height = 50;
		
		widget.addClass("class", "sparkline");
		
		var render = function() {
			var label = $('<span class="value-label">');
			widget.append(label);
			if (descriptor.title) {
				if (descriptor.title != '-') {
					label.append(descriptor.title);
				} 
			} else {
				label.append(fields.title(descriptor.field));
			}

			if (!_svg) {
				 var element  = d3.select(widget.get(0));
				_svg = element.append("svg")
					.attr("class", "chart" + id)
					.attr("height", _height)
					.attr("width", _width);
				defineBodyClip(_svg);
			}

			if (!_body) {
				_body = _svg.append("g")
					.attr("class", "body")
					.attr("transform", "translate(" + xStart() + "," + yEnd() + ")")
					.attr("clip-path", "url(#body-clip)");
			}
			
			_yScale.range([ _height, 0 ]);
			var yAxis = d3.svg.axis()
				.scale(_yScale)
				.ticks(0)
				.orient("left");
			_svg.append("g")
				.attr("class", "y axis")
				.attr("transform", function() {
					return "translate(" + xStart() + "," + yEnd() + ")";
				})
				.call(yAxis);
			
			var from = d3.min(data, function(v) { return v['date']; });
			var to = d3.max(data, function(v) { return v['date']; });
			_xScale = d3.time.scale().domain([from, to]);
			_xScale.range([ 0, _width ]);
			_yScale.range([ _height, 0 ]);
			drawGraphLine();
			
			if (descriptor.reading) {
				var reading = $('<span class="reading-label">');
				widget.append(reading);
				reading.append(fields.value(descriptor.field, data[data.length - 1][column]));
				reading.append(fields.unit(descriptor.field));
			}
		}
		
		function drawGraphLine() {
			var lines = [{
				id: column,
				values: data.map(function(d) {
					return {date: d.date, y: d[column]};
				})
			}];
			
			var line = d3.svg.line()
				.x(function(d) { 
					return _xScale(d.date); 
				})
				.y(function(d) { 
					return _yScale(d.y);
				});

			_body.selectAll("path.line")
				.data(lines)
				.enter()	
				.append("path")
				.style("stroke", function(d, i) { return _colors[i]; })
				.attr("class", "line");

			_body.selectAll("path.line")
				.data(lines)
				.transition()
				.attr("d", function(d) { return line(d.values); });
		}	

		function defineBodyClip(svg) {
			var padding = 5;

			svg.append("defs")
				.append("clipPath")
				.attr("id", "body-clip")
				.append("rect")
				.attr("x", 0 - padding)
				.attr("y", 0)
				.attr("width", chartWidth + 2 * padding)
				.attr("height", chartHeight);
		}

		function xStart() {
			return 10;
		}
		
		function yEnd() {
			return 10;
		}
		
		var yScale = function(scale) {
			_yScale = scale;
		}
		
		return {
			render : render,
			yScale : yScale,
		}
	}
	

	
	
	
	function drawTooltip(data2, container, width, height, left, bottom, _xscale) {
		var background = container;
		
		
		var data = data2.data();
		
		var vline = container.insert("svg:line", ":first-child")
	    	.attr("width", 20);
		var hline = [];
		for ( var p in chart.ports) {
			var line = container.insert("svg:line", ":first-child")
				.attr("width", 20);
			hline.push(line);
		}

		var tooltip = d3.select("#tooltip");

		background.on("mouseover", function(e) {
			vline.style("stroke", "black").style("opacity", "0.2");
			for ( var p in chart.ports) {
				var port = chart.ports[p];
				hline[p].style("stroke", port.color).style("opacity", "0.5");
			}
			tooltip.classed("hidden", false);
		});
		
		background.on("mouseout", function(e) {
			vline.style("stroke", "transparent");
			for ( var p in chart.ports) {
				hline[p].style("stroke", "transparent");
			}	
			tooltip.classed("hidden", true);
		});
		
		background.on("mousemove", function(e) {
			var top = bottom + height;
			//var bottom = chart.chartTop + chart.height;
			var boundary =  _xscale(data[0].data) + 1;
			var axisOffset = left;
		
			var mouse = d3.mouse(this);
			var x = mouse[0];
			x = Math.max(axisOffset, x);
			x = Math.min(axisOffset + boundary, x);
			x -= axisOffset;
			trac
			var y = mouse[1];
			vline
				.attr("y1", top)
				.attr("y2", bottom);
			
			var last = new Date(data[0].date).getTime();
			var cursorTime = new Date(_xscale.invert(x));
			var time = cursorTime.getTime();
			var element = 1;
			while (element < data.length) {
				var next = new Date(data[element].date).getTime();
				var checkpoint = last + (next - last) / 2;
				if (time < checkpoint) {
					break;
				}
				element++;
				last = next;
			}
			
			element--;
			var selectedTime = data[element].date;
			cursorAt = axisOffset + chart.xscale(selectedTime);
			vline
				.attr("x1", cursorAt)
				.attr("x2", cursorAt);
			
			var title = tooltip.select("div.title");
			title.text(moment(selectedTime).format(dateTimePattern));
				
			var para = tooltip.select("div.values");
			para.selectAll("div").remove();
			for ( var p in chart.ports) {
				var port = chart.ports[p];
				var entry = para.append("div");
				entry.append("span")
					.attr("class", "name")
					.attr("style", "border-color: " + port.color)
					.text(port.name); 

				var value = port.data[element];
				if (isNaN(value)) {
					continue;
				}
				
				hline[p]
				//	.attr("style", "border-color: " + port.color)
					.attr("x1", axisOffset - 5)
					.attr("x2", axisOffset + boundary + 10)
					.attr("y1", top + chart.scaleY(value))
					.attr("y2", top + chart.scaleY(value));
				
				entry.append("span")
					.attr("class", "value")
					.text(value); 
				entry.append("span")
					.attr("class", "unit")
					.text(port.units); 
			}	
			// console.log(dataSet.data);
			/*
			para.append("div")
			.text(cursorTime + " " + time); 
			para.append("div")
			.text(dates[0] + " " + dates[0].getTime()); 
			para.append("div")
			.text(dates[1] + " " + dates[1].getTime()); 
			para.append("div")
			.text(new Date(chart.scaleTime.invert(x - axisOffset)) + " " + chart.scaleTime.invert(x - axisOffset)); 
			para.append("div")
			.attr("class", "mouse")
			.text(" " + x + ", " + y); 
			 */

			tooltip.classed("hidden", false);
			tooltip.style("opacity", .6);
			
			offset = this.offsetTop - tooltip[0][0].clientHeight / 2;
			offset = 130; // TODO fix the offset (see above code) so that it the postion of the containing SVG
			var xx = x > $(window).width() - 340 ? (x - 340) : (x + 80);
			xx += 40;
			var yy = y + offset;
//			console.log(xx + " " + y + " " + " " + offset + " " + yy);
			tooltip
				.style("left", xx + "px")
				.style("top", yy + "px");

		});

	}

	
	
	var demo = function(parent) {
		var chart = lineChart(parent, 600, 300, 30, true, true, true);
		// chart.xScale(d3.time.scale().domain([from, to]));
		//chart.xScale(d3.time.scale().domain([0, 20]));
		chart.xScale(d3.scale.linear().domain([ 0, 30 ]));
		chart.yScale(d3.scale.linear().domain([ 5, 35 ]));
	//	chart.addSeries([[1, 12, 8], [4, 16, 9], [9, 10, 7]]);
		chart.addSeries([{x: 1, y: 12}, {x: 4, y: 16}, {x:9, y:10}, {x:12, y:11}]);
		chart.addSeries([{x: 1,y: 16}, {x:4,y:10}, {x: 9, y: 12}]);
		//chart.addSeries([10,20,24,19, 20, 10]);
		chart.render();
	};

	return {
		initModule : initModule,
		resetColors : resetColors,
	//	dataSet : dataSet,
		lineChart : lineChart,
		sparkLine : sparkLine,
		reading : reading,
		demo : demo,
	}

}());
