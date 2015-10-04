package org.estatio.dom;

import java.util.List;

import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;

public enum EstatioUserRole {

    USER_ROLE("user"),
    ADMIN_ROLE("admin");

    private String suffix;

    public String getSuffix() {
        return suffix;
    }

    EstatioUserRole(final String suffix) {
        this.suffix = suffix;
    }

    public boolean isAppliccableTo(UserMemento user) {
        for (RoleMemento role : user.getRoles()) {
            if (role.getName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
