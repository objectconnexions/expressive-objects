/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, ibexis */

ibexis.maps = (function () {
	
	initModule = function ( $container ) {
		
		ibexis.shell.registerSegmentProcessor('stations-map', function (segment, account, showContext) {
			showStationsOnMap2(segment, account);
			showContext(segment);
		});
		
		ibexis.shell.registerSegmentProcessor('tracking', function (segment, station, showContext) {
			trackOnMap(segment, station);
			showContext(segment);
		});

		/*
		
		ibexis.shell.registerSegmentProcessor('mapping', function (segment, id, showContext) {
			var descriptor = ibexis.display.descriptor(id);
			if (descriptor) {
				var view = ibexis.display.create(segment, descriptor);
				view.addDeviceOid(segment.data('device'));
				view.addStationOid(segment.data('station'));
				view.init();
				showContext(segment);
			} else {
				segment.append('<div>No descriptor for "' + descriptor + '"</div>');
			}
		});
		*/
	};
	
	
	
	
	
	//TODO refactor so code is shared with previous method
	var trackOnMap = function(segment, station) {
		function initialize() {
			var content = $('<div class="ibexis-ui-content the-real-contents">'); 
			/*
			var form = $('<form class="span-selection">');
//			<input type="hidden" name="_section_selection" value="${station}" />
			var select = $('<select name="duration">');
			select.append('<option value="1">1 hour</option>');	  	
			select.append('<option value="2">2 hours</option>');	  	
			select.append('<option value="3">3 hours</option>');	  	
			select.append('<option value="6">6 hours</option>');	  	
			select.append('<option value="12">12 hours</option>');	  	
			select.append('<option value="24">24 hours</option>');	  	
			select.append('<option value="48">2 days</option>');	  	
			select.append('<option value="72">3 days</option>');	  	
			select.append('<option value="120">5 days</option>');	  	
			select.append('<option value="240">10 days</option>');	  
			select.on('change', selectDuration);
			form.append(select);
			content.append(form);
*/
			
			segment.append(content);
			var mapSection = $('<div class="ibexis-map">');
			content.append(mapSection);		
			
			var mapOptions = {
					scaleControl: true,
					disableDoubleClickZoom: true,
					mapTypeId: google.maps.MapTypeId.ROAD,
					center: {lat: -34.397, lng: 150.644},
					zoom: 10,
			};
			var map = new google.maps.Map(mapSection.get(0), mapOptions);
	
			
			
			
			
			var data = [];
			var dateRange = ibexis.date.createDateRange(24);
			var file = ibexis.data.stationData(station, null, dateRange);
			console.log("requesting sample entries " + file);
			file.load('samples', function(name, results) {
				console.log("received " + results.length + " rows");

				var count = 0;
				for (var i = 0; i < results.length; i++) {
					var station = results[i];
					var latitude = station['latitude'];
					var longitude = station['longitude'];
					if (latitude && latitude) {
						data[count++] = station;
					}
				}
				console.log(count + ' locations');




				var regular = 1;
				var time;
				var last;
				for (var i = 0; i < data.length; i++) {
					var date = data[i]['date'];
					var time = new Date(date);
					if (last) {
						var difference = (last.getTime() - time.getTime()) / 1000;
						if (difference < 60) {
							regular++;
						}
					}
					last = time;
				};
				var ratio = regular /  data.length;
				var smooth = ratio > 0.9;

				var bounds = new google.maps.LatLngBounds();
				var markers = [];
				var flightPlanCoordinates = [];
		
				if (data.length > 0) {
					var waypointAt;
					var dataOverview;
					for (var i = 0; i < data.length; i++) {
						var datum = data[i];
						var time = datum['date'];
						var latitude = datum['latitude'];
						var longitude = datum['longitude'];
						console.log(latitude + " " + longitude)
						waypointAt = new google.maps.LatLng(latitude, longitude);
						bounds.extend(waypointAt);
						dataOverview = latitude + " " + longitude + "<br/>" + new Date(time);

						if (!smooth) {
							var path = google.maps.SymbolPath.CIRCLE;
							var marker = new google.maps.Marker({
								position: waypointAt,
								map: map,
								icon: {
									path: path, 
									scale: 6, 
									strokeWeight: 2,
									strokeColor: '#f33'
								},
								title: waypointAt.toString()
							});
							infowindow = new google.maps.InfoWindow({
								content: dataOverview
							});
							google.maps.event.addListener(marker, 'click', function() {
								infowindow.open(map, marker);
							});

							markers.push(marker);
						}

						flightPlanCoordinates.push(waypointAt);
					};

					var path = google.maps.SymbolPath.CIRCLE;
					var marker = new google.maps.Marker({
						position: waypointAt,
						map: map,
						icon: {
							path: path, 
							scale: 8, 
							strokeWeight: 0,
							fillOpacity: 1,
							fillColor: '#f33'
						},
						title: waypointAt.toString()
					});
					infowindow = new google.maps.InfoWindow({
						content: dataOverview
					});
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.open(map, marker);
					});


					var color = smooth ? '#f00' : '#f88'; 
					var flightPath = new google.maps.Polyline({
						path: flightPlanCoordinates,
						geodesic: true,
						strokeColor: color,
						strokeOpacity: 0.5,
						strokeWeight: 3
					});


					flightPath.setMap(map);
				}
				map.fitBounds(bounds);
			});
		}
		
		var selectDuration = function() {
			var field = $(this).find('select[name=duration]');
			var duration = field.context.value;
			console.log('selected ' + duration);
		}
		
		initialize();
		
		
	}
	 
 
	
	
	
	//TODO refactor so code is shared with next method
	showStationsOnMap2 = function(segment, account) {
		
		function initialize() {
			var content = $('<div class="ibexis-ui-content the-real-contents">'); 
			segment.append(content);
			content.append('<div id="map2" class="ibexis-map">');		
			
			var mapOptions = {
					scaleControl: true,
					disableDoubleClickZoom: true,
					center: {lat: -34.397, lng: 150.644},
					zoom: 8,
					mapTypeId: google.maps.MapTypeId.ROAD
			};

			var map = new google.maps.Map(document.getElementById("map2"), mapOptions);
			var markers = [];
			
			
			
			
			var dateRange = ibexis.date.createDateRange(24);
			file = ibexis.data.logData(deviceOid, 'transmission', dateRange);
			
			file = ibexis.data.stationData(Data, null, dateRange);
			console.log("requesting sample entries " + file);
			file.load(source.name, function(name, results) {
				data[name] = results;
				console.log("received " + results.length + " rows");
				count--;
				if (count === 0) {
					processData(content, descriptor, data, dateRange);
				}
			});

			
			
			console.log("requesting log entries " + file);
			file.load('name???', function(name, results) {
//				var isUploading = showTransmissionHealth(content, configuration, results);
//				showStatusHealth(content, isUploading, configuration, deviceOid, dateRange);
			});

			
			/*
			$('#stations .station').each(	function(i, station) {
				latitude = $(this).data('latitude');
				longitude = $(this).data('longitude');
				title = $(this).data('title');
				var stationAt = new google.maps.LatLng(latitude, longitude);
				var marker = new google.maps.Marker({
					position: stationAt,
					map: map,
					title: (i + 1) + ". " + title
				});
				
				// show info window
				var contentString = '<div id="map-info-content">' + $(this).html() + '</div>';
				var infowindow = new google.maps.InfoWindow({
					content: contentString
				});
				google.maps.event.addListener(marker, 'click', function() {
					infowindow.open(map, marker);
				});
	
				markers.push(marker);
			});
			
			// fit bounds to set of markers
			var bounds = new google.maps.LatLngBounds();
			for (var i=0; i<markers.length; i++) {
				if(markers[i].getVisible()) {
					bounds.extend( markers[i].getPosition() );
				}
			}
			map.fitBounds(bounds);
	*/
		}
		// google.maps.event.addDomListener(window, 'load', initialize);
		initialize();
		
	}
	
	//TODO refactor so code is shared with next method
	showStationsOnMap = function() {
		/*
		var map = new google.maps.Map(document.getElementById('map'), {
	        center: {lat: -34.397, lng: 150.644},
	        zoom: 8,
	        mapTypeId: 'terrain',
	      });
		
		
		*/
		
		
		
		
		function initialize() {
//			var myLatlng = new google.maps.LatLng(-34.397, 150.644);
			var mapOptions = {
					scaleControl: true,
					disableDoubleClickZoom: true,
					center: {lat: -34.397, lng: 150.644},
					zoom: 8,
					mapTypeId: google.maps.MapTypeId.ROAD
			};
	/*	
		mapOptions =	{
          center: {lat: -34.397, lng: 150.644},
          zoom: 8,
          mapTypeId: google.maps.MapTypeId.ROAD
        }
        */
			var map = new google.maps.Map(document.getElementById("map"), mapOptions);
			var markers = [];
			
			$('#stations .station').each(	function(i, station) {
				latitude = $(this).data('latitude');
				longitude = $(this).data('longitude');
				title = $(this).data('title');
				var stationAt = new google.maps.LatLng(latitude, longitude);
				var marker = new google.maps.Marker({
					position: stationAt,
					map: map,
					title: (i + 1) + ". " + title
				});
				
				// show info window
				var contentString = '<div id="map-info-content">' + $(this).html() + '</div>';
				var infowindow = new google.maps.InfoWindow({
					content: contentString
				});
				google.maps.event.addListener(marker, 'click', function() {
					infowindow.open(map, marker);
				});
	
				markers.push(marker);
			});
			
			// fit bounds to set of markers
			var bounds = new google.maps.LatLngBounds();
			for (var i=0; i<markers.length; i++) {
				if(markers[i].getVisible()) {
					bounds.extend( markers[i].getPosition() );
				}
			}
			map.fitBounds(bounds);
	
		}
		// google.maps.event.addDomListener(window, 'load', initialize);
		initialize();
		
	}
	
	//TODO refactor so code is shared with previous method
	showTracking = function(id) {
		function initialize() {
			var mapOptions = {
					scaleControl: true,
					disableDoubleClickZoom: true,
					mapTypeId: google.maps.MapTypeId.ROAD
			};
			var map = new google.maps.Map(document.getElementById('map-' + id), mapOptions);
	
			var markers = [];
				
			var station = $('.ibexis-tracking-' + id + ' #track .station');
			var latitude = station.data('latitude');
			var longitude = station.data('longitude');
			var title = station.data('title');
	
			var stationAt = new google.maps.LatLng(latitude, longitude);
			/*
			var marker = new google.maps.Marker({
				position: stationAt,
				map: map,
				title:title
			});
			markers.push(marker);
			
			// show info window
			var contentString = station.html();
		  var infowindow = new google.maps.InfoWindow({
		      content: contentString
		  });
		  google.maps.event.addListener(marker, 'click', function() {
		    infowindow.open(map, marker);
		  });
			 */
		  
	
		  var regular = 1;
		  var time;
		  var last;
		  var data = $('.ibexis-tracking-' + id + ' #track .point');
		  data.each(function(i, station) {
			  var date = $(this).data('time');
			  var time = new Date(date);
			  if (last) {
				  var difference = (last.getTime() - time.getTime()) / 1000;
				  if (difference < 60) {
					  regular++;
				  }
			  }
			  last = time;
		  });
		  var ratio = regular /  data.size();
		  var smooth = ratio > 0.9;
	
		  var bounds = new google.maps.LatLngBounds();
		  markers = [];
		  var flightPlanCoordinates = [];
	
		  if (data.size() > 0) {
		
			  var waypointAt;
			  var dataOverview;
			  $('.ibexis-tracking-' + id + ' #track .point').each(function(i, station) {
				  var latitude = $(this).data('latitude');
				  var longitude = $(this).data('longitude');
				  var time = $(this).data('time');
				  console.log(latitude + " " + longitude)
				  waypointAt = new google.maps.LatLng(latitude, longitude);
				  bounds.extend(waypointAt);
				  dataOverview = latitude + " " + longitude + "<br/>" + new Date(time);
				  
				  if (!smooth) {
					  var path = google.maps.SymbolPath.CIRCLE;
					  var marker = new google.maps.Marker({
						  position: waypointAt,
						  map: map,
						  icon: {
							  path: path, 
							  scale: 6, 
							  strokeWeight: 2,
							  strokeColor: '#f33'
						  },
						  title: waypointAt.toString()
					  });
					  infowindow = new google.maps.InfoWindow({
						  content: dataOverview
					  });
					  google.maps.event.addListener(marker, 'click', function() {
						  infowindow.open(map, marker);
					  });
			
					  markers.push(marker);
				  }
				  
				  flightPlanCoordinates.push(waypointAt);
			  });
		
			  var path = google.maps.SymbolPath.CIRCLE;
			  var marker = new google.maps.Marker({
				  position: waypointAt,
				  map: map,
				  icon: {
					  path: path, 
					  scale: 8, 
					  strokeWeight: 0,
					  fillOpacity: 1,
					  fillColor: '#f33'
				  },
				  title: waypointAt.toString()
			  });
			  infowindow = new google.maps.InfoWindow({
				  content: dataOverview
			  });
			  google.maps.event.addListener(marker, 'click', function() {
				  infowindow.open(map, marker);
			  });
			  
		
			  var color = smooth ? '#f00' : '#f88'; 
			  var flightPath = new google.maps.Polyline({
			    path: flightPlanCoordinates,
			    geodesic: true,
			    strokeColor: color,
			    strokeOpacity: 0.5,
			    strokeWeight: 3
			  });
		
			  
			  flightPath.setMap(map);
		  
		  }
		  
		  
			
	/*		
		  // fit bounds to set of markers
		   var bounds = new google.maps.LatLngBounds();
		    for (var i=0; i<markers.length; i++) {
		        if(markers[i].getVisible()) {
		            bounds.extend( markers[i].getPosition() );
		        }
		    }
	
		    for (var i=0; i<flightPlanCoordinates.length; i++) {
	           bounds.extend( flightPlanCoordinates[i] );
		    }
	*/
		    map.fitBounds(bounds);
	
		}
		//google.maps.event.addDomListener(window, 'load', initialize);
		initialize();
	}
	 
 

  
  return { 
	  initModule: initModule,
	  showStationsOnMap: showStationsOnMap,
	  showTracking : showTracking,
  };
  
}());