/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndBankAccountsAndCommunicationChannelsFixture;


public class EstatioTransactionalObjectsFixture extends AbstractFixture {

    @Override
    public void install() {
        
        final List<AbstractFixture> fixtures = Arrays.asList(
            new EstatioTransactionalObjectsTeardownFixture(),
            new PersonsAndOrganisationsAndBankAccountsAndCommunicationChannelsFixture(),
            new PropertiesAndUnitsFixture(),
            new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(),
            new InvoiceAndInvoiceItemFixture()
        );

        final FixturesInstallerDelegate installer = new FixturesInstallerDelegate().withOverride();
        for (AbstractFixture fixture : fixtures) {
            installer.addFixture(fixture);
        }
        installer.installFixtures(); 
        getContainer().flush();
    }
}
