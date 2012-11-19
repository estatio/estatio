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
        BigDecimal factor = getLeaseItem().getIndex().getIndexationFactor(getBaseIndexStartDate(), getNextIndexStartDate());
        if (factor.compareTo(BigDecimal.ZERO) != 0) {
            // we have found a indexation factor
            BigDecimal newValue = getValue().multiply(factor);
            createNextLeaseTerm(this.getIndexationApplicationDate(), newValue.round(new MathContext(0)));
        }
        return;
    }

    // }}

    // {{
    public IndexableLeaseTerm createNextLeaseTerm(@Named("Start Date") LocalDate startDate, BigDecimal value) {

        // create new term
        IndexableLeaseTerm term = (IndexableLeaseTerm) getNextTerm();
        if (getNextTerm() == null) {
            term = getLeaseTermsService().newIndexableLeaseTerm(this.getLeaseItem());
        }
        term.setStartDate(startDate);
        term.setBaseIndexStartDate(this.getNextIndexStartDate());
        term.setBaseIndexEndDate(this.getNextIndexEndDate());
        term.setNextIndexStartDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getNextIndexStartDate()));
        term.setNextIndexEndDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getNextIndexEndDate()));
        term.setIndexationApplicationDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getIndexationApplicationDate()));
        term.setValue(value);
        // terminate current term
        this.setEndDate(startDate.minusDays(1));
        this.setNextTerm(term);
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
