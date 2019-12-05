/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, scimpi */

var scimpi = (function () {
	var initModule = function () {
		makeCompatible();
		
		var $container = $('#scimpi');
		scimpi.date.initModule($container);

		scimpi.shell.initModule( $container );
		scimpi.dialog.initModule( $container );
		scimpi.login.initModule( $container );

		scimpi.plain.initModule($container);
		scimpi.tabs.initModule($container);
		scimpi.split.initModule($container);
		
//		scimpi.display.initModule($container);
		
//		scimpi.configuration.initModule($container);
//		scimpi.applications.initModule($container);

		scimpi.shell.registerView('scimpi-ui-dialog', scimpi.dialog);
		scimpi.shell.registerView('scimpi-ui-tabs', scimpi.tabs);
		scimpi.shell.registerView('scimpi-ui-split', scimpi.split);
	};

	function makeCompatible() {
		// Fixes lack of startString in IE
		if (!String.prototype.startsWith) {
			String.prototype.startsWith = function(searchString, position) {
				position = position || 0;
				return this.indexOf(searchString, position) === position;
			}
		}
		
		Number.isInteger = Number.isInteger || function(value) {
			  return typeof value === 'number' && 
			    isFinite(value) && 
			    Math.floor(value) === value;
			};
	}
	
	return { initModule: initModule };
}());