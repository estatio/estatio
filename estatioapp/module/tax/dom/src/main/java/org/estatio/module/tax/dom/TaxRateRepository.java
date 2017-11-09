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
package org.estatio.module.tax.dom;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = TaxRate.class, nature = NatureOfService.DOMAIN)
public class TaxRateRepository extends UdoDomainRepositoryAndFactory<TaxRate> {

    public TaxRateRepository() {
        super(TaxRateRepository.class, TaxRate.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public TaxRate newRate(
            final Tax tax,
            final LocalDate startDate,
            final BigDecimal percentage) {
        TaxRate currentRate = tax.taxRateFor(startDate);
        TaxRate rate;
        if (currentRate == null || !startDate.equals(currentRate.getStartDate())) {
            rate = repositoryService.instantiate(TaxRate.class);
            rate.setTax(tax);
            rate.setStartDate(startDate);
            if(repositoryService.isPersistent(tax)) {
                repositoryService.persist(rate);
            }
        } else {
            rate = currentRate;
        }
        rate.setPercentage(percentage);
        if (currentRate != null) {
            TaxRate currentNextRate = currentRate.getNext();
            currentRate.modifyNext(rate);
            rate.modifyNext(currentNextRate);
        }
        return rate;
    }

    @Programmatic
    public TaxRate findTaxRateByTaxAndDate(final Tax tax, final LocalDate date) {
        return firstMatch("findByTaxAndDate", "tax", tax, "date", date);
    }

    @Inject
    RepositoryService repositoryService;

}
