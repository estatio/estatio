package org.estatio.dom.geography;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioRefDataObject;

@PersistenceCapable
@Bounded
@Immutable
public class Country extends EstatioRefDataObject {

    // {{ Reference (attribute)
    private String reference;

    @Disabled
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

    // {{ Alpha2Code (property)
    private String alpha2Code;

    @Title
    @MemberOrder(sequence = "1")
    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(final String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }
    // }}

}
