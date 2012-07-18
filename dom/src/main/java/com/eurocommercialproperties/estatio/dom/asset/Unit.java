package com.eurocommercialproperties.estatio.dom.asset;

import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Discriminator("UNIT")
public class Unit {

    // {{ Reference (attribute)
    private String reference;

    @Title(sequence = "1", append = ", ")
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String code) {
        this.reference = code;
    }

    // }}

    // {{ Name (attribute)
    private String name;

    @Disabled
    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Type (attribute)
    private UnitType type;

    @MemberOrder(sequence = "3")
    public UnitType getType() {
        return type;
    }

    public void setType(final UnitType type) {
        this.type = type;
    }

    public List<UnitType> choicesType() {
        return Arrays.asList(UnitType.values());
    }

    // }}

    // {{ Area (attribute)
    // REVIEW: use a BigDecimal instead?
    private Double area;

    @MemberOrder(sequence = "4")
    public Double getArea() {
        return area;
    }

    public void setArea(final Double area) {
        this.area = area;
    }
    // }}
    
    // {{ Property (attribute)
    private Property property;

    @Disabled
    @MemberOrder(sequence = "5")
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // }}

}
