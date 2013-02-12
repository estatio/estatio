package org.estatio.dom.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Hidden
@Named("Lease Terms")
public class LeaseTerms extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        //TODO: Q: why does getId always returns the class name in lower case?
        return "leaseterms";
    }

    public String iconName() {
        return "LeaseTerm";
    }
    // }}

    // {{ newLeaseTerm
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem) {
        LeaseTerm leaseTerm = leaseItem.getType().createLeaseTerm(getContainer()) ;
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.setStatus(LeaseTermStatus.CONCEPT);
        persist(leaseTerm);
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
