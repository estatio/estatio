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

import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.financial.BankAccountsAndMandatesFixture;
import org.estatio.fixture.invoice.InvoicesAndInvoiceItemsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;


/**
 * Non-reference data.
 */
public class EstatioOperationalSetupFixture extends CompositeFixtureScript {

    public EstatioOperationalSetupFixture() {
        super(null, "operational-setup");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
        execute("properties", new PropertiesAndUnitsFixture(), executionContext);
        execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
        execute("invoices", new InvoicesAndInvoiceItemsFixture(), executionContext);
        execute("bank-accounts", new BankAccountsAndMandatesFixture(), executionContext);
    }

}
