package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

import org.joda.time.LocalDate;

@Named("Indices")
public class Indices extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "indices";
    }

    public String iconName() {
        return "Index";
    }
    // }}

    // {{ newIndex
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Index newIndex(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        final Index index = newTransientInstance(Index.class);
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }
    // }}

    // {{ newIndexBase
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public IndexBase newIndexBase(
            final @Named("Index") Index index, 
            final @Named("Previous Base") IndexBase previousBase, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("Factor") double factor) {
        IndexBase indexBase = newTransientInstance(IndexBase.class);
        indexBase.setIndex(index);
        indexBase.setPreviousBase(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(BigDecimal.valueOf(factor));
        persist(indexBase);
        index.addToIndexBases(indexBase);
        return indexBase;
    }
    // }}

    // {{ newIndexValue
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "3")
    public IndexValue newIndexValue(
            final @Named("Index Base") IndexBase indexBase, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate, 
            final @Named("Value") BigDecimal value) {
        IndexValue indexValue = newTransientInstance(IndexValue.class);
        indexValue.setIndexBase(indexBase);
        indexValue.setStartDate(startDate);
        indexValue.setEndDate(endDate);
        indexValue.setValue(value);
        persist(indexValue);
        indexBase.addToValues(indexValue);
        return indexValue;
    }
    // }}

    // {{ findByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Index findByReference(
            final @Named("Reference") String reference) {
        return firstMatch(Index.class, new Filter<Index>() {
            @Override
            public boolean accept(final Index index) {
                return reference.equals(index.getReference());
            }
        });
    }
    // }}

    // {{ allIndices
    // (not a prototype, bounded)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Index> allIndices() {
        return allInstances(Index.class);
    }
    // }}


    // {{ findIndexValueForDate
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "6")
    public IndexValue findIndexValueForDate(
            final Index index, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        return firstMatch(IndexValue.class, new Filter<IndexValue>() {
            @Override
            public boolean accept(final IndexValue indexValue) {
                return startDate.equals(indexValue.getStartDate()) && index.equals(indexValue.getIndexBase().getIndex())  ; // &&
                // this.equals(indexValue.getIndexBase().getIndex());
                // TODO: Should match two dates
            }
        });
    }

    // }}

    
    // {{ allIndexBases
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "7")
    public List<IndexBase> allIndexBases() {
        return allInstances(IndexBase.class);
    }
    // }}
    
    // {{ allIndexValues
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "8")
    public List<IndexValue> allIndexValues() {
        return allInstances(IndexValue.class);
    }
    // }}


}
