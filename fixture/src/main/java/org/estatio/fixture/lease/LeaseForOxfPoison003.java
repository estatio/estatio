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
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForPoison;
import org.estatio.fixture.party.PersonForJohnDoe;

import static org.estatio.integtests.VT.ld;

public class LeaseForOxfPoison003 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-POISON-003";
    public static final String UNIT_REFERENCE = PropertyForOxf.unitReference("003");
    public static final String LANDLORD_REFERENCE = OrganisationForHelloWorld.PARTY_REFERENCE;
    public static final String TENANT_REFERENCE = OrganisationForPoison.PARTY_REFERENCE;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            execute(new PersonForJohnDoe(), executionContext);
            execute(new OrganisationForHelloWorld(), executionContext);
            execute(new OrganisationForPoison(), executionContext);
            execute(new PropertyForOxf(), executionContext);
        }

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
        createLease(
                LEASE_REFERENCE, "Poison Lease",
                UNIT_REFERENCE,
                "Poison", "HEALT&BEAUTY", "PERFUMERIE",
                LANDLORD_REFERENCE,
                TENANT_REFERENCE,
                ld(2011, 1, 1), ld(2020, 12, 31), true, true, manager,
                executionContext);
    }

}
