package org.estatio.agreement;

import java.util.Arrays;
import java.util.List;

public enum AgreementType {

    DEFAULT("Default"),
    LEASE("Lease", AgreementRoleType.TENANT, AgreementRoleType.LANDLORD),
    MANDATE("Mandate", AgreementRoleType.OWNER, AgreementRoleType.EMPLOYEE);

    private final String title;
    private final List<AgreementRoleType> roles;

    private AgreementType(String title, AgreementRoleType... roles) {
        this.title = title;
        this.roles = Arrays.asList(roles);
    }

    public String title() {
        return title;
    }
    
    public List<AgreementRoleType> getRoles() {
        return roles;
    }

}
