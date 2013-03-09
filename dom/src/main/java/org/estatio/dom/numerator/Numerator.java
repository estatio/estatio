package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.EstatioTransactionalObject;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable(serializeRead = "true")
public class Numerator extends EstatioTransactionalObject {

    // {{ Strategy (property)
    private String key;

    @MemberOrder(sequence = "1")
    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
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
        BigInteger next = getLastIncrement().add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }
}
