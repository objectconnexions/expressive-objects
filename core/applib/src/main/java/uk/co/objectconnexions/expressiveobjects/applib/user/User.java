package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.applib.value.Password;


public interface User {

    TimeZone forTimeZone();

    Locale forLocale();
    
    String getEmail();
    
    void setEmail(String email);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);
    
    String getFullName();

    void setLanguageCode(String languageCode);
    
    Password getPassword();
    
    void setPassword(Password createPassword);

    void setTimeZone(String timeZone);
    
    String getUsername();

    void setUsername(String email);

    String getLanguageCode();

    String title();




}

