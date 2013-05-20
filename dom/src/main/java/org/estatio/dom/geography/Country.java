package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
    })*/
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Bounded
public class Country extends Geography implements Comparable<Country> {

    private String alpha2Code;

    @Title
    @MemberOrder(sequence = "1")
    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(final String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    
    // {{ Comparable impl
    @Override
    public int compareTo(Country o) {
        return getName().compareTo(o.getName());
    }
    // }}

}
