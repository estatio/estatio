package org.estatio.appsettings;

import javax.jdo.annotations.PersistenceCapable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
public class EstatioSetting extends AbstractDomainObject {


    // {{ LastDueDate (property)
    private LocalDate lastDueDate;

    @MemberOrder(sequence = "1")
    public LocalDate getMockDate() {
        return lastDueDate;
    }

    public void setMockDate(final LocalDate lastDueDate) {
        this.lastDueDate = lastDueDate;
    }
    // }}

}
