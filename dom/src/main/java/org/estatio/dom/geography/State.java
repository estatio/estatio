package org.estatio.dom.geography;

import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.EstatioRefDataObject;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Bounded
@Immutable
public class State extends EstatioRefDataObject {

    // {{ Reference (attribute)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    // }}

    // {{ Name (attribute, title)
    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Country (attribute)
    private Country country;

    @MemberOrder(sequence = "10")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }
    // }}

}
