package org.estatio.dom.currency;

import com.google.common.collect.Ordering;

import org.estatio.dom.EstatioRefDataObject;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject implements Comparable<Currency> {

    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    private String description;

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String despription) {
        this.description = despription;
    }

    @Override
    public int compareTo(Currency o) {
        return ORDERING_BY_REFERENCE.compare(this, o);
    }

    public final static Ordering<Currency> ORDERING_BY_REFERENCE = new Ordering<Currency>() {
        public int compare(Currency p, Currency q) {
            return Ordering.<String>natural().nullsFirst().compare(p.getReference(), q.getReference());
        }
    };

}
