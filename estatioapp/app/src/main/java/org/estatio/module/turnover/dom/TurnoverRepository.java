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
            final LocalDate date,
            final Type type,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal turnoverNetAmount,
            final BigDecimal turnoverGrossAmount,
            final BigInteger turnoverPurchaseCount,
            final String comments,
            final boolean nonComparable){
        Turnover turnover = findUnique(occupancy, reportedAt);
        if (turnover==null){
            turnover = create(occupancy, date, type, reportedAt, reportedBy, currency, turnoverNetAmount, turnoverGrossAmount, turnoverPurchaseCount, comments, nonComparable);
        }
        return turnover;
    }

    public Turnover create(
            final Occupancy occupancy,
            final LocalDate date,
            final Type type,
            final LocalDateTime reportedAt,
            final String reportedBy,
            final Currency currency,
            final BigDecimal turnoverNetAmount,
            final BigDecimal turnoverGrossAmount,
            final BigInteger turnoverPurchaseCount,
            final String comments,
            final boolean nonComparable) {
        Turnover turnover = new Turnover(occupancy, date, type, reportedAt, reportedBy, currency, turnoverNetAmount, turnoverGrossAmount, turnoverPurchaseCount, comments, nonComparable);
        serviceRegistry2.injectServicesInto(turnover);
        repositoryService.persistAndFlush(turnover);
        return turnover;
    }

    private Turnover findUnique(final Occupancy occupancy, final LocalDateTime reportedAt) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Turnover.class,
                        "findUnique",
                        "occupancy", occupancy,
                        "reportedAt", reportedAt));
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

}
