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
package org.estatio.fixture.invoice;

import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeasesEtcForOxfPret004;
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForPoison;
import org.estatio.integtests.VT;
import org.joda.time.LocalDate;

import static org.estatio.integtests.VT.ld;
import static org.estatio.integtests.VT.ldix;

public class InvoiceForOxfPoison003 extends InvoiceAbstract {

    public static final String SELLER_PARTY = OrganisationForHelloWorld.PARTY_REFERENCE;
    public static final String BUYER_PARTY = OrganisationForPoison.PARTY_REFERENCE;
    public static final String LEASE = LeaseForOxfPoison003.LEASE_REFERENCE;

    public static final LocalDate START_DATE = VT.ld(2012, 1, 1);

    public InvoiceForOxfPoison003() {
        this(null, null);
    }

    public InvoiceForOxfPoison003(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        execute(new OrganisationForHelloWorld(), executionContext);
        execute(new LeasesEtcForOxfPret004(), executionContext);

        // exec
        createInvoiceAndInvoiceItems(
                SELLER_PARTY, BUYER_PARTY, LEASE,
                "EUR", START_DATE, ldix(START_DATE, ld(2012, 4, 1)),
                executionContext);
    }

}
