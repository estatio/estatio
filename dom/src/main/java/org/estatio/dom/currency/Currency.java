package org.estatio.dom.currency;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.EstatioRefDataObject;

@PersistenceCapable
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject {

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

    public void setDescription(final String despription) {
        this.description = despription;
    }
    // }}

}
