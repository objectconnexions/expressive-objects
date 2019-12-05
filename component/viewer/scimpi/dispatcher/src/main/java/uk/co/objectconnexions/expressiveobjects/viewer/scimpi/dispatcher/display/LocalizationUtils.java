package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.display;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LocalizationUtils {
    
    public static final String DEFAULT_TIME_ZONE = "Europe/London";
    public static final String DEFAULT_LANGUAGE = "English, United Kingdom (en-gb)";
    

    private LocalizationUtils() {}

    public static boolean hasChanged(String version1, String version2) {
        return version2 == null && version1 != null || (version2 != null && !version2.equals(version1));
    }
    
    public static List<String> countries() {
        Locale[] locales = DateFormat.getAvailableLocales();
        List<String> list = new ArrayList<String>(locales.length);
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.length() > 0) {
                list.add(country);
            }
        }
        Collections.sort(list);
        return list;
    }

    public static List<String> languages() {
        Locale[] locales = DateFormat.getAvailableLocales();
        List<String> list = new ArrayList<String>(locales.length);
        for (Locale locale : locales) {
            list.add(localeName(locale));
        }
        Collections.sort(list);
        return list;
    }
    
    public static List<String> timeZones() {
        List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
        Collections.sort(timezones);
        return timezones;
    }
/*
    public static String format(DateTime date, Localization localization) {
        Locale locale = locale(localization.getLocale());
        TimeZone timeZone = timeZone(localization.getTimeZone());
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        format.setTimeZone(timeZone);
        return format.format(date.dateValue());
    }
*/
    
    public static TimeZone timeZone(String timeZoneEntry) {
        if (timeZoneEntry == null) {
            timeZoneEntry = "utc";
        }
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneEntry);
        return timeZone;
    }

    public static Locale locale(String localeCode) {
        if (localeCode == null) {
            localeCode = "en_gb";
        }
        String substring[] = localeCode.trim().split("-");
        Locale locale;
        switch (substring.length) {
        case 1:
            locale = new Locale(substring[0]);                    
            break;
        case 2:
            locale = new Locale(substring[0], substring[1]);                    
            break;
        case 3:
            locale = new Locale(substring[0], substring[1], substring[3]);                    
            break;
        default:
            locale = Locale.getDefault();
            break;
        }
        return locale;
    }

    public static String languageName(String languageCode) {
        Locale locale = locale(languageCode);
        return localeName(locale);
    }

    public static String codeForLanguage(String language) {
        Locale[] locales = DateFormat.getAvailableLocales();
        for (Locale locale : locales) {
            String name = localeName(locale);
            if (name.equals(language)) {
                return locale.toString().toLowerCase().replace('_', '-');
            }
        }
        return null;
    }

    public static String localeName(Locale locale) {
        String language = locale.getDisplayLanguage();
        String country = locale.getDisplayCountry().length() == 0 ? "" :  ", " + (locale.getDisplayCountry());
        return language + country + " (" +  locale.toString().toLowerCase().replace('_', '-') + ")";
    }

    
}
