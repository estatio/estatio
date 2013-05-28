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

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;

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
        return indexService.allIndices();
    }

    @javax.jdo.annotations.Persistent
    private LocalDate baseIndexStartDate;

    @MemberOrder(sequence = "12", name = "Indexable Rent")
    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

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

    @javax.jdo.annotations.Persistent
    private LocalDate nextIndexStartDate;

    @MemberOrder(sequence = "15", name = "Indexable Rent")
    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

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

    // REVIEW: why now @Column annotation for this one?
    private BigDecimal levellingPercentage;

    @MemberOrder(sequence = "21", name = "Indexable Rent")
    @Optional
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

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

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal baseValue;

    @MemberOrder(sequence = "30", name = "Values")
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

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

    @Override
    public LeaseTerm approve() {
        super.approve();
        return this;
    }

    public String disableApprove() {
        return getStatus().equals(LeaseItemStatus.APPROVED) ? "Already approved" : null;
    }

    @Override
    @Hidden
    public void initialize() {
        super.initialize();
        setFrequency(LeaseTermFrequency.YEARLY);
        LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPreviousTerm();
        if (previousTerm != null) {
            LeaseTermFrequency frequency = previousTerm.getFrequency();
            if (frequency != null) {
                setIndex(previousTerm.getIndex());
                setBaseIndexStartDate(previousTerm.getNextIndexStartDate());
                setNextIndexStartDate(frequency.nextDate(previousTerm.getNextIndexStartDate()));
                setEffectiveDate(frequency.nextDate(previousTerm.getEffectiveDate()));
                setBaseValue(previousTerm.getValue());
            }
        }
    }

    @Hidden
    @Override
    public void update() {
        super.update();
        // TODO: not really elegant to fetch the data from the previous term. Who
        // is responsible?
        LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPreviousTerm();
        if (previousTerm != null) {
            if (previousTerm.getValue() != null && (getBaseValue() == null || previousTerm.getValue().compareTo(getBaseValue()) != 0)) {
                setBaseValue(previousTerm.getValue());
            }
        }
        IndexationCalculator calculator = new IndexationCalculator(getIndex(), getBaseIndexStartDate(), getNextIndexStartDate(), getBaseValue());
        calculator.calculate(this);
        if (getStatus() == LeaseTermStatus.NEW) {
            if (MathUtils.isNotZeroOrNull(getIndexedValue())) {
                setValue(getIndexedValue());
            } else {
                setValue(getBaseValue());
            }
        } else {
            // TODO: handle updating values for other statuses
        }
    }

    @Override
    @Hidden
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        // use the indexed value on or after the effective date, use the base
        // value otherwise. If effective date is empty use a date two months
        // after next index date
        if (MathUtils.isNotZeroOrNull(getIndexedValue())) {
            if (getEffectiveDate() != null) {
                if (dueDate.compareTo(getEffectiveDate()) >= 0)
                    return getIndexedValue();
            } else {
                if (getNextIndexStartDate().plusMonths(2).compareTo(dueDate) > 0)
                    return getIndexedValue();
            }
        }
        return getBaseValue();
    }

    private Indices indexService;

    public void setIndixService(Indices indexes) {
        this.indexService = indexes;
    }
}
