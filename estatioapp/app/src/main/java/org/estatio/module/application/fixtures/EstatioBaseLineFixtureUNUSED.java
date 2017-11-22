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
package org.estatio.module.application.fixtures;

import java.util.List;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.module.base.fixtures.clock.TickingClockFixture;
import org.estatio.module.base.fixtures.security.EstatioSecurityModuleDemoFixture;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;

/**
 * Will reset to a fixed baseline of the {@link EstatioRefDataSetupFixtureUNUSED reference data}
 * but no transactional data.
 * <p/>
 * <p>
 * As a minor optimization, the script checks if any (immutable read-only) reference data exists, is only installs
 * it the first time (an idempotent operation).
 * </p>
 */
public class EstatioBaseLineFixtureUNUSED extends DiscoverableFixtureScript {

    private EstatioOperationalTeardownFixtureUNUSED teardownFixtureScript;

    public EstatioBaseLineFixtureUNUSED() {
        this(new EstatioOperationalTeardownFixtureUNUSED());
    }

    public EstatioBaseLineFixtureUNUSED(final EstatioOperationalTeardownFixtureUNUSED teardownFixtureScript) {
        super(null, "baseline");
        this.teardownFixtureScript = teardownFixtureScript;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new TickingClockFixture());


        teardown(executionContext);

        if (isRefDataPresent()) {
            return;
        }
        executionContext.executeChild(this, new EstatioRefDataSetupFixtureUNUSED());
        executionContext.executeChild(this, new EstatioSecurityModuleDemoFixture());
    }

    protected void teardown(final ExecutionContext executionContext) {
        executionContext.executeChild(this, teardownFixtureScript);
    }

    /**
     * Use the presence of any persisted {@link Currency} as the indicator as to whether
     * any reference data has previously been {@link EstatioRefDataSetupFixtureUNUSED setup}.
     */
    private boolean isRefDataPresent() {
        final List<Currency> currencyList = currencyRepository.allCurrencies();
        return !currencyList.isEmpty();
    }

    @javax.inject.Inject
    private CurrencyRepository currencyRepository;

}
