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
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(repositoryFor = Turnover.class, nature = NatureOfService.DOMAIN)
public class TurnoverRepository extends UdoDomainRepositoryAndFactory<Turnover> {

    public TurnoverRepository() {
        super(TurnoverRepository.class, Turnover.class);
    }

    public Turnover findOrCreate(
            final Occupancy occupancy,
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
        Turnover turnover = findUnique(occupancy, turnoverDate, type);
        if (turnover==null){
            turnover = create(occupancy, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, netAmount, grossAmount, purchaseCount, comments, nonComparable);
        }
        return turnover;
    }

    public Turnover upsert(
            final Occupancy occupancy,
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
        Turnover turnover = findUnique(occupancy, turnoverDate, type);
        if (turnover==null){
            turnover = create(occupancy, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, netAmount, grossAmount, purchaseCount, comments, nonComparable);
        }
        /* NOTE: occupancy, date, type = unique,
        we consider frequency and currency immutable */
        turnover.setStatus(status);
        turnover.setReportedAt(reportedAt);
        turnover.setReportedBy(reportedBy);
        turnover.setNetAmount(netAmount);
        turnover.setGrossAmount(grossAmount);
        turnover.setPurchaseCount(purchaseCount);
        turnover.setComments(comments);
        turnover.setNonComparable(nonComparable);
        return turnover;
    }

    public Turnover create(
            final Occupancy occupancy,
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
        Turnover turnover = new Turnover(occupancy, turnoverDate, type, frequency, status, reportedAt, reportedBy, currency, turnoverNetAmount, turnoverGrossAmount, turnoverPurchaseCount, comments, nonComparable);
        serviceRegistry2.injectServicesInto(turnover);
        repositoryService.persistAndFlush(turnover);
        return turnover;
    }

    public Turnover createNewEmpty(
            final Occupancy occupancy,
            final LocalDate turnoverDate,
            final Type type,
            final Frequency frequency,
            final Currency currency) {
        Turnover turnover = findUnique(occupancy, turnoverDate, type);
        if (turnover==null) {
            turnover = new Turnover(occupancy, turnoverDate, type, frequency, currency, Status.NEW);
            serviceRegistry2.injectServicesInto(turnover);
            repositoryService.persistAndFlush(turnover);
        }
        return turnover;
    }

    public Turnover findUnique(final Occupancy occupancy, final LocalDate turnoverDate, final Type type) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Turnover.class,
                        "findUnique",
                        "occupancy", occupancy,
                        "date", turnoverDate,
                        "type", type));
    }

    public List<Turnover> findApprovedByOccupancyAndTypeAndFrequencyBeforeDate(final Occupancy occupancy, final Type type, final Frequency frequency, final LocalDate turnoverDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByOccupancyAndTypeAndFrequencyAndStatusBeforeDate",
                        "occupancy", occupancy,
                        "type", type,
                        "frequency", frequency,
                        "status", Status.APPROVED,
                        "threshold", turnoverDate)); //NOTE: we changed the parameter name to be different from the property name because of a bug in Datanucleus ... (otherwise the order by close in the produced query holds the hardcoded value of the variable).
    }

    public List<Turnover> findByOccupancy(final Occupancy occupancy) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByOccupancy",
                        "occupancy", occupancy));
    }

    public List<Turnover> findByOccupancyWithStatusNew(final Occupancy occupancy) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByOccupancyAndStatus",
                        "occupancy", occupancy,
                        "status", Status.NEW));
    }

    public List<Turnover> findByOccupancyAndTypeWithStatusNew(final Occupancy occupancy, final Type type) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByOccupancyAndTypeAndStatus",
                        "occupancy", occupancy,
                        "type", type,
                        "status", Status.NEW));
    }

    public List<Turnover> findByOccupancyAndTypeAndDateWithStatusNew(final Occupancy occupancy, final Type type, final LocalDate date) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Turnover.class,
                        "findByOccupancyAndTypeAndDateAndStatus",
                        "occupancy", occupancy,
                        "type", type,
                        "date", date,
                        "status", Status.NEW));
    }

    public List<Turnover> listAll() {
        return allInstances();
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;
}
