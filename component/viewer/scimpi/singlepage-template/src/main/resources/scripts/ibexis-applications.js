/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, ibexis */

ibexis.applications = (function() {
	
	var l10n = ibexis.localization;

	var descriptors = {};

	var initModule = function($container) {
		ibexis.shell.registerSegmentProcessor('applications', function (segment, descriptor, showContext) {
			segment.attr('data-context', 'applications')
			createTabs(segment, descriptor);
		});		
	};
	
	var createTabs = function(segment, descriptor) {
		segment.append('<pre>' + descriptor + '</pre>');
	}
	
	return {
		initModule : initModule,
		createTabs : createTabs,
	}

}());
