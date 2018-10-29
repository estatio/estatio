package org.estatio.module.base.dom;

import java.util.List;

import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EstatioRole {

    USER("estatio-user"),
    ADMINISTRATOR("estatio-admin"),
    SUPERUSER("estatio-superuser"),
    CAPEX_USER("estatio-capex-user");

    @Getter
    private String roleName;

    public boolean isApplicableFor(UserMemento userMemento) {
        final List<RoleMemento> roles = userMemento.getRoles();
        for (RoleMemento roleMemento : roles) {
            if (roleMemento.getName().endsWith(roleName)) {
                return true;
            }
        }
        return false;
    }

}
