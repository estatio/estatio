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
package org.estatio.module.lease.fixtures.lease;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForJohnSmithGb;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfMediaX002Gb extends LeaseAbstract {

    public static final String REF = "OXF-MEDIAX-002";

    public static final String UNIT_REF = PropertyAndUnitsAndOwnerAndManagerForOxfGb.unitReference("002");
    public static final String PARTY_REF_LANDLORD = Organisation_enum.HelloWorldGb.getRef();
    public static final String PARTY_REF_TENANT = Organisation_enum.MediaXGb.getRef();

    public static final String BRAND = "Mediax";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = Country_enum.GBR.getRef3();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, Organisation_enum.HelloWorldGb.toFixtureScript());
        executionContext.executeChild(this, Organisation_enum.MediaXGb.toFixtureScript());
        executionContext.executeChild(this, new PersonAndRolesForJohnSmithGb());
        executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());

        // exec
        Party manager = partyRepository.findPartyByReference(Person_enum.JohnSmithGb.getRef());

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
