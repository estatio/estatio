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
package org.estatio.integtests.currency;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;
import org.estatio.fixture.currency.CurrenciesRefData;
import org.estatio.integtests.EstatioIntegrationTest;

public class CurrenciesTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new CurrenciesRefData());
            }
        });
    }

    @Inject
    Currencies currencies;

    public static class FindCurrency extends CurrenciesTest {

        @Test
        public void happyCase() throws Exception {
            Currency euro = currencies.findCurrency(CurrenciesRefData.EUR);
            assertThat(euro.getName(), is("Euro"));
        }
    }
}