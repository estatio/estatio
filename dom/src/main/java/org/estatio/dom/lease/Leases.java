package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.DateTimeUtils;
import org.estatio.dom.utils.StringUtils;

@Named("Leases")
public class Leases extends EstatioDomainService {

    public enum InvoiceRunType {
        NORMAL_RUN, RETRO_RUN;
    }
    
    public Leases() {
        super(Leases.class, Lease.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Lease newLease(
            final @Named("Reference") String reference, 
            final @Named("Name") String name, 
            final @Named("Start Date") LocalDate startDate, 
            final @Optional @Named("Duration") @DescribedAs("Duration in a text format. Example 6y5m2d") String duration,
            final @Optional @Named("End Date") @DescribedAs("Can be omitted when duration is filled in") LocalDate endDate, 
            final @Optional @Named("Landlord") Party landlord, 
            final @Optional @Named("Tentant") Party tenant) {
        LocalDate calculatedEndDate = endDate;
        if (duration != null) {
            Period p = DateTimeUtils.stringToPeriod(duration);
            if (p != null) {
                calculatedEndDate = startDate.plus(p).minusDays(1);
            }
        }
        Lease lease = newTransientInstance(Lease.class);
        lease.setAgreementType(agreementTypes.find(LeaseConstants.AT_LEASE));
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(calculatedEndDate);
        persistIfNotAlready(lease);
        final AgreementRoleType artTenant = agreementRoleTypes.find(LeaseConstants.ART_TENANT);
        lease.addRole(tenant, artTenant, null, null);
        final AgreementRoleType artLandlord = agreementRoleTypes.find(LeaseConstants.ART_LANDLORD);
        lease.addRole(landlord, artLandlord, null, null);
        return lease;
    }

    // //////////////////////////////////////

    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Lease> findLeasesByReference(@Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference));
    }

    private static QueryDefault<Lease> queryForFindByReference(String reference) {
        return new QueryDefault<Lease>(Lease.class, "findLeasesByReference", "r", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<Lease> findLeases(@Named("Fixed Asset") FixedAsset fixedAsset, @Named("Active On Date") LocalDate activeOnDate) {
        return allMatches(queryForFind(fixedAsset, activeOnDate));
    }

    private static QueryDefault<Lease> queryForFind(FixedAsset fixedAsset, LocalDate activeOnDate) {
        return new QueryDefault<Lease>(Lease.class, "findLeases", "fixedAsset", fixedAsset, "activeOnDate", activeOnDate);
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
    public List<InvoiceItemForLease> calculate(final @Named("Lease reference") String leaseReference, final @Named("Period Start Date") LocalDate startDate, final @Named("Due date") LocalDate dueDate, final @Named("Run Type") InvoiceRunType runType) {
        List<Lease> leases = findLeasesByReference(leaseReference);
        for (Lease lease : leases) {
            lease.calculate(startDate, dueDate, runType);
        }
        // As a convenience, we now go find them and display them.
        // We've done it this way so that the user can always just go to the
        // menu and make this query.
        return invoices.findItems(leaseReference, startDate, dueDate);
    }

    public List<FixedAsset> autoComplete0FindLeases(final String searchPhrase) {
        return fixedAssets.search(searchPhrase);
    }



    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Lease> allLeases() {
        return allInstances(Lease.class);
    }


    // //////////////////////////////////////

    private InvoicesForLease invoices;

    public void injectInvoicesService(final InvoicesForLease invoices) {
        this.invoices = invoices;
    }

    private FixedAssets fixedAssets;

    public void injectFixedAssets(FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    private AgreementTypes agreementTypes;

    public void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }
}
