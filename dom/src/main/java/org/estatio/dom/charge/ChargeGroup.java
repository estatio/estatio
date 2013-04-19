package org.estatio.dom.charge;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioRefDataObject;

@PersistenceCapable
@Immutable
@Bounded
public class ChargeGroup extends EstatioRefDataObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    @Title(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Description (property)
    private String description;

    @Title(sequence = "2", prepend = "-")
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ Charges (Collection)
    @Persistent(mappedBy = "group")
    private Set<Charge> charges = new LinkedHashSet<Charge>();

    @MemberOrder(sequence = "1")
    public Set<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final Set<Charge> charges) {
        this.charges = charges;
    }
    // }}

}
