package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
})*/
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Bounded
public class State extends Geography implements Comparable<State> {

    private Country country;

    @MemberOrder(sequence = "10")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    
    // {{ Comparable impl
    @Override
    public int compareTo(State o) {
        int result = getCountry().compareTo(o.getCountry());
        if (result == 0) {
            result = getName().compareTo(o.getName());
        }
        return result;
    }
    // }}

}
