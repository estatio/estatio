package com.eurocommercialproperties.estatio.dom.index;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.filter.Filter;
import org.joda.time.LocalDate;

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
    @Persistent(mappedBy = "index")
    private Set<IndexBase> indexBases = new LinkedHashSet<IndexBase>();

    @MemberOrder(sequence = "3")
    public Set<IndexBase> getIndexBases() {
        return indexBases;
    }

    public void setIndexBases(final Set<IndexBase> indexBases) {
        this.indexBases = indexBases;
    }

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

    // {{ GetValueForDate (action)

    public BigDecimal GetIndexationFactor(@Named("Start Date") LocalDate startDate, @Named("End Date") LocalDate endDate) {
        IndexValue startIndexValue = GetIndexValueForDate(startDate);
        IndexValue endIndexValue = GetIndexValueForDate(endDate);
        BigDecimal rebaseFactor = BigDecimal.ONE;
        
        rebaseFactor = endIndexValue.getIndexBase().getFactorForDate(startDate);
        
        return endIndexValue.getValue().divide(startIndexValue.getValue()).multiply(rebaseFactor);
        
    }

    // }}

    // {{ GetIndexValueForDate

    @Hidden
    public IndexValue GetIndexValueForDate(final LocalDate date) {
        // TODO: what I'm doing here should be made more efficient, maybe move
        // to the repository?
        return firstMatch(IndexValue.class, new Filter<IndexValue>() {
            @Override
            public boolean accept(final IndexValue indexValue) {
                return date.equals(indexValue.getStartDate()); // &&
                                                               // this.equals(indexValue.getIndexBase().getIndex());
            }
        });
    }
    // }}

}
