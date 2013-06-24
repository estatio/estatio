package org.estatio.dom.currency;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(
        name = "findByReference", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.currency.Currency " +
        		"WHERE reference.matches(:reference)")
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject<Currency> implements ComparableByReference<Currency> {

    public Currency() {
        super("reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    // //////////////////////////////////////

    private String description;

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String despription) {
        this.description = despription;
    }

}
