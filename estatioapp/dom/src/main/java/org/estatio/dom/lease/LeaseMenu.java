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
package org.estatio.dom.lease;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

import org.estatio.dom.Dflt;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.asset.EstatioApplicationTenancyRepositoryForProperty;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetRepository;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.utils.StringUtils;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Leases",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "40.1"
)
public class LeaseMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            // CHECKSTYLE:OFF ParameterNumber
            final ApplicationTenancy applicationTenancy,
            final @Parameter(regexPattern = RegexValidation.Lease.REFERENCE, regexPatternReplacement = RegexValidation.Lease.REFERENCE_DESCRIPTION) String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(describedAs = "Duration in a text format. Example 6y5m2d") String duration,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(describedAs = "Can be omitted when duration is filled in") LocalDate endDate,
            final @Parameter(optionality = Optionality.OPTIONAL) Party landlord,
            final @Parameter(optionality = Optionality.OPTIONAL) Party tenant
            // CHECKSTYLE:ON
    ) {

        LocalDate calculatedEndDate = calculateEndDate(startDate, endDate, duration);
        return leaseRepository.newLease(applicationTenancy, reference.trim(), name.trim(), leaseType, startDate, calculatedEndDate, startDate, calculatedEndDate, landlord, tenant);
    }

    public List<ApplicationTenancy> choices0NewLease() {
        return estatioApplicationTenancyRepository.propertyTenanciesForCurrentUser();
    }

    public ApplicationTenancy default0NewLease() {
        return Dflt.of(choices0NewLease());
    }

    public List<LeaseType> choices3NewLease(final ApplicationTenancy applicationTenancy) {
        if (applicationTenancy == null)
            return null;
        List<LeaseType> result = new ArrayList<>();
        for (LeaseType leaseType : leaseTypeRepository.allLeaseTypes()) {
            if (
                    ApplicationTenancyLevel.of(applicationTenancy)
                            .equals(ApplicationTenancyLevel.of(leaseType.getApplicationTenancy())
                            )
                            ||
                            ApplicationTenancyLevel.of(applicationTenancy)
                                    .childOf(ApplicationTenancyLevel.of(leaseType.getApplicationTenancy()))
                    ) {
                result.add(leaseType);
            }
        }
        return result;
    }

    public String validateNewLease(
            // CHECKSTYLE:OFF ParameterNumber - Wicket viewer does not support
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final String duration,
            final LocalDate endDate,
            final Party landlord,
            final Party tenant
            // CHECKSTYLE:ON
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
        // check apptenancy landlord and tenant against param applicationTenancy
        // because these can't be filtered in autoComplete
        if (!(ApplicationTenancyLevel.of(applicationTenancy)
                .equals(ApplicationTenancyLevel.of(landlord.getApplicationTenancy()))
                ||
                ApplicationTenancyLevel.of(landlord.getApplicationTenancy())
                        .parentOf(ApplicationTenancyLevel.of(applicationTenancy))
        )) {
            return "Landlord not valid. (wrong application tenancy)";
        }
        if (!(ApplicationTenancyLevel.of(applicationTenancy)
                .equals(ApplicationTenancyLevel.of(tenant.getApplicationTenancy()))
                ||
                ApplicationTenancyLevel.of(tenant.getApplicationTenancy())
                        .parentOf(ApplicationTenancyLevel.of(applicationTenancy))
        )) {
            return "Tenant not valid. (wrong application tenancy)";
        }
        return null;
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
