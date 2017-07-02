package org.estatio.dom.roles;

import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;

/**
 * Used only for security...
 */
public enum EstatioRole {

    USER("estatio-user", "user"),
    ADMINISTRATOR("estatio-admin", "admin"),
    SUPERUSER("estatio-superuser", "superuser" );

    private String roleName;
    private String suffix;

    public String getSuffix() {
        return suffix;
    }

    EstatioRole(final String name, final String suffix) {
        this.roleName = name;
        this.suffix = suffix;
    }

    public boolean isApplicableFor(UserMemento userMemento) {
        for (RoleMemento roleMemento : userMemento.getRoles()) {
            if (roleMemento.getName().endsWith(roleName)) {
                return true;
            }
        }
        return false;
    }

    public String getRoleName() {
        return roleName;
    }
}
