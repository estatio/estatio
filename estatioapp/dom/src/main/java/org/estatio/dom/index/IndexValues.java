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
import java.util.concurrent.Callable;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = IndexValue.class)
@DomainServiceLayout(
        named="Indices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "60.4"
)
public class IndexValues
        extends UdoDomainRepositoryAndFactory<IndexValue> {

    public IndexValues() {
        super(IndexValues.class, IndexValue.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotInServiceMenu
    @MemberOrder(sequence = "1")
    public IndexValue newIndexValue(
            final @Named("Index Base") IndexBase indexBase,
            final @Named("Start Date") LocalDate startDate,
            final @Named("Value") BigDecimal value) {
        IndexValue indexValue = findIndexValueByIndexAndStartDate(indexBase.getIndex(), startDate);
        if (indexValue == null) {
            indexValue = newTransientInstance();
            indexValue.setStartDate(startDate);
            indexValue.setIndexBase(indexBase);
            persistIfNotAlready(indexValue);
        }
        indexValue.setValue(value);
        return indexValue;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotInServiceMenu
    @MemberOrder(sequence = "1")
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
        return queryResultsCache.execute(
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
    @MemberOrder(sequence = "99")
    public List<IndexValue> allIndexValues() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Inject
    IndexBases indexBases;

    @Inject
    Indices indices;

    @Inject
    QueryResultsCache queryResultsCache;

}
