/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import java.math.BigDecimal;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.joda.time.LocalDate;


public class TaxFixture extends AbstractFixture {

    @Override
    public void install() {
       Tax tax = taxes.newTax("IT-VATSTD");
       tax.newRate(new LocalDate(1980,1,1), BigDecimal.valueOf(19)).newRate(new LocalDate(2011,9,17), BigDecimal.valueOf(21));
    }

    private Taxes taxes;

    public void injectTaxes(Taxes taxes) {
        this.taxes = taxes;
    }

}
