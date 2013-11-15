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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

/**
 * Domain service acting as a repository of {@link IndexBase}s.
 */
public class IndexBases
        extends EstatioDomainService<IndexBase> {

    public IndexBases() {
        super(IndexBases.class, IndexBase.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Indices", sequence = "2")
    @NotInServiceMenu
    public IndexBase newIndexBase(
            final @Named("Index") Index index,
            final @Named("Previous Base") IndexBase previousBase,
            final @Named("Start Date") LocalDate startDate,
            final @Named("Factor") BigDecimal factor) {
        IndexBase indexBase = newTransientInstance();
        indexBase.modifyPrevious(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(factor);
        indexBase.setIndex(index);
        persistIfNotAlready(indexBase);
        return indexBase;
    }

    // //////////////////////////////////////

    @Programmatic
    public IndexBase findByIndexAndDate(final Index index, final LocalDate date) {
        return firstMatch("findByIndexAndDate", "index", index, "date", date);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Indices", sequence = "7")
    public List<IndexBase> allIndexBases() {
        return allInstances();
    }

}
