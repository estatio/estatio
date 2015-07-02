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
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForPretGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;

import javax.inject.Inject;

import static org.estatio.integtests.VT.ld;

public class _LeaseForOxfPret004Gb extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-PRET-004";
    public static final String UNIT_REFERENCE = _PropertyForOxfGb.unitReference("004");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldNl.REF;
    public static final String PARTY_REF_TENANT = OrganisationForPretGb.REF;

    public static final String BRAND = "Pret-a-Partir";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.REGIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.FRA;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        if (isExecutePrereqs()) {
            executionContext.executeChild(this, new PersonForJohnDoeNl());
            executionContext.executeChild(this, new OrganisationForPretGb());
            executionContext.executeChild(this, new _PropertyForOxfGb());
        }

        // exec
        Party manager = parties.findPartyByReference(PersonForJohnDoeNl.REF);
        createLease(
                LEASE_REFERENCE,
                "Pret-a-Partir lease",
                UNIT_REFERENCE,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "FASHION",
                "ALL",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2011, 7, 1),
                ld(2014, 6, 30),
                false,
                false,
                manager,
                executionContext);
    }

    @Inject
    Parties parties;

}
