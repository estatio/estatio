package org.estatio.dom.event;

public enum LeaseEventType implements Titled {

    LEASE_BRK_OPT_LNDLRD("Break Option - Landlord"), 
    LEASE_BRK_OPT_MTL("Break Option - Mutual"), 
    LEASE_BRK_OPT_TNT("Break Option - Tenant"), 
    LEASE_EVENT("Event"), 
    LEASE_MEETING("Meeting"), 
    LEASE_PROLONGATION("Prolongation"), 
    LEASE_TASK("Task");

    private String title;

    private LeaseEventType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
