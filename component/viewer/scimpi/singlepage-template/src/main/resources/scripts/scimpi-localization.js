/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, scimpi */

scimpi.localization = (function() {

	var userData;
	var init = [];
	
	var initModule = function($container) {
	};
	
	var prepare = function(fn) {
		init.push(fn);
	}
	
	var loadUserData = function() {
		$.getJSON('user-data.app')
		.done(function(data) {
			userData = data;
			for (var i = 0; i < init.length; i++) {
				init[i]();
			}
		}).fail(function() {
			console.error('error loading user data ' + this);
		});
		
	};

	var isReady = function() {
		return userData != undefined;
	}

	var getDatePattern = function() {
		return userData.datePattern;
	};
	
	var getDateTimePattern = function() {
		return userData.dateTimePattern;
	};
	
	var getTimePattern = function() {
		return userData.timePattern;
	};

	var getI18n = function() {
		return userData.i18n;
	}

	var getLocale = function() {
		return userData.locale;
	}

	var getLanguage = function() {
		return userData.lang;
	}

	var getTimeZone = function() {
		return userData.timeZone;
	}

	var getTimeOffset = function() {
		if (userData) {
			return userData.offset;
		}
	}

	
	
	var formatInteger = function(number) {
		if (number != undefined) {
			return Math.trunc(number).toLocaleString(userData.locale);
		} else {
			return '';
		}
	}
		
	var formatFloat = function(number) {
		if (number != undefined) {
			return number.toLocaleString(userData.locale, {minimumFractionDigits: 1, maximumFractionDigits: 1} );
		} else {
			return '';
		}

	}
	
	var formatTime = function(date) {
		if (date) {
			return date.tz(userData.timeZone).format(userData.timePattern);
		} else {
			return '';
		}
	}
	
	var formatLongTime = function(date) {
		if (date) {
			return date.tz(userData.timeZone).format(userData.longTimePattern);
		} else {
			return '';
		}
	}
	
	var formatDate = function(date) {
		if (date) {
			return date.tz(userData.timeZone).format(userData.datePattern);
		} else {
			return '';
		}
	}
	
	var formatDateTime = function(date) {
		if (date) {
			return date.tz(userData.timeZone).format(userData.dateTimePattern);
		} else {
			return '';
		}
	}

	var formatLongDateTime = function(date) {
		if (date) {
			return date.tz(userData.timeZone).format(userData.longDateTimePattern)
		} else {
			return '';
		}	
	}
	
	
	var localDate = function(date) {
		if (!date) {
			date = moment().utc();
		}
		return date.tz(userData.timeZone);
	}
	
	
	var debug = function() {
		var display = JSON.stringify(userData, null, 4);
		var pre =  $('<pre>');
		pre.append(display);
		return pre;
	}
	
	return {
		initModule : initModule,
		loadUserData : loadUserData,
		isReady : isReady,
		prepare : prepare,
		
		timePattern : getTimePattern,
		datePattern : getDatePattern,
		dateTimePattern : getDateTimePattern,
		
		i18n : getI18n,
		locale : getLocale,
		language : getLanguage,
		timeZone : getTimeZone,
		timeOffset : getTimeOffset,

		formatInteger : formatInteger,
		formatFloat : formatFloat,
		formatTime : formatTime,
		formatLongTime : formatLongTime,
		formatDate : formatDate,
		formatDateTime : formatDateTime,
		formatLongDateTime : formatLongDateTime,
		
		localDate : localDate,
		debug : debug,
	};
}());