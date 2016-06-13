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

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.apptenancy.ApplicationTenancyInvariantsService;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitMenu;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.Country;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.LeaseTypes;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Sets up the lease, and the roles, and also the first occupancy.
 */
public abstract class LeaseAbstract extends EstatioFixtureScript {

    protected Lease createLease(
            String reference, String name,
            String unitReference,
            String brand,
            BrandCoverage brandCoverage,
            String countryOfOriginRef,
            String sector,
            String activity,
            String landlordReference,
            String tenantReference,
            LocalDate startDate,
            LocalDate endDate,
            boolean createManagerRole,
            boolean createLeaseUnitAndTags,
            Party manager, ExecutionContext fixtureResults) {

        Unit unit = unitRepository.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);

        final LeaseType leaseType = leaseTypes.findOrCreate("STD", "Standard", applicationTenancyRepository.findByPathCached(ApplicationTenancyInvariantsService.GLOBAL_APPLICATION_TENANCY_PATH));
        Lease lease = leases.newLease(
                unit.getApplicationTenancy(), reference,
                name,
                leaseType,
                startDate,
                null,
                endDate,
                landlord,
                tenant
        );
        fixtureResults.addResult(this, lease.getReference(), lease);

        if (createManagerRole) {
            final AgreementRole role = lease.createRole(agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_MANAGER), manager, null, null);
            fixtureResults.addResult(this, role);
        }
        if (createLeaseUnitAndTags) {
            Country countryOfOrigin = countryRepository.findCountry(countryOfOriginRef);
            Occupancy occupancy = occupancies.newOccupancy(lease, unit, startDate);
            occupancy.setBrandName(brand, brandCoverage, countryOfOrigin);
            occupancy.setSectorName(sector);
            occupancy.setActivityName(activity);
            fixtureResults.addResult(this, occupancy);
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
    protected UnitMenu unitMenu;
    @Inject
    protected UnitRepository unitRepository;

    @Inject
    protected Leases leases;

    @Inject
    protected Occupancies occupancies;

    @Inject
    protected Parties parties;

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    protected LeaseTypes leaseTypes;

    @Inject
    CountryRepository countryRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;
}
