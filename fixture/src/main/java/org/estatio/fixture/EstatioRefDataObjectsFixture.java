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

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.core.runtime.fixtures.FixturesInstallerDelegate;

import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.asset.registration.FixedAssetRegistrationTypeForItalyFixture;
import org.estatio.fixture.charge.ChargeAndChargeGroupFixture;
import org.estatio.fixture.currency.CurrenciesFixture;
import org.estatio.fixture.geography.CountriesAndStatesFixture;
import org.estatio.fixture.index.IndexAndIndexBaseAndIndexValueFixture;
import org.estatio.fixture.lease.LeaseTypeForItalyFixture;
import org.estatio.fixture.link.LinksFixture;
import org.estatio.fixture.tax.TaxesAndTaxRatesFixture;

public class EstatioRefDataObjectsFixture extends AbstractFixture {

    @Override
    public void install() {

        final List<AbstractFixture> fixtures = Arrays.asList(
                new EstatioTransactionalObjectsTeardownFixture(),
                new EstatioRefDataObjectsTeardownFixture(),
                new CurrenciesFixture(),
                new CountriesAndStatesFixture(),
                new FixedAssetRegistrationTypeForItalyFixture(),
                new LeaseTypeForItalyFixture(),
                new AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture(),
                new TaxesAndTaxRatesFixture(),
                new LinksFixture(),
                new ChargeAndChargeGroupFixture(),
                new IndexAndIndexBaseAndIndexValueFixture()
                );

        final FixturesInstallerDelegate installer = new FixturesInstallerDelegate().withOverride();
        for (AbstractFixture fixture : fixtures) {
            installer.addFixture(fixture);
        }
        installer.installFixtures();
        getContainer().flush();
    }

}
