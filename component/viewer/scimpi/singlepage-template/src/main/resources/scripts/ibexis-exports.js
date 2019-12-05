/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.exports = (function() {
	
	var l10n = ibexis.localization;

	var initModule = function($container) {
		ibexis.shell.registerContentProcessor('export', function (content, container) {
			console.log("exporting data");

			var params = {};
			params['user']  =content.data('user');
			params['station'] =content.data('station');
			params['device'] =content.data('device');
			params['from'] = content.data('from');
			params['to'] = content.data('to');
			params['type'] = content.data('type');
			params['format'] = content.data('format');
			params['processor'] = content.data('processor');
			//	params['parameters'] = '';

			var rootUrl = window.location.protocol + "//" + window.location.host + "/";
			var url = 'data-export.app?' + $.param(params);
			
			var newWindow = window.open(url, 'new_window');
			if (!newWindow) {
				// 	This came up as a popup and so was blocked
				ibexis.shell.feedback('Ready to export data');
				ibexis.shell.feedback('Download was blocked by browser. Download <a id="download123" href="' + url + '">' +
					params['format']  + ' file</a> manually (or right-click to save with another file name)');
			} else {
					ibexis.shell.feedback('Data exported to file');

			}

			return false;

		});
	};

	return {
		initModule : initModule,
	}

}());
