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

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
    public List<LeaseTermForServiceCharge> findServiceChargeByPropertyAndStartDate(
            final Property property, final LocalDate startDate) {
        return Lists.newArrayList(
                Iterables.filter(
                    Iterables.filter(allLeaseTerms(), LeaseTermForServiceCharge.class),
                    whetherForPropertyAndStartDate(property, startDate)));
    }

    private static Predicate<LeaseTermForServiceCharge> whetherForPropertyAndStartDate(
            final Property property, final LocalDate startDate) {
        return new Predicate<LeaseTermForServiceCharge>() {
            @Override
            public boolean apply(final LeaseTermForServiceCharge input) {
                Property candidateProperty = input.getLeaseItem().getLease().getProperty();
                LocalDate candidateStartDate = input.getStartDate();
                return Objects.equal(candidateProperty, property) && Objects.equal(candidateStartDate, startDate);
            }
        };
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<LocalDate> findServiceChargeDatesByProperty(Property property) {
        return Lists.newArrayList(
                Sets.newTreeSet(
                    Iterables.transform(
                        Iterables.filter(
                            Iterables.filter(allLeaseTerms(), LeaseTermForServiceCharge.class),
                            whetherForProperty(property)),
                        startDateOf())));
    }

    private static Function<LeaseTermForServiceCharge, LocalDate> startDateOf() {
        return new Function<LeaseTermForServiceCharge, LocalDate>(){
            @Override
            public LocalDate apply(LeaseTermForServiceCharge input) {
                return input.getStartDate();
            }};
    }

    private static Predicate<LeaseTermForServiceCharge> whetherForProperty(
            final Property property) {
        return new Predicate<LeaseTermForServiceCharge>() {
            @Override
            public boolean apply(final LeaseTermForServiceCharge input) {
                Property candidateProperty = input.getLeaseItem().getLease().getProperty();
                return Objects.equal(candidateProperty, property);
            }
        };
    }




}
