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

import org.apache.isis.applib.clock.Clock;
import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.incode.module.integtestsupport.dom.ClockFixture;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.base.fixtures.security.EstatioSecurityModuleDemoFixture;
import org.estatio.module.base.platform.applib.TickingFixtureClock;

/**
 * Will reset to a fixed baseline of the {@link EstatioRefDataSetupFixture reference data}
 * but no transactional data.
 * <p/>
 * <p>
 * As a minor optimization, the script checks if any (immutable read-only) reference data exists, is only installs
 * it the first time (an idempotent operation).
 * </p>
 */
public class EstatioBaseLineFixture extends DiscoverableFixtureScript {

    private EstatioOperationalTeardownFixture teardownFixtureScript;

    public EstatioBaseLineFixture() {
        this(new EstatioOperationalTeardownFixture());
    }

    public EstatioBaseLineFixture(final EstatioOperationalTeardownFixture teardownFixtureScript) {
        super(null, "baseline");
        this.teardownFixtureScript = teardownFixtureScript;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        final Clock instance = Clock.getInstance();

        if(instance instanceof TickingFixtureClock) {
            TickingFixtureClock.reinstateExisting();
            executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
            TickingFixtureClock.replaceExisting();
        }

        if(instance instanceof FixtureClock) {
            executionContext.executeChild(this, ClockFixture.setTo("2014-05-18"));
        }


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
