package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;


public class DateFormatString extends AbstractElementProcessor {

    public String getName() {
        return "date-format";
    }

    public void process(Request request) {
        RequestContext context = request.getContext();
        String localeCode = (String) context.getVariable("user-language");
        Locale locale = localeCode == null ? Locale.UK : LocalizationUtils.locale(localeCode);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        dateFormat.getNumberFormat();
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        String timePattern = ((SimpleDateFormat) timeFormat).toPattern();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        String longDatePattern = ((SimpleDateFormat) dateTimeFormat).toPattern();
        
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
        DateFormatSymbols dateFormatSymbols = simpleDateFormat.getDateFormatSymbols();
        String shortDatePattern = simpleDateFormat.toPattern();
        
        StringBuffer i18n = new StringBuffer();
        i18n.append("{\n");
        i18n.append(" \"previousMonth\": \"Previous Month\",\n");
        i18n.append(" \"nextMonth\": \"Next Month\", \n");
        i18n.append(" \"months\": [");
        String[] months = dateFormatSymbols.getMonths();
        for (int i = 0; i < months.length - 1; i++) {
            if (i > 0) {
                i18n.append(", ");            
            }
            i18n.append("\"" + months[i] + "\"");            
        }
        i18n.append("],\n");

        i18n.append(" \"monthsShort\": [");
        months = dateFormatSymbols.getShortMonths();
        for (int i = 0; i < months.length - 1; i++) {
            if (i > 0) {
                i18n.append(", ");            
            }
            i18n.append("\"" + months[i] + "\"");            
        }
        i18n.append("],\n");

        i18n.append(" \"weekdays\": [");
        String[] days = dateFormatSymbols.getWeekdays();
        for (int i = 1; i < days.length; i++) {
            if (i > 1) {
                i18n.append(", ");            
            }
            i18n.append("\"" + days[i] + "\"");            
        }
        i18n.append("],\n");
        
        i18n.append(" \"weekdaysShort\": [");
       days = dateFormatSymbols.getShortWeekdays();
        for (int i = 1; i < days.length; i++) {
            if (i > 1) {
                i18n.append(", ");            
            }
            i18n.append("\"" + days[i] + "\"");            
        }
        i18n.append("]\n");
        i18n.append("}");
        
        
        longDatePattern = convertPattern(longDatePattern);
        shortDatePattern = convertPattern(shortDatePattern);
        request.appendHtml("<div id=\"date-format\" data-lang=\"" + locale.getLanguage() + "\"  data-date-pattern=\"" + shortDatePattern
                + "\"  data-time-pattern=\"" + timePattern + "\"  data-date-time-pattern=\"" + longDatePattern + "\" data-i18n='" + i18n + "' ></div>");
     
        // debugging use only
        /*
        context.addVariable("__date-format-pattern", "Moment.js: '" + pickerPattern + "' (Java: '" + simpleDateFormat.toPattern() + "')", Scope.REQUEST);
        context.addVariable("__date-format-i18n", i18n, Scope.REQUEST);
        */
        // request.appendHtml("<p>" + simpleDateFormat.toPattern() + "<br/>" + pattern + "<br/>" + i18n + "</p>");
    }

    private String convertPattern(String pattern) {
        pattern = pattern.replace("yyyy", "YYYY");
        pattern = pattern.replace("yy", "YY");
        pattern = pattern.replace("dd", "DD");
        pattern = pattern.replace("d", "D");
        pattern = pattern.replace("EEEE", "dddd");
        pattern = pattern.replace("EEE", "ddd");
        pattern = pattern.replace("E", "ddd");
        return pattern;
    }

}

