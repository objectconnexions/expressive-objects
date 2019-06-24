package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractDomainObject;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Disabled;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Programmatic;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.RegEx;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.TypicalLength;
import uk.co.objectconnexions.expressiveobjects.applib.security.RoleMemento;
import uk.co.objectconnexions.expressiveobjects.applib.util.TitleBuffer;
import uk.co.objectconnexions.expressiveobjects.applib.value.Date;
import uk.co.objectconnexions.expressiveobjects.applib.value.Password;

@ObjectType("USER")
@Named("User")
public class BasicUser extends AbstractDomainObject implements User, Localized {
    
    public String title() {
        TitleBuffer title = new TitleBuffer();
        title.append(getFirstName());
        title.append(lastName);
        return title.toString();
    }

    public void created() {
        setLanguageCode("en-gb");
        setTimeZone("GMT");
        setDateCreated(new Date());
    }
    
    /*
    public void loaded() {
        if (getRole() == null) {
            setRole(Role.defaultRole());
        }
    }
    */
    
    // {{ DateCreated
    private Date dateCreated;

    @MemberOrder(sequence = "3.1")
    @Disabled
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    // }}

    // {{ FirstName
    private String firstName;

    @MemberOrder(sequence = "1.0")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
    // }}

    // {{ LastName
    private String lastName;

    @MemberOrder(sequence = "1.1")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
    // }}
    
    public String getFullName() {
        return new TitleBuffer().append(getLastName()).append(",", firstName).toString();
    }

    // {{ Email
    private String email;

    @MemberOrder(sequence = "1.2")
    @RegEx(validation="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", caseSensitive=false)
    @TypicalLength(40)
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
    // }}

    // {{ LanguageCode
    private String languageCode;

    @Disabled
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
    }
    // }}

    // {{ Language
    @MemberOrder(sequence = "1.5")
    // @NotPersisted   // TODO this is disabled as not-persisted fields are not treated properly yet,
    @TypicalLength(40)
    public String getLanguage() {
        String code = getLanguageCode();
        return code == null ? "" : LocalizationUtils.languageName(code);
    }

    public void setLanguage(final String language) {
    }
    
    public void modifyLanguage(final String language) {
        String codeForLanguage = LocalizationUtils.codeForLanguage(language);
        if (codeForLanguage != null) {
            setLanguageCode(codeForLanguage);
        }
    }
    
    public List<String> choicesLanguage() {
        return LocalizationUtils.languages();
    }
    // }}
  
    public Locale forLocale() {
        return LocalizationUtils.locale(getLanguageCode());
    }
    
    // {{ TimeZone
    private String timeZone;

    @MemberOrder(sequence = "1.6")
    @TypicalLength(40)
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }
    
    public List<String> choicesTimeZone() {
        List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
        Collections.sort(timezones);
        return timezones;
    }
    // }}
    
    public TimeZone forTimeZone() {
        return LocalizationUtils.timeZone(getTimeZone());
    }
    
    // {{ Username
    private String username;

    @MemberOrder(sequence = "2.1")
    public String getUsername() {
        // TODO remove this block once users updated
        if (username == null) {
            return email;
        }
        return username;
    }

    public void setUsername(final String email) {
        this.username = email;
    }
    // }}
    
    // {{ Password
    private Password password;

    @MemberOrder(sequence = "2.2")
    @Disabled
    public Password getPassword() {
        return password;
    }

    public void setPassword(final Password password) {
        this.password = password;
    }
    // }}


    // {{ new Role
    @MemberOrder(sequence = "2.3")
    public String getServiceRole() {
        return getRole() + "/" + getUsername();
    }

    
    // {{ Role
    private Role role;

    @MemberOrder(sequence = "2.3")
    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
    
    public List<Role> choicesRole() {
        ArrayList<Role> list = new ArrayList<Role>();
        list.addAll(role.getAssignableRoles());
        /*
        if (userHasRole(Role.MANAGER)) {
            list.add(Role.READER);
            list.add(Role.CONTROLLER);
            list.add(Role.OPERATOR);
            list.add(Role.MANAGER);

            boolean isUserAdmin = userHasRole(Role.ADMIN);
            boolean isUserSysadmin = userHasRole(Role.SYSADMIN);
            if (getRole() == Role.ADMIN || getRole() == Role.SYSADMIN || isUserAdmin || isUserSysadmin) {
                list.add(Role.ADMIN);
            }
            if (getRole() == Role.SYSADMIN || isUserSysadmin) {
                list.add(Role.SYSADMIN);
            }
        }
        */
        return list;
    }
    
    public String disableRole() {
        return !role.canAssignRoles() ? "Can't assign roles to users" : null;
        /*
        boolean isUserAdmin = userHasRole(Role.ADMIN);
        boolean isUserSysadmin = userHasRole(Role.ADMIN);
        if (getRole() == Role.ADMIN) {
            return !isUserSysadmin && !isUserAdmin ? "Can't change admin role" : null;
        } else if (getRole() == Role.SYSADMIN) {
            return !isUserSysadmin ? "Can't change sysadmin role" : null;
        } else {
            return null;
        }
        */
    }
    
    @Programmatic
    public boolean userHasRole(Role role) {
        boolean hasRole = false;
        for(RoleMemento userRole : getUser().getRoles()) {
            if (userRole.getName().equals(role.getName())) {
                hasRole = true;
                break;
            }
        }
        return hasRole;
    }
    
      
    public String getRolesNames() {
        return getRole().getRoleNames();
    }
    // }}

    
    // {{ initialize password
    public void initializePassword(
            @Named("New Password") @TypicalLength(20) Password newPassword,
            @Named("Confirm Password") @TypicalLength(20) Password confirm) {
        setPassword(PasswordHash.createPassword(newPassword.getPassword()));
        informUser("Password has been successfully changed");
    }

    public String validateInitializePassword(
            Password newPassword,
            Password confirm) {
        boolean samePassword = newPassword.equals(confirm);
        return samePassword ? null : "Passwords do not match";
    }
    
    public boolean hideInitializePassword(Password newPassword, Password confirm) {
        return password != null;
    }
    // }}
    
    // {{ change password
    public void changePassword(
            @Named("Current Password") @TypicalLength(20) Password oldPassword,
            @Named("New Password") @TypicalLength(20) Password newPassword,
            @Named("Confirm Password") @TypicalLength(20) Password confirm) {
        setPassword(PasswordHash.createPassword(newPassword.getPassword()));
        informUser("Password has been successfully changed");
    }

    public String validateChangePassword(
            Password oldPassword,
            Password newPassword,
            Password confirm) {
        if (!PasswordHash.check(password, oldPassword)) {
            return "Old password not valid"; 
        }
        boolean samePassword = newPassword.equals(confirm);
        return samePassword ? null : "Passwords do not match";
    }
    
    public boolean hideChangePassword(Password newPassword, Password confirm) {
        return password == null;
    }
    // }}


    @Override
    public String toString() {
            return  "User" + "[username=" + username + ",firstName=" + firstName + ",lastName=" + lastName + ",timeZone=" + timeZone + "]";
    }
}
