package com.eurocommercialproperties.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.index.Index;
import com.eurocommercialproperties.estatio.dom.index.Indexable;
import com.eurocommercialproperties.estatio.dom.index.IndexationCalculator;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("LTRI")
public class LeaseTermForIndexableRent extends LeaseTerm implements Indexable {

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

    // {{ BaseIndexValue (property)
    private BigDecimal baseIndexValue;

    @MemberOrder(sequence = "12")
    @Optional
    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    public void setBaseIndexValue(final BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    // }}

    // {{ NextIndexStartDate (property)
    private LocalDate nextIndexStartDate;

    @Persistent
    @MemberOrder(sequence = "13")
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
    @MemberOrder(sequence = "14")
    public LocalDate getNextIndexEndDate() {
        return nextIndexEndDate;
    }

    public void setNextIndexEndDate(final LocalDate nextIndexEndDate) {
        this.nextIndexEndDate = nextIndexEndDate;
    }

    // }}

    // {{ NextIndexValue (property)
    private BigDecimal nextIndexValue;

    @MemberOrder(sequence = "15")
    @Optional
    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    public void setNextIndexValue(final BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    // }}

    // {{ ReviewDate (property)
    private LocalDate reviewDate;

    @Persistent
    @MemberOrder(sequence = "16")
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
    @MemberOrder(sequence = "17")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // }}

    // {{ BaseValue (property)
    private BigDecimal baseValue;

    @MemberOrder(sequence = "18")
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // }}

    // {{ IndexationPercentage (property)
    private BigDecimal indexationPercentage;

    @MemberOrder(sequence = "20")
    @Optional
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // }}

    // {{ LevellingPercentage (property)
    private BigDecimal levellingPercentage;

    @MemberOrder(sequence = "21")
    @Optional
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // }}

    // {{ LevellingValue (property)
    private BigDecimal levellingValue;

    @MemberOrder(sequence = "21")
    @Optional
    public BigDecimal getLevellingValue() {
        return levellingValue;
    }

    public void setLevellingValue(final BigDecimal levellingValue) {
        this.levellingValue = levellingValue;
    }

    // }}

    // {{ IndexedValue (property)
    private BigDecimal indexedValue;

    @MemberOrder(sequence = "22")
    @Optional
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // }}

    // {{
    public void verify() {
        IndexationCalculator calculator = new IndexationCalculator(getIndex(), getBaseIndexStartDate(), getBaseIndexEndDate(), getNextIndexStartDate(), getNextIndexEndDate(), getBaseValue());
        calculator.calculate(this);
    }

    // }}

    // {{
    public LeaseTermForIndexableRent createNextLeaseTerm(@Named("Start Date") LocalDate startDate, BigDecimal value) {

        // create new term
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) getNextTerm();
        if (getNextTerm() == null) {
            term = leaseTermsService.newIndexableLeaseTerm(this.getLeaseItem());
        }
        term.setStartDate(startDate);
        term.setBaseIndexStartDate(this.getNextIndexStartDate());
        term.setBaseIndexEndDate(this.getNextIndexEndDate());
        term.setNextIndexStartDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getNextIndexStartDate()));
        term.setNextIndexEndDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getNextIndexEndDate()));
        term.setEffectiveDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getEffectiveDate()));
        term.setReviewDate(this.getLeaseItem().getIndexationFrequency().nextDate(this.getReviewDate()));
        term.setBaseValue(value);
        // terminate current term
        this.setEndDate(startDate.minusDays(1));
        // this.setNextTerm(term);
        return null;
    }

    // }}

    // {{
    private LeaseTerms leaseTermsService;

    public void setLeaseTermsService(LeaseTerms leaseTerms) {
        this.leaseTermsService = leaseTerms;
    }

    // }}
    @Hidden
    public Index getIndex() {
        return this.getLeaseItem().getIndex();
    }

}
