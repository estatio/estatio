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

import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJohnSmithGb;
import org.estatio.module.application.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForMiracleGb;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfMiracl005Gb extends LeaseAbstract {

    public static final String REF = "OXF-MIRACL-005";
    public static final String UNIT_REF = PropertyAndOwnerAndManagerForOxfGb.unitReference("005");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_TENANT = OrganisationForMiracleGb.REF;

    public static final String BRAND = "Miracle";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.GBR;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new OrganisationForMiracleGb());
        executionContext.executeChild(this, new PersonAndRolesForJohnSmithGb());
        executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());

        // exec

        Party manager = partyRepository.findPartyByReference(PersonAndRolesForJohnSmithGb.REF);

        createLease(
                REF,
                "Miracle lease",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "FASHION",
                "ALL",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2013, 11, 7),
                ld(2023, 11, 6),
                false,
                true,
                manager,
                executionContext);
    }

}
