package org.estatio.dom.lease.contributed;

import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermStatus;

import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;

@Named("Lease Terms")
public class LeaseTermContributedActions {

    @NotInServiceMenu
    public LeaseTerm changeStatus(LeaseTerm lt, LeaseTermStatus status) {
        lt.setStatus(status);
        return lt;
    }
    
}
