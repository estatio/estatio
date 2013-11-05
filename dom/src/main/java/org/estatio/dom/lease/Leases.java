/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.core.commons.exceptions.IsisApplicationException;

import org.estatio.app.InvoiceSummaries;
import org.estatio.app.InvoiceSummaryForPropertyDueDate;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.utils.StringUtils;

public class Leases extends EstatioDomainService<Lease> {

    public enum InvoiceRunType {
        NORMAL_RUN, RETRO_RUN;
    }

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
        String validate = validateNewLease(reference, name, leaseType, startDate, duration, endDate, landlord, tenant);
        if (validate != null){
            throw new IsisApplicationException(validate);
        }
        
        LocalDate calculatedEndDate = endDate;
        if (duration != null) {
            final Period p = JodaPeriodUtils.asPeriod(duration);
            if (p != null) {
                calculatedEndDate = startDate.plus(p).minusDays(1);
            }
        }
        Lease lease = newTransientInstance();
        final AgreementType at = agreementTypes.find(LeaseConstants.AT_LEASE);
        lease.setType(at);
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(calculatedEndDate);
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

    public String validateNewLease(
            final String reference,
            final String name,
            final LeaseType leaseType,
            final LocalDate startDate,
            final String duration,
            final LocalDate endDate,
            final Party landlord,
            final Party tenant
            ) {
        if ((endDate == null && duration == null) || (endDate != null && duration != null)){
            return "Either end date or duration must be filled in.";
        }
        if (duration !=null){
            final Period p = JodaPeriodUtils.asPeriod(duration);
            if (p == null){
                return "This is not a valid duration.";
            }
        } else {
            if (endDate.isBefore(startDate)){
                return "End date can not be before start date"; 
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName) {
        return allMatches("findByReferenceOrName", "referenceOrName", StringUtils.wildcardToRegex(referenceOrName));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<Lease> findLeasesActiveOnDate(
            final FixedAsset fixedAsset,
            final @Named("Active On Date") LocalDate activeOnDate) {
        return allMatches("findByAssetAndActiveOnDate", "asset", fixedAsset, "activeOnDate", activeOnDate);
    }

    public List<FixedAsset> autoComplete0FindLeasesActiveOnDate(final String searchPhrase) {
        return fixedAssets.findAssetsByReferenceOrName(searchPhrase);
    }

    public LocalDate default1FindLeasesActiveOnDate() {
        return getClockService().now();
    }

    // //////////////////////////////////////

    public List<Lease> findAboutToExpireOnDate(final LocalDate date) {
        return allMatches("findAboutToExpireOnDate", "date", date);
    }

    // //////////////////////////////////////

    /**
     * Returns the {@link InvoiceSummary}s that are newly
     * {@link Lease#calculate(LocalDate, LocalDate) calculate}d for all of the
     * {@link Lease}s matched by the provided <tt>property</tt> and the other
     * parameters.
     */
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "6")
    public List<InvoiceSummaryForPropertyDueDate> calculateProperty(
            final @Named("Property") @DescribedAs("") Property property,
            final @Named("Period Start Date") LocalDate startDate,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Run Type") InvoiceRunType runType) {
        final List<Lease> leases = findLeasesByProperty(property);
        for (Lease lease : leases) {
            lease.verify();
            lease.calculate(startDate, dueDate, runType);
        }
        // As a convenience, we now go find them and display them.
        // We've done it this way so that the user can always just go to the
        // menu and make this query.
        return invoiceSummaries.invoiceSummary();
    }

    public LocalDate default1CalculateProperty() {
        return getClockService().beginningOfQuarter();
    }

    public LocalDate default2CalculateProperty() {
        return getClockService().beginningOfQuarter();
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Lease> allLeases() {
        return allInstances();
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "99")
    @Prototype
    public List<InvoiceItemForLease> calculateLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String referenceOrName,
            final @Named("Period Start Date") LocalDate startDate,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Run Type") InvoiceRunType runType) {
        final List<Lease> leases = findLeases(referenceOrName);
        for (Lease lease : leases) {
            lease.verify();
            lease.calculate(startDate, dueDate, runType);
        }
        // As a convenience, we now go find them and display them.
        // We've done it this way so that the user can always just go to the
        // menu and make this query.
        return invoiceItemsForLease.findInvoiceItemsByLease(referenceOrName,
                startDate, dueDate);
    }

    public LocalDate default1CalculateLeases() {
        return getClockService().beginningOfQuarter();
    }

    public LocalDate default2CalculateLeases() {
        return getClockService().beginningOfQuarter();
    }

// //////////////////////////////////////
    
    @Prototype
    public String verifyAllLeases() {
        DateTime dt = DateTime.now();
        List<Lease> leases = allLeases();
        for (Lease lease : leases){
            lease.verify();
        }
        Period p = new Period(dt, DateTime.now());
        return String.format("Verified %d leases in %s", leases.size(), JodaPeriodUtils.asString(p));
    }
    
    // //////////////////////////////////////
    
    @Programmatic
    public Lease findLeaseByReference(final String reference) {
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }
    
    @Programmatic
    public List<Lease> findLeasesByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }
    
    // //////////////////////////////////////

    private InvoiceItemsForLease invoiceItemsForLease;

    public final void injectInvoiceItemsForLease(final InvoiceItemsForLease invoiceItemsForLease) {
        this.invoiceItemsForLease = invoiceItemsForLease;
    }

    private FixedAssets fixedAssets;

    public final void injectFixedAssets(final FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    private AgreementTypes agreementTypes;

    public final void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public final void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    private InvoiceSummaries invoiceSummaries;

    public void injectInvoiceSummaries(final InvoiceSummaries invoiceSummaries) {
        this.invoiceSummaries = invoiceSummaries;
    }

}
