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

public abstract class LeaseAndRolesAndLeaseUnitsAndTagsAbstract extends SimpleFixtureScript {

    protected Lease createLease(
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

    protected Party findPartyByReferenceOrNameElseNull(String partyReference) {
        return partyReference != null ? parties.findPartyByReference(partyReference) : null;
    }

    // //////////////////////////////////////

    @Inject
    protected Units<Unit> units;

    @Inject
    protected Leases leases;

    @Inject
    protected Occupancies occupancies;

    @Inject
    protected Parties parties;

    @Inject
    protected AgreementRoleTypes agreementRoleTypes;
}
