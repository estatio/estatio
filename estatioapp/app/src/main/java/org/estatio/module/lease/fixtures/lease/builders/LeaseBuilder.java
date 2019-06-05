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

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLinkRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelType;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.module.agreement.dom.commchantype.IAgreementRoleCommunicationChannelType;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.lease.dom.AgreementRoleCommunicationChannelTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.LeaseTypeRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.party.dom.Party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public final class LeaseBuilder
        extends BuilderScriptAbstract<Lease,LeaseBuilder> {

    @Getter @Setter
    String reference;
    @Getter @Setter
    String externalRef;
    @Getter @Setter
    String name;
    @Getter @Setter
    Property property;

    @Getter @Setter
    Party landlord;
    @Getter @Setter
    Party tenant;
    @Getter @Setter
    LocalDate startDate;
    @Getter @Setter
    LocalDate endDate;

    @AllArgsConstructor
    @Data
    public static class OccupancySpec {
        @Getter @Setter
        Unit unit;
        String brand;
        BrandCoverage brandCoverage;
        Country countryOfOrigin;
        String sector;
        String activity;
        LocalDate startDate;
        LocalDate endDate;
    }
    @Getter @Setter
    List<OccupancySpec> occupancySpecs = Lists.newArrayList();

    @Getter @Setter
    Party manager;

    @Getter @Setter
    InvoiceAddressCreationPolicy invoiceAddressCreationPolicy;
    @Getter @Setter
    AddressesCreationPolicy addressesCreationPolicy;

    public enum InvoiceAddressCreationPolicy {
        CREATE,
        DONT_CREATE
    }
    public enum AddressesCreationPolicy {
        CREATE,
        DONT_CREATE
    }

    @Getter
    private Lease object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("reference", executionContext, String.class);
        checkParam("name", executionContext, String.class);
        checkParam("property", executionContext, Unit.class);
        checkParam("landlord", executionContext, Party.class);
        checkParam("tenant", executionContext, Party.class);
        checkParam("startDate", executionContext, LocalDate.class);
        checkParam("endDate", executionContext, LocalDate.class);

        defaultParam("invoiceAddressCreationPolicy", executionContext, InvoiceAddressCreationPolicy.DONT_CREATE);
        defaultParam("addressesCreationPolicy", executionContext, AddressesCreationPolicy.DONT_CREATE);

        landlord.addRole(LeaseRoleTypeEnum.LANDLORD);
        tenant.addRole(LeaseRoleTypeEnum.TENANT);

        final ApplicationTenancy atPath = ApplicationTenancy_enum.Global.findUsing(serviceRegistry);
        final LeaseType leaseType = leaseTypeRepository.findOrCreate("STD", "Standard", atPath);

        Lease lease = leaseRepository.newLease(
                property.getApplicationTenancy(), reference,
                name,
                leaseType,
                startDate,
                null,
                endDate,
                landlord,
                tenant
        );
        lease.setExternalReference(externalRef);
        executionContext.addResult(this, lease.getReference(), lease);

        if (manager != null) {
            final AgreementRole role = lease.createRole(agreementRoleTypeRepository.find(
                    LeaseAgreementRoleTypeEnum.MANAGER), manager, null, null);
            executionContext.addResult(this, role);
        }
        for (final OccupancySpec spec : occupancySpecs) {
            Occupancy occupancy = occupancyRepository.newOccupancy(lease, spec.unit, spec.startDate);
            occupancy.setEndDate(spec.endDate);
            occupancy.setBrandName(spec.brand, spec.brandCoverage, spec.countryOfOrigin);
            occupancy.setSectorName(spec.sector);
            occupancy.setActivityName(spec.activity);
            occupancy.setReportTurnover(Occupancy.OccupancyReportingType.YES);
            executionContext.addResult(this, occupancy);

        }

        if(invoiceAddressCreationPolicy == InvoiceAddressCreationPolicy.CREATE) {
            addInvoiceAddressForTenant(lease, tenant, CommunicationChannelType.EMAIL_ADDRESS);
        }

        if(addressesCreationPolicy == AddressesCreationPolicy.CREATE) {
            createAddress(lease, AgreementRoleCommunicationChannelTypeEnum.ADMINISTRATION_ADDRESS);
            createAddress(lease, AgreementRoleCommunicationChannelTypeEnum.INVOICE_ADDRESS);
        }

        if (leaseRepository.findLeaseByReference(reference) == null) {
            throw new RuntimeException("could not find lease reference='" + reference + "'");
        }

        object = lease;
    }

    private void addInvoiceAddressForTenant(
            final Lease lease,
            final Party tenant,
            final CommunicationChannelType channelType) {

        final AgreementRoleType inRoleOfTenant =
                agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.TENANT);
        final AgreementRoleCommunicationChannelType inRoleOfInvoiceAddress =
                agreementRoleCommunicationChannelTypeRepository.find(
                        AgreementRoleCommunicationChannelTypeEnum.INVOICE_ADDRESS);

        final List<CommunicationChannelOwnerLink> addressLinks =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(tenant, channelType);

        final Optional<CommunicationChannel> communicationChannelIfAny = addressLinks.stream()
                .map(CommunicationChannelOwnerLink::getCommunicationChannel).findFirst();
        final SortedSet<AgreementRole> roles = lease.getRoles();

        final Optional<AgreementRole> agreementRoleIfAny =
                Lists.newArrayList(roles).stream()
                        .filter(x -> x.getType() == inRoleOfTenant).findFirst();

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


    private void createAddress(Lease lease, IAgreementRoleCommunicationChannelType addressType) {
        final AgreementRoleType agreementRoleType =
                agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.TENANT);

        final AgreementRole agreementRole = lease.findRoleWithType(agreementRoleType, ld(2010, 7, 15));
        AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType =
                agreementRoleCommunicationChannelTypeRepository.find(addressType);

        final SortedSet<CommunicationChannel> channels =
                communicationChannelRepository.findByOwnerAndType(
                        lease.getSecondaryParty(), CommunicationChannelType.POSTAL_ADDRESS);

        final CommunicationChannel postalAddress = channels.first();
        agreementRole.addCommunicationChannel(agreementRoleCommunicationChannelType, postalAddress, null);
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    LeaseTypeRepository leaseTypeRepository;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;




}
