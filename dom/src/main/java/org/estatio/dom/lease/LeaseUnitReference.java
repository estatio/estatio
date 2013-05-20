package org.estatio.dom.lease;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
})*/
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Bounded
public abstract class LeaseUnitReference extends EstatioRefDataObject {

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

    private LeaseUnitReferenceType type;

    @MemberOrder(sequence = "3")
    @Hidden
    public LeaseUnitReferenceType getType() {
        return type;
    }

    public void setType(final LeaseUnitReferenceType type) {
        this.type = type;
    }

}
