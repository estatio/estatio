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

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.tags.BrandCoverage;
import org.estatio.module.party.dom.Party;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;

import static org.incode.module.base.integtests.VT.ld;

public class LeaseForOxfTopModel001Gb extends LeaseAbstract {

    public static final String REF = "OXF-TOPMODEL-001";

    public static final String UNIT_REF = PropertyForOxfGb.unitReference("001");
    public static final String PARTY_REF_LANDLORD = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_TENANT = OrganisationForTopModelGb.REF;

    public static final String BRAND = "Topmodel";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.NATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.GBR;

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new PersonForGinoVannelliGb());
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new OrganisationForTopModelGb());
        executionContext.executeChild(this, new PersonForGinoVannelliGb());
        executionContext.executeChild(this, new PropertyForOxfGb());

        // exec
        Party manager = partyRepository.findPartyByReference(PersonForGinoVannelliGb.REF);
        Lease lease = createLease(
                REF,
                "Topmodel Lease",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "FASHION",
                "WOMEN",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2010, 7, 15),
                ld(2022, 7, 14),
                true,
                true,
                manager,
                executionContext);

        addAddresses(lease);
    }

}
