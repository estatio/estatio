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

    // {{ ReviewDate (property)
    private LocalDate reviewDate;

    @Persistent
    @MemberOrder(sequence = "14")
    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(final LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    // }}

    // {{ EffectiveDate (property)
    private LocalDate effectiveDate;

    @Persistent
    @MemberOrder(sequence = "14")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // }}

    // {{ BaseValue (property)
    private BigDecimal baseValue;

    @MemberOrder(sequence = "15")
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // }}

    // {{ IndexedValue (property)
    private BigDecimal indexedValue;

    @MemberOrder(sequence = "16")
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // }}

    // {{ IndexationPercentage (property)
    private BigDecimal indexationPercentage;

    @MemberOrder(sequence = "17")
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // }}

    // {{ LevellingPercentage (property)
    private BigDecimal levellingPercentage;

    @MemberOrder(sequence = "18")
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // }}

    // {{
    public void verify() {
        BigDecimal factor = getLeaseItem().getIndex().getIndexationFactor(getBaseIndexStartDate(), getNextIndexStartDate());
        if (factor.compareTo(BigDecimal.ZERO) != 0) {
            // we have found a indexation factor
            BigDecimal newValue = getValue().multiply(factor);
            createNextLeaseTerm(this.getEffectiveDate(), newValue.round(new MathContext(0)));
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
        term.setEffectiveDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getEffectiveDate()));
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
