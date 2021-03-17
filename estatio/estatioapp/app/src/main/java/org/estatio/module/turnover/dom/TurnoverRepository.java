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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(repositoryFor = Turnover.class, nature = NatureOfService.DOMAIN)
public class TurnoverRepository extends UdoDomainRepositoryAndFactory<Turnover> {

    public TurnoverRepository() {
        super(TurnoverRepository.class, Turnover.class);
    }

    public Turnover findOrCreate(
            final TurnoverReportingConfig config,
            final LocalDate turnoverDate,
            final Type type,
            final Frequency frequency,
            final Status status,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal netAmount,
            final BigDecimal grossAmount,
            final BigInteger purchaseCount,
            final String comments,
            final boolean nonComparable){
        Turnover turnover = findUnique(config, turnoverDate, type);
        if (turnover==null){
            turnover = create(config, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, netAmount, grossAmount, purchaseCount, comments, nonComparable);
        }
        return turnover;
    }

    public static class TurnoverUpsertEvent
            extends ActionDomainEvent<Turnover> {}

    public Turnover upsert(
            final TurnoverReportingConfig config,
            final LocalDate turnoverDate,
            final Type type,
            final Frequency frequency,
            final Status status,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal netAmount,
            final BigDecimal grossAmount,
            final BigInteger purchaseCount,
            final String comments,
            final boolean nonComparable){
        Turnover turnover = findUnique(config, turnoverDate, type);
        if (turnover==null){
            turnover = create(config, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, netAmount, grossAmount, purchaseCount, comments, nonComparable);
        }
        /* NOTE: config, date, type = unique,
        we consider frequency and currency immutable */
        turnover.setStatus(status);
        turnover.setReportedAt(reportedAt);
        turnover.setReportedBy(reportedBy);
        turnover.setNetAmount(netAmount);
        turnover.setGrossAmount(grossAmount);
        turnover.setPurchaseCount(purchaseCount);
        turnover.setComments(comments);
        turnover.setNonComparable(nonComparable);

        // fire upsert event

        final TurnoverUpsertEvent event = new TurnoverUpsertEvent();
        event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
        event.setSource(turnover);
        eventBusService.post(event);

        return turnover;
    }

    public Turnover create(
            final TurnoverReportingConfig config,
            final LocalDate turnoverDate,
            final Type type,
            final Frequency frequency,
            final Status status,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal turnoverNetAmount,
            final BigDecimal turnoverGrossAmount,
            final BigInteger turnoverPurchaseCount,
            final String comments,
            final boolean nonComparable) {
        Turnover turnover = new Turnover(config, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, turnoverNetAmount, turnoverGrossAmount, turnoverPurchaseCount, comments, nonComparable);
        serviceRegistry2.injectServicesInto(turnover);
        repositoryService.persistAndFlush(turnover);
        return turnover;
    }

    private static final Logger LOG = LoggerFactory.getLogger(TurnoverRepository.class);

    public Turnover createNewEmpty(
            final TurnoverReportingConfig config,
            final LocalDate turnoverDate,
            final Type type,
            final Frequency frequency,
            final Currency currency) {
        Turnover turnover = findUnique(config, turnoverDate, type);
        if (turnover==null) {
            turnover = new Turnover(config, turnoverDate, type, frequency, currency, Status.NEW);
            serviceRegistry2.injectServicesInto(turnover);
            repositoryService.persistAndFlush(turnover);
            LOG.info(String.format("Empty turnover created with type %s, date %s for lease %s", turnover.getType(), turnover.getDate(), turnover.getOccupancy().getLease().getReference()));
        }
        return turnover;
    }

    public Turnover findUnique(final TurnoverReportingConfig config, final LocalDate turnoverDate, final Type type) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Turnover.class,
                        "findUnique",
                        "config", config,
                        "date", turnoverDate,
                        "type", type));
    }

    public List<Turnover> findApprovedByConfigAndTypeAndFrequencyBeforeDate(final TurnoverReportingConfig config, final Type type, final Frequency frequency, final LocalDate turnoverDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndFrequencyAndStatusBeforeDate",
                        "config", config,
                        "type", type,
                        "frequency", frequency,
                        "status", Status.APPROVED,
                        "threshold", turnoverDate)); //NOTE: we changed the parameter name to be different from the property name because of a bug in Datanucleus ... (otherwise the order by close in the produced query holds the hardcoded value of the variable).
    }

    public List<Turnover> findByOccupancy(final Occupancy occupancy) {
        List<Turnover> result = new ArrayList<>();
        turnoverReportingConfigRepository.findByOccupancy(occupancy).forEach(cf->{
            result.addAll(findByConfig(cf));
        });
        return result;
    }

    public List<Turnover> findByConfig(final TurnoverReportingConfig config) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfig",
                        "config", config));
    }

    public List<Turnover> findByConfigWithStatusNew(final TurnoverReportingConfig config) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndStatus",
                        "config", config,
                        "status", Status.NEW));
    }

    public List<Turnover> findByConfigAndTypeWithStatusNew(final TurnoverReportingConfig config, final Type type) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndStatus",
                        "config", config,
                        "type", type,
                        "status", Status.NEW));
    }

    public List<Turnover> findByConfigAndTypeAndDateWithStatusNew(final TurnoverReportingConfig config, final Type type, final LocalDate date) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndDateAndStatus",
                        "config", config,
                        "type", type,
                        "date", date,
                        "status", Status.NEW));
    }

    public List<Turnover> findByConfigAndTypeAndDate(final TurnoverReportingConfig config, final Type type, final LocalDate date) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndDate",
                        "config", config,
                        "type", type,
                        "date", date ));
    }

    public List<Turnover> findApprovedByOccupancyAndTypeAndFrequencyAndPeriod(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate periodStartDate, final LocalDate periodEndDate){
        final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository
                .findByOccupancyAndTypeAndFrequency(occupancy, type, frequency);
        List<Turnover> result = new ArrayList<>();
        configs.forEach(
                c->{
                    result.addAll(findByConfigAndTypeAndFrequencyAndStatusInPeriod(c, type, frequency, Status.APPROVED, periodStartDate, periodEndDate));
                });
        return result;
    }

    public List<Turnover> findApprovedByOccupancyAndTypeAndFrequency(
            final Occupancy occupancy,
            final Type type,
            final Frequency frequency) {
        final List<TurnoverReportingConfig> configs = turnoverReportingConfigRepository
                .findByOccupancyAndTypeAndFrequency(occupancy, type, frequency);
        List<Turnover> result = new ArrayList<>();
        configs.forEach(
                c->{
                    result.addAll(findApprovedByConfigAndTypeAndFrequency(c, type, frequency));
                });
        return result;
    }

    public List<Turnover> findApprovedByConfigAndTypeAndFrequency(final TurnoverReportingConfig config, final Type type, final Frequency frequency) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndFrequencyAndStatus",
                        "config", config,
                        "type", type,
                        "frequency", frequency,
                        "status", Status.APPROVED));
    }

    public List<Turnover> findByConfigAndTypeAndFrequencyAndStatusInPeriod(final TurnoverReportingConfig config, final Type type, final Frequency frequency, final Status status, final LocalDate startDate, final LocalDate endDate){
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByConfigAndTypeAndFrequencyAndStatusInPeriod",
                        "config", config,
                        "type", type,
                        "frequency", frequency,
                        "status", status,
                        "startDate", startDate,
                        "endDate", endDate));
    }

    public List<Turnover> listAll() {
        return allInstances();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject
    EventBusService eventBusService;

    @Inject MeService meService;
}
