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

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.*;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.utils.StringUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(menuOrder = "40", repositoryFor = Lease.class)
public class Leases extends EstatioDomainService<Lease> {

    public Leases() {
        super(Leases.class, Lease.class);
    }

    // //////////////////////////////////////

    @NotContributed
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            // CHECKSTYLE:OFF ParameterNumber - Wicket viewer does not support
            // aggregate value types
            final @Named("Reference") @RegEx(validation = "[-/_A-Z0-9]+", caseSensitive = true) String reference,
            final @Named("Name") String name,
            final @Named("Type") LeaseType leaseType,
            final @Named("Start Date") LocalDate startDate,
            final @Optional @Named("Duration") @DescribedAs("Duration in a text format. Example 6y5m2d") String duration,
            final @Optional @Named("End Date") @DescribedAs("Can be omitted when duration is filled in") LocalDate endDate,
            final @Optional @Named("Landlord") Party landlord,
            final @Optional @Named("Tentant") Party tenant
            // CHECKSTYLE:ON
            ) {

        LocalDate calculatedEndDate = calculateEndDate(startDate, endDate, duration);
        return newLease(reference, name, leaseType, startDate, calculatedEndDate, startDate, calculatedEndDate, landlord, tenant);
    }

    @Programmatic
    public Lease newLease(
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
        final AgreementType at = agreementTypes.find(LeaseConstants.AT_LEASE);
        lease.setType(at);
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setTenancyStartDate(tenancyStartDate);
        lease.setTenancyEndDate(tenancyEndDate);
        lease.setLeaseType(leaseType);
        persistIfNotAlready(lease);

        if (tenant != null) {
            final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
            lease.newRole(artTenant, tenant, null, null);
        }
        if (landlord != null) {
            final AgreementRoleType artLandlord = agreementRoleTypes.findByTitle(LeaseConstants.ART_LANDLORD);
            lease.newRole(artLandlord, landlord, null, null);
        }
        return lease;
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

    public String validateNewLease(
            // CHECKSTYLE:OFF ParameterNumber - Wicket viewer does not support
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
        return null;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String refOrName) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrName);
        return allMatches("matchByReferenceOrName", "referenceOrName", pattern);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<Lease> findLeasesActiveOnDate(
            final FixedAsset fixedAsset,
            final @Named("Active On Date") LocalDate activeOnDate) {
        return allMatches("findByAssetAndActiveOnDate", "asset", fixedAsset, "activeOnDate", activeOnDate);
    }

    public List<FixedAsset> autoComplete0FindLeasesActiveOnDate(final String searchPhrase) {
        return fixedAssets.matchAssetsByReferenceOrName(searchPhrase);
    }

    public LocalDate default1FindLeasesActiveOnDate() {
        return getClockService().now();
    }

    // //////////////////////////////////////

    public List<Lease> findExpireInDateRange(final LocalDate rangeStartDate, final LocalDate rangeEndDate) {
        return allMatches(
                "findExpireInDateRange",
                "rangeStartDate", rangeStartDate,
                "rangeEndDate", rangeEndDate);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Lease> allLeases() {
        return allInstances();
    }

    // //////////////////////////////////////

    public String verifyAllLeases() {
        DateTime dt = DateTime.now();
        List<Lease> leases = allLeases();
        for (Lease lease : leases) {
            lease.verifyUntil(getClockService().now());
        }
        Period p = new Period(dt, DateTime.now());
        return String.format("Verified %d leases in %s", leases.size(), JodaPeriodUtils.asString(p));
    }

    // //////////////////////////////////////

    @Programmatic
    public Lease findLeaseByReference(final String reference) {
        return mustMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public Lease findLeaseByReferenceElseNull(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<Lease> findLeasesByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    // //////////////////////////////////////

    @Hidden
    public List<Lease> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findLeases("*" + searchPhrase + "*")
                : Lists.<Lease> newArrayList();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<LeaseStatusReason> findLeaseStatusReasonByLease(final Lease lease) {
        return getContainer().allMatches(
                new QueryDefault<LeaseStatusReason>(LeaseStatusReason.class, "findByLease", "lease", lease));
    }

    // //////////////////////////////////////

    @PostConstruct
    @Programmatic
    public void init(Map<String, String> properties) {
        AgreementType agreementType = agreementTypes.findOrCreate(LeaseConstants.AT_LEASE);
        agreementRoleTypes.findOrCreate(LeaseConstants.ART_TENANT, agreementType);
        agreementRoleTypes.findOrCreate(LeaseConstants.ART_LANDLORD, agreementType);
        agreementRoleTypes.findOrCreate(LeaseConstants.ART_MANAGER, agreementType);
        agreementRoleCommunicationChannelTypes.findOrCreate(LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS, agreementType);
        agreementRoleCommunicationChannelTypes.findOrCreate(LeaseConstants.ARCCT_INVOICE_ADDRESS, agreementType);
    }

    // //////////////////////////////////////

    @Inject
    private FixedAssets fixedAssets;

    @Inject
    private AgreementTypes agreementTypes;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Inject
    private AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes;

}
