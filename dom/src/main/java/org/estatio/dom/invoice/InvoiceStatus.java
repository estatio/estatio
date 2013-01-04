package org.estatio.dom.invoice;

public enum InvoiceStatus {
    
    NEW("New"),
    APPROVED("Approved"),
    INVOICED("Invoiced");
    
    private final String title;

    private InvoiceStatus(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

    

}
