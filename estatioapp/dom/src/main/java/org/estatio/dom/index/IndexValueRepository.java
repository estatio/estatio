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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = IndexValue.class)
public class IndexValueRepository
        extends UdoDomainRepositoryAndFactory<IndexValue> {

    public IndexValueRepository() {
        super(IndexValueRepository.class, IndexValue.class);
    }

    public IndexValue findOrCreate(
            final Index index,
            final LocalDate startDate,
            final BigDecimal value) {
        IndexBase indexBase = indexBaseRepository.findByIndexAndDate(index, startDate);
        return findOrCreate(indexBase, startDate, value);
    }

    public IndexValue findOrCreate(
            final IndexBase indexBase,
            final LocalDate startDate,
            final BigDecimal value) {
        IndexValue indexValue = findByIndexAndStartDate(indexBase.getIndex(), startDate);
        if (indexValue == null) {
            indexValue = create(indexBase, startDate, value);
        }
        indexValue.setValue(value);
        final IndexValue.UpdateEvent event = new IndexValue.UpdateEvent();
        event.setSource(indexValue);
        eventBusService.post(event);
        return indexValue;
    }

    public IndexValue create(
            final IndexBase indexBase,
            final LocalDate startDate,
            final BigDecimal value) {
        final IndexValue indexValue;
        indexValue = newTransientInstance();
        indexValue.setStartDate(startDate);
        indexValue.setIndexBase(indexBase);
        indexValue.setValue(value);
        persistIfNotAlready(indexValue);
        return indexValue;
    }

    public IndexValue findByIndexAndStartDate(
            final Index index,
            final @ParameterLayout(named = "Start Date") LocalDate startDate) {
        return queryResultsCache.execute(
                new Callable<IndexValue>() {
                    @Override
                    public IndexValue call() throws Exception {
                        return firstMatch("findByIndexAndStartDate",
                                "index", index,
                                "startDate", startDate);
                    }
                },
                IndexValueRepository.class, "findIndexValueByIndexAndStartDate", index, startDate);
    }

    public IndexValue findLastByIndex(
            final Index index) {
        return firstMatch("findLastByIndex",
                "index", index);
    }

    public List<IndexValue> all() {
        return allInstances();
    }

    @Inject
    IndexBaseRepository indexBaseRepository;

    @Inject
    IndexRepository indexRepository;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    EventBusService eventBusService;
}
