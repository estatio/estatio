package com.eurocommercialproperties.estatio.dom.index;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Title;
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

    public BigDecimal getIndexationFactor(@Named("Base Date") LocalDate baseDate, @Named("Next Date") LocalDate nextDate) {
        IndexValue startIndexValue = getIndices().findIndexValueForDate(this, baseDate, baseDate.dayOfMonth().withMaximumValue());
        IndexValue endIndexValue = getIndices().findIndexValueForDate(this, nextDate, nextDate.dayOfMonth().withMaximumValue());
        if (startIndexValue == null || endIndexValue == null) {
            getContainer().warnUser("No index value found");
            //TODO: specify further
            return BigDecimal.ZERO;
        } else {
            BigDecimal rebaseFactor = BigDecimal.ONE;
            rebaseFactor = endIndexValue.getIndexBase().getFactorForDate(baseDate);
            BigDecimal indexationFactor = endIndexValue.getValue().divide(startIndexValue.getValue(), 5, RoundingMode.HALF_UP).multiply(rebaseFactor);
            return indexationFactor;
        }
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
