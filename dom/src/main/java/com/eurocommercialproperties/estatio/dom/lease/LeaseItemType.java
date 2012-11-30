package com.eurocommercialproperties.estatio.dom.lease;


public enum LeaseItemType {

    RENT("Rent", IndexableLeaseTerm.class), 
    DISCOUNT("Discount", LeaseTerm.class), 
    SERVICE_CHARGE("Service Charge", LeaseTerm.class), 
    OTHER("Other", LeaseTerm.class);

    private final String title;
    private final Class<?> clss;

    private LeaseItemType(String title, Class<?> clss) {
        this.title = title;
        this.clss = clss;
    }

    public String title() {
        return title;
    }
        
    public Class<?> getLeaseTermCLass() {
        return clss;
    }
    
}
