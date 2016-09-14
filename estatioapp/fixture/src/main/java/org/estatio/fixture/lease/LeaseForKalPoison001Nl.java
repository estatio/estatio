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
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.geography.CountriesRefData;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;

import static org.estatio.integtests.VT.ld;

public class LeaseForKalPoison001Nl extends LeaseAbstract {

    public static final String REF = "KAL-POISON-001";

    public static final String UNIT_REF = PropertyForKalNl.unitReference("001");
    public static final String PARTY_REF_LANDLORD = OrganisationForAcmeNl.REF;
    public static final String PARTY_REF_TENANT = OrganisationForPoisonNl.REF;
    public static final String PARTY_REF_MANAGER = PersonForJohnDoeNl.REF;

    public static final String BRAND = "Poison";
    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.INTERNATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.NLD;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new PersonForJohnDoeNl());
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new OrganisationForPoisonNl());
        executionContext.executeChild(this, new PropertyForKalNl());

        // exec
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);
        createLease(
                REF,
                "Poison Amsterdam",
                UNIT_REF,
                BRAND,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "HEALT&BEAUTY",
                "PERFUMERIE",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT,
                ld(2011, 1, 1),
                ld(2020, 12, 31),
                true,
                true,
                manager,
                executionContext);
    }

}
