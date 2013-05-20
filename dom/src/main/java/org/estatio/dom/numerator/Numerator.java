package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;

@javax.jdo.annotations.PersistenceCapable( /*extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    }*/
        /* serializeRead = "true" */
        )
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "NUMERATOR_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class Numerator extends EstatioTransactionalObject {

    // {{ Type (property)
    private NumeratorType type;

    @MemberOrder(sequence = "1")
    public NumeratorType getType() {
        return type;
    }

    public void setType(final NumeratorType type) {
        this.type = type;
    }

    // }}

    // {{ Description (property)
    private String description;

    @Title
    @MemberOrder(sequence = "1")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ LastValue (property)
    @javax.jdo.annotations.Persistent
    private BigInteger lastIncrement;

    @MemberOrder(sequence = "1")
    public BigInteger getLastIncrement() {
        return lastIncrement;
    }

    public void setLastIncrement(final BigInteger lastIncrement) {
        this.lastIncrement = lastIncrement;
    }

    // }}

    @Hidden
    public BigInteger increment() {
        BigInteger last = getLastIncrement();
        if (last == null) {
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }
}
