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
package org.estatio.fixture.currency.refdata;

import javax.inject.Inject;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;
import org.apache.isis.applib.fixturescripts.FixtureScript;


public class CurrenciesRefData extends FixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        createCurrency(fixtureResults, "EUR", "Euro");
        createCurrency(fixtureResults, "SEK", "Swedish krona");
        createCurrency(fixtureResults, "GBP", "Pound sterling");
        createCurrency(fixtureResults, "USD", "US dollar");
    }

    private void createCurrency(ExecutionContext fixtureResults, String reference, String name) {
        final Currency currency = currencies.findOrCreateCurrency(reference, name);
        fixtureResults.add(this, currency.getReference(), currency);
    }

    @Inject
    Currencies currencies;

}
