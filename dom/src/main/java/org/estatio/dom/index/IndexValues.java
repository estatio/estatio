package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

public class IndexValues extends EstatioDomainService<IndexValue> {

    public IndexValues() {
        super(IndexValues.class, IndexValue.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Indices", sequence = "3")
    public IndexValue newIndexValue(final @Named("Index Base") IndexBase indexBase, final @Named("Start Date") LocalDate startDate, final @Named("Value") BigDecimal value) {
        IndexValue indexValue = newTransientInstance();
        indexValue.setStartDate(startDate);
        indexValue.setValue(value);
        persist(indexValue);
        indexBase.addToValues(indexValue);
        return indexValue;
    }
    

    @MemberOrder(name="Indices", sequence = "6")
    public IndexValue findIndexValueForDate(final Index index, final @Named("Start Date") LocalDate startDate) {
        return firstMatch("findForDate", "index", index, "date", startDate);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Indices", sequence = "8")
    public List<IndexValue> allIndexValues() {
        return allInstances();
    }

}
