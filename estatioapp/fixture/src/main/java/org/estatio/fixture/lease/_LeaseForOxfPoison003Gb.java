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
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;

import static org.estatio.integtests.VT.ld;

public class _LeaseForOxfPoison003Gb extends LeaseAbstract {

    public static final String REF = "OXF-POISON-003";
    public static final String UNIT_REFERENCE = _PropertyForOxfGb.unitReference("003");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldNl.REF;
    public static final String TENANT_REFERENCE = OrganisationForPoisonNl.REF;
    public static final String PARTY_REF_MANAGER = PersonForJohnDoeNl.REF;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new PersonForJohnDoeNl());
            executionContext.executeChild(this, new OrganisationForHelloWorldNl());
            executionContext.executeChild(this, new OrganisationForPoisonNl());
            executionContext.executeChild(this, new _PropertyForOxfGb());
        }

        // exec
        final Party manager = parties.findPartyByReference(PARTY_REF_MANAGER);
        createLease(
                REF, "Poison Lease",
                UNIT_REFERENCE,
                "Poison", "HEALT&BEAUTY", "PERFUMERIE",
                PARTY_REF_LANDLORD,
                TENANT_REFERENCE,
                ld(2011, 1, 1), ld(2020, 12, 31), true, true, manager,
                executionContext);
    }

}
