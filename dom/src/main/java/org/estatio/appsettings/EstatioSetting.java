package org.estatio.appsettings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

public class EstatioSetting {


    // {{ LastDueDate (property)
    private LocalDate lastDueDate;

    @MemberOrder(sequence = "1")
    public LocalDate getLastDueDate() {
        return lastDueDate;
    }

    public void setLastDueDate(final LocalDate lastDueDate) {
        this.lastDueDate = lastDueDate;
    }
    // }}


    
    

}
