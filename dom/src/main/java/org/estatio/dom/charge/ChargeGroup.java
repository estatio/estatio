package org.estatio.dom.charge;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Extension;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
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
    @javax.jdo.annotations.Persistent(mappedBy = "group")
    private SortedSet<Charge> charges = new TreeSet<Charge>();

    @MemberOrder(sequence = "1")
    public SortedSet<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final SortedSet<Charge> charges) {
        this.charges = charges;
    }
    // }}

}
