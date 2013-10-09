/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

public class LeaseTerms extends EstatioDomainService<LeaseTerm> {

    public LeaseTerms() {
        super(LeaseTerms.class, LeaseTerm.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem, final LeaseTerm previous) {
        LeaseTerm leaseTerm = leaseItem.getType().create(getContainer());
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.modifyPrevious(previous);
        persistIfNotAlready(leaseTerm);

        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated
        
        // REVIEW: is this still an issue?
        getContainer().flush();
        isisJdoSupport.refresh(leaseItem);
        leaseTerm.initialize();
        return leaseTerm;
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public LeaseTerm newLeaseTerm(final LeaseItem leaseItem) {
        return newLeaseTerm(leaseItem, null);
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Leases", sequence="20")
    public List<LeaseTerm> leaseTermsToBeApproved(final LocalDate date) {
        return allMatches("findByStatusAndActiveDate", "status", LeaseTermStatus.NEW, "date", date);
    }

    public LocalDate default0LeaseTermsToBeApproved() {
        return getClockService().now();
    }


    // //////////////////////////////////////

    @Hidden
    public LeaseTerm findLeaseTermByLeaseItemAndSequence(final LeaseItem leaseItem, final BigInteger sequence) {
        return firstMatch("findByLeaseItemAndSequence", "leaseItem", leaseItem, "sequence", sequence);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Leases", sequence="99")
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances();
    }

    // //////////////////////////////////////

    // commenting out since suspect may now be ok...
    // if reoccurs, add back in...
    
//    /**
//     * Horrid hack... without this hsqldb was attempting to do the DDL for the
//     * table intermixed with DML, and so hit a deadlock in the driver.
//     * 
//     * HSQLDB 1.8.10 didn't have this problem.
//     * 
//     * this might not be needed now that we have {@link RegisterEntities}.
//     */
//    @Hidden
//    public LeaseTerm dummy() {
//        return null;
//    }

    

}
