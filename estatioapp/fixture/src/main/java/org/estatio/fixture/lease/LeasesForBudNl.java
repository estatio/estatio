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

import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.party.Party;
import org.estatio.fixture.asset.PropertyForBudNl;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.OrganisationForDagoBankNl;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForHyperNl;
import org.estatio.fixture.party.OrganisationForMiracleNl;
import org.estatio.fixture.party.OrganisationForNlBankNl;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.PersonForJohnDoeNl;

import static org.incode.module.base.integtests.VT.ld;

public class LeasesForBudNl extends LeaseAbstract {

    public static final String REF1 = "BUD-POISON-001";
    public static final String REF2 = "BUD-MIRACLE-002";
    public static final String REF3 = "BUD-HELLO-003";
    public static final String REF4 = "BUD-DAGO-004";
    public static final String REF4A = "BUD-NLBANK-004";
    public static final String REF5 = "BUD-HYPER-005";
    public static final String REF6 = "BUD-HELLO-006";
    public static final String REF7 = "BUD-DAGO-007";

    public static final String UNIT_REF1 = PropertyForBudNl.unitReference("001");
    public static final String UNIT_REF2 = PropertyForBudNl.unitReference("002");
    public static final String UNIT_REF3 = PropertyForBudNl.unitReference("003");
    public static final String UNIT_REF4 = PropertyForBudNl.unitReference("004");
    public static final String UNIT_REF5 = PropertyForBudNl.unitReference("005");
    public static final String UNIT_REF6 = PropertyForBudNl.unitReference("006");
    public static final String UNIT_REF7 = PropertyForBudNl.unitReference("007");

    public static final String PARTY_REF_TENANT1 = OrganisationForPoisonNl.REF;
    public static final String PARTY_REF_TENANT2 = OrganisationForMiracleNl.REF;
    public static final String PARTY_REF_TENANT3 = OrganisationForHelloWorldNl.REF;
    public static final String PARTY_REF_TENANT4 = OrganisationForDagoBankNl.REF;
    public static final String PARTY_REF_TENANT4A = OrganisationForNlBankNl.REF;
    public static final String PARTY_REF_TENANT5 = OrganisationForHyperNl.REF;

    public static final String PARTY_REF_LANDLORD = OrganisationForAcmeNl.REF;
    public static final String PARTY_REF_MANAGER = PersonForJohnDoeNl.REF;

    public static final String BRAND1 = "Poison";
    public static final String BRAND2 = "Miracle";
    public static final String BRAND3 = "Hello";
    public static final String BRAND4 = "Dago Bank";
    public static final String BRAND4A = "Nl Bank";
    public static final String BRAND5 = "Nl Hypermarkt";

    public static final BrandCoverage BRAND_COVERAGE = BrandCoverage.INTERNATIONAL;
    public static final String COUNTRY_OF_ORIGIN_REF = CountriesRefData.NLD;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new PersonForJohnDoeNl());
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new OrganisationForPoisonNl());
        executionContext.executeChild(this, new OrganisationForMiracleNl());
        executionContext.executeChild(this, new OrganisationForHelloWorldNl());
        executionContext.executeChild(this, new OrganisationForDagoBankNl());
        executionContext.executeChild(this, new OrganisationForNlBankNl());
        executionContext.executeChild(this, new OrganisationForHyperNl());
        executionContext.executeChild(this, new PropertyForBudNl());

        // exec
        final Party manager = partyRepository.findPartyByReference(PARTY_REF_MANAGER);
        createLease(
                REF1,
                "Poison Amsterdam",
                UNIT_REF1,
                BRAND1,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "HEALT&BEAUTY",
                "PERFUMERIE",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT1,
                ld(2011, 1, 1),
                ld(2020, 12, 31),
                true,
                true,
                manager,
                executionContext);

        createLeaseWithOccupancyEndDate(
                REF2,
                "Miracle Amsterdam",
                UNIT_REF2,
                BRAND2,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "HEALT&BEAUTY",
                "PERFUMERIE",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT2,
                ld(2011, 1, 1),
                ld(2015, 06, 30),
                ld(2015, 06, 30),
                true,
                true,
                manager,
                executionContext);

        createLease(
                REF3,
                "Hello Amsterdam",
                UNIT_REF3,
                BRAND3,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "IT",
                "TELECOM",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT3,
                ld(2015, 4, 1),
                ld(2020, 12, 31),
                true,
                true,
                manager,
                executionContext);

        createLeaseWithOccupancyEndDate(
                REF4,
                "Dago Bank Amsterdam",
                UNIT_REF4,
                BRAND4,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "BANK",
                "LOANS",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT4,
                ld(2011, 1, 1),
                ld(2015, 6, 30),
                ld(2015, 6, 30),
                true,
                true,
                manager,
                executionContext);

        createOccupancyWithEndDate(
                REF4,
                UNIT_REF7,
                BRAND4,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "BANK",
                "LOANS",
                ld(2011, 1, 1),
                ld(2015, 6, 30),
                executionContext
                );

        createLease(
                REF4A,
                "NL Bank Amsterdam",
                UNIT_REF4,
                BRAND4A,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "BANK",
                "LOANS",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT4A,
                ld(2015, 10, 1),
                ld(2020, 6, 30),
                true,
                true,
                manager,
                executionContext);

        createLeaseWithOccupancyEndDate(
                REF5,
                "Hypermarkt Amsterdam",
                UNIT_REF5,
                BRAND5,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "SUPERMARKET",
                "RETAIL",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT5,
                ld(2015, 4, 1),
                ld(2015, 6, 30),
                ld(2015, 6, 30),
                true,
                true,
                manager,
                executionContext);

        createLeaseWithOccupancyEndDate(
                REF6,
                "Hello Amsterdam",
                UNIT_REF6,
                BRAND4,
                BRAND_COVERAGE,
                COUNTRY_OF_ORIGIN_REF,
                "BANK",
                "LOANS",
                PARTY_REF_LANDLORD,
                PARTY_REF_TENANT4,
                ld(2011, 1, 1),
                ld(2014, 12, 31),
                ld(2014, 12, 31),
                true,
                true,
                manager,
                executionContext);
    }

}
