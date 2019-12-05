/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.data = (function() {

	var statusMetadata;
	var stationMetadata;
	var logMetadata = {};

	var application = function() {
		var fieldMap = {};
		var data = [];
		var dataList = [];
		var sensors = {};
		var alarms = {};
		var alarmsList = [];
		var target = {};

		
		var downloadApplicationData = function(stationOid, callback) {
			console.log('downloading application data for ' + stationOid);
			$.getJSON('application-data.app', 'station=' + stationOid)
			.done(function(data) {
				add(data);
				callback();
			}).fail (function(data) {
				ibexis.shell.showHtmlError(data);
			});
		}


		
		var add = function(_data) {
			if (_data.sensors) {
				for (var i = 0; i < _data.sensors.length; i++) {
					var sensor = _data.sensors[i];
					sensors[sensor.id] = sensor; 
				}
			}
			
			if (_data.alarms) {
				for (var i = 0; i < _data.alarms.length; i++) {
					var alarm = _data.alarms[i];
					alarms[alarm.id] = alarm; 
					alarmsList.push(alarm.id);
				}
			}
			
			/*
			if (_data.data) {
				for (var h = 0; h < _data.data.length; h++) {
					var set = _data.data[h];
					for (var i = 0; i < set.length; i++) {
						var manual = _data.manual[i];
						manual[manual.id] = manual; 
						manualList.push(manual.id);
					}
				}
			}
			*/
			
			
			// TODO pass over this data in an object {}
			if (_data.device) {
				target.deviceId = _data.device;
			}
			if (_data.station) {
				target.stationNo = _data.station;
			}
			if (_data.customer) {
				target.customerId = _data.customer;
			}
			if (_data.id) {
				target.dataAccess = _data.id;
			}
			
			for (var i = 0; i < _data.data.length; i++) {
				data.push(_data.data[i]);
			}
		}

		
		
		
		
		var addFields = function(name, fields) {
			data.push({'id' : name, 'fields' : fields});
		}

		var mapDataSource = function(dataSourceId, fieldSetId) {
			fieldMap[dataSourceId] = fieldSetId;
		}
		
		var alarmData = function() {
			
			var title = function(name) {
				var s = alarms[name];
				return s ? s.name : (name.substring(0,1).toUpperCase() + name.substring(1));
			}
			
			var description = function(name) {
				var s = alarms[name];
				return s ? s.name : (name.substring(0,1).toUpperCase() + name.substring(1));
			}
			
			var ids = function() {
				var ids = [];
				for (var p in alarms) {
					ids.push(alarms[p].id)
				}
				return ids;
			}		
				
			return {
				ids : ids,
				title : title,
				description : description,
			}
		}
		
		var fieldData = function(source) {
			var fields = {};
			var id = fieldMap[source];
			for (var i = 0; i < data.length; i++) {
				if (data[i].id === id) {
					var flds = data[i].fields;
					for (var j = 0; j < flds.length; j++) {
						fields[flds[j].id] = flds[j];
					}
					break;
				}
			}
			
			var type = function(name) {
				var s = fields[name];
				if (s) {
					return s.type;
				}
			}

			var title = function(name) {
				var s = fields[name];
				return s ? s.name : (name.substring(0,1).toUpperCase() + name.substring(1));
			}
			
			var unit = function(name) {
				var s = fields[name];
				return s ? s.unit : "";
			}
			
			var value = function(name, value) {
				var s = fields[name];
				if (s) {
					if (s.type === 'STATE') {
						if (value === 1) {
							return s.trueName;
						} else if (value === 0) {
							return s.falseName;
						}
						return value;				
					}
					if (value) {
						return value;
					} else {
						return '';
					}
				} else {
					return value;
				}
			}
			
			var valueWithUnit = function(name, value) {
				var s = fields[name];
				if (s) {
					if (s.type === 'STATE') {
						if (value === 1) {
							return s.trueName;
						} else if (value === 0) {
							return s.falseName;
						}
						return value;				
					}
					if (value) {
						var unit = s.unit;
						return value + (unit ? unit : '');
					} else {
						return '';
					}
				} else {
					return value;
				}
			}
			
			var ids = function() {
				var ids = [];
				for (var p in fields) {
					ids.push(fields[p].id)
				}
				return ids;
			}		
				
			
			var add = function(newFields) {
				for (var i = 0; i < newFields.length; i++) {
					var fld = newFields[i];
					fields[fld.id] = fld; 
				}
			}

			return {
				add : add,
				ids : ids,
				title : title,
				type : type,
				unit : unit,
				value : value,
				valueWithUnit,
			}
		}
		
		
		
		/*
		var sensorType = function(sensor) {
			var s = sensors[sensor];
			if (s) {
				return s.type;
			}
		}

		var sensorTitle = function(sensor) {
			var s = sensors[sensor];
			return s ? s.name : (sensor.substring(0,1).toUpperCase() + sensor.substring(1));
		}
		
		var alarmTitle = function(alarm) {
			var a = alarms[alarm];
			return a ? a.name : '*' + alarm;
		}
		
		var sensorUnit = function(sensor) {
			var s = sensors[sensor];
			return s ? s.unit : "";
		}
		
		var value = function(sensor, value) {
			var s = sensors[sensor];
			if (s) {
				if (s.type === 'STATE') {
					if (value === 1) {
						return s.trueName;
					} else if (value === 0) {
						return s.falseName;
					}
					return value;				
				}
				if (value) {
					return value;
				} else {
					return '';
				}
			} else {
				return value;
			}
		}
		
		var valueWithUnit = function(sensor, value) {
			var s = sensors[sensor];
			if (s) {
				if (s.type === 'STATE') {
					if (value === 1) {
						return s.trueName;
					} else if (value === 0) {
						return s.falseName;
					}
					return value;				
				}
				if (value) {
					var unit = sensors[sensor].unit;
					return value + (unit ? unit : '');
				} else {
					return '';
				}
			} else {
				return value;
			}
		}
		
		var ids = function() {
			return Object.keys(sensors);
		}
		
		var fieldNames = function(source) {
			for (var i = 0; i < data.length; i++) {
				var set = data[i];
				if (set.id === source) {
					var fields = set.fields;
					var names = [];
					for (var j = 0; j < fields.length; j++) {
						name.push(fields[j].id)
					}
					break;
				}
			}
				
		// TODO find the right set...
			return Object.keys(sensors);
		}
		*/
		
		
		
		
		
		var addForLog = function(name, log) {
			mapDataSource(name, log);
			if (log === 'transmission') {
				addFields('transmission',  [
					{ 
						id: "offset",
						name: "Offset",
						unit: "s",
						type: "INTEGER"
					}, { 
						id: "sequence",
						name: "Seqeunce",
						type: "INTERGER"
					}, { 
						id: "duration",
						name: "Duration",
						unit: "s",
						type: "INTEGER"
					}, { 
						id: "type",
						name: "Packet",
						type: "STRING"
					}, { 
						id: "sections",
						name: "Sections",
						type: "STRING"
					}, { 
						id: "size",
						name: "Size",
						unit: "b",
						type: "INTEGER"
					}, { 
						id: "error",
						name: "Has Error",
						type: "STRING"
					},
				]);
				
			} else if (log === 'lifecycle') {
				addFields('lifecycle',  [
					{ 
						id: "status",
						name: "Status",
					}, { 
						id: "type",
						name: "Type",
					}, { 
						id: "param1",
						name: "Param #1",
					}, { 
						id: "param2",
						name: "Param #2",
					}, { 
						id: "param3",
						name: "Param #3",
					}, { 
						id: "param4",
						name: "Param #4",
					},
				]);
				
			} else if (log === 'request') {
				addFields('request',  [
					{ 
						id: "source",
						name: "Source",
					}, { 
						id: "details",
						name: "Details",
					},
				]);

			} else if (log === 'command') {
				addFields('command',  [
					{ 
						id: "command",
						name: "Command",
					}, { 
						id: "parameters",
						name: "Parameters",
					}, { 
						id: "result",
						name: "Result",
					},
				]);

			} else if (log === 'property') {
				addFields('property',  [
					{ 
						id: "property",
						name: "Property ID",
					}, { 
						id: "from",
						name: "Original Value",
					}, { 
						id: "to",
						name: "New Value",
					},
				]);

			} else if (log === 'debug') {
				addFields('debug',  [
					{ 
						id: "level",
						name: "Level",
					}, { 
						id: "component",
						name: "Component",
					}, { 
						id: "message",
						name: "Message",
					},
				]);

			} else if (log === 'peripheral') {
				addFields('peripheral',  [
					{ 
						id: "transmission count",
						name: "Transmission Count",
						type: "INTEGER"
					}, { 
						id: "awake time",
						name: "Time Running",
						type: "FLOAT"
					}, { 
						id: "node rssi",
						name: "Node RSSI",
						type: "INTEGER"
					}, { 
						id: "device rssi",
						name: "Device RSSI",
						type: "INTEGER"
					},	{ 
						id: "battery voltage",
						name: "Battery Voltage",
						unit: "V",
						type: "FLOAT"
					},
				]);

			} else if (log === 'status') {
				addFields('status',  [
					{ 
						id: "uv",
						name: "USB Voltage",
						unit: "V",
						type: "FLOAT"
					}, { 
						id: "ev",
						name: "External Supply",
						unit: "V",
						type: "FLOAT"
					}, { 
						id: "bv",
						name: "Battery",
						unit: "V",
						type: "FLOAT"
					}, { 
						id: "bi",
						name: "Battery Charge",
						unit: "mA",
						type: "FLOAT"
					}, { 
						id: "sv",
						name: "System",
						unit: "V",
						type: "FLOAT"
					}, { 
						id: "si",
						name: "System",
						unit: "mA",
						type: "FLOAT"
					}, { 
						id: "ss",
						name: "Signal Strength",
						type: "FLOAT"
					},
				]);

			}

		}


		return {
			downloadApplicationData : downloadApplicationData,
			//add : add,
			addForLog : addForLog,
			mapDataSource: mapDataSource,
			fields : fieldData,
			addFields : addFields,
			alarms : alarmData,
			/*
			sensorTitle : sensorTitle,
			alarmTitle : alarmTitle,
			sensorType : sensorType,
			sensorUnit : sensorUnit,
			ids : ids,
			fieldNames : fieldNames,  // TODO replaces the line above
			value : value,
			valueWithUnit : valueWithUnit,
			 */
//			alarms : alarmsList,
//			manualFields : data,
			target : target,
		}
	};
	
	/*
	 *        {
            "unit": "",
            "form": "ANALOG",
            "name": "Digital AL",
            "description": "Industrial IO, 4-20mA",
            "id": "digital_al",
            "type": "FLOAT"
        },

	 */
	var initModule = function($container) {
	};
	
	var csvData = function(url, callback) {
		var dateValue = function(value) { return ibexis.date.parseUtcDate(value); };
		var intValue = function(value) {
			if (value.startsWith('*')) {
				return '';
			}
			return parseInt(value);
		};
		var stateValue = function(value) {
			if (value.startsWith('1')) {
				return 1;
			} else {
				return 0;
			}
		};
		var floatValue = function(value) { 
			if (value.startsWith('*')) {
				return '';
			}
			return parseFloat(value); 
		};
		var stringValue = function(value) { return value; };
		
		var types = {
			'date' : dateValue,
		};
		
		/*
		 * Specify the type for a specific column. This determines if the data
		 * is parsed and how. By default the column is assumed to be floating
		 * point.
		 */
		var addType = function(column, type) {
			types[column] = type;
		};
		
		var load = function(name, results) {
			d3.csv(url)
				.row(function(d) {
					var row = {};
					for (p in d) {
						if (d[p]) {
							var parser = types[p];
							if (parser) {
								row[p] = parser(d[p]);
							} else {
								row[p] = floatValue(d[p]);
							}
						} else {
							row[p] = '';
						}
					}
					return row;
				})
				.get(function(error, rows) {
					if (error) {
						ibexis.shell.showHtmlError(error);
						//	console.error(error);
					} else {
						results(name, rows); 					
					}
				});
		};
		
		return {
			source : url,
			dateValue : dateValue,
			intValue : intValue,
			floatValue : floatValue,
			stringValue : stringValue,
			
			addType : addType,
			load : load,
		};
	};

	var stationList = function(accountOid, logType, dateRange) {
		console.log('downloading station list for ' + accountOid);
		var request = 'station-list.app?account=' + accountOid;
		var file = csvData(request);
		file.addType('oid', file.stringValue);
		file.addType('name', file.stringValue);
		file.addType('device', file.stringValue);
		return file;
	};

	var sourceData = function(deviceOid, type, size, toId) {
		var request = 'device=' + deviceOid + '&type=' + type;
		if (toId) {
			request += '&to-id=' + toId;
		}
		if (size) {
			request += '&size=' + size;
		}
		var load = function(name, results) {
			$.getJSON('source-data.app', request)
			.done (function(data) {
				results (name, data);
			}).fail (function(data) {
				ibexis.shell.showHtmlError(data);
			});
		};
		
		return {
			load : load,
		};
	};

	var logData = function(deviceOid, logType, dateRange) {
		var clause = function(dateRange) {
			var offset = ibexis.date.offset(dateRange);
			var period = ibexis.date.period(dateRange);
//			return '?device=' + deviceOid + '&period=' + period + '&offset=' + offset + '&limit=1000';
			return '?device=' + deviceOid + '&period=' + period + '&offset=' + offset;
		}
		var request = 'log-data.app' + clause(dateRange) + '&type=' + logType;
		var file = csvData(request);
		if (logType === 'transmission') {
			file.addType('type', file.stringValue);
			file.addType('sections', file.stringValue);
			file.addType('error', file.stringValue);			
		} else if (logType === 'status') {
			file.addType('signal strength', file.intValue);
		} else if (logType === 'lifecycle') {
			file.addType('status', file.stringValue);
			file.addType('type', file.stringValue);
			file.addType('param1', file.stringValue);
			file.addType('param2', file.stringValue);
			file.addType('param3', file.stringValue);
			file.addType('param4', file.stringValue);
		} else if (logType === 'request') {
			file.addType('source', file.stringValue);
			file.addType('details', file.stringValue);
		} else if (logType === 'command') {
			file.addType('command', file.stringValue);
			file.addType('parameters', file.stringValue);
			file.addType('result', file.stringValue);
		} else if (logType === 'property') {
			file.addType('property', file.stringValue);
			file.addType('from', file.stringValue);
			file.addType('to', file.stringValue);
		} else if (logType === 'debug') {
			file.addType('level', file.stringValue);
			file.addType('component', file.stringValue);
			file.addType('message', file.stringValue);
		}
		return file;
	};

	var peripheralData = function(peripheralOid, dataType, dateRange) {
		var clause = function(dateRange) {
			var offset = ibexis.date.offset(dateRange);
			var period = ibexis.date.period(dateRange);
			var interval = intervalFor(dateRange);
			return '?peripheral=' + peripheralOid + '&period=' + period + '&offset=' + offset + '&interval=' + interval;
		}
		var request = 'peripheral-data.app' + clause(dateRange);
		if (dataType) {
			request += '&type=' + dataType;
		}
		var file = csvData(request);
		return file;
	};

	var statusData = function(deviceOid, dataType, dateRange) {
		var clause = function(dateRange) {
			var offset = ibexis.date.offset(dateRange);
			var period = ibexis.date.period(dateRange);
			var interval = intervalFor(dateRange);
			return '?device=' + deviceOid + '&period=' + period + '&offset=' + offset + '&interval=' + interval;
		}
		var request = 'status-data.app' + clause(dateRange);
		if (dataType) {
			request += '&type=' + dataType;
		}
		var file = csvData(request);
		return file;
	};

	var stationData = function(stationOid, logType, dateRange, metadata, downSample) {
		var clause = function(dateRange) {
			var offset = ibexis.date.offset(dateRange);
			var period = ibexis.date.period(dateRange);
			var interval = (downSample == undefined || downSample == true) ? intervalFor(dateRange) : 0;
			return '?station=' + stationOid + '&period=' + period + '&offset=' + offset + '&interval=' + interval;
		}
		var request = 'station-data.app' + clause(dateRange) + '&type=' + logType;
		var file = csvData(request);
		if (logType === 'events'  || logType === 'alarms') {
			file.addType('type', file.stringValue);
			file.addType('id', file.stringValue);
		}
		return file;
	};
		
	function intervalFor(dateRange) {
		var period = ibexis.date.period(dateRange);
		if (period < 3) {
			return 0;
		} 
		var days = period / 24;
		if (days <=1) {
			return 1;
		} else if (days < 5) {
			return 2;
		} else if (days < 10) {
			return 5;
		} else if (days < 15) {
			return 10;
		} else if (days < 20) {
			return 15;
		} else if (days < 30) {
			return 20;
		} else if (days < 60) {
			return 30;
		} else if (days < 180) {
			return 60;
		} else if (days < 300) {
			return 180;
		} else {
			return 300;
		}
	}
	
	var getStationMetadata = function() {
		/*
		if (!stationMetadata) {
			stationMetadata = application();
		}
		*/
		return stationMetadata;
	}
	
	var getStatusMetadata = function() {
		return statusMetadata;
	}
	
	var getPeripheralMetadata = function() {
		return logMetadata['peripheral'];
	}
	
	var createMetadata = function() {
		return application();
	}
	
	return {
		initModule : initModule,
		createMetadata : createMetadata,
		logData : logData,
		peripheralData : peripheralData,
		statusData : statusData,
		stationList : stationList,
		stationData : stationData,
		sourceData : sourceData,
//		logMetadata : getLogMetadata,
//		statusMetadata : getStatusMetadata,
//		stationMetadata : getStationMetadata,
//		logMetadata : getLogMetadata,
//		peripheralMetadata : getPeripheralMetadata,
	}

}());
