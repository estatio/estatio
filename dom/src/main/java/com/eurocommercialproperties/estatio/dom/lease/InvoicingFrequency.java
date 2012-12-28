package com.eurocommercialproperties.estatio.dom.lease;

public enum InvoicingFrequency {

    WEEKLY_IN_ADVANCE("Weekly","RRULE:FREQ=WEEKLY;INTERVAL=1", true),
    WEEKLY_IN_ARREARS("Weekly","RRULE:FREQ=WEEKLY;INTERVAL=1", false),
    MONTHLY_IN_ADVANCE("Monthly","RRULE:FREQ=MONTHLY;INTERVAL=1", true), 
    MONTHLY_IN_ARREARS("Monthly","RRULE:FREQ=MONTHLY;INTERVAL=1", false), 
    QUARTERLY_IN_ADVANCE("Quarterly","RRULE:FREQ=MONTHLY;INTERVAL=3", true), 
    QUARTERLY_IN_ADVANCE_ZARA("Quarterly","RRULE:FREQ=MONTHLY;INTERVAL=3;BYMONTH=2,5,8,11", true), 
    QUARTERLY_IN_ARREARS("Quarterly","RRULE:FREQ=MONTHLY;INTERVAL=3", false), 
    SEMI_YEARLY_IN_ADVANCE("Semi-yearly","RRULE:FREQ=MONTHLY;INTERVAL=6", true),
    SEMI_YEARLY_IN_ARREARS("Semi-yearly","RRULE:FREQ=MONTHLY;INTERVAL=6", false),
    YEARLY_IN_ADVANCE("Yearly","RRULE:FREQ=YEARLY;INTERVAL=1", true),
    YEARLY_IN_ARREARS("Yearly","RRULE:FREQ=YEARLY;INTERVAL=1", false);

    private InvoicingFrequency(String title, String rrule, Boolean inAdvance){
        this.rrule = rrule;
        this.title = title;
    }
    
    private String title;
    
    public String title(){
        return title;
    }
    
    private String rrule;
    
    public String rrule() {
        return rrule;
    }
    
    private Boolean inAdvance;
    
    public Boolean inAdvance() {
        return inAdvance;
    }
    
}
