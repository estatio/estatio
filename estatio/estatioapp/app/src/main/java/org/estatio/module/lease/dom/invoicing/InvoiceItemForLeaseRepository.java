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
package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermValueType;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(repositoryFor = InvoiceItemForLease.class, nature = NatureOfService.DOMAIN)
public class InvoiceItemForLeaseRepository extends UdoDomainRepositoryAndFactory<InvoiceItemForLease> {

    public InvoiceItemForLeaseRepository() {
        super(InvoiceItemForLeaseRepository.class, InvoiceItemForLease.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public InvoiceItemForLease newInvoiceItem(
            final InvoiceForLease invoice,
            final LocalDate dueDate) {

        final InvoiceItemForLease invoiceItem = newItem(invoice, dueDate);

        persistIfNotAlready(invoiceItem);
        return invoiceItem;
    }

    @Programmatic
    public InvoiceItemForLease newInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval,
            final LocalDateInterval calculationInterval,
            final LocalDateInterval effectiveInterval,
            final LocalDate dueDate,
            final String interactionId) {

        final Lease lease = leaseTerm.getLeaseItem().getLease();
        final InvoiceForLease invoice = invoiceRepository.findOrCreateMatchingInvoice(
                leaseTerm.getApplicationTenancy(),
                leaseTerm.getLeaseItem().getPaymentMethod(),
                lease,
                InvoiceStatus.NEW,
                dueDate, interactionId);

        final InvoiceItemForLease invoiceItem = newItem(invoice, dueDate);

        invoiceItem.setLeaseTerm(leaseTerm);

        invoiceItem.setStartDate(interval.startDate());
        invoiceItem.setEndDate(interval.endDate());
        invoiceItem.setEffectiveStartDate(effectiveInterval.startDate());
        invoiceItem.setEffectiveEndDate(effectiveInterval.endDate());
        invoiceItem.setCalculationStartDate(calculationInterval.startDate());
        invoiceItem.setCalculationEndDate(calculationInterval.endDate());

        // redundantly persist, these are immutable
        // assumes only one occupancy per lease...
        invoiceItem.setLease(lease);
        final Optional<Occupancy> occupancyIfAny = lease.primaryOccupancy();
        occupancyIfAny.ifPresent(occupancy -> {
            Unit unit = occupancy.getUnit();
            invoiceItem.setFixedAsset(unit);
        });

        persistIfNotAlready(invoiceItem);
        return invoiceItem;
    }

    private InvoiceItemForLease newItem(final InvoiceForLease invoice, final LocalDate dueDate) {
        return new InvoiceItemForLease(invoice, dueDate);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseTerm(final LeaseTerm leaseTerm){
        return allMatches("findByLeaseTerm", "leaseTerm", leaseTerm);
    }


    @Programmatic
    public List<InvoiceItemForLease> findByLeaseTermAndInterval(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval) {
        return allMatches(
                "findByLeaseTermAndInterval",
                "leaseTerm", leaseTerm,
                "startDate", interval.startDate(),
                "endDate", interval.endDate());
    }

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseTermAndEffectiveInterval(
            final LeaseTerm leaseTerm,
            final LocalDateInterval effectiveInterval) {
        return allMatches(
                "findByLeaseTermAndEffectiveInterval",
                "leaseTerm", leaseTerm,
                "effectiveStartDate", effectiveInterval.startDate(),
                "effectiveEndDate", effectiveInterval.endDate());
    }

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseTermAndIntervalAndInvoiceStatus(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseTermAndIntervalAndInvoiceStatus",
                "leaseTerm", leaseTerm,
                "startDate", interval.startDate(),
                "endDate", interval.endDate(),
                "invoiceStatus", invoiceStatus);
    }

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseAndInvoiceStatus(
            final Lease lease,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseAndInvoiceStatus",
                "lease", lease,
                "invoiceStatus", invoiceStatus);
    }

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseItemAndInvoiceStatus(
            final LeaseItem leaseItem,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseItemAndInvoiceStatus",
                "leaseItem", leaseItem,
                "invoiceStatus", invoiceStatus);
    }

    @Programmatic
    public List<InvoiceItemForLease> findByLeaseTermAndInvoiceStatus(
            final LeaseTerm leaseTerm,
            final InvoiceStatus invoiceStatus) {
        return allMatches(
                "findByLeaseTermAndInvoiceStatus",
                "leaseTerm", leaseTerm,
                "invoiceStatus", invoiceStatus);
    }

    // //////////////////////////////////////

    @Programmatic
    public InvoiceItemForLease createUnapprovedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval,
            final LocalDateInterval calculationInterval,
            final LocalDateInterval effectiveInterval,
            final LocalDate dueDate,
            final String interactionId) {
        // TODO:Removing items returns unwanted results, perhaps remove all old
        // runs before?
        // removeUnapprovedInvoiceItems(leaseTerm, invoiceInterval);
        return newInvoiceItem(leaseTerm, invoiceInterval, calculationInterval, effectiveInterval, dueDate, interactionId);
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDateInterval invoiceInterval) {

        List<InvoiceItemForLease> invoiceItems =
                findByLeaseTermAndIntervalAndInvoiceStatus(
                        leaseTerm, invoiceInterval, InvoiceStatus.NEW);
        if (invoiceItems.size() > 1) {
            throw new ApplicationException("Found more then one unapproved invoice items for" + leaseTerm.toString() + invoiceInterval.toString());
        }
        if (invoiceItems.size() == 1) {
            return invoiceItems.get(0);
        }
        return null;
    }

    @Programmatic
    public BigDecimal invoicedValue(
            final LeaseTerm leaseTerm,
            final LocalDateInterval interval) {
        BigDecimal invoicedValue = new BigDecimal(0);
        List<InvoiceItemForLease> items = leaseTerm.valueType() == LeaseTermValueType.FIXED ? findByLeaseTerm(leaseTerm) : findByLeaseTermAndInterval(leaseTerm, interval);
        for (InvoiceItemForLease invoiceItem : items) {
            invoicedValue = invoicedValue.add(invoiceItem.getNetAmount());
        }
        return invoicedValue;
    }

    // //////////////////////////////////////

    @Inject
    private InvoiceForLeaseRepository invoiceRepository;

}
