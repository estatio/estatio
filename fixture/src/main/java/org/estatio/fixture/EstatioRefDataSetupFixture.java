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
package org.estatio.fixture;

import org.estatio.fixture.charge.refdata.ChargeAndChargeGroupRefData;
import org.estatio.fixture.currency.refdata.CurrenciesRefData;
import org.estatio.fixture.geography.refdata.CountriesAndStatesRefData;
import org.estatio.fixture.index.refdata.IndexAndIndexBaseAndIndexValueRefData;
import org.estatio.fixture.link.refdata.LinksRefData;
import org.estatio.fixture.tax.refdata.TaxesAndTaxRatesRefData;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class EstatioRefDataSetupFixture extends DiscoverableFixtureScript {

    public EstatioRefDataSetupFixture() {
        super(null, "ref-data");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute("currencies", new CurrenciesRefData(), executionContext);
        execute("countries", new CountriesAndStatesRefData(), executionContext);
        execute("tax-refdata", new TaxesAndTaxRatesRefData(), executionContext);
        execute("charge-refdata", new ChargeAndChargeGroupRefData(), executionContext);
        execute("index-refdata", new IndexAndIndexBaseAndIndexValueRefData(), executionContext);
        execute("links", new LinksRefData(), executionContext);
    }
}
