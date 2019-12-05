package uk.co.objectconnexions.expressiveobjects.applib.user;

/**
 * Defines a class that can manage the logging in of users.
 *
 */
public interface Login {

    /**
     * Determine if a user, given the specified name, exists and is giving a valid
     * password, and so should be able to use the application. If authenticated then
     * return User object describing the user.
     * 
     * <p>
     * Using this allow users to managed from within the domain model rather than by
     * the framework itself.
     */
    public User login(String username, String password);

}
