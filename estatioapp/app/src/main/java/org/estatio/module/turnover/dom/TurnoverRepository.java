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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.lease.dom.Lease;

@DomainService(repositoryFor = Turnover.class, nature = NatureOfService.DOMAIN)
public class TurnoverRepository extends UdoDomainRepositoryAndFactory<Turnover> {

    public TurnoverRepository() {
        super(TurnoverRepository.class, Turnover.class);
    }

    public Turnover findOrCreate(
            final Lease lease,
            final LocalDate date,
            final BigDecimal amount,
            final Currency currency){
        Turnover turnover = findUnique(lease, date);
        if (turnover==null){
            turnover = create(lease, date, amount, currency);
        }
        return turnover;
    }

    public Turnover upsert(
            final Lease lease,
            final LocalDate date,
            final BigDecimal amount,
            final Currency currency){
        Turnover turnover = findUnique(lease, date);
        if (turnover==null){
            turnover = create(lease, date, amount, currency);
        }
        turnover.setAmount(amount);
        turnover.setCurrency(currency);
        return turnover;
    }

    public Turnover create(
            final Lease lease,
            final LocalDate date,
            final BigDecimal amount,
            final Currency currency) {
        Turnover turnover = new Turnover(lease, date, amount, currency);
        serviceRegistry2.injectServicesInto(turnover);
        repositoryService.persistAndFlush(turnover);
        return turnover;
    }

    private Turnover findUnique(final Lease lease, final LocalDate date) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Turnover.class,
                        "findUnique",
                        "lease", lease,
                        "date", date));
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    RepositoryService repositoryService;

}
