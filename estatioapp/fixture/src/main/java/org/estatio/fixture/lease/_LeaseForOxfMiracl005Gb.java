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

import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.party.Party;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForMiracleGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;

import static org.estatio.integtests.VT.ld;

public class _LeaseForOxfMiracl005Gb extends LeaseAbstract {

    public static final String REF = "OXF-MIRACL-005";
    public static final String UNIT_REF = _PropertyForOxfGb.unitReference("005");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldNl.REF;
    public static final String PARTY_REF_TENANT = OrganisationForMiracleGb.REF;

    public static final String BRAND = "Miracle";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.GBR;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new PersonForJohnDoeNl());
            executionContext.executeChild(this, new OrganisationForHelloWorldNl());
            executionContext.executeChild(this, new OrganisationForMiracleGb());
            executionContext.executeChild(this, new _PropertyForOxfGb());
        }

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoeNl.REF);
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
