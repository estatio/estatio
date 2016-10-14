package org.estatio.dom.roles;

import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;

public enum EstatioRole {

    USER("estatio-user"),
    ADMINISTRATOR("estatio-admin");

    private String roleName;

    EstatioRole(final String name) {
        this.roleName = name;
    }

    public boolean isApplicableFor(UserMemento userMemento) {
        for (RoleMemento roleMemento : userMemento.getRoles()) {
            if (roleMemento.getName().contains(roleName)) {
                return true;
            }
        }
        return false;
    }

}
