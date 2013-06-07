package org.estatio.dom.lease.contributed;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermStatus;

@Named("Lease Terms")
public class LeaseTermContributedActions {

    @NotInServiceMenu
    public LeaseTerm changeStatus(LeaseTerm lt, LeaseTermStatus status) {
        lt.setStatus(status);
        return lt;
    }
    
}
