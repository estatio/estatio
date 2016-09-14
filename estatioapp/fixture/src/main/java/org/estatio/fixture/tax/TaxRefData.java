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
package org.estatio.fixture.tax;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRepository;

import static org.estatio.integtests.VT.bd;
import static org.estatio.integtests.VT.ld;

public class TaxRefData extends FixtureScript {

    private static final String SUFFIX_VATSTD = "-VATSTD";
    public static final String NL_VATSTD = "NLD-VATSTD";

    public static final String vatStdFor(final String country2AlphaCode) {
        return country2AlphaCode.toUpperCase() + SUFFIX_VATSTD;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final List<ApplicationTenancy> countryTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        for (final ApplicationTenancy countryTenancy : countryTenancies) {

            final String countryPrefix = countryTenancy.getPath().substring(1).toUpperCase();

            final String reference = countryPrefix + SUFFIX_VATSTD;
            final Tax tax = taxRepository.newTax(
                    reference,
                    "Value Added Tax (Standard, " + countryPrefix + ")",
                    countryTenancy);
            executionContext.addResult(this, tax.getReference(), tax);

            final TaxRate taxRate1 = tax.newRate(ld(1980, 1, 1), bd(19));
            final TaxRate taxRate2 = taxRate1.newRate(ld(2011, 9, 17), bd(21));

            executionContext.addResult(this, taxRate1);
            executionContext.addResult(this, taxRate2);
        }
    }

    @Inject
    private TaxRepository taxRepository;
    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
