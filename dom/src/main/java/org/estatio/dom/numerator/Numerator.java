package org.estatio.dom.numerator;

import java.math.BigInteger;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.ComparableByDescription;
import org.estatio.dom.EstatioTransactionalObject;

@javax.jdo.annotations.PersistenceCapable(/* serializeRead = "true" */)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "NUMERATOR_ID")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(
        name = "numerator_find", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.numerator.Numerator " +
        		"WHERE type == :type")
public class Numerator extends EstatioTransactionalObject implements ComparableByDescription<Numerator> {

    private NumeratorType type;

    @MemberOrder(sequence = "1")
    public NumeratorType getType() {
        return type;
    }

    public void setType(final NumeratorType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private String description;

    @Title
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private BigInteger lastIncrement;

    @MemberOrder(sequence = "3")
    public BigInteger getLastIncrement() {
        return lastIncrement;
    }

    public void setLastIncrement(final BigInteger lastIncrement) {
        this.lastIncrement = lastIncrement;
    }

    // //////////////////////////////////////

    @Programmatic
    public BigInteger increment() {
        BigInteger last = getLastIncrement();
        if (last == null) {
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return ToString.of(this);
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Numerator other) {
        return ObjectContracts.compare(this, other, "description");
        //return ORDERING_BY_DESCRIPTION.compare(this, other);
    }

}