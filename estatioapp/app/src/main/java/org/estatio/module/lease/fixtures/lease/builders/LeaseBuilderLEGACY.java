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
package org.estatio.module.lease.fixtures.lease.builders;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.fixtures.LeaseTypeForItalyRefData;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

public class LeaseBuilderLEGACY extends FixtureScript {

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private LeaseType leaseType;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private String duration;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private Party landlord;

    @Getter @Setter
    private Party tenant;

    @Getter @Setter
    private String atPath;

    @Getter
    private Lease lease;

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
            Party manager,
            ExecutionContext fixtureResults) {

        Unit unit = unitRepository.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);

        Lease lease = leaseRepository.newLease(unit.getApplicationTenancy(), reference, name, null, startDate, null, endDate, landlord, tenant);
        fixtureResults.addResult(this, lease.getReference(), lease);

        if (createManagerRole) {
            final AgreementRole role = lease.createRole(agreementRoleTypeRepository.findByTitle(
                    LeaseAgreementRoleTypeEnum.MANAGER.getTitle()), manager, null, null);
            fixtureResults.addResult(this, role);
        }
        if (createLeaseUnitAndTags) {
            Country countryOfOrigin = countryRepository.findCountry(countryOfOriginRef);
            Occupancy occupancy = occupancyRepository.newOccupancy(lease, unit, startDate);
            occupancy.setBrandName(brand, brandCoverage, countryOfOrigin);
            occupancy.setSectorName(sector);
            occupancy.setActivityName(activity);
            fixtureResults.addResult(this, occupancy);
        }

        if (leaseRepository.findLeaseByReference(reference) == null) {
            throw new RuntimeException("could not find lease reference='" + reference + "'");
        }
        return lease;
    }

    protected Party findPartyByReferenceOrNameElseNull(String partyReference) {
        return partyReference != null ? partyRepository.findPartyByReference(partyReference) : null;
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new LeaseTypeForItalyRefData());

        defaultParam("reference", executionContext, fakeDataService.strings().fixed(3));
        defaultParam("name", executionContext, fakeDataService.name().lastName() + " Mall");
        defaultParam("leaseType", executionContext, fakeDataService.collections().anyBounded(LeaseType.class));
        defaultParam("atPath", executionContext, ApplicationTenancy_enum.Gb.getPath());

        defaultParam("startDate", executionContext, fakeDataService2
                .dates().before(fakeDataService2.periods().daysUpTo(2 * 365)));
        if (getDuration() == null && getEndDate() == null) {
            defaultParam("endDate", executionContext, getStartDate().plus(fakeDataService2.periods().years(10, 20)));
        }
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(getAtPath());

        this.lease = leaseRepository.newLease(applicationTenancy, getReference(), getName(), getLeaseType(), getStartDate(), getDuration(), getEndDate(), getLandlord(), getTenant());
    }

    // //////////////////////////////////////

    @Inject
    UnitRepository unitRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    ApplicationTenancies applicationTenancies;

    @Inject
    CountryRepository countryRepository;

    @Inject
    FakeDataService fakeDataService;
    @Inject
    EstatioFakeDataService fakeDataService2;


}
