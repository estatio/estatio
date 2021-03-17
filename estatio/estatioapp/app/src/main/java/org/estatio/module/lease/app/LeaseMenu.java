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
package org.estatio.module.lease.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;

import org.incode.module.base.dom.utils.JodaPeriodUtils;
import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.LeaseTypeRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentManager;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentState;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentTemplate;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.lease.LeaseMenu"
)
@DomainServiceLayout(
        named = "Leases",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "40.1"
)
public class LeaseMenu {

    /**
     *
     * @param property - obtained via {@link PropertyRepository#autoComplete(String)} ... nb this does server-side filtering based on user's atPath
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            final Property property,
            final @Parameter(regexPattern = Lease.ReferenceType.Meta.REGEX, regexPatternReplacement = Lease.ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final @Nullable @ParameterLayout(describedAs = "Duration in a text format. Example 6y5m2d") String duration,
            final @Nullable @ParameterLayout(describedAs = "Can be omitted when duration is filled in") LocalDate endDate,
            final @Nullable Party landlord,
            final @Nullable Party tenant
    ) {
        final ApplicationTenancy applicationTenancy = property.getApplicationTenancy();
        return leaseRepository.newLease(applicationTenancy, reference, name, leaseType, startDate, duration, endDate, landlord, tenant);
    }


    public List<LeaseType> choices3NewLease(final Property property) {
        if(property == null) {
            return null;
        }

        final ApplicationTenancy propertyApplicationTenancy = property.getApplicationTenancy();
        List<LeaseType> result = Lists.newArrayList();
        for (LeaseType leaseType : leaseTypeRepository.allLeaseTypes()) {

            final ApplicationTenancyLevel propertyAppTenancyLevel = ApplicationTenancyLevel.of(propertyApplicationTenancy);
            final ApplicationTenancyLevel leaseTypeAppTenancyLevel = ApplicationTenancyLevel.of(leaseType.getApplicationTenancy());

            if (    propertyAppTenancyLevel.equals(leaseTypeAppTenancyLevel) ||
                    propertyAppTenancyLevel.childOf(leaseTypeAppTenancyLevel)) {
                result.add(leaseType);
            }
        }
        return result;
    }

    public List<Party> autoComplete7NewLease(@MinLength(3) String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, LeaseRoleTypeEnum.LANDLORD);
    }
    public String validate7NewLease(Party landlord) {
        return partyRoleRepository.validateThat(landlord, LeaseRoleTypeEnum.LANDLORD);
    }
    public List<Party> autoComplete8NewLease(@MinLength(3) String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, LeaseRoleTypeEnum.TENANT)
                .stream()
                .filter(party -> {
                    if (party.getAtPath().startsWith("/BEL")) {
                        return party.getReference().startsWith("BECL");
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }
    public String validate8NewLease(Party tenant) {
        return partyRoleRepository.validateThat(tenant, LeaseRoleTypeEnum.TENANT);
    }

    @Inject
    PartyRoleRepository partyRoleRepository;

    @Inject
    PartyRepository partyRepository;

    public String validateNewLease(
            final Property property,
            final String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final String duration,
            final LocalDate endDate,
            final Party landlord,
            final Party tenant
    ) {
        if ((endDate == null && duration == null) || (endDate != null && duration != null)) {
            return "Either end date or duration must be filled in.";
        }
        if (duration != null) {
            final Period p = JodaPeriodUtils.asPeriod(duration);
            if (p == null) {
                return "This is not a valid duration.";
            }
        } else {
            if (!new LocalDateInterval(startDate, endDate).isValid()) {
                return "End date can not be before start date";
            }
        }

        // for the selected property, both the landlord and the tenant must be for the same country
        // since we don't (currently) persist the country for Party, we use app tenancy instead.
        final ApplicationTenancyLevel propertyAppTenancyLevel = ApplicationTenancyLevel.of(property.getApplicationTenancy());
        final ApplicationTenancyLevel landlordAppTenancyLevel = landlord != null ? ApplicationTenancyLevel.of(landlord.getApplicationTenancy()) : null;
        final ApplicationTenancyLevel tenantAppTenancyLevel = tenant != null ? ApplicationTenancyLevel.of(tenant.getApplicationTenancy()) : null;

        if (landlordAppTenancyLevel != null && !propertyAppTenancyLevel.equals(landlordAppTenancyLevel) && !landlordAppTenancyLevel.parentOf(propertyAppTenancyLevel)) {
            return "Landlord not valid. (wrong application tenancy)";
        }

        if (tenantAppTenancyLevel != null && !propertyAppTenancyLevel.equals(tenantAppTenancyLevel) && !tenantAppTenancyLevel.parentOf(propertyAppTenancyLevel)) {
            return "Tenant not valid. (wrong application tenancy)";
        }

        return null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(
            @ParameterLayout(describedAs = "May include wildcards '*' and '?'") final String referenceOrName,
            final boolean includeTerminated) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName);
        return leaseRepository.matchByReferenceOrName(referenceOrName, includeTerminated);
    }

    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public List<Lease> findLeasesByBrand(
            final Brand brand,
            final boolean includeTerminated) {
        return leaseRepository.findByBrand(brand, includeTerminated);
    }

    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "5")
    public List<Lease> findLeasesByTenantName(
            final String tenantName,
            final Property property) {
        return leaseRepository.matchByTenantName(tenantName, property);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "6")
    public List<Lease> findLeasesActiveOnDate(
            final FixedAsset fixedAsset,
            final LocalDate activeOnDate) {
        return leaseRepository.findByAssetAndActiveOnDate(fixedAsset, activeOnDate);
    }

    public List<FixedAsset> autoComplete0FindLeasesActiveOnDate(final String searchPhrase) {
        return fixedAssetRepository.matchAssetsByReferenceOrName(searchPhrase);
    }

    public LocalDate default1FindLeasesActiveOnDate() {
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "4")
    public String verifyLeasesUntil(
            final LeaseItemType leaseItemType,
            final LocalDate untilDate) {
        DateTime start = DateTime.now();
        List<Lease> leases = filterForUser(allLeases());
        for (Lease lease : leases) {
            for (LeaseItem leaseItem : lease.getItems()) {
                if (leaseItem.getType().equals(leaseItemType)) {
                    leaseItem.verifyUntil(untilDate);
                }
            }
        }
        Period p = new Period(start, DateTime.now());
        return String.format("Verified %d leases in %s", leases.size(), JodaPeriodUtils.asString(p));
    }

    List<Lease> filterForUser(List<Lease> leasesToFilter){
        return leasesToFilter.
                stream()
                .filter(lease -> applicationTenancyEvaluator.hides(lease, meService.me())==null || applicationTenancyEvaluator.hides(lease, meService.me()).isEmpty())
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Lease> findLeasesByExternalReference(final String externalReference){
        return leaseRepository.matchLeaseByExternalReference(externalReference);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Lease> allLeases() {
        return leaseRepository.allLeases();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public String verifyAllLeases() {
        DateTime dt = DateTime.now();
        List<Lease> leases = allLeases();
        for (Lease lease : leases) {
            lease.verifyUntil(clockService.now());
        }
        Period p = new Period(dt, DateTime.now());
        return String.format("Verified %d leases in %s", leases.size(), JodaPeriodUtils.asString(p));
    }

    @Action(semantics = SemanticsOf.SAFE)
    public LeaseAmendmentManager maintainLeaseAmendments(@Nullable final Property property, @Nullable
            LeaseAmendmentTemplate leaseAmendmentTemplate, @Nullable LeaseAmendmentState leaseAmendmentState){
        return new LeaseAmendmentManager(property, leaseAmendmentTemplate,
                leaseAmendmentState);
    }

    @Inject
    private FixedAssetRepository fixedAssetRepository;

    @Inject
    ClockService clockService;

    @Inject
    private EstatioApplicationTenancyRepositoryForProperty estatioApplicationTenancyRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private LeaseTypeRepository leaseTypeRepository;

    @Inject
    private ApplicationTenancyEvaluator applicationTenancyEvaluator;

    @Inject
    private MeService meService;

}
