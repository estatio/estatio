package org.estatio.services.appsettings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
public class EstatioSetting extends AbstractDomainObject {


    // {{ LastDueDate (property)
    private LocalDate epochDate;

    @MemberOrder(sequence = "1")
    public LocalDate getEpochDate() {
        return epochDate;
    }

    public void setEpochDate(final LocalDate lastDueDate) {
        this.epochDate = lastDueDate;
    }
    // }}

}
