package org.estatio.fixturescripts;

import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.joda.time.LocalDate;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;

public class GenerateTopModelInvoice implements Callable<Object> {
    
    @Override
    public Object call() throws Exception {
        final Lease lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        lease.verify();

        final SortedSet<LeaseItem> items = lease.getItems();
        for (LeaseItem leaseItem : items) {
            final SortedSet<LeaseTerm> terms = leaseItem.getTerms();
            for (LeaseTerm leaseTerm : terms) {
                if(leaseTerm.getStatus().isNew()) {
                    leaseTerm.check();
                }
            }

            for (LeaseTerm leaseTerm : terms) {
                if(leaseTerm.getStartDate().equals(new LocalDate(2012,7,15))) {
                    leaseTerm.calculate(new LocalDate(2013,4,1), new LocalDate(2013,4,1));
                }
            }
        }
        
        return lease;
    }
    
    
    // {{ injected: Leases
    private Leases leases;

    public void setLeases(final Leases leases) {
        this.leases = leases;
    }
    // }}

}
