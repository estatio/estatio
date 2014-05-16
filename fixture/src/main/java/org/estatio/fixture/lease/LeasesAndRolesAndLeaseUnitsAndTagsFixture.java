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
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.lease.*;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class LeasesAndRolesAndLeaseUnitsAndTagsFixture extends SimpleFixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {

        Party manager = parties.findPartyByReference("JDOE");
        createLease("OXF-TOPMODEL-001", "Topmodel Lease", "OXF-001", "Topmodel", "FASHION", "WOMEN", "ACME", "TOPMODEL", new LocalDate(2010, 7, 15), new LocalDate(2022, 7, 14), true, true, manager, fixtureResults);
        createLease("OXF-MEDIAX-002", "Mediax Lease", "OXF-002", "Mediax", "ELECTRIC", "ELECTRIC", "ACME", "MEDIAX", new LocalDate(2008, 1, 1), new LocalDate(2017, 12, 31), true, true, manager, fixtureResults);
        createLease("OXF-POISON-003", "Poison Lease", "OXF-003", "Poison", "HEALT&BEAUTY", "PERFUMERIE", "ACME", "POISON", new LocalDate(2011, 1, 1), new LocalDate(2020, 12, 31), true, true, manager, fixtureResults);
        createLease("OXF-PRET-004", "Pret lease", "OXF-004", "Pret", "FASHION", "ALL", null, null, new LocalDate(2011, 7, 1), new LocalDate(2015, 6, 30), false, false, manager, fixtureResults);
        createLease("OXF-MIRACL-005", "Miracle lease", "OXF-005", "Miracle", "FASHION", "ALL", "ACME", "MIRACLE", new LocalDate(2013, 11, 7), new LocalDate(2023, 11, 6), false, true, manager, fixtureResults);
        createLease("KAL-POISON-001", "Poison Amsterdam", "KAL-001", "Poison", "HEALT&BEAUTY", "PERFUMERIE", "ACME", "POISON", new LocalDate(2011, 1, 1), new LocalDate(2020, 12, 31), true, true, manager, fixtureResults);

    }

    public Lease createLease(
            String reference, String name,
            String unitReference,
            String brand,
            String sector,
            String activity,
            String landlordReference,
            String tenantReference,
            LocalDate startDate,
            LocalDate endDate,
            boolean createManagerRole,
            boolean createLeaseUnitAndTags,
            Party manager, ExecutionContext fixtureResults) {

        UnitForLease unit = (UnitForLease) units.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);

        Lease lease = leases.newLease(reference, name, null, startDate, null, endDate, landlord, tenant);
        fixtureResults.add(this, lease.getReference(), lease);

        if (createManagerRole) {
            final AgreementRole role = lease.createRole(agreementRoleTypes.findByTitle(LeaseConstants.ART_MANAGER), manager, null, null);
            fixtureResults.add(this, role);
        }
        if (createLeaseUnitAndTags) {
            Occupancy occupancy = occupancies.newOccupancy(lease, unit, startDate);
            occupancy.setBrandName(brand);
            occupancy.setSectorName(sector);
            occupancy.setActivityName(activity);
            fixtureResults.add(this, occupancy);
        }

        if (leases.findLeaseByReference(reference) == null) {
            throw new RuntimeException("could not find lease reference='" + reference + "'");
        }
        return lease;
    }

    private Party findPartyByReferenceOrNameElseNull(String partyReference) {
        return partyReference != null ? parties.findPartyByReference(partyReference) : null;
    }

    // //////////////////////////////////////

    @Inject
    private Units<Unit> units;

    @Inject
    private Leases leases;

    @Inject
    private Occupancies occupancies;

    @Inject
    private Parties parties;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;
}
