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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioFixtureScript;

import static org.estatio.integtests.VT.bd;
import static org.estatio.integtests.VT.ld;


public class TaxRefData extends EstatioFixtureScript {

    private static final String SUFFIX_VATSTD = "-VATSTD";

    public static final String vatStdFor(final String country2AlphaCode) {
        return country2AlphaCode.toUpperCase() + SUFFIX_VATSTD;
    }

    public static final String IT_VATSTD = "IT-VATSTD";
    public static final String NL_VATSTD = "NL-VATSTD";
    public static final String GB_VATSTD = "GB-VATSTD";
    public static final String FR_VATSTD = "FR-VATSTD";
    public static final String SE_VATSTD = "SE-VATSTD";

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final List<ApplicationTenancy> countryTenancies = estatioApplicationTenancyRepository.allCountryTenancies();

        for (final ApplicationTenancy countryTenancy : countryTenancies) {

            final String country2AlphaCode = countryTenancy.getPath().substring(1).toUpperCase();

            final String reference = country2AlphaCode + SUFFIX_VATSTD;
            final Tax tax = taxes.newTax(
                    reference,
                    "Value Added Tax (Standard, " + country2AlphaCode + ")",
                    countryTenancy);
            executionContext.addResult(this, tax.getReference(), tax);

            final TaxRate taxRate1 = tax.newRate(ld(1980, 1, 1), bd(19));
            final TaxRate taxRate2 = taxRate1.newRate(ld(2011, 9, 17), bd(21));

            executionContext.addResult(this, taxRate1);
            executionContext.addResult(this, taxRate2);
        }
    }

    // //////////////////////////////////////

    @Inject
    private Taxes taxes;
    @Inject
    private ApplicationTenancies applicationTenancies;
    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

}
