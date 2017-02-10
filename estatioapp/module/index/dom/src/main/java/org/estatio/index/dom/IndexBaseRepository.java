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
package org.estatio.index.dom;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = IndexBase.class, nature = NatureOfService.DOMAIN)
public class IndexBaseRepository
        extends UdoDomainRepositoryAndFactory<IndexBase> {

    public IndexBaseRepository() {
        super(IndexBaseRepository.class, IndexBase.class);
    }

    public IndexBase newIndexBase(
            final Index index,
            final IndexBase previousBase,
            final LocalDate startDate,
            final BigDecimal factor) {
        IndexBase indexBase = newTransientInstance();
        indexBase.setPrevious(previousBase);
        indexBase.setStartDate(startDate);
        indexBase.setFactor(factor);
        indexBase.setIndex(index);
        persistIfNotAlready(indexBase);
        return indexBase;
    }

    public IndexBase findOrCreate(
            final Index index,
            final LocalDate startDate,
            final BigDecimal factor) {
        final IndexBase indexBase = findByIndexAndActiveOnDate(index, startDate);
        if (indexBase == null || indexBase.getStartDate().isBefore(startDate)) {
            return findOrCreate(index, indexBase, startDate, factor);
        }
        return indexBase;
    }


    public IndexBase findOrCreate(
        final Index index,
        final IndexBase previousBase,
        final LocalDate startDate,
        final BigDecimal factor) {
        final IndexBase indexBase = findByIndexAndDate(index, startDate);
        return indexBase != null ? indexBase : newIndexBase(index, previousBase, startDate, factor);
    }

    public IndexBase findByIndexAndActiveOnDate(final Index index, final LocalDate date) {
        // The is deliberately a firstmatch
        return firstMatch("findByIndexAndActiveOnDate", "index", index, "date", date);
    }

    public IndexBase findByIndexAndDate(final Index index, final LocalDate date) {
        return uniqueMatch("findByIndexAndDate", "index", index, "date", date);
    }

    public List<IndexBase> allIndexBases() {
        return allInstances();
    }

}
