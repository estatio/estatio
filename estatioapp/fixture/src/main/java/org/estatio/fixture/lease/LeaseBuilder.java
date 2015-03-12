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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.lease.refdata.LeaseTypeForItalyRefData;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGb;

public class LeaseBuilder extends EstatioFixtureScript {

    //region > reference
    private String reference;
    public String getReference() {
        return reference;
    }
    public void setReference(final String reference) {
        this.reference = reference;
    }
    //endregion

    //region > name
    private String name;
    public String getName() {
        return name;
    }
    //endregion

    public void setName(final String name) {
        this.name = name;
    }

    //region > leaseType
    private LeaseType leaseType;
    public LeaseType getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(final LeaseType leaseType) {
        this.leaseType = leaseType;
    }
    //endregion

    //region > startDate
    private LocalDate startDate;
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }
    //endregion

    //region > duration
    private String duration;
    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }
    //endregion

    //region > endDate
    private LocalDate endDate;
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    //endregion

    //region > landlord
    private Party landlord;
    public Party getLandlord() {
        return landlord;
    }

    public void setLandlord(final Party landlord) {
        this.landlord = landlord;
    }
    //endregion

    //region > tenant
    private Party tenant;
    public Party getTenant() {
        return tenant;
    }

    public void setTenant(final Party tenant) {
        this.tenant = tenant;
    }
    //endregion

    //region > atPath (input property)
    private String atPath;
    public String getAtPath() {
        return atPath;
    }
    public void setAtPath(final String atPath) {
        this.atPath = atPath;
    }
    //endregion


    //region > lease (output)
    private Lease lease;

    public Lease getLease() {
        return lease;
    }

    //endregion

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
            Party manager,
            ExecutionContext fixtureResults) {

        Unit unit = units.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);

        Lease lease = leases.newLease(reference, name, null, startDate, null, endDate, landlord, tenant, unit.getApplicationTenancy());
        fixtureResults.addResult(this, lease.getReference(), lease);

        if (createManagerRole) {
            final AgreementRole role = lease.createRole(agreementRoleTypes.findByTitle(LeaseConstants.ART_MANAGER), manager, null, null);
            fixtureResults.addResult(this, role);
        }
        if (createLeaseUnitAndTags) {
            Occupancy occupancy = occupancies.newOccupancy(lease, unit, startDate);
            occupancy.setBrandName(brand);
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

    @Override
    protected void execute(final ExecutionContext executionContext) {

        if(isExecutePrereqs()) {
            executionContext.executeChild(this, new LeaseTypeForItalyRefData());
        }

        defaultParam("reference", executionContext, faker().lorem().fixedString(3));
        defaultParam("name", executionContext, faker().name().lastName() + " Mall");
        defaultParam("leaseType", executionContext, faker().collections().aBounded(LeaseType.class));
        defaultParam("atPath", executionContext, ApplicationTenancyForGb.PATH);

        defaultParam("startDate", executionContext, faker().dates().before(faker().periods().daysUpTo(2 * 365)));
        if(getDuration()==null && getEndDate() == null) {
            defaultParam("endDate", executionContext, getStartDate().plus(faker().periods().years(10,20)));
        }
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(getAtPath());

        this.lease = leases.newLease(getReference(), getName(), getLeaseType(), getStartDate(), getDuration(), getEndDate(), getLandlord(), getTenant(), applicationTenancy);
    }

    // //////////////////////////////////////

    @Inject
    protected Units units;

    @Inject
    protected Leases leases;

    @Inject
    protected Occupancies occupancies;

    @Inject
    protected Parties parties;

    @Inject
    protected AgreementRoleTypes agreementRoleTypes;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}
