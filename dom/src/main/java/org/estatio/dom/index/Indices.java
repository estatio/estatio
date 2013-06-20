package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;

public class Indices extends EstatioDomainService<Index> {

    public Indices() {
        super(Indices.class, Index.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Index newIndex(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Index index = newTransientInstance();
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public IndexBase newIndexBase(final @Named("Index") Index index, final @Named("Previous Base") IndexBase previousBase, final @Named("Start Date") LocalDate startDate, final @Named("Factor") BigDecimal factor) {
        IndexBase indexBase = newTransientInstance(IndexBase.class);
        indexBase.modifyPreviousBase(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(factor);
        persist(indexBase);
        index.addToIndexBases(indexBase);
        return indexBase;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "3")
    public IndexValue newIndexValue(final @Named("Index Base") IndexBase indexBase, final @Named("Start Date") LocalDate startDate, final @Named("Value") BigDecimal value) {
        IndexValue indexValue = newTransientInstance(IndexValue.class);
        indexValue.setStartDate(startDate);
        indexValue.setValue(value);
        persist(indexValue);
        indexBase.addToValues(indexValue);
        return indexValue;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Index findByReference(final @Named("Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Index> allIndices() {
        return allInstances();
    }

    @MemberOrder(sequence = "6")
    public IndexValue findIndexValueForDate(final Index index, final @Named("Start Date") LocalDate startDate) {
        return firstMatch(new QueryDefault<IndexValue>(IndexValue.class, "findForDate", "index", index, "date", startDate));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "7")
    public List<IndexBase> allIndexBases() {
        return allInstances(IndexBase.class);
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "8")
    public List<IndexValue> allIndexValues() {
        return allInstances(IndexValue.class);
    }

}
