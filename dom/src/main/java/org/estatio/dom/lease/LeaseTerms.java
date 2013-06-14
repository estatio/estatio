package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

@Named("Lease Terms")
public class LeaseTerms extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "leaseTerms";
    }

    public String iconName() {
        return "LeaseTerm";
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

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances(LeaseTerm.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> leaseTermsToBeApproved(LocalDate date) {
        throw new NotImplementedException();
    }

    public LocalDate default0LeaseTermsToBeApproved() {
        return clockService.now();
    }

    // //////////////////////////////////////

    @Hidden
    public LeaseTerm findLeaseTermWithSequence(LeaseItem leaseItem, BigInteger sequence) {
        throw new NotImplementedException();
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

}
