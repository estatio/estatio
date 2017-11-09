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

import org.incode.module.country.fixture.CountriesRefData;

import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForMediaXGb;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJohnSmithGb;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfMediaX002Gb extends LeaseAbstract {

    public static final String REF = "OXF-MEDIAX-002";

    public static final String UNIT_REF = PropertyAndOwnerAndManagerForOxfGb.unitReference("002");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_TENANT = OrganisationForMediaXGb.REF;

    public static final String BRAND = "Mediax";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.GBR;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new OrganisationForMediaXGb());
        executionContext.executeChild(this, new PersonAndRolesForJohnSmithGb());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());

        // exec
        Party manager = partyRepository.findPartyByReference(PersonAndRolesForJohnSmithGb.REF);

        final Lease lease = createLease(
                REF,
                "Mediax Lease",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "ELECTRIC",
                "ELECTRIC",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2008, 1, 1),
                ld(2017, 12, 31),
                true,
                true,
                manager,
                executionContext);

        addAddresses(lease);
    }

}
