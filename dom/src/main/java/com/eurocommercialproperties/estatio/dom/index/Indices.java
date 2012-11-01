package com.eurocommercialproperties.estatio.dom.index;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.joda.time.LocalDate;

@Named("Indices")
public interface Indices {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Index newIndex(@Named("Reference") String reference, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public IndexBase newIndexBase(@Named("Index") Index index, @Named("Previous Base") IndexBase previousBase, @Named("Start Date") LocalDate startDate, double factor);
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public IndexValue newIndexValue(@Named("Index Base") IndexBase indexBase, @Named("Start Date") LocalDate startDate, @Named("End Date") LocalDate endDate, BigDecimal value);
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public Index findByReference(@Named("Reference") String reference);

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public IndexValue findIndexValueForDate(Index index, @Named("Start Date") LocalDate startDate, @Named("End Date") LocalDate endDate);
    
    List<Index> allIndices();

    List<IndexBase> allIndexBases();
    
    List<IndexValue> allIndexValues();
    
}


