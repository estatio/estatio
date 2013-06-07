package org.estatio.dom.charge;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@Immutable
@Bounded
public class ChargeGroup extends EstatioRefDataObject implements ComparableByReference<ChargeGroup> {

    private String reference;

    @MemberOrder(sequence = "1")
    @Title(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String description;

    @Title(sequence = "2", prepend = "-")
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "group")
    private SortedSet<Charge> charges = new TreeSet<Charge>();

    @MemberOrder(sequence = "1")
    public SortedSet<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final SortedSet<Charge> charges) {
        this.charges = charges;
    }

    // //////////////////////////////////////
    
    @Override
    public String toString() {
        return ToString.of(this);
    }
    
    // //////////////////////////////////////

    @Override
    public int compareTo(ChargeGroup other) {
        return ORDERING_BY_REFERENCE.compare(this, other);
    }


}
