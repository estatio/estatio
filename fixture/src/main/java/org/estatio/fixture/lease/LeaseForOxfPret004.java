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
package org.estatio.fixture.lease;

import org.estatio.dom.party.Party;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.party.OrganisationForPret;
import org.estatio.fixture.party.PersonForJohnDoe;

import static org.estatio.integtests.VT.ld;

public class LeaseForOxfPret004 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-PRET-004";
    public static final String UNIT_REFERENCE = PropertyForOxf.unitReference("004");

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executeChild(new PersonForJohnDoe(), executionContext);
            executeChild(new OrganisationForPret(), executionContext);
            executeChild(new PropertyForOxf(), executionContext);
        }

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
        createLease(
                LEASE_REFERENCE, "Pret lease",
                UNIT_REFERENCE,
                "Pret", "FASHION", "ALL",
                null,
                null,
                ld(2011, 7, 1), ld(2015, 6, 30), false, false, manager,
                executionContext);
    }

}
