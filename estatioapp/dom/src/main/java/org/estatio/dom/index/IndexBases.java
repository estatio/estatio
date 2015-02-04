/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.EstatioDomainService;

@DomainService(repositoryFor = IndexBase.class)
@DomainServiceLayout(
        named = "Indices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "60.3")
public class IndexBases
        extends EstatioDomainService<IndexBase> {

    public IndexBases() {
        super(IndexBases.class, IndexBase.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    @NotInServiceMenu
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public IndexBase newIndexBase(
            final @ParameterLayout(named = "Index") Index index,
            final @ParameterLayout(named = "Previous Base") IndexBase previousBase,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "Factor") BigDecimal factor) {
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

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<IndexBase> allIndexBases() {
        return allInstances();
    }

}
