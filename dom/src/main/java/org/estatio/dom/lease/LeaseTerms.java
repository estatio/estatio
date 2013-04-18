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
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem) {
        LeaseTerm leaseTerm = leaseItem.getType().createLeaseTerm(getContainer()) ;
        leaseTerm.setLeaseItem(leaseItem);
        persist(leaseTerm);
        leaseTerm.initialize();
        return leaseTerm;
    }
    // }}

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @NotContributed
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem, final LeaseTerm previous) {
        LeaseTerm leaseTerm = newLeaseTerm(leaseItem);
        leaseTerm.modifyPreviousTerm(previous);       
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
}
