package org.estatio.agreement;

public enum AgreementRoleType {

    TENANT("Tenant"), 
    LANDLORD("Landlord"),
    MANAGER("Manager");

    private final String title;

    private AgreementRoleType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}
