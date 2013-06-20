package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;
import org.apache.isis.objectstore.jdo.service.RegisterEntities;

import org.estatio.dom.EstatioDomainService;
import org.estatio.services.clock.ClockService;

@Named("Lease Terms")
public class LeaseTerms extends EstatioDomainService {

    public LeaseTerms() {
        super(LeaseTerms.class, LeaseTerm.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem, final LeaseTerm previous) {
        LeaseTerm leaseTerm = leaseItem.getType().create(getContainer());
        persist(leaseTerm);
        leaseTerm.modifyLeaseItem(leaseItem);
        leaseTerm.modifyPreviousTerm(previous);

        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated
        getContainer().flush();
        isisJdoSupport.refresh(leaseItem);
        leaseTerm.initialize();
        return leaseTerm;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem) {
        LeaseTerm leaseTerm = newLeaseTerm(leaseItem, null);
        return leaseTerm;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> leaseTermsToBeApproved(LocalDate date) {
        return allMatches(queryForLeaseTermsWithStatus(LeaseTermStatus.NEW, date));
    }

    public LocalDate default0LeaseTermsToBeApproved() {
        return clockService.now();
    }

    private static QueryDefault<LeaseTerm> queryForLeaseTermsWithStatus(LeaseTermStatus status, LocalDate date) {
        return new QueryDefault<LeaseTerm>(LeaseTerm.class, "leaseTerm_findLeaseTermsWithStatus", "status", status, "date", date);
    }


    // //////////////////////////////////////

    @Hidden
    public LeaseTerm findLeaseTermWithSequence(LeaseItem leaseItem, BigInteger sequence) {
        return firstMatch(queryForLeaseTermsWithSequence(leaseItem, sequence));
    }

    private static QueryDefault<LeaseTerm> queryForLeaseTermsWithSequence(LeaseItem leaseItem, BigInteger sequence) {
        return new QueryDefault<LeaseTerm>(LeaseTerm.class, "leaseTerm_findLeaseTermsWithSequence", "leaseItem", leaseItem, "sequence", sequence);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances(LeaseTerm.class);
    }

    // //////////////////////////////////////

    private IsisJdoSupport isisJdoSupport;

    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    
    
    
    
    /**
     * Horrid hack... without this hsqldb was attempting to do the DDL for the
     * table intermixed with DML, and so hit a deadlock in the driver.
     * 
     * HSQLDB 1.8.10 didn't have this problem.
     * 
     * REVIEW: this might not be needed now that we have {@link RegisterEntities}.
     */
    @Hidden
    public LeaseTerm dummy() {
        return null;
    }

    
}
