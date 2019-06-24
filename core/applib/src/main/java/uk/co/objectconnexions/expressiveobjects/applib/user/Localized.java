package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;

public interface Localized {

    @Programmatic
    Locale forLocale();
    
    @Programmatic
    TimeZone forTimeZone();
}

