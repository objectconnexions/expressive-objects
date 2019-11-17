/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, ibexis */

ibexis.alarms = (function () {
	var l10n = ibexis.localization;
	var intervalInUse;
	var processors = [];

	var initModule = function ( $container ) {
	};	

	
	var isInUse;
	var id;
	
	var initNotifications = function () {
		addEventProcessor(function(event) {
			var message = event.message;
			ibexis.shell.feedback(message);
		});
		
		isInUse = true;
		id = $('#ibexis-account').data('id');
		wait(id);
	}
		
	var stopNotifications = function() {
		isInUse = false;
	}
	
	function wait(account) {
		if (isInUse) {
			$.getJSON('/notification', 'id=' + account)
			.done(function(data) {
				if (isInUse && account === id) {
					if (!data.timeout) {
						for (var i = 0; i < processors.length; i++) {
							processors[i](data);
						}
					}
					wait(account);
					
					// TODO pause briefly to prevent overloading server
					//	sleep(1);
				}
			}).fail(function( jqxhr, textStatus, error ) {
				if (isInUse && account === id) {
					var err = textStatus + ", " + error;
					console.log( "Request Failed: " + err );
					
					// TODO pause briefly to prevent overloading server
				//	sleep(15);
					wait(account);
				}
			});
		}
	}
	
	var addEventProcessor = function(processor) {
		processors.push(processor);
	}

	var initDashboard = function(accountOid, id) {
		var stations = '';
		var cells = $('#' + id).find('div.ibexis-ui-cell');
		cells.each(function() {
			stations +=  (stations.length > 0 ? ',' : '') + $(this).data('station-oid');
		});
		var query = "stations=" + stations + "&offset=" + 28 * 7 + "&period="  + 28 * 7;
		$.getJSON("alarm-data.app", query)
		.done(function(data) {
			console.log(data);

			$.each(data,  function() {
				if (this.alarm) {
					var station = this.station;
					var cell = cells.filter('div.ibexis-ui-cell[data-station-no="' + station + '"]');
					cell.addClass("alarm");
					var status = { _state: 'alarm' };
					var description = cell.find('.description');
					description.empty();
					$.each(this.status, function() {
						description.append('<div>' + this + '</div>'); 
						status[this] = 'raised';
					});
					cell.attr('data-status', JSON.stringify(status));
				}
			});

		}).fail(function( jqxhr, textStatus, error ) {
			if (error) {
				ibexis.shell.showHtmlError(error);
				//	console.error(error);
			}
			console.log( "Request Failed: " + error );
		});
		
		var processor = function(notification) {
			var event = notification.event;
			var cell = $('#' + id + ' div.ibexis-ui-cell[data-station-no="' + event.station + '"]');
			var isRaised = event.type.endsWith('R');
			var exposition = event.exposition;
			var description = cell.find('.description');
			
			var status = cell.data('status');
			if (!status) {
				status = {};
				status._state = 'normal';
			}
			
			if (isRaised) {
				cell.addClass('alarm');
				status._state = 'alarm';
				status[exposition] = 'raised'; 
				description.find('div:contains(Normal)').remove();
				description.append('<div>' + exposition + '</div>'); 
			} else {
				delete status[exposition];
				description.find('div:contains(' + exposition + ')').remove();
				
				if (Object.keys(status).length == 1) {
					cell.removeClass("alarm");
					status._state = 'normal';
					description.append('<div>Normal</div>'); 
				}
			}

			cell.attr('data-status', JSON.stringify(status));
		};
		addEventProcessor(processor);
	}

	return { 
	  initModule: initModule,
	  
	  initNotifications: initNotifications,
	  stopNotifications : stopNotifications,
	  addEventProcessor : addEventProcessor,
	  initDashboard : initDashboard,
  };
  
}());