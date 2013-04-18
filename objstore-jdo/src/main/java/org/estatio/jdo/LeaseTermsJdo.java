package org.estatio.jdo;

import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTerms;

import org.apache.isis.applib.annotation.Hidden;

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
}
