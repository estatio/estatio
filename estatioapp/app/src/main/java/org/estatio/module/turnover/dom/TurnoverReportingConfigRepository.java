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
package org.estatio.module.turnover.dom;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.party.dom.Person;

@DomainService(repositoryFor = TurnoverReportingConfig.class, nature = NatureOfService.DOMAIN)
public class TurnoverReportingConfigRepository extends UdoDomainRepositoryAndFactory<TurnoverReportingConfig> {

    public TurnoverReportingConfigRepository() {
        super(TurnoverReportingConfigRepository.class, TurnoverReportingConfig.class);
    }

    public TurnoverReportingConfig upsert(
            final Occupancy occupancy,
            final Type type,
            final Person reporter,
            final LocalDate startDate,
            final Frequency frequency,
            final Currency currency) {
        TurnoverReportingConfig config = findUnique(occupancy, type);
        if (config==null){
            config = create(occupancy, type, reporter, startDate, frequency, currency);
        } else {
            config.setReporter(reporter);
            config.setStartDate(startDate);
            config.setFrequency(frequency);
            config.setCurrency(currency);
        }
        return config;
    }

    public TurnoverReportingConfig findOrCreate(
            final Occupancy occupancy,
            final Type type,
            final Person reporter,
            final LocalDate startDate,
            final Frequency frequency,
            final Currency currency) {
        TurnoverReportingConfig config = findUnique(occupancy, type);
        if (config==null){
            config = create(occupancy, type, reporter, startDate, frequency, currency);
        }
        return config;
    }

    public TurnoverReportingConfig create(
            final Occupancy occupancy,
            final Type type,
            final Person reporter,
            final LocalDate startDate,
            final Frequency frequency,
            final Currency currency) {
        TurnoverReportingConfig config = new TurnoverReportingConfig(occupancy, type, reporter, startDate, frequency, currency);
        serviceRegistry2.injectServicesInto(config);
        repositoryService.persistAndFlush(config);
        return config;
    }

    public TurnoverReportingConfig findUnique(final Occupancy occupancy, final Type type) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findUnique",
                        "occupancy", occupancy,
                        "type", type));
    }

    public List<TurnoverReportingConfig> findByOccupancy(final Occupancy occupancy) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findByOccupancy",
                        "occupancy", occupancy));
    }

    public List<TurnoverReportingConfig> findByOccupancyAndType(final Occupancy occupancy, final Type type) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findByOccupancyAndType",
                        "occupancy", occupancy,
                        "type", type));
    }

    public List<TurnoverReportingConfig> findAllActiveOnDate(final LocalDate date) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findByStartDateOnOrBefore",
                        "date", date));
    }

    public List<TurnoverReportingConfig> findByReporter(final Person reporter) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TurnoverReportingConfig.class,
                        "findByReporter",
                        "reporter", reporter));
    }

    public List<TurnoverReportingConfig> findByPropertyActiveOnDate(final Property property, final LocalDate date) {
        List<TurnoverReportingConfig> result = new ArrayList<>();
        List<Occupancy> occupanciesActiveOnDate = occupancyRepository.occupanciesByPropertyAndInterval(property,LocalDateInterval.including(date,date));
        for (Occupancy occupancy : occupanciesActiveOnDate){
            List<TurnoverReportingConfig> configs = findByOccupancy(occupancy);
            for (TurnoverReportingConfig config : configs){
                if (config.isActiveOnDate(date)) result.add(config);
            }
        }
        return result;
    }

    public List<TurnoverReportingConfig> findByPropertyAndTypeActiveOnDate(final Property property, final Type type, final LocalDate date) {
        List<TurnoverReportingConfig> result = new ArrayList<>();
        List<Occupancy> occupanciesActiveOnDate = occupancyRepository.occupanciesByPropertyAndInterval(property,LocalDateInterval.including(date,date));
        for (Occupancy occupancy : occupanciesActiveOnDate){
            List<TurnoverReportingConfig> configs = findByOccupancyAndType(occupancy, type);
            for (TurnoverReportingConfig config : configs){
                if (config.isActiveOnDate(date)) result.add(config);
            }
        }
        return result;
    }

    public List<TurnoverReportingConfig> listAll() {
        return allInstances();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

    @Inject OccupancyRepository occupancyRepository;
}
