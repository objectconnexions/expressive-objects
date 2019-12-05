package uk.co.objectconnexions.expressiveobjects.applib.user;

import uk.co.objectconnexions.expressiveobjects.applib.value.Password;


/**
 * Implementing class represents a User of the system. Users can log in to the
 * system to interact with it.
 *
 */
public interface User {

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);
    
    String getFullName();
    
    String getEmail();
    
    void setEmail(String email);
    
    String getUsername();

    void setUsername(String email);
    
    Password getPassword();
    
    void setPassword(Password createPassword);

    String title();




}

