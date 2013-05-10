package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioRefDataObject;
import org.joda.time.LocalDate;

@PersistenceCapable
@Immutable
public class IndexBase extends EstatioRefDataObject implements Comparable<IndexBase> {

    // {{ Index (property)
    private Index index;

    @Title(sequence = "1", append = ", ")
    @MemberOrder(sequence = "1")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Title(sequence = "2")
    @Persistent
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ PreviousBase (property)
    private IndexBase previousBase;

    @Optional
    @MemberOrder(sequence = "3")
    public IndexBase getPreviousBase() {
        return previousBase;
    }

    public void setPreviousBase(final IndexBase previousBase) {
        this.previousBase = previousBase;
    }

    // }}

    // {{ Factor (property)
    private BigDecimal factor;

    @Optional
    @MemberOrder(sequence = "4")
    @Persistent
    @Column(scale = 4)
    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(final BigDecimal factor) {
        this.factor = factor;
    }

    public String validateFactor(final BigDecimal factor) {
        return (getPreviousBase() == null) ? null : (factor == null || factor.compareTo(BigDecimal.ZERO) == 0) ? "Factor is mandatory when there is a previous base" : null;
    }

    // {{ NextBase (property)
    private IndexBase nextBase;

    @Optional
    @MemberOrder(sequence = "5")
    public IndexBase getNextBase() {
        return nextBase;
    }

    public void setNextBase(final IndexBase nextBase) {
        this.nextBase = nextBase;
    }

    // }}

    // {{ Values (Collection)
    // @Persistent(mappedBy = "indexBase")
    // private SortedSet<IndexValue> values = new TreeSortedSet<IndexValue>();
    //
    // @MemberOrder(sequence = "6")
    // public SortedSet<IndexValue> getValues() {
    // return values;
    // }
    //
    // public void setValues(final SortedSet<IndexValue> values) {
    // this.values = values;
    // }

    // {{ Values (Collection)
    @Persistent(mappedBy = "indexBase")
    private List<IndexValue> values = new ArrayList<IndexValue>();

    @MemberOrder(sequence = "6")
    public List<IndexValue> getValues() {
        return values;
    }

    public void setValues(final List<IndexValue> values) {
        this.values = values;
    }

    // }}

    public void addToValues(final IndexValue indexValue) {
        // check for no-op
        if (indexValue == null || getValues().contains(indexValue)) {
            return;
        }
        // associate new
        getValues().add(indexValue);
        // additional business logic
        onAddToValues(indexValue);
    }

    public void removeFromValues(final IndexValue indexValue) {
        // check for no-op
        if (indexValue == null || !getValues().contains(indexValue)) {
            return;
        }
        // dissociate existing
        getValues().remove(indexValue);
        // additional business logic
        onRemoveFromValues(indexValue);
    }

    protected void onAddToValues(final IndexValue indexValue) {
    }

    protected void onRemoveFromValues(final IndexValue indexValue) {
    }

    // }}

    public BigDecimal getFactorForDate(@Named("Date") LocalDate date) {
        if (date.isBefore(getStartDate())) {
            return getFactor().multiply(getPreviousBase().getFactorForDate(date));
        }
        return BigDecimal.ONE;
    }

    @Override
    public int compareTo(IndexBase o) {
        return o.getStartDate().compareTo(this.getStartDate());
    }

}
