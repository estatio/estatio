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
package org.estatio.fixture.currency;

import javax.inject.Inject;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;
import org.apache.isis.applib.fixturescripts.FixtureResultList;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;


public class CurrenciesFixture extends SimpleFixtureScript {

    @Override
    protected void doRun(String parameters, FixtureResultList fixtureResults) {
        createCurrency(fixtureResults, "EUR", "Euro");
        createCurrency(fixtureResults, "SEK", "Swedish krona");
        createCurrency(fixtureResults, "GBP", "Pound sterling");
        createCurrency(fixtureResults, "USD", "US dollar");
    }

    private void createCurrency(FixtureResultList fixtureResults, String reference, String name) {
        final Currency currency = currencies.createCurrency(reference, name);
        fixtureResults.add(this, currency.getReference(), currency);
    }

    @Inject
    Currencies currencies;

}
