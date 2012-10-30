package com.eurocommercialproperties.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.joda.time.LocalDate;

// TODO: error when choosing discriminator strategy = Classname. 
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator("LTRM")
public class LeaseTerm extends AbstractDomainObject {

    // {{ Lease (property)
    private LeaseItem leaseItem;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "1")
    public LeaseItem getLeaseItem() {
        return leaseItem;
    }

    public void setLeaseItem(final LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
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

    // // {{NextIndexMonth (property)
    // private Interval nextIndexMonth;
    //
    // @Persistent
    // @MemberOrder(sequence = "1")
    // public Interval getNextIndexMonth() {
    // return nextIndexMonth;
    // }
    //
    // public void setNextIndexMonth(final Interval nextIndexMonth) {
    // this.nextIndexMonth = nextIndexMonth;
    // }
    // // }}

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

    public void verify() {
        return;
    }

}
