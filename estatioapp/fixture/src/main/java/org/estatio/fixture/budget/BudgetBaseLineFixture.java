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
package org.estatio.fixture.budget;

import java.util.List;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.incode.module.integtestsupport.dom.ClockFixture;

import org.estatio.dom.currency.Currency;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.fixture.EstatioRefDataSetupFixture;
import org.estatio.fixture.security.EstatioSecurityModuleDemoFixture;


public class BudgetBaseLineFixture extends DiscoverableFixtureScript {

    private BudgetTeardownFixture teardownFixtureScript;

    public BudgetBaseLineFixture() {
        this(new BudgetTeardownFixture());
    }

    public BudgetBaseLineFixture(final BudgetTeardownFixture teardownFixtureScript) {
        super(null, "baseline");
        this.teardownFixtureScript = teardownFixtureScript;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
        teardown(executionContext);
        if (isRefDataPresent()) {
            return;
        }
        executionContext.executeChild(this, new EstatioRefDataSetupFixture());
        executionContext.executeChild(this, new EstatioSecurityModuleDemoFixture());
    }

    protected void teardown(final ExecutionContext executionContext) {
        executionContext.executeChild(this, teardownFixtureScript);
    }

    /**
     * Use the presence of any persisted {@link Currency} as the indicator as to whether
     * any reference data has previously been {@link EstatioRefDataSetupFixture setup}.
     */
    private boolean isRefDataPresent() {
        final List<Currency> currencyList = currencyRepository.allCurrencies();
        return !currencyList.isEmpty();
    }

    @javax.inject.Inject
    private CurrencyRepository currencyRepository;

}
