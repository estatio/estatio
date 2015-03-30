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
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.EstatioUserRoles;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(menuOrder = "40", repositoryFor = LeaseTerm.class)
public class LeaseTerms extends UdoDomainRepositoryAndFactory<LeaseTerm> {

    public LeaseTerms() {
        super(LeaseTerms.class, LeaseTerm.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    public LeaseTerm newLeaseTerm(
            final LeaseItem leaseItem,
            final LeaseTerm previous,
            final LocalDate startDate,
            final LocalDate endDate) {
        LeaseTerm leaseTerm = leaseItem.getType().create(getContainer());
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.modifyStartDate(startDate);
        leaseTerm.modifyEndDate(endDate);
        leaseTerm.setStatus(LeaseTermStatus.NEW);
        persistIfNotAlready(leaseTerm);
        if (previous != null) {
            previous.setNext(leaseTerm);
        }
        // TOFIX: without this flush and refresh, the collection of terms on the
        // item is not updated. Removing code below will fail integration tests
        // too.
        getContainer().flush();
        getIsisJdoSupport().refresh(leaseItem);
        return leaseTerm;
    }

    @Deprecated
    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(name = "Leases", sequence = "20")
    public List<LeaseTerm> allLeaseTermsToBeApproved(final LocalDate date) {
        return allMatches("findByStatusAndActiveDate", "status", LeaseTermStatus.NEW, "date", date);
    }

    public LocalDate default0AllLeaseTermsToBeApproved() {
        return getClockService().now();
    }

    /**
     * Returns terms by LeaseItem and sequence. Used by the API
     */
    @Action(hidden = Where.EVERYWHERE)
    public LeaseTerm findByLeaseItemAndSequence(final LeaseItem leaseItem, final BigInteger sequence) {
        return firstMatch("findByLeaseItemAndSequence", "leaseItem", leaseItem, "sequence", sequence);
    }

    @Programmatic
    public LeaseTerm findByLeaseItemAndStartDate(final LeaseItem leaseItem, final LocalDate startDate) {
        return firstMatch("findByLeaseItemAndStartDate", "leaseItem", leaseItem, "startDate", startDate);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(name = "Leases", sequence = "99")
    public List<LeaseTerm> allLeaseTerms() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LeaseTerm> findByPropertyAndTypeAndStartDate(
            final Property property,
            final LeaseItemType leaseItemType,
            final LocalDate startDate) {
        return allMatches("findByPropertyAndTypeAndStartDate",
                "property", property,
                "leaseItemType", leaseItemType,
                "startDate", startDate);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LeaseTermForServiceCharge> findServiceChargeByPropertyAndStartDate(
            final Property property,
            final LocalDate startDate) {
        final List leaseTerms = findByPropertyAndTypeAndStartDate(property, LeaseItemType.SERVICE_CHARGE, startDate);
        return leaseTerms;
    }

    // //////////////////////////////////////

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LocalDate> findStartDatesByPropertyAndType(
            final Property property,
            final LeaseItemType leaseItemType) {
        List startDates = allMatches(
                "findStartDatesByPropertyAndType",
                "property", property,
                "leaseItemType", leaseItemType);
        return startDates;
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<LocalDate> findServiceChargeDatesByProperty(final Property property) {
        return findStartDatesByPropertyAndType(property, LeaseItemType.SERVICE_CHARGE);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(name = "Leases", sequence = "30")
    public List<LeaseTerm> findTermsWithInvalidInterval() {
        List<LeaseTerm> lts = allLeaseTerms();
        List<LeaseTerm> returnList = new ArrayList<LeaseTerm>();
        LocalDateInterval ldi;
        for (LeaseTerm lt : lts) {
            try {
                if ((ldi = lt.getEffectiveInterval()) == null) {
                    returnList.add(lt);
                    continue;
                }

                if (!ldi.isValid()) {
                    returnList.add(lt);
                    continue;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                returnList.add(lt);
            }
        }

        if (returnList.isEmpty()) {
            return null;
        } else {
            return returnList;
        }
    }

    public boolean hideFindTermsWithInvalidInterval() {
        return !getContainer().getUser().hasRole(EstatioUserRoles.ADMIN_ROLE);
    }
}
