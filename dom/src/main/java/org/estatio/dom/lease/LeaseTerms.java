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
@Named("Leases")
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
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseTerm newLeaseTerm(
            final LeaseItem leaseItem) {
        LeaseTerm leaseTerm = newTransientInstance(LeaseTerm.class);
        leaseTerm.setLeaseItem(leaseItem);
        persist(leaseTerm);
        return leaseTerm;
    }
    // }}

    // {{ newIndexableLeaseTerm
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseTermForIndexableRent newIndexableLeaseTerm(LeaseItem leaseItem) {
        LeaseTermForIndexableRent leaseTerm = newTransientInstance(LeaseTermForIndexableRent.class);
        leaseTerm.setLeaseItem(leaseItem);
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
