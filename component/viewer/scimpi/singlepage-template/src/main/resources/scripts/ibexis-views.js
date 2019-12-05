/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.views = (function() {

	function addDescriptor(descriptor) {
		ibexis.display.addDescriptor(descriptor);
	}

	var registerDescriptors = function() {
		
		/*
		 * This replaces the old set up and will be removed when everybody switched over
		 */
		addDescriptor({
			id : "app-default",
			name : "Default Application",
			description : "Provides the same content as the orignal hard coded app",

			apps : [ {
				name : "Exploration",
				content : "old_application/_apps-data-viewer-test.shtml?account=$account"
			}, {
				name : "Export",
				content : "old_application/_apps-data-export-example.shtml?account=$account"
			}, {
				name : "Alarms",
				content : "old_application/_apps-alarms.shtml?account=$account"
			}, {
				name : "Station Map",
				content : "old_application/_apps-map-example.shtml?account=$account"
			}, ],
		});

		

		/*
		 * This is a test to rebuild the oringal using desciptors and templates
		 */
		addDescriptor({
			id : "app-test",
			name : "Test Application",
			description : "Designing dynamic app concept...",

			apps : [ {
				name : "Exploration",
				descriptor: "test-exploration",
				data : "account",
			}, {
				name : "Export",
				descriptor: "test-export",
				// data : "account",
				// content: "station/_stations-grid.shtml?account=$account"
			}, {
				name : "Alarms",
				descriptor: "test-alarm",
				data : "account",
				// content: "station/_stations-grid.shtml?account=$account"
			}, {
				name : "Station Map",
				content : "old_application/_apps-map-example.shtml?account=$account"
			}, ],
		});
		
		addDescriptor({
			id : "test-exploration",
			name : "Test Exploration",
			description : "Designing dynamic app concept...",

			sections : [ {
				name : "Station select",
				/*
				descriptor: "station-selector",
				type : "account", // ?? 
				data : "account",
				*/
				
				selector : "table",
				action : "test-alarm-station"
			}, ],
		});
		
		addDescriptor({
			id : "test-alarm",
			name : "Test Alarm",
			description : "Designing dynamic app concept...",

			sections : [ {
				name : "Stations XX",
				template : "stations-grid",
				action: "test-alarm-station"
			}, ],
		});

		addDescriptor({
			id : "test-alarm-station",
			name : "Alarms",

			sections : [ {
				name : "Table",
				descriptor : "test-sample-table",
				data : "station",
			}, {
				name : "Charts",
				descriptor : "sample-charts",
				data : "station",
			}, ],
		});

		
		addDescriptor({
			id : "test-export",
			name : "Test Export",
			description : "Designing dynamic app concept...",

			sections : [ {
				name : "Stations",
//				descriptor: "station-selector",
				/*
				content: "application/__stations-grid.shtml?account=$account&descriptor=test-descriptor",
				data : "account",
				*/
				template : "stations-grid",
				action: "sample-table",
			}, ],
		});

		
		
		
		
		/*
		 * This is the first real app.
		 */
		addDescriptor({
			id : "app-basic",
			name : "Test Application",
			description : "Designing dynamic app concept...",
			apps : [ {
				name : "Data",
				descriptor: "basic-data",
			}, ],
		});
		
		addDescriptor({
			id : "basic-data",
			sections : [ {
				name : "Stations",
				template : "stations-grid",
				action: "basic-station"
			}, ],
		});

		addDescriptor({
			id : "basic-station",
			sections : [ {
				name : "Data",
				descriptor : "basic-station-data",
				data : "station",
			}, {
				name : "Alarms",
				descriptor : "basic-station-alarms",
				data : "station",
			}, ],
		});

		addDescriptor({
			id : "basic-station-data",
			sections : [ {
				name : "Charts",
				descriptor : "sample-charts",
				data : "station",
			}, {
				name : "Table",
				descriptor : "sample-table",
				data : "station",
			}, ],
		});

		addDescriptor({
			id : "basic-station-alarms",
			sections : [ {
				name : "Dashboard",
				descriptor : "alarm-dashboard",
				data : "station",
			}, {
				name : "History",
				descriptor : "alarm-history",
				data : "station",
			}, ],
		});

		addDescriptor({
			id : 'alarm-history',
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 7,
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "AlarmHistory",
					source : "data",
					/* title : "Sample Data", */
				}, ]
			}
		});

		addDescriptor({
			id : 'alarm-dashboard',
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 7,
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "AlarmDashboard",
					source : "data",
					/* title : "Sample Data", */
				}, ]
			}
		});

		
		
		
		
		

		addDescriptor({
			id : "test-analysis",
			name : "Analysis",
			description : "Same as Analysis tab that is defined by /ibexis-single-page/src/main/webapp/device/_device-analysis.shtml",
			/*
				<swf:template file="../__tabs-inner-existing-segment.shtml" />
				<div class="ibexis-ui-segment" data-label="Power" data-descriptor="power-analysis" data-device="${device}"></div>	
				<div class="ibexis-ui-segment" data-label="Cellular" data-descriptor="cellular-analysis" data-device="${device}"></div>	
				<div class="ibexis-ui-segment" data-label="Transmissions" data-descriptor="transmission-frequency" data-device="${device}"></div>	
				<div class="ibexis-ui-segment" data-label="Status" data-descriptor="device-analysis" data-device="${device}"></div>	
			*/
			
			template : "__tabs-inner-existing-segment.shtml",

			sections : [ {
				name : "Power",
				descriptor : "power-analysis",
				data : "device"
			}, {
				name : "Cellular",
				descriptor : "cellular-analysis",
				data : "device"
			}, {
				name : "Transmissions",
				descriptor : "transmission-frequency",
				data : "device"
			}, {
				name : "Status",
				descriptor : "device-analysis",
				data : "device"
			}, ],
		});
		

		addDescriptor({
			id : 'test-sample-table',
			data : {
				dataSources : [ {
					name : "data",
					type : "Samples",
				}, ],
				timeframe : {
					type : "LastHour",
				},
				processes : [],
				components : [ {
					type : "Table",
					source : "data",
					title : "Sample Data"
				}, ]
			}
		});

		
		
		
		
		addDescriptor({
			id : "station-selector",
			name : "Station Selection",
			description : "Designing dynamic app concept...",

			stationSelector : {
				type : "table",
				title : "List of Stations",
				action : "test-sample-table"
			}
		});

		
		
		addDescriptor({
			id : "event-grid",
			name : "Event Panel Grid",
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					splitLine : true,
					type : "AlarmGrid",
					source : "data",
				}, ]
			}
		});
		
		
		addDescriptor({
			id : "event-history",
			name : "Event Panel History",
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					splitLine : true,
					type : "Table",
					source : "data",
					title : "Recent Events",
				}, ]
			}
		});


		addDescriptor({
			id : "alarm-grid",
			name : "Alarm Panel Grid",
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					splitLine : true,
					type : "AlarmGrid",
					source : "data",
				}, ]
			}
		});
		
		
		addDescriptor({
			id : "event-history",
			name : "Event HistoryTable",
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					splitLine : true,
					type : "Table",
					source : "data",
					title : "Recent Alarms (last 7 days)",
				}, ]
			}
		});

		addDescriptor({
			id : "data-viewer",
			name : "Data logging viewer",
			description : "Display the data collected by a logger",
			data : {
				dataSources : [ {
					name : "data",
					type : "Log",
					log : "status",
				}, ],
				stations : {
					type : "SelectStation",
				},
				ports : {
					type : "AllPorts",
				},
				timeframe : {
					type : "Last24Hours",
				},
				sections : [ {
					name : "Table",
					components : [ {
						splitLine : true,
						type : "Table",
						source : "data",
					// fields : "All"
					} ],
				}, {
					name : "Export",
					splitLine : true,
					type : "Table",
					source : "data",
				}, {
					name : "Charts",
					splitLine : true,
					type : "Table",
					source : "data",
				}, ],
			}
		});
		
		
		addDescriptor({
			id : "todays-status-table",
			data : {
				dataSources : [ {
					name : "stat",
					type : "Log",
					log : "status",
				}, ],
				stations : {
					type : "CurrentStations",
				},
				ports : {
					type : "AllPorts",
				},
				timeframe : {
					type : "Last24Hours",
				},
				processes : [],
				components : [
						{
							splitLine : true,
							type : "Table",
							source : "stat",
							fields : [ "ss", "ev",
									"bv", "bi", ],
							title : "ps",
						}, ]
			}
		});

		addDescriptor({
			id : 'summary',
			name : "Status Table",
			data : {
				dataSources : [ {
					name : "stat",
					type : "Log",
					log : "status",
				}, {
					name : "xmit",
					type : "Log",
					log : "transmission",
				}, {
					name : "debug",
					type : "Log",
					log : "debug",
				}, ],
				stations : {
					type : "CurrentStations",
				},
				ports : {
					type : "AllPorts",
				},
				timeframe : {
					type : "TimeframeSelector",
					xxoffset : 72,
					period : 4,
				},
				processes : [],
				components : [
						{
							type : "DateRangeHeading",
						},
						{
							splitLine : true,
							type : "Table",
							source : "xmit",
							fields : [ "date", "type", "size" ],
							title : "Transmission Summary",
						},
						{
							splitLine : true,
							type : "Table",
							source : "stat",
							fields : [ "ss", "ev",
									"bv", "bi", ],
							title : "Power Summary",
						}, {
							splitLine : true,
							type : "Table",
							source : "debug",
							fields : [],
							title : "Debug Log",
							class : "ibexis-debug"
						}, ]
			}
		});

		addDescriptor({
			id : "diagnostic-summary",
			data : {
				dataSources : [ {
					name : "status",
					type : "Log",
					log : "status",
				}, {
					name : "xmit",
					type : "Log",
					log : "transmission",
				}, {
					name : "data",
					type : "Samples",
				}, {
					name : "events",
					type : "Events",
				}, ],
				timeframe : {
					type : "Last6Hours",
				},
				processes : [ {
					type : "Interval",
					source : "status",
					magnitude: "seconds",
				},  {
					type : "Interval",
					source : "xmit",
					magnitude: "seconds",
				},  {
					type : "Interval",
					source : "data",
					magnitude: "seconds",
				}, {
					type : "Interval",
					source : "events",
					magnitude: "minutes",
				}],
				components : [
				              /*
						{
							type : "FormEntry",
							source : "status",
							title : "Initial Power Levels",
						},
						*/
						{
							type : "Value",
							source : "status",
							field : "bv",
							xxxunit: 'V',
							xxxtitle : "Battery",
						}, {
							type : "Value",
							source : "status",
							field : "ev",
							xxunit: 'V',
							title : "-",
						}, {
							type : "Value",
							source : "status",
							field : "ev",
							unit: 'V',
							title : "External",
						}, {
							splitLine : true,
							type : "SparkLine",
							source : "status",
							field : "bv",
							xxxtitle : "Battery Voltage (V)",
							max: 4.5,
							min: 3,
							reading : true,
						}, {
							type : "SparkLine",
							source : "status",
							field : "ev",
							title : "External Voltage (V)",
							max: 15,
							min: 6,
						}, {
							type : "SparkLine",
							source : "status",
							field : "si",
							title : "System Current",
							min: 0,
							max: 480,
						}, {
							splitLine : true,
							type : "Graph",
							source : "status",
							fields : [ "bv", "sv",
									"uv", ],
							title : "Voltages (V)",
							width: 700,
							height: 200,
							min: 3,
							max: 5.5,
							margin: 10,
						}, {
							splitLine : true,
							type : "Table",
							source : "xmit",
							limit: 8,
							title : "Transmissions",
						}, {
							type : "Table",
							source : "status",
							limit: 8,
							fields : [ "interval", "ev",
									"uv", "bv",
									"bi", "sv",
									"si", ],
							title : "Power Statistics (Table)",
						}, {
							splitLine : true,
							type : "Table",
							source : "data",
							limit: 8,
							title : "Sample Data",
						}, {
							splitLine : true,
							type : "Table",
							source : "events",
							limit: 8,
							title : "Events Data",
						}, ]
			}
		});

		addDescriptor({
			id : "power-analysis-stats",
			data : {
				dataSources : [ {
					name : "status",
					type : "Status",
					log : "basic",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [ {
					type : "Interval",
					source : "status",
					magnitude: "hours",
				}, {
					name : "stats",
					type : "BasicStats",
					source : "status",
				} ],
				components : [
						{
							type : "FormEntry",
							source : "stats",
							title : "Power Statistics (Form)",
						},
						{
							type : "Table",
							source : "stats",
							fields : [ "interval", "ev",
									"uv", "bv",
									"bi", "sv",
									"si", ],
							title : "Power Statistics (Table)",
						},
						{
							splitLine : true,
							type : "Table",
							source : "status",
							fields : [ "interval", "ev",
									"uv", "bv",
									"bi", "sv",
									"si", ],
							title : "power summary",
						}, ]
			}
		});

		addDescriptor({
			id : "power-analysis",
			data : {
				dataSources : [ {
					name : "status",
					type : "Status",
					log : "basic",
				}, ],
				timeframe : {
					type : "TimeframeOptions",
					period : 72,
				},
				processes : [],
				components : [
				              /*
						{
							type : "FormEntry",
							source : "status",
							title : "Initial Power Levels",
						},
						*/
						{
							type : "Tooltip",
							source : "status",
							/*
							 * Fields: ....
							 */
						},
						{
							splitLine : true,
							type : "Graph",
							source : "status",
							fields : [ "bv", "sv",
									"uv", ],
							title : "Voltages (V)",
							width: 700,
							height: 200,
							min: 3,
							max: 5.5,
							margin: 10,
						},
						{
							splitLine : true,
							type : "Graph",
							source : "status",
							fields : [ "si" ],
							title : "System Current (mA)",
							width: 700,
							height: 200,
							min: 30,
							max: 270,
							margin: 10,
						}, 					
						{
							splitLine : true,
							type : "Graph",
							source : "status",
							fields : [ "ev" ],
							title : "External Voltage (V)",
							width: 700,
							height: 200,
							min: 3,
							max: 36,
							margin: 10,
							showTimeCursor: false,
							showValueCursors : true,
						}, ]
			}
		});

		addDescriptor({
			id : "cellular-analysis",
			data : {
				dataSources : [ {
					name : "status",
					type : "Log",
					log : "status",
				}, ],
				timeframe : {
					type : "TimeframeOptions",
					period : 72,
				},
				processes : [],
				components : [
				              /*
						{
							type : "FormEntry",
							source : "status",
							title : "Initial Power Levels",
						},
						*/
						{
							splitLine : true,
							type : "Graph",
							source : "status",
							fields : [ "ss" ],
							title : "Signal Strength (0-31)",
							width: 700,
							height: 200,
							min: 0,
							max: 32,
							margin: 10,
						},
						{
							splitLine : true,
							type : "Table",
							source : "status",
							fields : [ "ss"],
						}, ]
			}
		});

		addDescriptor({
			id : 'device-health',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "status",
				}, {
					name : "log",
					type : "Log",
					log : "transmission",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Graph",
					source : "log",
					fields : [ "size" ],
					title : "",
					width: 700,
	/*				
					height: 200,
					min: 0,
					margin: 10,				
					*/
					max: 10000,
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					title : "Transmissions sent",
				}, ]
			}
		});

		addDescriptor({
			id : 'transmission-frequency',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "transmission",
				}, ],
				timeframe : {
					type : "Last7Days",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Graph",
					source : "log",
					timeLimit : 24, 
					fields : [ "size" ],
					title : "Last 24 Hours",
					width: 500,
					showPoints: true,
					showLine: false,
					max: 8100,
				}, {
					splitLine : false,
					type : "Graph",
					source : "log",
					timeLimit : 3, 
					fields : [ "size" ],
					title : "Last 3 Hours",
					width: 160,
					showPoints: true,
					showLine: false,
					max: 8100,
				}, {
					splitLine : true,
					type : "Graph",
					source : "log",
					fields : [ "size" ],
					title : "Last 7 Days",
					width: 780,
					height: 300,
					showPoints: true,
					showLine: false,
					max: 8100,
				}, ]
			}
		});

		addDescriptor({
			id : 'status-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "status",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No Status enties for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					title : "Recorded status",
					reversed : true,
				}, ]
			}
		});

		addDescriptor({
			id : 'peripheral-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Peripheral_Log",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [{
					type : "Interval",
					source : "log",
					magnitude: "seconds",
				},{
					type : "Transmissions",
					source : "log",
				},
				],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No Status enties for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					title : "Peripheral status",
				}, ]
			}
		});

		addDescriptor({
			id : 'peripheral-analysis',
			data : {
				dataSources : [ {
					name : "log",
					type : "Peripheral_Log",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 1,
				},
				processes : [{
					type : "Interval",
					source : "log",
					magnitude: "seconds",
				},{
					type : "Transmissions",
					source : "log",
				},
				],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No Status enties for this date range',
				}, {
					splitLine : true,
					type : "Graph",
					format : "Line",
					source : "log",
					width: 700,
					height: 120,
					min: "Auto",
					max: "Auto",
					margin: 0,
				}, ]
			}
		});

		addDescriptor({
			id : 'device-analysis',
			data : {
				dataSources : [ {
					name : "log",
					type : "Status",
					log: "basic",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 1,
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No Status enties for this date range',
				}, {
					splitLine : true,
					type : "Graph",
					format : "Line",
					source : "log",
					width: 700,
					height: 120,
					min: "Auto",
					max: "Auto",
					margin: 0,
				}, ]
			}
		});

		addDescriptor({
			id : 'transmission-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "transmission",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [{
					type : "Interval",
					source : "log",
					magnitude: "seconds",
				}, ],
				components : [ {
					type : 'EmptyHeading',
					text : 'No transmissions for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					reversed : true,
					title : "Transmissions sent",
				}, ]
			}
		});

		addDescriptor({
			id : 'lifecycle-log',
			data : {
				dataSources : [ {
					name : "life",
					type : "Log",
					log : "lifecycle",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					source : "life",
					text : 'No lifecycle enties for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "life",
					reversed : true,
					title : "Lifecycle events",
				}, ]
			}
		});

		addDescriptor({
			id : 'request-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "request",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No requests for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					title : "Requests made to device",
				}, ]
			}
		});

		addDescriptor({
			id : 'command-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "command",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No commands executed during this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					reversed : true,
					title : "Commands executed",
				}, ]
			}
		});

		addDescriptor({
			id : 'property-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "property",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No properties set during this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					reversed : true,
					title : "Properties set",
				}, ]
			}
		});

		addDescriptor({
			id : 'debug-log',
			data : {
				dataSources : [ {
					name : "log",
					type : "Log",
					log : "debug",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					type : 'EmptyHeading',
					text : 'No debug enties for this date range',
				}, {
					splitLine : true,
					type : "Table",
					source : "log",
					reversed : true,
					title : "Logged debug messages",
				}, ]
			}
		});

		addDescriptor({
			id : 'sample-table',
			data : {
				dataSources : [ {
					name : "data",
					type : "Samples",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Table",
					source : "data",
					/* title : "Sample Data", */
				}, ]
			}
		});

		addDescriptor({
			id : 'event-data-table',
			data : {
				dataSources : [ {
					name : "data",
					type : "Events",
					data: "events"
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Table",
					source : "data",
					title : "Events",
				}, ]
			}
		});


		addDescriptor({
			id : 'sample-data-table',
			description: "Display a station's sampled data in its original form in a table",
			data : {
				dataSources : [ {
					name : "data",
					type : "Samples",
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Table",
					source : "data",
					title : "Collected Sample Data",
				}, ]
			}
		});

		addDescriptor({
			id : 'manual-data-table',
			description: "Display a station's manually entered data in its original form in a table",
			data : {
				dataSources : [ {
					name : "data",
					type : "Data",
					data: "manual"
				}, ],
				timeframe : {
					type : "DateSelector",
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					splitLine : true,
					type : "Table",
					source : "data",
					title : "User Entered Data",
				}, ]
			}
		});

		addDescriptor({
			id : 'sample-charts',
			data : {
				dataSources : [ {
					name : "data",
					type : "Samples",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 1,
				},
				processes : [],
				components : [ {
					type : "DateRangeHeading",
				}, {
					show: false,
					splitLine : true,
					type : "Table",
					source : "data",
					title : "Sample Data",
				}, {
					splitLine : true,
					type : "Graph",
					format : "Line",
					source : "data",
					width: 700,
					height: 200,
					min: "Auto",
					max: "Auto",
					margin: 10,
				}, ]
			}
		});

		addDescriptor({
			id : 'test-charts',
			data : {
				dataSources : [ {
					name : "data",
					type : "Samples",
				}, ],
				timeframe : {
					type : "TimeframeSelector",
					period : 7,
				},
				processes : [],
				components : [ {
					type : "Value",
					field : "Consumption"
				}, {
					type : "Value",
					title : "The latest voltage",
					field : "Voltage"
				}, {
					type : "Value",
					field : "Current",
					title : "-"
				}, {
					type : "SparkLine",
					field : "Meter"
				}, {
					type: "Header",
					text: "A special graph"
				}, {
					type : "DateTimeRangeHeading",
				}, {
					splitLine : true,
					type : "Graph",
					format : "Line",
					source : "data",
					fields : [ "voltage", "power" ],
					fields2 : [ "Current" ],
					xxtitle : "Liquids used",
					width: 700,
					height: 200,
					min: "Auto",
					max: "Auto",
					margin: 10,
				}, ]
			}
		});
	}

	return {
		registerDescriptors : registerDescriptors,
	}
})();
