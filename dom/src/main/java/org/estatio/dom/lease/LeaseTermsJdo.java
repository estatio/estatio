package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.QueryDefault;

public class LeaseTermsJdo extends LeaseTerms {

    /**
     * Horrid hack... without this hsqldb was attempting to do the DDL for the
     * table intermixed with DML, and so hit a deadlock in the driver.
     * 
     * HSQLDB 1.8.10 didn't have this problem.
     */
    @Hidden
    public LeaseTerm dummy() {
        return null;
    }

    @Override
    public List<LeaseTerm> leaseTermsToBeApproved(LocalDate date) {
        return allMatches(queryForLeaseTermsWithStatus(LeaseTermStatus.NEW, date));
    }

    private static QueryDefault<LeaseTerm> queryForLeaseTermsWithStatus(LeaseTermStatus status, LocalDate date) {
        return new QueryDefault<LeaseTerm>(LeaseTerm.class, "leaseTerm_findLeaseTermsWithStatus", "status", status, "date", date);
    }

    @Override 
    public LeaseTerm findLeaseTermWithSequence(LeaseItem leaseItem, BigInteger sequence) {
        return firstMatch(queryForLeaseTermsWithSequence(leaseItem, sequence));
    }

    private static QueryDefault<LeaseTerm> queryForLeaseTermsWithSequence(LeaseItem leaseItem, BigInteger sequence) {
        return new QueryDefault<LeaseTerm>(LeaseTerm.class, "leaseTerm_findLeaseTermsWithSequence", "leaseItem", leaseItem, "sequence", sequence);
    }

    
}
