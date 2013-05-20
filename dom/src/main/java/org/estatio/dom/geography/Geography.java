package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
})*/
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public abstract class Geography extends EstatioRefDataObject {

    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    
    
    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
