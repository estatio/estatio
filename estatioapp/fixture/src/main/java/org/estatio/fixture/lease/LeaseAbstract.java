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

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLinkRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.LeaseTypeRepository;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

/**
 * Sets up the lease, and the roles, and also the first occupancy.
 */
public abstract class LeaseAbstract extends FixtureScript {

    protected Lease createLease(
            String reference,
            String name,
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
        return createLeaseWithOccupancyEndDate(
                reference, name, unitReference,
                brand, brandCoverage, countryOfOriginRef,
                sector, activity, landlordReference, tenantReference,
                startDate, endDate, null,
                createManagerRole, createLeaseUnitAndTags, manager, fixtureResults
        );
    }

    protected Lease createLeaseWithOccupancyEndDate(
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
            LocalDate occupancyEndDate,
            boolean createManagerRole,
            boolean createLeaseUnitAndTags,
            Party manager, ExecutionContext fixtureResults) {

        Unit unit = unitRepository.findUnitByReference(unitReference);
        Party landlord = findPartyByReferenceOrNameElseNull(landlordReference);
        Party tenant = findPartyByReferenceOrNameElseNull(tenantReference);

        final LeaseType leaseType = leaseTypeRepository.findOrCreate("STD", "Standard", applicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH));
        Lease lease = leaseRepository.newLease(
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
            Occupancy occupancy = occupancyRepository.newOccupancy(lease, unit, startDate);
            occupancy.setEndDate(occupancyEndDate);
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

    protected void addInvoiceAddressForTenant(
            final Lease lease,
            final String partyRefTenant,
            final CommunicationChannelType channelType) {

        final AgreementRoleType inRoleOfTenant =
                agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRoleCommunicationChannelType inRoleOfInvoiceAddress =
                agreementRoleCommunicationChannelTypeRepository.findByTitle(LeaseConstants.ARCCT_INVOICE_ADDRESS);

        final Party tenant = partyRepository.findPartyByReference(partyRefTenant);

        final List<CommunicationChannelOwnerLink> addressLinks = communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(tenant,
                channelType);

        final Optional<CommunicationChannel> communicationChannelIfAny = addressLinks.stream()
                .map(x -> x.getCommunicationChannel()).findFirst();
        final SortedSet<AgreementRole> roles = lease.getRoles();

        // huh? why not working when guaa version does?
        // final Optional<AgreementRole> agreementRoleIfAny = roles.stream().filter(x -> x.getType() == inRoleOfTenant).findFirst();
        final com.google.common.base.Optional<AgreementRole> agreementRoleIfAny =
                FluentIterable.from(roles).firstMatch(x -> x.getType() == inRoleOfTenant);

        if(agreementRoleIfAny.isPresent() && communicationChannelIfAny.isPresent()) {

            final AgreementRole agreementRole = agreementRoleIfAny.get();
            if (!Sets.filter(agreementRole.getCommunicationChannels(), inRoleOfInvoiceAddress.matchingCommunicationChannel()).isEmpty()) {
                // already one set up
                return;
            }

            final CommunicationChannel communicationChannel = communicationChannelIfAny.get();
            agreementRole.addCommunicationChannel(inRoleOfInvoiceAddress, communicationChannel, null, null);
        }
    }


    // //////////////////////////////////////

    @Inject
    protected UnitRepository unitRepository;

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected OccupancyRepository occupancyRepository;

    @Inject
    protected PartyRepository partyRepository;

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    protected LeaseTypeRepository leaseTypeRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    protected AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    @Inject
    protected CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

}
