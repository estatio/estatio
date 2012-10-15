package com.eurocommercialproperties.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.joda.time.LocalDate;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("LTRI")
public class IndexableLeaseTerm extends LeaseTerm {

    // {{ BaseIndexStartDate (property)
    private LocalDate baseIndexStartDate;

    @Persistent
    @MemberOrder(sequence = "10")
    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

    // }}

    // {{ BaseIndexEndDate (property)
    private LocalDate baseIndexEndDate;

    @Persistent
    @MemberOrder(sequence = "11")
    public LocalDate getBaseIndexEndDate() {
        return baseIndexEndDate;
    }

    public void setBaseIndexEndDate(final LocalDate baseIndexEndDate) {
        this.baseIndexEndDate = baseIndexEndDate;
    }

    // }}

    // {{ NextIndexStartDate (property)
    private LocalDate nextIndexStartDate;

    @Persistent
    @MemberOrder(sequence = "12")
    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

    // }}

    // {{ NextIndexEndDate (property)
    private LocalDate nextIndexEndDate;

    @Persistent
    @MemberOrder(sequence = "13")
    public LocalDate getNextIndexEndDate() {
        return nextIndexEndDate;
    }

    public void setNextIndexEndDate(final LocalDate nextIndexEndDate) {
        this.nextIndexEndDate = nextIndexEndDate;
    }

    // }}

    // {{ IndexationApplicationDate (property)
    private LocalDate indexationApplicationDate;

    @Persistent
    @MemberOrder(sequence = "1")
    public LocalDate getIndexationApplicationDate() {
        return indexationApplicationDate;
    }

    public void setIndexationApplicationDate(final LocalDate indexationApplicationDate) {
        this.indexationApplicationDate = indexationApplicationDate;
    }

    // }}

    // {{
    public void verify() {
        BigDecimal factor = getLeaseItem().getIndex().getIndexationFactor(getBaseIndexStartDate(), getBaseIndexStartDate());
        if (factor.compareTo(BigDecimal.ZERO) != 0) {
            // we have found a indexation factor
            createNextLeaseTerm(this.getIndexationApplicationDate(), getValue().multiply(factor).round(new MathContext(2)));
        }
        return;
    }

    // }}

    // {{
    public IndexableLeaseTerm createNextLeaseTerm(@Named("Start Date") LocalDate startDate, BigDecimal value) {

        // create new term
        IndexableLeaseTerm t = getLeaseTermsService().newIndexableLeaseTerm(this.getLeaseItem());
        t.setStartDate(startDate);
        // TODO: use indexation frequency
        t.setBaseIndexStartDate(this.getNextIndexStartDate());
        t.setBaseIndexEndDate(this.getNextIndexEndDate());
        t.setNextIndexStartDate(this.getNextIndexStartDate().plusYears(1));
        t.setNextIndexEndDate(this.getNextIndexEndDate().plusYears(1));
        t.setIndexationApplicationDate(this.getIndexationApplicationDate().plusYears(1));
        t.setValue(value);
        // terminate current term
        this.setEndDate(startDate.minusDays(1));
        return null;
    }

    // }}

    // {{
    private LeaseTerms leaseTermsService;

    public LeaseTerms getLeaseTermsService() {
        return leaseTermsService;
    }

    public void setLeaseTermsService(LeaseTerms leaseTerms) {
        this.leaseTermsService = leaseTerms;
    }

    // }}
}
