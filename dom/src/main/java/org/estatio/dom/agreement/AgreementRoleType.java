package org.estatio.dom.agreement;

public enum AgreementRoleType {

    CREDITOR("Creditor"), 
    DEBTOR("Debtor"), 
    TENANT("Tenant"), 
    LANDLORD("Landlord"),
    MANAGER("Manager"),
    OWNER("Owner");

    private final String title;

    private AgreementRoleType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}
