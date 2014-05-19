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
package org.estatio.fixture.tax.refdata;

import java.math.BigDecimal;
import javax.inject.Inject;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.Taxes;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

import static org.estatio.integtests.VT.ld;


public class TaxesAndTaxRatesRefData extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        Tax tax = taxes.newTax("IT-VATSTD", "Value Added Tax (Standard)");
        executionContext.add(this, tax.getReference(), tax);

        final TaxRate taxRate = tax.newRate(ld(1980, 1, 1), BigDecimal.valueOf(19)).newRate(ld(2011, 9, 17), BigDecimal.valueOf(21));
        executionContext.add(this, taxRate.getExternalReference(), taxRate);
    }

    // //////////////////////////////////////

    @Inject
    private Taxes taxes;

}
