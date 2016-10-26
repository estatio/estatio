package org.estatio.dom.roles;

import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;

public enum EstatioRole {

    USER("estatio-user", "user"),
    ADMINISTRATOR("estatio-admin", "admin");

    private String roleName;
    private String suffix;

    public String getSuffix() {
        return suffix;
    }

    EstatioRole(final String name, final String suffix) {
        this.roleName = name;
        this.suffix = suffix;
    }

    public boolean hasRoleWithSuffix(UserMemento userMemento) {
        for (RoleMemento role : userMemento.getRoles()) {
            if (role.getName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isApplicableFor(UserMemento userMemento) {
        for (RoleMemento roleMemento : userMemento.getRoles()) {
            if (roleMemento.getName().contains(roleName)) {
                return true;
            }
        }
        return false;
    }

    public String getRoleName() {
        return roleName;
    }
}
