package org.estatio.dom.currency;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
@Bounded
public class Currency extends AbstractDomainObject {

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
    private String despription;

    @MemberOrder(sequence = "1")
    public String getDescription() {
        return despription;
    }

    public void setDescription(final String despription) {
        this.despription = despription;
    }
    // }}

}
