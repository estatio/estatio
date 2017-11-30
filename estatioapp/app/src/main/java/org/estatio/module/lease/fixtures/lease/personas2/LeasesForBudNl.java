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
package org.estatio.module.lease.fixtures.lease.personas2;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.lease.fixtures.LeaseAbstract;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

public class LeasesForBudNl extends LeaseAbstract {

    public static final String REF1 = Lease_enum.BudPoison001Nl.getRef();
    public static final String REF2 = Lease_enum.BudMiracle002Nl.getRef();
    public static final String REF3 = Lease_enum.BudHello003Nl.getRef();
    public static final String REF4 = Lease_enum.BudDago004Nl.getRef();
    public static final String REF4A = Lease_enum.BudNlBank004Nl.getRef();
    public static final String REF5 = Lease_enum.BudHyper005Nl.getRef();
    public static final String REF6 = Lease_enum.BudHello006Nl.getRef();

    private static final String UNIT_REF7 = Lease_enum.BudHello006Nl.getPropertyAndUnits_d().getProperty_d().unitRef("007");

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, Person_enum.JohnDoeNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.AcmeNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.PoisonNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.MiracleNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.DagoBankNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.NlBankNl.toFixtureScript());
        executionContext.executeChild(this, OrganisationAndComms_enum.HyperNl.toFixtureScript());
        executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.BudNl.toFixtureScript());


        executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHello003Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudDago004Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.toFixtureScript());
        executionContext.executeChildT(this, Lease_enum.BudHello006Nl.toFixtureScript());

        // exec
//        final Party manager = partyRepository.findPartyByReference(Person_enum.JohnDoeNl.getRef());
//        createLease(
//                REF1,
//                "Poison Amsterdam",
//                UNIT_REF1,
//                "Poison",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "HEALT&BEAUTY",
//                "PERFUMERIE",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudPoison001Nl.getTenant_d().getRef(),
//                ld(2011, 1, 1),
//                ld(2020, 12, 31),
//                true,
//                true,
//                manager,
//                executionContext);
//
//        createLeaseWithOccupancyEndDate(
//                REF2,
//                "Miracle Amsterdam",
//                UNIT_REF2,
//                "Miracle",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "HEALT&BEAUTY",
//                "PERFUMERIE",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudMiracle002Nl.getTenant_d().getRef(),
//                ld(2011, 1, 1),
//                ld(2015, 06, 30),
//                ld(2015, 06, 30),
//                true,
//                true,
//                manager,
//                executionContext);

//        createLease(
//                REF3,
//                "Hello Amsterdam",
//                UNIT_REF3,
//                "Hello",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "IT",
//                "TELECOM",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudHello003Nl.getTenant_d().getRef(),
//                ld(2015, 4, 1),
//                ld(2020, 12, 31),
//                true,
//                true,
//                manager,
//                executionContext);

//        createLeaseWithOccupancyEndDate(
//                REF4,
//                "Dago Bank Amsterdam",
//                UNIT_REF4,
//                "Dago Bank",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "BANK",
//                "LOANS",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudDago004Nl.getTenant_d().getRef(),
//                ld(2011, 1, 1),
//                ld(2015, 6, 30),
//                ld(2015, 6, 30),
//                true,
//                true,
//                manager,
//                executionContext);

//        createOccupancyWithEndDate(
//                REF4,
//                UNIT_REF7,
//                "Dago Bank",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "BANK",
//                "LOANS",
//                ld(2011, 1, 1),
//                ld(2015, 6, 30),
//                executionContext
//                );

//        createLease(
//                REF4A,
//                "NL Bank Amsterdam",
//                UNIT_REF4,
//                "Nl Bank",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "BANK",
//                "LOANS",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudNlBank004Nl.getTenant_d().getRef(),
//                ld(2015, 10, 1),
//                ld(2020, 6, 30),
//                true,
//                true,
//                manager,
//                executionContext);

//        createLeaseWithOccupancyEndDate(
//                REF5,
//                "Hypermarkt Amsterdam",
//                UNIT_REF5,
//                "Nl Hypermarkt",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "SUPERMARKET",
//                "RETAIL",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudHello006Nl.getTenant_d().getRef(),
//                ld(2015, 4, 1),
//                ld(2015, 6, 30),
//                ld(2015, 6, 30),
//                true,
//                true,
//                manager,
//                executionContext);

//        createLeaseWithOccupancyEndDate(
//                REF6,
//                "Hello Amsterdam",
//                UNIT_REF6,
//                "Dago Bank",
//                BrandCoverage.INTERNATIONAL,
//                Country_enum.NLD.getRef3(),
//                "BANK",
//                "LOANS",
//                OrganisationAndComms_enum.AcmeNl.getRef(),
//                Lease_enum.BudDago004Nl.getTenant_d().getRef(),
//                ld(2011, 1, 1),
//                ld(2014, 12, 31),
//                ld(2014, 12, 31),
//                true,
//                true,
//                manager,
//                executionContext);
    }

}
