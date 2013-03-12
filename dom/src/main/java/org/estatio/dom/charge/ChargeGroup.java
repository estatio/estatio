package org.estatio.dom.charge;

import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.EstatioRefDataObject;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
@Immutable
public class ChargeGroup extends EstatioRefDataObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    // }}

}
