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

import org.joda.time.LocalDate;
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

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.invoice.Invoices;
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
            // CHECKSTYLE:OFF
            final @Named("Reference") String reference, 
            final @Named("Name") String name, 
            final @Named("Start Date") LocalDate startDate, 
            final @Optional @Named("Duration") @DescribedAs("Duration in a text format. Example 6y5m2d") 
            String duration,
            final @Optional @Named("End Date") @DescribedAs("Can be omitted when duration is filled in") 
            LocalDate endDate, 
            final @Optional @Named("Landlord") Party landlord, 
            final @Optional @Named("Tentant") Party tenant
            // CHECKSTYLE:ON
            ) {
        LocalDate calculatedEndDate = endDate;
        if (duration != null) {
            final Period p = JodaPeriodUtils.asPeriod(duration);
            if (p != null) {
                calculatedEndDate = startDate.plus(p).minusDays(1);
            }
        }
        Lease lease = newTransientInstance();
        final AgreementType at = agreementTypes.find(LeaseConstants.AT_LEASE);
        lease.setAgreementType(at);
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(calculatedEndDate);
        persistIfNotAlready(lease);
        
        if(tenant != null) {
            final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
            lease.newRole(artTenant, tenant, null, null);
        }
        if(landlord != null) {
            final AgreementRoleType artLandlord = agreementRoleTypes.findByTitle(LeaseConstants.ART_LANDLORD);
            lease.newRole(artLandlord, landlord, null, null);
        }
        return lease;
    }

    // //////////////////////////////////////

    
    @Programmatic
    public Lease findLeaseByReference(final String reference) {
        return firstMatch("findByReference", 
                "reference", StringUtils.wildcardToRegex(reference));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeases(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") 
            String referenceOrName) {
        return allMatches("findByReferenceOrName", 
                "referenceOrName", StringUtils.wildcardToRegex(referenceOrName));
    }
    public String default0FindLeases() {
        return "AAA-BBBBBBB*";
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

    
    /**
     * Returns the {@link InvoiceItemForLease}s that are newly
     * {@link Lease#calculate(LocalDate, LocalDate) calculate}d for all of the
     * {@link Lease}s matched by the provided <tt>leaseReference</tt> and the
     * other parameters.
     */
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "6")
    public List<InvoiceItemForLease> calculate(
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
        return invoiceItemsForLease.findInvoiceItemsByLease(referenceOrName, startDate, dueDate);
    }
    public String default0Calculate() {
        return "AAA-*";
    }
    public LocalDate default1Calculate() {
        return getClockService().beginningOfQuarter();
    }
    public LocalDate default2Calculate() {
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

    private Invoices invoices;

    public final void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }
    
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
    
    
}
