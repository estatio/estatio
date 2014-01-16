/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.EstatioInteractionCache;
import org.estatio.dom.lease.invoicing.InvoiceService;

/**
 * Domain service acting as a repository of {@link IndexValue}s.
 */
public class IndexValues
        extends EstatioDomainService<IndexValue> {

    public IndexValues() {
        super(IndexValues.class, IndexValue.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotInServiceMenu
    public IndexValue newIndexValue(
            final @Named("Index Base") IndexBase indexBase,
            final @Named("Start Date") LocalDate startDate,
            final @Named("Value") BigDecimal value) {
        IndexValue indexValue = newTransientInstance();
        indexValue.setStartDate(startDate);
        indexValue.setValue(value);
        persist(indexValue);
        indexBase.addToValues(indexValue);
        return indexValue;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotInServiceMenu
    public IndexValue newIndexValue(
            final @Named("Index") Index index,
            final @Named("Start Date") LocalDate startDate,
            final @Named("Value") BigDecimal value) {
        IndexBase indexBase = indexBases.findByIndexAndDate(index, startDate);
        return newIndexValue(indexBase, startDate, value);
    }

    public LocalDate default1NewIndexValue() {
        // TODO: this action is contributed on an Index and it should fetch the
        // Index it's contributed on
        Index index = indices.allIndices().get(0);
        IndexValue last = findLastByIndex(index);
        return last == null ? null : last.getStartDate().plusMonths(1);
    }

    @ActionSemantics(Of.SAFE)
    @Programmatic
    public IndexValue findIndexValueByIndexAndStartDate(
            final Index index,
            final @Named("Start Date") LocalDate startDate) {
        return EstatioInteractionCache.execute(
                new Callable<IndexValue>() {
                    @Override
                    public IndexValue call() throws Exception {
                        return firstMatch("findByIndexAndStartDate",
                                "index", index,
                                "startDate", startDate);
                    }
                }, 
                IndexValues.class, "findIndexValueByIndexAndStartDate", index, startDate);
    }

    @ActionSemantics(Of.SAFE)
    @Programmatic
    public IndexValue findLastByIndex(
            final Index index) {
        return firstMatch("findLastByIndex",
                "index", index);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Indices", sequence = "8")
    public List<IndexValue> allIndexValues() {
        return allInstances();
    }

    // //////////////////////////////////////

    private IndexBases indexBases;

    public void injectIndexBases(final IndexBases indexBases) {
        this.indexBases = indexBases;
    }

    private Indices indices;

    public void injectIndices(final Indices indices) {
        this.indices = indices;
    }

}
