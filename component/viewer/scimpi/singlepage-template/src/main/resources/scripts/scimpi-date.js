/*jslint           browser : true,   continue : true,
  devel  : true,    indent : 2,       maxerr  : 50,
  newcap : true,     nomen : true,   plusplus : true,
  regexp : true,    sloppy : true,       vars : false,
  white  : true
 */
/*global $, scimpi */

scimpi.date = (function() {

	var l10n = scimpi.localization;
	var prepareMoment, changeSelectorDate, changeSelectorPeriod, changeOptionPeriod;
	
	var initModule = function($container) {
		$('#scimpi').on("click", "div.scimpi-date-controller button", changeSelectorDate);
		$('#scimpi').on("change", "div.scimpi-date-controller select", changeSelectorPeriod);
		$('#scimpi').on("change", "div.scimpi-date-controller input.date", changeSelectorPeriod);
		$('#scimpi').on("change", "div.scimpi-period-controller select", changeOptionPeriod);
		scimpi.localization.prepare(prepare);
	};
	
	var prepare = function() {
		prepareMoment();
	};

	
	
	
	function changeOptionPeriod() {
		var element = $(this);
		 adjustPeriod(element);
	}
	
	function adjustPeriod(element) {
		var controller = element.closest('.scimpi-period-controller');
		var viewer = controller.parent();
		var view = viewer.find('.scimpi-date-view');
		var startDate = moment.tz();
		var endDate;
		var periodField = controller.find('select');
		var period = parseInt(periodField.val());
		
		endDate = moment(startDate);
		endDate.add(period - 1, 'hours');
		if (endDate.isAfter(l10n.localDate())) {
			startDate = l10n.localDate();
			startDate.subtract(period, 'hours');
		}
		var target = viewer.find('.scimpi-date-view');
		target.trigger("periodSelected", [target, startDate, period]);
	}

	var addPeriodOptions = function(element, period, fn) {
		var options = ['Last three hours', 'Last six hours', 'Last 12 hours', 'Today', 'Since yesterday', 'Over last three days', 
			'Over last five days', 'Over the last week', 'Over the last two weeks', 
			'Over the last month', 'Over the last quarter', 'Over the last two quarters', 'Over the last year' ];
		var days = [3, 6, 12, 1 * 24, 2 * 24, 3 * 24, 5 * 24, 7 * 24, 14 * 24, 31 * 24, 90 * 24, 180 * 24, 366 * 24];
		var view = element.find('.scimpi-date-view');
		var controller = $('<div class="scimpi-period-controller"></div>');
		var select = $('<select name="selected-duration">');
		var start = l10n.localDate();
		var i;
		var isSelected = false;

		if (!period) {
			period = 2 * 24;
		}	
		controller.append('<span>Period: </span>');
		
		var option;
		option = $('<option value="' + days[0] + '">' + options[0] + '</option>');
		select.append(option);
		for (i = 1; i < options.length; i++) {
			if (!isSelected && period > 0 && period < days[i]) {
				option.attr('selected', 'selected');
				isSelected = true;
			}
			option = $('<option value="' + days[i] + '">' + options[i] + '</option>');
			select.append(option);
		}
		controller.append(select);
	 	
	 	element.prepend(controller);
		
		view.bind("periodSelected", fn);

		adjustPeriod(controller); // initialise the controlled view with the current date
		
		return view;
	};

	
	
	
	changeSelectorDate = function() {
		var button = $(this);
		var adjustment = button.data('adjustment');
		adjustDate(button, adjustment);
	};

	changeSelectorPeriod = function(evt) {
		var selector = $(this);
		adjustDate(selector, 0);
	};
	
	function adjustDate(element, adjustment) {
		var controller = element.closest('.scimpi-date-controller');
		var viewer = controller.parent();
		var view = viewer.find('.scimpi-date-view');
		var dateField = controller.find('input.date');
		var dateString = dateField.val();
		var startDate = moment.tz(dateString, l10n.datePattern(), l10n.timeZone());
		var endDate;
		var periodField = controller.find('select');
		var period = periodField.length == 0 ? 1 : parseInt(periodField.val());
		
		if (adjustment != 0) {
			startDate.add(adjustment, 'days');
			dateField.val(l10n.formatDate(startDate));
		}
		
		// TODO convert to a DataRange
		
		endDate = moment(startDate);
		endDate.add(period - 1, 'days');
		if (endDate.isAfter(l10n.localDate())) {
			endDate = l10n.localDate();
			startDate = l10n.localDate();
			startDate.startOf();
			startDate.subtract(period - 1, 'days');
			dateField.val(l10n.formatDate(startDate));
		}
		var target = viewer.find('.scimpi-date-view');
		target.trigger("dateSelected", [target, startDate, period]);
	}

	
	var addDatePeriodSelector = function(element, period, fn) {
		var options = ['1 day', '2 days', '3 days', '5 days', '1 week', '2 week', '3 weeks', '4 weeks', 
		               '1 month', '3 months', '6 months', '1 year', ];
		var days = [1, 2, 3, 5, 7, 14, 21, 28, 30, 91, 183, 366];

		var select = $('<select name="selected-duration">');
		var isSelected = false;
		var i;
		for (i = 0; i < options.length; i++) {
			if (!isSelected && period > 0 && period < days[i]) {
				option.attr('selected', 'selected');
				isSelected = true;
			}
			option = $('<option value="' + days[i] + '">' + options[i] + '</option>');
			select.append(option);
		}

		if (!period) {
			period = 1;
		}	

		addSelector(element, period, select, fn);
	}
		
	
	var addDateSelector = function(element,  fn) {
		addSelector(element, 1, null, fn);
	}
		
		
	/*
	 * Adds a date selector controller element (div.scimpi-data-controller) to the specified element. The 
	 * date specified in the date range is used to initialise the controller (date and period). The view element
	 * is returned .... 
	 */
	var addSelector = function(element, period, select, fn) {
		var view = element.find('.scimpi-date-view');
		var controller = $('<div class="scimpi-date-controller"></div>');
		var start = l10n.localDate();
		var dateField = $('<input class="date" data-max="true" type="text" name="selected-date"/>');
		
		dateField.attr('value', l10n.formatDate(start));

		console.log("Adding date selector");
		controller.append('<button id="previous-month" class="btn date-adjust" alt="Back four weeks" title="Back four weeks" data-adjustment="-28"><i class="icon-fast-backward">&lt;&lt;&lt;</i></button>');
		controller.append('<button id="previous-week" class="btn date-adjust" alt="Back a week" title="Back a week" data-adjustment="-7"><i class="icon-fast-backward">&lt;&lt;</i></button>');
		controller.append('<button id="previous-day" class="btn date-adjust" alt="Back a day" title="Back a day" data-adjustment="-1"><i class="icon-step-backward">&lt;</i></button>');
		controller.append(dateField);
		if (select) {
			controller.append(select);
		}
		controller.append('<button id="next-day" class="btn date-adjust" alt="Forward a day" title="Forward a day" data-adjustment="1">&gt;<i class="icon-step-forward"></i></button>');
		controller.append('<button id="next-week" class="btn date-adjust" alt="Forward a week" title="Forward a week" data-adjustment="7"><i class="icon-fast-forward">&gt;&gt;</i></button>');
		controller.append('<button id="next-week" class="btn date-adjust" alt="Forward four weeks" title="Forward four weeks" data-adjustment="28"><i class="icon-fast-forward">&gt;&gt;&gt;</i></button>');
	 	element.prepend(controller);
		
		view.bind("dateSelected", fn);		
		adjustDate(controller, 0); // initialise the controlled view with the current date
		
		var maxDate = new Date();
	    var picker = new Pikaday({
	        field: dateField[0],
	        format: l10n.datePattern(),
	        i18n: l10n.i18n(),
	        maxDate: maxDate,
	        onSelect: function() {
				
			}
	    });
		
		return view;
	};

	prepareMoment = function() {
		var i18n = l10n.i18n();
		//if (i18n) {
			moment.locale(l10n.language(), {
			    months : i18n.monthsShort,
			    monthsShort : i18n.monthsShort,
			    weekdays : i18n.weekdays,
			    weekdaysShort : i18n.weekdaysShort,
			    weekdaysMin : i18n.weekdaysShort
			});
			console.log(l10n.localDate().toISOString());
			console.log(l10n.localDate().format("LLL"));
		//}
	};

	var getDatePattern = function() {
		return l10n.datePattern();
	};
	
	var getDateTimePattern = function() {
		return l10n.dateTimePattern();
	};
	
	var getTimePattern = function() {
		return l10n.timePattern();
	};
	
	var getCustomTimeFormat = function() {
		return customTimeFormat;
	};
	

	/*
	 * Derive a new DateRange in the past (up until the present time) that
	 * starts the specified number of hours ago (the offset) and lasts for
	 * the specified period. If no offset is specified then 24 hours is
	 * used. Similarly, if the period is not specified then that too is
	 * deemed to be 24. Hence, if no parameters are given the DateRange
	 * covers the last 24 hours.
	 */
	var createDateRange = function(period, startDate) {
		var start;
		if (!period) {
			period = 24;
		}
		if (startDate == null) {
			var now, end;
			now = l10n.localDate().set('minute', 0).set('second', 0).set('milllisecond', 0).add(1, 'hour');
			start = moment(now);
			start.subtract(period, 'hours');

		} else {
			start = moment(startDate);
			if (period >= 24) {
				start.startOf('day');
			} else {
				start.startOf('hour');
			}
			
		}
		
		console.log(period + ' hours from ' + l10n.formatDateTime(start) + ' => ' + l10n.formatDateTime(start) + ' +' + period);
		return { 
			start: start, 
			period: period,
		}
	}
	/*
	 * Determine the offset (hours before the current time) for the specified 
	 * DateRange. This will include the amount of the User's offset so relative 
	 * requests to the server will accommodate the difference in time.
	 */
	var offset = function(dateRange) {
//		return moment().diff(dateRange.start, 'hours') + Math.ceil(l10n.timeOffset());
		return l10n.localDate().diff(dateRange.start, 'hours') + 1;
	}
	
	/*
	 * Determine the period (hours covered by the range) for the specified DateRange.
	 */
	var period = function(dateRange) {
		return dateRange.period;
	}
	
	/*
	 * Determine the date at that the end of the specified DateRange.
	 */
	var endDate = function(dateRange) {
		var endDate = moment(dateRange.start)
		endDate.add(dateRange.period, 'hours');
		return endDate;
	}
	
	var currentTime = function () {
		var offset = l10n.timeOffset();
		if (offset != undefined) {
			var d = new Date ( );
			// convert to msec add local time zone offset
			// get UTC time in msec
			var utc = d.getTime() + (d.getTimezoneOffset() * 60000);
		 
			// create new Date object for different city using supplied offset
			var currentTime = new Date(utc + (3600000 * offset));
			return moment(currentTime);
		}
	}
	  
	var parseUtcDate = function(value) {
		return moment.utc(value);
//		return moment.tz(value, 'UTC'); //.clone().tz(l10n.timeZone());		
	}
	
	
	return {
		initModule : initModule,
		timePattern : getTimePattern,
		datePattern : getDatePattern,
		dateTimePattern : getDateTimePattern,
		
		addDateSelector : addDateSelector,
		addDatePeriodSelector : addDatePeriodSelector,
		addPeriodOptions : addPeriodOptions,
		
		createDateRange : createDateRange,
		offset : offset,
		period : period,
		endDate : endDate,
		
		parseUtcDate : parseUtcDate,
		currentTime : currentTime,
		//localized : localized,
	};
}());