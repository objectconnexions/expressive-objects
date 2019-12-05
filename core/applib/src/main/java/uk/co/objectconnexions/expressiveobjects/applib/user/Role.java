package uk.co.objectconnexions.expressiveobjects.applib.user;

import java.util.Collection;

public interface Role {

    String getId();
    
    String getName();

    Collection<? extends Role> getAssignableRoles();

    boolean canAssignRoles();
    
    String getRoleNames();
}
