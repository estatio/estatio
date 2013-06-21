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

public class IndexBases extends EstatioDomainService<IndexBase> {

    public IndexBases() {
        super(IndexBases.class, IndexBase.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Indices", sequence = "2")
    public IndexBase newIndexBase(final @Named("Index") Index index, final @Named("Previous Base") IndexBase previousBase, final @Named("Start Date") LocalDate startDate, final @Named("Factor") BigDecimal factor) {
        IndexBase indexBase = newTransientInstance();
        indexBase.modifyPreviousBase(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(factor);
        persist(indexBase);
        index.addToIndexBases(indexBase);
        return indexBase;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Indices", sequence = "7")
    public List<IndexBase> allIndexBases() {
        return allInstances(IndexBase.class);
    }


}
