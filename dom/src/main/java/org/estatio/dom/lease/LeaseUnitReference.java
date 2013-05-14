package org.estatio.dom.lease;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.EstatioRefDataObject;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
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
