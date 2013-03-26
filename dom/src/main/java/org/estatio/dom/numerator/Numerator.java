package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable(/* serializeRead = "true" */)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "NUMERATOR_ID")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
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
    private BigInteger lastIncrement;

    @MemberOrder(sequence = "1")
    @Persistent
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
