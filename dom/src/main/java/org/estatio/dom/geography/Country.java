package org.estatio.dom.geography;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Query(
        name = "findByReference", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.geography.Country " +
        		"WHERE reference.matches(:reference)")
@Bounded
@Immutable
public class Country extends Geography {

    @javax.jdo.annotations.Unique
    private String alpha2Code;

    @Title
    @MemberOrder(sequence = "1")
    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(final String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

}
