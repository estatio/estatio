package org.estatio.dom.currency;

import javax.jdo.annotations.Extension;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
})*/
@Bounded
@Immutable
public class Currency extends EstatioRefDataObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }    

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String despription) {
        this.description = despription;
    }
    // }}

}
