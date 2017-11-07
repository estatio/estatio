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

import javax.inject.Inject;

import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.incode.module.country.fixture.CountriesRefData;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForPretGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfPret004Gb extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-PRET-004";
    public static final String UNIT_REFERENCE = PropertyForOxfGb.unitReference("004");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_TENANT = OrganisationForPretGb.REF;

    public static final String BRAND = "Pret-a-Partir";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.REGIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.FRA;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForPretGb());
        executionContext.executeChild(this, new OrganisationForTopModelGb());
        executionContext.executeChild(this, new PersonForGinoVannelliGb());
        executionContext.executeChild(this, new PropertyForOxfGb());

        // exec
        Party manager = partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF);
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
    PartyRepository partyRepository;

}
