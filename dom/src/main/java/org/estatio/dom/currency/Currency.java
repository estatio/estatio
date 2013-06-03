package org.estatio.dom.currency;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithReference;

@javax.jdo.annotations.PersistenceCapable
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject implements WithReference<Currency> {

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

}
