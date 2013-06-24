package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(name = "findByCountry", language = "JDOQL", value = "SELECT FROM org.estatio.dom.geography.State WHERE country == :country"),
    @javax.jdo.annotations.Query(name = "findByReference", language = "JDOQL", value = "SELECT FROM org.estatio.dom.geography.State WHERE reference == :reference") 
})
@Bounded
public class State extends Geography {

    private Country country;

    @MemberOrder(sequence = "10")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

}
