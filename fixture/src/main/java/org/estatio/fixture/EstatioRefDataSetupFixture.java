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

import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.charge.ChargeAndChargeGroupFixture;
import org.estatio.fixture.currency.CurrenciesFixture;
import org.estatio.fixture.geography.CountriesAndStatesFixture;
import org.estatio.fixture.index.IndexAndIndexBaseAndIndexValueFixture;
import org.estatio.fixture.link.LinksFixture;
import org.estatio.fixture.tax.TaxesAndTaxRatesFixture;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class EstatioRefDataSetupFixture extends CompositeFixtureScript {

    public EstatioRefDataSetupFixture() {
        super(null, "ref-data");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute("currencies", new CurrenciesFixture(), executionContext);
        execute("countries", new CountriesAndStatesFixture(), executionContext);
        execute("italy-refdata", new EstatioRefDataForItalySetupFixture(), executionContext);
        execute("agreement-refdata", new AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture(), executionContext);
        execute("tax-refdata", new TaxesAndTaxRatesFixture(), executionContext);
        execute("charge-refdata", new ChargeAndChargeGroupFixture(), executionContext);
        execute("index-refdata", new IndexAndIndexBaseAndIndexValueFixture(), executionContext);
        execute("links", new LinksFixture(), executionContext);
    }
}
