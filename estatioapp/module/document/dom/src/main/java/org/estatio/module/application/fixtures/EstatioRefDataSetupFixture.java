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

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.incode.module.country.fixture.CountriesRefData;
import org.incode.module.country.fixture.StatesRefData;

import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.charge.fixtures.ChargeGroupRefData;
import org.estatio.module.charge.fixtures.ChargeRefData;
import org.estatio.module.currency.fixtures.CurrenciesRefData;
import org.estatio.module.index.fixtures.IndexRefData;
import org.estatio.module.lease.fixtures.DocFragmentDemoFixture;
import org.estatio.module.tax.fixtures.TaxModule_setup;

public class EstatioRefDataSetupFixture extends DiscoverableFixtureScript {

    public EstatioRefDataSetupFixture() {
        super(null, "ref-data");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, "currencies", new CurrenciesRefData());
        executionContext.executeChild(this, "countries", new CountriesRefData());
        executionContext.executeChild(this, "states", new StatesRefData());
        executionContext.executeChild(this, "taxes", new TaxModule_setup());
        executionContext.executeChild(this, "chargegroups", new ChargeGroupRefData());
        executionContext.executeChild(this, "charges", new ChargeRefData());
        executionContext.executeChild(this, "incomingCharges", new IncomingChargeFixture());
        executionContext.executeChild(this, "indexs", new IndexRefData());
        executionContext.executeChild(this, "docFrags", new DocFragmentDemoFixture());

    }
}
