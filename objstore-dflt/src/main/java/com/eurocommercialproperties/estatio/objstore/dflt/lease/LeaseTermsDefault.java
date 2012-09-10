package com.eurocommercialproperties.estatio.objstore.dflt.lease;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerms;

public class LeaseTermsDefault extends AbstractFactoryAndRepository implements LeaseTerms {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leaseterms";
        //TODO: Q: why does getId always returns the class name in lower case?
    }

    public String iconName() {
        return "LeaseTerm";
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public LeaseTerm newLeaseTerm(@Named("LeaseItem") LeaseItem leaseItem) {
        LeaseTerm leaseTerm = newTransientInstance(LeaseTerm.class);
        leaseTerm.setLeaseItem(leaseItem);
        persist(leaseTerm);
        return leaseTerm;
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<LeaseTerm> allInstances() {
        return allInstances(LeaseTerm.class);
    }
}
