package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@Immutable
public class IndexBase extends EstatioRefDataObject implements Comparable<IndexBase> {

    private Index index;

    @Title(sequence = "1", append = ", ")
    @MemberOrder(sequence = "1")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    public void modifyIndex(final Index index) {
        Index currentIndex = getIndex();
        if (index == null || index.equals(currentIndex)) {
            return;
        }
        index.addToIndexBases(this);
    }

    public void clearIndex() {
        Index currentIndex = getIndex();
        if (currentIndex == null) {
            return;
        }
        currentIndex.removeFromIndexBases(this);
    }

    
    
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(scale = 4)
    private BigDecimal factor;

    @Optional
    @MemberOrder(sequence = "4")
    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(final BigDecimal factor) {
        this.factor = factor;
    }

    public String validateFactor(final BigDecimal factor) {
        return (getPreviousBase() == null) ? null : (factor == null || factor.compareTo(BigDecimal.ZERO) == 0) ? "Factor is mandatory when there is a previous base" : null;
    }

    
    
    @javax.jdo.annotations.Persistent(mappedBy = "nextBase")
    private IndexBase previousBase;

    @Optional
    @MemberOrder(sequence = "3")
    public IndexBase getPreviousBase() {
        return previousBase;
    }

    public void setPreviousBase(final IndexBase previousBase) {
        this.previousBase = previousBase;
    }

    
    private IndexBase nextBase;

    @Optional
    @MemberOrder(sequence = "5")
    public IndexBase getNextBase() {
        return nextBase;
    }

    public void setNextBase(final IndexBase nextBase) {
        this.nextBase = nextBase;
    }

    
    
    @javax.jdo.annotations.Persistent(mappedBy = "indexBase")
    private List<IndexValue> values = new ArrayList<IndexValue>();

    @MemberOrder(sequence = "6")
    public List<IndexValue> getValues() {
        return values;
    }

    @Hidden
    public void modifyPreviousBase(IndexBase previous) {
        setPreviousBase(previous);
        if (previous != null)
            previous.setNextBase(this);
    }

    public void setValues(final List<IndexValue> values) {
        this.values = values;
    }

    public void addToValues(final IndexValue value) {
        if (value == null || getValues().contains(value)) {
            return;
        }
        value.clearIndexBase();
        value.setIndexBase(this);
        getValues().add(value);
    }

    public void removeFromValues(final IndexValue value) {
        // check for no-op
        if (value == null || !getValues().contains(value)) {
            return;
        }
        value.setIndexBase(null);
        getValues().remove(value);
    }

    public BigDecimal getFactorForDate(@Named("Date") LocalDate date) {
        if (date.isBefore(getStartDate())) {
            return getFactor().multiply(getPreviousBase().getFactorForDate(date));
        }
        return BigDecimal.ONE;
    }

    
    // {{ Comparable impl
    @Override
    public int compareTo(IndexBase o) {
        return o.getStartDate().compareTo(this.getStartDate());
    }
    // }}

}
