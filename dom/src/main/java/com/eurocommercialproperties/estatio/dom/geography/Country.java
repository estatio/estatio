package com.eurocommercialproperties.estatio.dom.geography;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(schema="geography")
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.NEW_TABLE) // table-per-type
@ObjectType("CTRY")
@Bounded
public class Country extends Geography {

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
