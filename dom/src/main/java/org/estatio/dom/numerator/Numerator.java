package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.EstatioTransactionalObject;

//@PersistenceCapable(serializeRead = "true")
@PersistenceCapable
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "NUMERATOR_ID")
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
        if (last==null){
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }
}
