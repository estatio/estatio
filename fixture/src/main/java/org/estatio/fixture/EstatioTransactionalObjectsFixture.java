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
import org.estatio.fixture.financial.BankAccountAndMandateFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;


public class EstatioTransactionalObjectsFixture extends CompositeFixtureScript {

    public EstatioTransactionalObjectsFixture() {
        super(null, "transactional-objects");
    }

    @Override
    protected void addChildren() {

        add(new EstatioTransactionalObjectsTeardownFixture());
        add("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture());
        add("properties", new PropertiesAndUnitsFixture());
        add("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture());
        add("invoices", new InvoiceAndInvoiceItemFixture());
        add("bank-accounts", new BankAccountAndMandateFixture());

    }

}
