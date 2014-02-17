/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import org.estatio.dom.asset.Property;

public class LeaseTerms extends EstatioDomainService<LeaseTerm> {

    public LeaseTerms() {
        super(LeaseTerms.class, LeaseTerm.class);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public LeaseTerm newLeaseTerm(
            final LeaseItem leaseItem,
            final LeaseTerm previous,
            final LocalDate startDate) {
        LeaseTerm leaseTerm = leaseItem.getType().create(getContainer());
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.modifyPrevious(previous);
        leaseTerm.modifyStartDate(startDate);
        persistIfNotAlready(leaseTerm);

        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated. Removing code below will fail integration tests
        // too.
        getContainer().flush();
        isisJdoSupport.refresh(leaseItem);
        leaseTerm.initialize();
        return leaseTerm;
    }

    @Deprecated
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Leases", sequence = "20")
    public List<LeaseTerm> leaseTermsToBeApproved(final LocalDate date) {
        return allMatches("findByStatusAndActiveDate", "status", LeaseTermStatus.NEW, "date", date);
    }

    public LocalDate default0LeaseTermsToBeApproved() {
        return getClockService().now();
    }

    /**
     * Returns terms by LeaseItem and sequence. Used by the API
     */
    @Hidden
    public LeaseTerm findByLeaseItemAndSequence(final LeaseItem leaseItem, final BigInteger sequence) {
        return firstMatch("findByLeaseItemAndSequence", "leaseItem", leaseItem, "sequence", sequence);
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Leases", sequence = "99")
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances();
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<LeaseTerm> findByPropertyAndTypeAndStartDate(
            final Property property, final LeaseItemType leaseItemType, final LocalDate startDate) {
        return allMatches("findByPropertyAndTypeAndStartDate", 
                "property", property, "leaseItemType", leaseItemType, "startDate", startDate);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<LeaseTermForServiceCharge> findServiceChargeByPropertyAndStartDate(
            final Property property, final LocalDate startDate) {
        final List leaseTerms = findByPropertyAndTypeAndStartDate(property, LeaseItemType.SERVICE_CHARGE, startDate);
        return leaseTerms;
    }

    // //////////////////////////////////////
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<LocalDate> findStartDatesByPropertyAndType(
            final Property property, final LeaseItemType leaseItemType) {
        List startDates = allMatches(
                "findStartDatesByPropertyAndType", 
                "property", property, "leaseItemType", leaseItemType);
        return startDates;
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<LocalDate> findServiceChargeDatesByProperty(final Property property) {
        return findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }
}
