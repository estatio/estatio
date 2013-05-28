package org.estatio.dom.charge;

import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Ordering;

import org.estatio.dom.EstatioRefDataObject;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@Immutable
@Bounded
public class ChargeGroup extends EstatioRefDataObject implements Comparable<ChargeGroup> {

    private String reference;

    @MemberOrder(sequence = "1")
    @Title(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    private String description;

    @Title(sequence = "2", prepend = "-")
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @javax.jdo.annotations.Persistent(mappedBy = "group")
    private SortedSet<Charge> charges = new TreeSet<Charge>();

    @MemberOrder(sequence = "1")
    public SortedSet<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final SortedSet<Charge> charges) {
        this.charges = charges;
    }

    @Override
    public int compareTo(ChargeGroup other) {
        return ORDERING_BY_CODE.compare(this, other);
    }

    public final static Ordering<ChargeGroup> ORDERING_BY_CODE = new Ordering<ChargeGroup>() {
        public int compare(ChargeGroup p, ChargeGroup q) {
            return Ordering.<String> natural().nullsFirst().compare(p.getReference(), q.getReference());
        }
    };

}
