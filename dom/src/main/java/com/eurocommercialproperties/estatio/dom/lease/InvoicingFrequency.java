package com.eurocommercialproperties.estatio.dom.lease;

public enum InvoicingFrequency {

    WEEKLY("Weekly","RRULE:FREQ=WEEKLY;INTERVAL=1"),
    MONTHLY("Weekly","RRULE:FREQ=MONTHLY;INTERVAL=1"), 
    QUARTERLY("Weekly","RRULE:FREQ=QUARTERLY;INTERVAL=3"), 
    SEMI_YEARLY("Semi-yearly","RRULE:FREQ=MONTHLY;INTERVAL=6"),
    YEARLY("Yearly","RRULE:FREQ=YEARLY;INTERVAL=1");
    
    private String title;
    
    public String title(){
        return title;
    }
    
    private String rrule;
    
    public String rrule() {
        return rrule;
    }
    
    private InvoicingFrequency(String title, String rrule){
        this.rrule = rrule;
        this.title = title;
    }

}
