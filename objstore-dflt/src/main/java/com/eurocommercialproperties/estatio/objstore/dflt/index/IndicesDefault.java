package com.eurocommercialproperties.estatio.objstore.dflt.index;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.index.Index;
import com.eurocommercialproperties.estatio.dom.index.IndexBase;
import com.eurocommercialproperties.estatio.dom.index.IndexValue;
import com.eurocommercialproperties.estatio.dom.index.Indices;

public class IndicesDefault extends AbstractFactoryAndRepository implements Indices {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "indices";
    }

    public String iconName() {
        return "Index";
    }

    // }}

    // {{ NewIndex (action)
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Index newIndex(final String reference, String name) {
        final Index index = newTransientInstance(Index.class);
        index.setReference(reference);
        index.setName(name);
        persist(index);
        return index;
    }

    // }}

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public IndexBase newIndexBase(@Named("Index") Index index, @Named("Previous Base") IndexBase previousBase, @Named("Start Date") LocalDate startDate, double factor) {
        IndexBase indexBase = newTransientInstance(IndexBase.class);
        indexBase.setIndex(index);
        indexBase.setPreviousBase(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(BigDecimal.valueOf(factor));
        persist(indexBase);
        index.addToIndexBases(indexBase);
        return indexBase;
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public IndexValue newIndexValue(@Named("Index Base") IndexBase indexBase, @Named("Start Date") LocalDate startDate, @Named("End Date") LocalDate endDate, BigDecimal value) {
        IndexValue indexValue = newTransientInstance(IndexValue.class);
        indexValue.setIndexBase(indexBase);
        indexValue.setStartDate(startDate);
        indexValue.setEndDate(endDate);
        indexValue.setValue(value);
        persist(indexValue);
        indexBase.addToValues(indexValue);
        return indexValue;
    }

    // {{ findByReference
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Index findByReference(@Named("Reference") final String reference) {
        return firstMatch(Index.class, new Filter<Index>() {
            @Override
            public boolean accept(final Index index) {
                return reference.equals(index.getReference());
            }
        });
    }

    // }}
    
    // {{ AllInstances
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Index> allInstances() {
        return allInstances(Index.class);
    }
    // }}

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "6")
    public List<IndexBase> allIndexBases() {
        return allInstances(IndexBase.class);
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "7")
    public List<IndexValue> allIndexValues() {
        return allInstances(IndexValue.class);
    }

    
//    // {{ findByReference
//    @Override
//    @ActionSemantics(Of.SAFE)
//    @MemberOrder(sequence = "4")
//    public Index findIndexValue(@Named("Reference") final String reference) {
//        return firstMatch(Index.class, new Filter<Index>() {
//            @Override
//            public boolean accept(final Index index) {
//                return reference.equals(index.getReference());
//            }
//        });
//    }

    // }}

    

}
