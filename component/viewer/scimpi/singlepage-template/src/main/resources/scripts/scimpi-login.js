/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
*/
/*global $, scimpi */

scimpi.login = (function () {
	var showFirst;
	
	initModule = function ( $container ) {
		$('#scimpi-dialog').on('submit', '.scimpi-login form', checkUser);
	};

	var showLogin = function(user, initialSection) {
		// TODO rename these both to reflect a callback
		if (initialSection) {
			showFirst = initialSection;
		}
		if (user) {
			$('#scimpi').removeClass('disable');
			$('#scimpi').removeClass('in-login');
			$('#scimpi').addClass('logged-in');
			
		  // spa.shell.hideDialog(); // TODO create method to hide dialog
			// $('.scimpi-login').closest('#scimpi-dialog').addClass('hide');
			showFirst();
		} else {
			showLoginPanel();
		}
	};

	var showLoginPanel = function(timedOut) {
		$('#scimpi').addClass('in-login');
		scimpi.dialog.openResource('_login.shtml'); //, null, function() {
			/*if (timedOut) {

				$('#scimpi-dialog h1').after('<p class="errors">For security reasons you have been disconnected. Please log-in again to continue</p>');
			}
		});
		*/
	}

  var logOut = function() {
	  var parameters = {view: '_logged-out.shtml'};
	  $.ajax({url: 'logout.app', type: 'GET', data: parameters, dataType: 'html', success: showLoginPanel});
	  scimpi.shell.clear();
	  scimpi.alarms.stopNotifications();
	  $('#scimpi').removeClass('logged-in');
	  $('#scimpi').addClass('in-login');
  }
  
  var hideLogin = function() {
	  scimpi.dialog.close();
	  $('#scimpi').removeClass('in-login');
  }

  var timedOut = function() {
	  scimpi.shell.clear();
//	  $('#scimpi-content').empty();
	  scimpi.alarms.stopNotifications();
	  $('#scimpi').removeClass('logged-in').addClass('in-login');
	  scimpi.dialog.openResource('_login.shtml', null, function() {
		  $('#scimpi-dialog h1').after('<p class="errors">For security reasons you have been disconnected. Please log-in again to continue</p>');
	  });
  }
	  
  var checkUser = function() {
	  scimpi.dialog.showBusy();

		var form = $(this);
		var formData = form.serialize();
		form.find('input,textarea,select,button').attr('disabled', true);
		form.find('div.errors, span.error').remove();

		var action = form.attr('action');
		var container = $('.scimpi-login');
		$.ajax({
			url : action,
			type : 'POST',
			data : formData,
			dataType : 'html'
		}).done(function(data) {
			var content = $(data);
			if (content.filter('.scimpi-login').length > 0) {
				// still logging in
				container.empty();
				content = content.appendTo(container);
				content.find('input[name="username"]').focus();
			} else {
				showFirst();
				hideLogin();
				$('#scimpi').addClass('logged-in');
			}
		}).fail(function(data) {
			container.html(data.responseText)
		}).complete(function(data) {
			scimpi.dialog.clearBusy();
		});
		return false;
  };  
  
  return {
	  initModule: initModule,
	  timedOut : timedOut,
	  showLogin: showLogin,
	  checkUser: checkUser,
	  logOut: logOut
  };
}());