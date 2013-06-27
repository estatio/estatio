package org.estatio.dom.currency;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithDescriptionUnique;
import org.estatio.dom.WithReferenceComparable;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(
        name = "findByReference", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.currency.Currency " +
        		"WHERE reference.matches(:reference)")
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject<Currency> implements WithReferenceComparable<Currency>, WithDescriptionUnique {

    public Currency() {
        super("reference");
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "CURRENCY_REFERENCE_UNIQUE_IDX")
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "CURRENCY_DESCRIPTION_UNIQUE_IDX")
    private String description;

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String despription) {
        this.description = despription;
    }

}
