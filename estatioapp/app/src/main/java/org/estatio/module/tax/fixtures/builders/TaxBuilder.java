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
package org.estatio.module.tax.fixtures.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"ref"}, callSuper = false)
@Accessors(chain = true)
public final class TaxBuilder extends BuilderScriptAbstract<Tax, TaxBuilder> {

    @Getter @Setter
    String ref;
    @Getter @Setter
    String description;

    @Getter @Setter
    Country country;
    @Getter @Setter
    ApplicationTenancy applicationTenancy;

    @Getter @Setter
    List<RateData> rates;

    @Data
    public static class RateData {
        private final LocalDate date;
        private final BigDecimal rateValue;
    }


    @Getter
    Tax object;

    @Override
    protected void doExecute(final ExecutionContext executionContext) {

        checkParam("ref", executionContext, String.class);
        checkParam("country", executionContext, Country.class);
        checkParam("applicationTenancy", executionContext, ApplicationTenancy.class);
        checkParam("rates", executionContext, List.class);

        Tax tax = taxRepository.findByReference(ref);
        if(tax == null) {

            defaultParam("description", executionContext, getRef());

            final String name = "Value Added Tax (Standard, " + country.getReference() + ")";
            tax = taxRepository.newTax(ref, name, applicationTenancy);
            for (RateData rate : rates) {
                final TaxRate taxRate = tax.newRate(rate.date, rate.rateValue);
                tax.getRates().add(taxRate);
            }
        }

        object = tax;

    }

    @Inject
    TaxRepository taxRepository;

}
