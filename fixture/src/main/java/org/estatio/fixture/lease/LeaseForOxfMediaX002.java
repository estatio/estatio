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
import org.estatio.fixture.party.OrganisationForMediaX;
import org.estatio.fixture.party.PersonForJohnDoe;

import static org.estatio.integtests.VT.ld;

public class LeaseForOxfMediaX002 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-MEDIAX-002";
    public static final String UNIT_REFERENCE = PropertyForOxf.unitReference("002");
    public static final String LANDLORD_REFERENCE = OrganisationForHelloWorld.PARTY_REFERENCE;
    public static final String TENANT_REFERENCE = OrganisationForMediaX.PARTY_REFERENCE;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        execute(new PersonForJohnDoe(), executionContext);
        execute(new OrganisationForHelloWorld(), executionContext);
        execute(new OrganisationForMediaX(), executionContext);
        execute(new PropertyForOxf(), executionContext);

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoe.PARTY_REFERENCE);
        createLease(
                LEASE_REFERENCE, "Mediax Lease",
                UNIT_REFERENCE,
                "Mediax", "ELECTRIC", "ELECTRIC",
                LANDLORD_REFERENCE,
                TENANT_REFERENCE,
                ld(2008, 1, 1), ld(2017, 12, 31), true, true, manager,
                executionContext);
    }

}
