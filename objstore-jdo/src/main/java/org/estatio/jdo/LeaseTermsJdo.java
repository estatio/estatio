package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.QueryDefault;

public class LeaseTermsJdo extends LeaseTerms {

    
    /**
     * Horrid hack... without this hsqldb was attempting to do the DDL for the table intermixed with DML,
     * and so hit a deadlock in the driver.
     * 
     * <p>
     * HSQLDB 1.8.10 didn't have this problem.
     */
    @Hidden
    public LeaseTerm dummy() {return null;}

    @Override
    public List<LeaseTerm> leaseTermsToBeApproved() {
        return allMatches(queryForLeaseTermsWithStatus(LeaseTermStatus.NEW));
    }
    
    private static QueryDefault<LeaseTerm> queryForLeaseTermsWithStatus(LeaseTermStatus status) {
        return new QueryDefault<LeaseTerm>(LeaseTerm.class, "leaseTerm_findLeaseTermsWithStatus", "status", status);
    }
    
    
    
}
