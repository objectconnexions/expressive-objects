package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;

/**
 * 
 */
public interface Localized {
    
    String getLanguageCode();
    
    void setLanguageCode(String languageCode);

    void setTimeZone(String timeZone);
    
    @Programmatic
    Locale forLocale();
    
    @Programmatic
    TimeZone forTimeZone();
    
//    TimeZone forTimeZone();
//    Locale forLocale();
}

