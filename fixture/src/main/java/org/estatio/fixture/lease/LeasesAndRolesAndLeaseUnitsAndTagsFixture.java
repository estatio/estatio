/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.UnitForLease;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

public class LeasesAndRolesAndLeaseUnitsAndTagsFixture extends AbstractFixture {

    private Party manager;

    @Override
    public void install() {

        manager = parties.findPartyByReference("JDOE");
        createLease("OXF-TOPMODEL-001", "Topmodel Lease", "OXF-001", "ACME", "TOPMODEL", new LocalDate(2010, 7, 15), new LocalDate(2022, 7, 14), true, true);
        createLease("OXF-MEDIAX-002", "Meadiax Lease", "OXF-002", "ACME", "MEDIAX", new LocalDate(2008, 1, 1), new LocalDate(2017, 12, 31), true, true);
        createLease("OXF-POISON-003", "Poison Lease", "OXF-003", "ACME", "POISON", new LocalDate(2011, 1, 1), new LocalDate(2020, 12, 31), true, true);
        createLease("OXF-PRET-004", "Pret lease", "OXF-004", null, null, new LocalDate(2011, 7, 1), new LocalDate(2015, 6, 30), false, false);
    }

    public Lease createLease(
            String reference, String name, 
            String unitReference, 
            String landlordReference, String tenantReference, 
            LocalDate startDate, LocalDate endDate, 
            boolean createManagerRole, boolean createLeaseUnitAndTags) {
        UnitForLease unit = (UnitForLease) units.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);
        Lease lease = leases.newLease(reference, name, startDate, null, endDate, landlord, tenant);

        if(createManagerRole) {
            lease.newRole(agreementRoleTypes.findByTitle(LeaseConstants.ART_MANAGER), manager, null, null);
        }
        if(createLeaseUnitAndTags) {
            Occupancy lu = leaseUnits.newOccupancy(lease, unit);
            lu.setBrandName(tenantReference);
            lu.setSectorName("OTHER");
            lu.setActivityName("OTHER");
        }
        
        if (leases.findLeaseByReference(reference) == null) {
            throw new RuntimeException("could not find lease reference='" + reference + "'");
        }
        return lease;
    }

    private Party findPartyByReferenceOrNameElseNull(String partyReference) {
        return partyReference != null? parties.findPartyByReference(partyReference): null;
    }


    private Units<Unit> units;

    public void injectUnits(final Units<Unit> units) {
        this.units = units;
    }

    private Leases leases;

    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private Occupancies leaseUnits;

    public void injectLeaseUnits(final Occupancies leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private Parties parties;

    public void injectParties(final Parties parties) {
        this.parties = parties;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

}
