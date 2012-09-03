package com.eurocommercialproperties.estatio.dom.geography;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
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
