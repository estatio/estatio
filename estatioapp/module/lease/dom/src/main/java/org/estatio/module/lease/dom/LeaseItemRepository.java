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
package org.estatio.module.lease.dom;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;

@DomainService(menuOrder = "40", repositoryFor = LeaseItem.class, nature = NatureOfService.DOMAIN)
public class LeaseItemRepository extends UdoDomainRepositoryAndFactory<LeaseItem> {

    public LeaseItemRepository() {
        super(LeaseItemRepository.class, LeaseItem.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseItem newLeaseItem(
            final Lease lease,
            final LeaseItemType type,
            final LeaseAgreementRoleTypeEnum invoicedBy,
            final Charge charge,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final LocalDate startDate) {
        BigInteger nextSequence = nextSequenceFor(lease, type);
        LeaseItem leaseItem = newTransientInstance();
        leaseItem.setApplicationTenancyPath(lease.getProperty() == null ? lease.getApplicationTenancyPath() : estatioApplicationTenancyRepository.findOrCreateTenancyFor(lease.getProperty(), lease.getPrimaryParty()).getPath());
        leaseItem.setType(type);
        leaseItem.setCharge(charge);
        leaseItem.setPaymentMethod(paymentMethod);
        leaseItem.setInvoicingFrequency(invoicingFrequency);
        leaseItem.setLease(lease);
        leaseItem.setStartDate(startDate);
        leaseItem.setStatus(LeaseItemStatus.ACTIVE);
        leaseItem.setSequence(nextSequence);
        leaseItem.setInvoicedBy(invoicedBy);
        persistIfNotAlready(leaseItem);
        return leaseItem;
    }

    private BigInteger nextSequenceFor(final Lease lease, final LeaseItemType type) {
        LeaseItem last = Iterables.getLast(findLeaseItemsByType(lease, type), null);
        if (last == null) {
            return BigInteger.ONE;
        }
        return last.getSequence() == null ? BigInteger.ONE : last.getSequence().add(BigInteger.ONE);
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseItem findLeaseItem(
            final Lease lease,
            final LeaseItemType type,
            final LocalDate startDate,
            final BigInteger sequence) {
        return firstMatch("findByLeaseAndTypeAndStartDateAndSequence",
                "lease", lease,
                "type", type,
                "startDate", startDate,
                "sequence", sequence);
    }

    @Programmatic
    public List<LeaseItem> findLeaseItemsByType(
            final Lease lease,
            final LeaseItemType type) {
        return allMatches("findByLeaseAndType",
                "lease", lease,
                "type", type);
    }

    @Programmatic
    public LeaseItem findByLeaseAndTypeAndCharge(
            final Lease lease,
            final LeaseItemType type,
            final Charge charge) {
        return firstMatch("findByLeaseAndTypeAndCharge",
                "lease", lease,
                "type", type,
                "charge", charge);
    }

    @Programmatic
    public LeaseItem findByLeaseAndTypeAndChargeAndStartDateAndInvoicedBy(
            final Lease lease,
            final LeaseItemType type,
            final Charge charge,
            final LocalDate startDate,
            final LeaseAgreementRoleTypeEnum invoicedBy) {
        return firstMatch("findByLeaseAndTypeAndChargeAndStartDateAndInvoicedBy",
                "lease", lease,
                "type", type,
                "charge", charge,
                "startDate", startDate,
                "invoicedBy", invoicedBy);
    }

    @Programmatic
    public LeaseItem findByLeaseAndTypeAndStartDateAndInvoicedBy(
            final Lease lease,
            final LeaseItemType type,
            final LocalDate startDate,
            final LeaseAgreementRoleTypeEnum invoicedBy) {
        return firstMatch("findByLeaseAndTypeAndStartDateAndInvoicedBy",
                "lease", lease,
                "type", type,
                "startDate", startDate,
                "invoicedBy", invoicedBy);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<LeaseItem> allLeaseItems() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Inject
    EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepository;

}
