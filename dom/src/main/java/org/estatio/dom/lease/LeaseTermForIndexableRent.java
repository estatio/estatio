package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.index.IndexationCalculator;
import org.estatio.dom.index.Indices;
import org.estatio.dom.utils.MathUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForIndexableRent extends LeaseTerm implements Indexable {

    private Index index;

    @MemberOrder(sequence = "10", name = "Indexable Rent")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    public List<Index> choicesIndex() {
        return indices.allIndices();
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate baseIndexStartDate;

    @MemberOrder(sequence = "12", name = "Indexable Rent")
    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal baseIndexValue;

    @MemberOrder(sequence = "14", name = "Indexable Rent")
    @Optional
    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    public void setBaseIndexValue(final BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate nextIndexStartDate;

    @MemberOrder(sequence = "15", name = "Indexable Rent")
    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal nextIndexValue;

    @MemberOrder(sequence = "17", name = "Indexable Rent")
    @Optional
    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    public void setNextIndexValue(final BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate effectiveDate;

    @Optional
    @MemberOrder(sequence = "19", name = "Indexable Rent")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 1)
    private BigDecimal indexationPercentage;

    @MemberOrder(sequence = "20", name = "Indexable Rent")
    @Optional
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 1)
    private BigDecimal levellingPercentage;

    @MemberOrder(sequence = "21", name = "Indexable Rent")
    @Optional
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal levellingValue;

    @MemberOrder(sequence = "22", name = "Indexable Rent")
    @Optional
    public BigDecimal getLevellingValue() {
        return levellingValue;
    }

    public void setLevellingValue(final BigDecimal levellingValue) {
        this.levellingValue = levellingValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal baseValue;

    @MemberOrder(sequence = "30", name = "Values")
    @Optional
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal indexedValue;

    @MemberOrder(sequence = "31", name = "Values")
    @Optional
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal settledValue;

    @MemberOrder(sequence = "32", name = "Values")
    @Optional
    public BigDecimal getSettledValue() {
        return settledValue;
    }

    public void setSettledValue(final BigDecimal settledValue) {
        this.settledValue = settledValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        if (getStatus() == LeaseTermStatus.APPROVED)
            return getSettledValue();
        return null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
    }

    // ///////////////////////////////////////////

    @Override
    public LeaseTerm approve() {
        super.approve();
        if (MathUtils.isZeroOrNull(getSettledValue()))
            setSettledValue(getTrialValue());
        return this;
    }

    public String disableApprove() {
        return getStatus().equals(LeaseItemStatus.APPROVED) ? "Already approved" : null;
    }

    // ///////////////////////////////////////////

    @Override
    @Programmatic
    public void initialize() {
        super.initialize();
        LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPreviousTerm();
        if (previousTerm != null) {
            LeaseTermFrequency frequency = previousTerm.getFrequency();
            if (frequency != null) {
                setIndex(previousTerm.getIndex());
                setBaseIndexStartDate(previousTerm.getNextIndexStartDate());
                setNextIndexStartDate(frequency.nextDate(previousTerm.getNextIndexStartDate()));
                setEffectiveDate(frequency.nextDate(previousTerm.getEffectiveDate()));
                setBaseValue(previousTerm.getSettledValue());
            }
        }
    }

    private BigDecimal firstValue(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }

    @Programmatic
    @Override
    public void update() {
        super.update();
        LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPreviousTerm();
        if (previousTerm != null) {
            BigDecimal newBaseValue = firstValue(previousTerm.getApprovedValue(), previousTerm.getIndexedValue(), previousTerm.getBaseValue());
            if (getBaseValue() == null || newBaseValue.compareTo(getBaseValue()) != 0) {
                setBaseValue(newBaseValue);
            }
        }
        IndexationCalculator calculator = new IndexationCalculator(getIndex(), getBaseIndexStartDate(), getNextIndexStartDate(), getBaseValue());
        calculator.calculate(this);
    }

    @Override
    @Programmatic
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        // use the indexed value on or after the effective date, use the base
        // otherwise
        if (getEffectiveDate() == null)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        if (getStartDate().compareTo(getEffectiveDate()) == 0)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        if (dueDate.compareTo(getEffectiveDate()) >= 0)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        return firstValue(getBaseValue(), getSettledValue());
    }

    // ///////////////////////////////////////////

    private Indices indices;

    public void injectIndices(Indices indexes) {
        this.indices = indexes;
    }

}
