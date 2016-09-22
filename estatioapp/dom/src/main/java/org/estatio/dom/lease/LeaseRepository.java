/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.lease;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Lease.class, nature = NatureOfService.DOMAIN)
public class LeaseRepository extends UdoDomainRepositoryAndFactory<Lease> {

    public LeaseRepository() {
        super(LeaseRepository.class, Lease.class);
    }

    @PostConstruct
    @Programmatic
    public void init(final Map<String, String> properties) {
        super.init(properties);
        final AgreementType agreementType = agreementTypeRepository.findOrCreate(LeaseConstants.AT_LEASE);
        agreementRoleTypeRepository.findOrCreate(LeaseConstants.ART_TENANT, agreementType);
        agreementRoleTypeRepository.findOrCreate(LeaseConstants.ART_LANDLORD, agreementType);
        agreementRoleTypeRepository.findOrCreate(LeaseConstants.ART_MANAGER, agreementType);
        agreementRoleCommunicationChannelTypeRepository.findOrCreate(LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS, agreementType);
        agreementRoleCommunicationChannelTypeRepository.findOrCreate(LeaseConstants.ARCCT_INVOICE_ADDRESS, agreementType);
    }

    @Programmatic
    public Lease newLease(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final String duration,
            final LocalDate endDate,
            final Party landlord,
            final Party tenant) {
        LocalDate calculatedEndDate = calculateEndDate(startDate, endDate, duration);
        return newLease(applicationTenancy, reference.trim(), name.trim(), leaseType, startDate, calculatedEndDate, startDate, calculatedEndDate, landlord, tenant);
    }
    private static LocalDate calculateEndDate(
            final LocalDate startDate, final LocalDate endDate, final String duration) {
        if (duration != null) {
            final Period p = JodaPeriodUtils.asPeriod(duration);
            if (p != null) {
                return startDate.plus(p).minusDays(1);
            }
        }
        return endDate;
    }


    @Programmatic
    public Lease newLease(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final LocalDate endDate,
            final LocalDate tenancyStartDate,
            final LocalDate tenancyEndDate,
            final Party landlord,
            final Party tenant) {
        Lease lease = newTransientInstance();
        final AgreementType at = agreementTypeRepository.find(LeaseConstants.AT_LEASE);
        lease.setType(at);
        lease.setApplicationTenancyPath(applicationTenancy.getPath());
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setTenancyStartDate(tenancyStartDate);
        lease.setTenancyEndDate(tenancyEndDate);
        lease.setLeaseType(leaseType);
        persistIfNotAlready(lease);

        if (tenant != null) {
            final AgreementRoleType artTenant = agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT);
            lease.newRole(artTenant, tenant, null, null);
        }
        if (landlord != null) {
            final AgreementRoleType artLandlord = agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_LANDLORD);
            lease.newRole(artLandlord, landlord, null, null);
        }
        return lease;
    }

    public List<Lease> allLeases() {
        return allInstances();
    }

    public List<Lease> matchByReferenceOrName(
            final String referenceOrName,
            final boolean includeTerminated) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName);
        return allMatches("matchByReferenceOrName", "referenceOrName", pattern, "includeTerminated", includeTerminated, "date", clockService.now());
    }

    public List<Lease> findByAssetAndActiveOnDate(
            final FixedAsset fixedAsset,
            final LocalDate activeOnDate) {
        return allMatches("findByAssetAndActiveOnDate", "asset", fixedAsset, "activeOnDate", activeOnDate);
    }

    @Programmatic
    public Lease findLeaseByReference(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Lease findLeaseByReferenceElseNull(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<Lease> findLeasesByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    @Programmatic
    public List<Lease> findExpireInDateRange(final LocalDate rangeStartDate, final LocalDate rangeEndDate) {
        return allMatches(
                "findExpireInDateRange",
                "rangeStartDate", rangeStartDate,
                "rangeEndDate", rangeEndDate);
    }

    @Programmatic
    public List<Lease> findByBrand(final Brand brand, final boolean includeTerminated) {
        return allMatches(
                "findByBrand",
                "brand", brand,
                "includeTerminated", includeTerminated,
                "date", clockService.now());
    }

    @Programmatic
    public List<Lease> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? matchByReferenceOrName("*" + searchPhrase + "*", true)
                : Lists.<Lease>newArrayList();
    }

    // //////////////////////////////////////

    @Inject
    private AgreementTypeRepository agreementTypeRepository;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    ClockService clockService;

    @Inject
    private AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

}
