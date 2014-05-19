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

import org.estatio.fixture.asset.PropertiesAndUnitsForAll;
import org.estatio.fixture.financial.BankAccountsAndMandatesForAll;
import org.estatio.fixture.invoice.InvoicesAndInvoiceItemsForAll;
import org.estatio.fixture.lease.LeasesEtcForAll;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsForAll;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class EstatioDemoFixture extends CompositeFixtureScript {

    public EstatioDemoFixture() {
        super(null, "demo");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new EstatioBaseLineFixture(), executionContext);

        execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
        execute("properties", new PropertiesAndUnitsForAll(), executionContext);
        execute("leases", new LeasesEtcForAll(), executionContext);
        execute("invoices", new InvoicesAndInvoiceItemsForAll(), executionContext);
        execute("bank-accounts", new BankAccountsAndMandatesForAll(), executionContext);

    }
}
