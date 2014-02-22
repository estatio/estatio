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

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.dom.currency.Currencies;


public class CurrenciesFixture extends AbstractFixture {

    @Override
    public void install() {
        currencies.newCurrency("EUR", "Euro");
        currencies.newCurrency("SEK", "Swedish krona");
        currencies.newCurrency("GBP", "Pound sterling");
        currencies.newCurrency("USD", "US dollar");
    }

    Currencies currencies;
    
    public void injectCurrencies(final Currencies currencies) {
        this.currencies = currencies;
    }
    
    
}
