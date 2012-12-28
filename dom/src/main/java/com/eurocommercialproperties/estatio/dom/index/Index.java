package com.eurocommercialproperties.estatio.dom.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
public class Index extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ IndexBases (Collection)
    // @Persistent(mappedBy = "index")
    // @Order(extensions = @Extension(vendorName = "datanucleus", key =
    // "list-ordering", value = "startDate asc"))
    // private Set<IndexBase> indexBases = new LinkedHashSet<IndexBase>();
    //
    // @MemberOrder(sequence = "3")
    // public Set<IndexBase> getIndexBases() {
    // return indexBases;
    // }
    //
    // public void setIndexBases(final Set<IndexBase> indexBases) {
    // this.indexBases = indexBases;
    // }

    // {{ IndexBases (Collection)
    @Persistent(mappedBy = "index")
    private List<IndexBase> indexBases = new ArrayList<IndexBase>();

    @MemberOrder(sequence = "3")
    public List<IndexBase> getIndexBases() {
        return indexBases;
    }

    public void setIndexBases(final List<IndexBase> indexBases) {
        this.indexBases = indexBases;
    }

    // }}

    public void addToIndexBases(final IndexBase indexBase) {
        // check for no-op
        if (indexBase == null || getIndexBases().contains(indexBase)) {
            return;
        }
        // associate new
        getIndexBases().add(indexBase);
        // additional business logic
        onAddToIndexBases(indexBase);
    }

    public void removeFromIndexBases(final IndexBase indexBase) {
        // check for no-op
        if (indexBase == null || !getIndexBases().contains(indexBase)) {
            return;
        }
        // dissociate existing
        getIndexBases().remove(indexBase);
        // additional business logic
        onRemoveFromIndexBases(indexBase);
    }

    protected void onAddToIndexBases(final IndexBase indexBase) {
    }

    protected void onRemoveFromIndexBases(final IndexBase indexBase) {
    }

    // }}

    // {{ Actions

    @Hidden
    public BigDecimal getIndexValueForDate(LocalDate date) {
        IndexValue indexValue = getIndices().findIndexValueForDate(this, date, date.dayOfMonth().withMaximumValue());
        return indexValue == null ? null : indexValue.getValue();
    }

    @Hidden
    public BigDecimal getRebaseFactorForDates(LocalDate baseIndexStartDate, LocalDate nextIndexStartDate) {
        IndexValue nextIndexValue = getIndices().findIndexValueForDate(this, nextIndexStartDate, nextIndexStartDate.dayOfMonth().withMaximumValue());
        if (nextIndexValue != null) {
            BigDecimal rebaseFactor = nextIndexValue.getIndexBase().getFactorForDate(baseIndexStartDate);
            return rebaseFactor;
        }
        return null;
    }

    @Hidden
    public void initialize(IndexationCalculator indexationCalculator, LocalDate baseIndexStartDate, LocalDate nextIndexStartDate) {
        indexationCalculator.setBaseIndexValue(getIndexValueForDate(baseIndexStartDate));
        indexationCalculator.setNextIndexValue(getIndexValueForDate(nextIndexStartDate));
        indexationCalculator.setRebaseFactor(getRebaseFactorForDates(baseIndexStartDate, nextIndexStartDate));

    }

    // }}

    // {{ injected: Indices
    private Indices indices;

    public Indices getIndices() {
        return indices;
    }

    public void setIndices(Indices indices) {
        this.indices = indices;
    }

    // }}

}
