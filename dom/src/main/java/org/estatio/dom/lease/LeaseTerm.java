package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "LEASETERM_ID")
public class LeaseTerm extends AbstractDomainObject implements Comparable<LeaseTerm>{
    
    // {{ Lease (property)
    private LeaseItem leaseItem;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "1")
    @Persistent
    public LeaseItem getLeaseItem() {
        return leaseItem;
    }

    public void setLeaseItem(final LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
    }

    // }}
    
    // {{ Sequence (property)
    private BigInteger sequence;

    @Hidden
    @Optional
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }
    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @MemberOrder(sequence = "2")
    @Title(sequence = "1")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "3")
    @Title(sequence = "2", prepend = "-")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ Value (property)
    private BigDecimal value;

    @MemberOrder(sequence = "4")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    // }}

    // {{ NextTerm (property)
    private LeaseTerm nextTerm;

    @Hidden
    @Optional
    @MemberOrder(sequence = "1")
    public LeaseTerm getNextTerm() {
        return nextTerm;
    }

    public void setNextTerm(final LeaseTerm nextTerm) {
        this.nextTerm = nextTerm;
    }

    // }}
    
    // {{ Status (property)
    private LeaseTermStatus status;

    @MemberOrder(sequence = "1")
    public LeaseTermStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseTermStatus status) {
        this.status = status;
    }
    // }}

    public void verify() {
        return;
    }

    @Override
    public int compareTo(LeaseTerm o) {
        int i = this.getClass().getName().compareTo(o.getClass().getName());
        if (i != 0) return i;
        return this.getStartDate().compareTo(o.getStartDate());
    }

}
