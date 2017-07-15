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
package org.estatio.app.menus.invoice;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.InvoiceForLeaseRepository;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForInvoiceRun;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.dom.lease.invoicing.viewmodel.InvoiceSummaryForPropertyInvoiceDateRepository;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Invoices Out",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.4")
public class InvoiceMenu extends UdoDomainRepositoryAndFactory<Invoice> {

    public InvoiceMenu() {
        super(InvoiceMenu.class, Invoice.class);
    }

    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoiceForLease(
            final Lease lease,
            final LocalDate dueDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final PaymentMethod paymentMethod,
            final Currency currency) {

        final Property propertyIfAny = lease.getProperty();
        final Party seller = lease.getPrimaryParty();
        final Party buyer = lease.getSecondaryParty();

        final ApplicationTenancy propertySellerTenancy =
                estatioApplicationTenancyRepositoryForLease.findOrCreateTenancyFor(propertyIfAny, seller);

        return invoiceForLeaseRepository.newInvoice(propertySellerTenancy,
                seller,
                buyer,
                paymentMethod==null ? lease.defaultPaymentMethod() : paymentMethod,
                currency,
                dueDate,
                lease, null);
    }

    public String validateNewInvoiceForLease(final Lease lease, final LocalDate dueDate, final PaymentMethod paymentMethod, final Currency currency ) {
        final Property propertyIfAny = lease.getProperty();
        if(propertyIfAny == null) {
            return "Can only create invoices for leases that have an occupancy";
        }
        if(paymentMethod==null && lease.defaultPaymentMethod()==null){
            return "A payment method has to be provided";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<InvoiceSummaryForInvoiceRun> allInvoiceRuns() {
        return invoiceSummaryForInvoiceRunRepository.allInvoiceRuns();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "22")
    public List<InvoiceSummaryForPropertyDueDateStatus> allRecentlyInvoiced(final LocalDate dueDateOnOrAfter) {
        return invoiceSummaryForPropertyDueDateStatusRepository.findInvoicesByStatusAndDueDateAfter(InvoiceStatus.INVOICED, dueDateOnOrAfter);
    }
    public LocalDate default0AllRecentlyInvoiced() {
        return clockService.now().minusDays(6);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<InvoiceForLease> findInvoices(
            final FixedAsset fixedAsset,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate dueDate,
            final InvoiceStatus status) {
        if (status == null) {
            return invoiceForLeaseRepository.findByFixedAssetAndDueDate(fixedAsset, dueDate);
        } else if (dueDate == null) {
            return invoiceForLeaseRepository.findByFixedAssetAndStatus(fixedAsset, status);
        } else {
            return invoiceForLeaseRepository.findByFixedAssetAndDueDateAndStatus(fixedAsset, dueDate, status);
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Invoice> findInvoicesByInvoiceNumber(
            final String invoiceNumber) {
        return invoiceRepository.findMatchingInvoiceNumber(invoiceNumber).stream()
                .filter(i -> i instanceof InvoiceForLease)
                .collect(Collectors.toList());
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "20")
    public List<InvoiceSummaryForPropertyDueDateStatus> allNewInvoices() {
        return invoiceSummaryForPropertyDueDateStatusRepository.findInvoicesByStatus(InvoiceStatus.NEW);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "21")
    public List<InvoiceSummaryForPropertyDueDateStatus> allApprovedInvoices() {
        return invoiceSummaryForPropertyDueDateStatusRepository.findInvoicesByStatus(InvoiceStatus.APPROVED);
    }





    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return invoiceRepository.allInvoices().stream()
                .filter(i -> i instanceof InvoiceForLease)
                .collect(Collectors.toList());
    }


    @Inject
    InvoiceSummaryForPropertyInvoiceDateRepository invoiceSummaryForPropertyInvoiceDateRepository;

    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryForPropertyDueDateStatusRepository;

    @Inject
    InvoiceSummaryForInvoiceRunRepository invoiceSummaryForInvoiceRunRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    ClockService clockService;

    @Inject
    EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepositoryForLease;

}
