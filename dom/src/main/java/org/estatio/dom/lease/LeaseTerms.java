package org.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

@Hidden
@Named("Lease Terms")
public class LeaseTerms extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseTerms";
    }

    public String iconName() {
        return "LeaseTerm";
    }

    // }}

    // {{ newLeaseTerm
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem, final LeaseTerm previous) {
        LeaseTerm leaseTerm = leaseItem.getType().createLeaseTerm(getContainer());
        persist(leaseTerm);
        leaseTerm.modifyLeaseItem(leaseItem);
        if (previous != null) {
            previous.modifyNextTerm(leaseTerm);
        }
        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated
        getContainer().flush();
        isisJdoSupport.refresh(leaseItem);
        leaseTerm.initialize();
        return leaseTerm;
    }

    // }}

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem) {
        LeaseTerm leaseTerm = newLeaseTerm(leaseItem, null);
        return leaseTerm;
    }

    // }}

    // {{ allLeaseTerms
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances(LeaseTerm.class);
    }

    // }}

    // {{ injected
    private IsisJdoSupport isisJdoSupport;

    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
    // }}

}
