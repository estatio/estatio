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
package org.estatio.app.menus.lease;

import java.util.List;

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
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.JodaPeriodUtils;
import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.dom.asset.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.LeaseTypeRepository;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.party.Party;

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
     * @param property - obtained via {@link org.estatio.dom.asset.PropertyRepository#autoComplete(String)} ... nb this does server-side filtering based on user's atPath
     */
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            final Property property,
            final @Parameter(regexPattern = Lease.ReferenceType.Meta.REGEX, regexPatternReplacement = Lease.ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(describedAs = "Duration in a text format. Example 6y5m2d") String duration,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(describedAs = "Can be omitted when duration is filled in") LocalDate endDate,
            final @Parameter(optionality = Optionality.OPTIONAL) Party landlord,
            final @Parameter(optionality = Optionality.OPTIONAL) Party tenant
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

        final ApplicationTenancy applicationTenancyOfProperty = property.getApplicationTenancy();
        if (!(ApplicationTenancyLevel.of(applicationTenancyOfProperty)
                .equals(ApplicationTenancyLevel.of(landlord.getApplicationTenancy()))
                ||
                ApplicationTenancyLevel.of(landlord.getApplicationTenancy())
                        .parentOf(ApplicationTenancyLevel.of(applicationTenancyOfProperty))
        )) {
            return "Landlord not valid. (wrong application tenancy)";
        }
        if (!(ApplicationTenancyLevel.of(applicationTenancyOfProperty)
                .equals(ApplicationTenancyLevel.of(tenant.getApplicationTenancy()))
                ||
                ApplicationTenancyLevel.of(tenant.getApplicationTenancy())
                        .parentOf(ApplicationTenancyLevel.of(applicationTenancyOfProperty))
        )) {
            return "Tenant not valid. (wrong application tenancy)";
        }

        return null;
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(
            final @ParameterLayout(describedAs = "May include wildcards '*' and '?'") String referenceOrName,
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

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "5")
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
        List<Lease> leases = allLeases();
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

}
