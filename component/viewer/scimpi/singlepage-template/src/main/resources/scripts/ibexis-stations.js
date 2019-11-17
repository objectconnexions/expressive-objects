/*jslint         browser : true, continue : true,
  devel  : true, indent  : 2,    maxerr   : 50,
  newcap : true, nomen   : true, plusplus : true,
  regexp : true, sloppy  : true, vars     : false,
  white  : true
*/
/*global $, ibexis */

// TODO no longer used?
ibexis.xxxstations = (function () {

	var initModule = function() {};

	/*
	 * parent - the jquery node
	 * device - the device OID
	 */
	var showStatusData = function(parent, device) {
		var data = ibexis.data.data();
		
		ibexis.data.load({stationProvider: "${owner}", stations: "${_result}", from: "${selected-date:optional}", duration: 1, language: "${user-language}", timezone:"${user-time-zone}", formatting: ${formatting}});
		
		
		var request = 'status-data.app?device=' + device + '&period=20&offset=30&xxxinclude=BV,SV,UV,SI,SS,MC';
		data.load(request, function(results) {
			/*
			console.log(results);
		//	$(this).find('pre.data-set').append("testing");
		//	$('pre.data-set').append(results.length); 
			parent.append("<p>" + results.length + " items for device " + device + "</p>");
			*/

			var chartSettings = {
					width: 800,
					height: 300,
					margin: 30,
			};
			
			var element = d3.select(parent.get(0));
			var chart = ibexis.charting.lineChart(element, results, chartSettings);
			chart.yScale(d3.scale.linear().domain([ 0, 150 ]));
			chart.addColumn('SI');
			chart.render();
			
			chartSettings.height = 200;
			var chart2 = ibexis.charting.lineChart(element, results, chartSettings);
			chart2.yScale(d3.scale.linear().domain([ 3, 5.5 ]));
			chart2.addColumn('BV');
			chart2.addColumn('UV');
			chart2.addColumn('SV');
			chart2.render();
			
			chartSettings.height = 100;
			chart = ibexis.charting.lineChart(element, results, chartSettings);
			chart.yScale(d3.scale.linear().domain([ 0, 32 ]));
			chart.addColumn('SS');
			chart.render();
		});
	};

	return { 
		initModule: initModule,
		showStatusData: showStatusData,
	};

}());
