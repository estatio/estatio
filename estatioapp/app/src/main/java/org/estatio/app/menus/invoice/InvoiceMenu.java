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

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForInvoiceRun;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyInvoiceDate;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyInvoiceDateRepository;
import org.estatio.dom.lease.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Invoices",
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
            final PaymentMethod paymentMethod,
            final Currency currency) {

        final Property propertyIfAny = lease.getProperty();
        final Party seller = lease.getPrimaryParty();
        final Party buyer = lease.getSecondaryParty();

        final ApplicationTenancy propertySellerTenancy =
                estatioApplicationTenancyRepositoryForLease.findOrCreateTenancyFor(propertyIfAny, seller);

        return invoiceRepository.newInvoice(propertySellerTenancy,
                seller,
                buyer,
                paymentMethod,
                currency,
                dueDate,
                lease, null);
    }

    public String validate0NewInvoiceForLease(final Lease lease) {
        final Property propertyIfAny = lease.getProperty();
        if(propertyIfAny == null) {
            return "Can only create invoices for leases that have an occupancy";
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
    @MemberOrder(sequence = "1")
    public List<InvoiceSummaryForPropertyInvoiceDate> allRecentlyInvoiced() {
        return invoiceSummaryForPropertyInvoiceDateRepository.byInvoiceDate(clockService.now().minusDays(6));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> findInvoices(
            final FixedAsset fixedAsset,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate dueDate,
            final InvoiceStatus status) {
        if (status == null) {
            return invoiceRepository.findByFixedAssetAndDueDate(fixedAsset, dueDate);
        } else if (dueDate == null) {
            return invoiceRepository.findByFixedAssetAndStatus(fixedAsset, status);
        } else {
            return invoiceRepository.findByFixedAssetAndDueDateAndStatus(fixedAsset, dueDate, status);
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Invoice> findInvoicesByInvoiceNumber(
            final String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
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
        return invoiceRepository.allInvoices();
    }


    @Inject
    private InvoiceSummaryForPropertyInvoiceDateRepository invoiceSummaryForPropertyInvoiceDateRepository;

    @Inject
    private InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryForPropertyDueDateStatusRepository;

    @Inject
    private InvoiceSummaryForInvoiceRunRepository invoiceSummaryForInvoiceRunRepository;

    @Inject
    private InvoiceRepository invoiceRepository;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private MeService meService;

    @Inject
    private ClockService clockService;

    @Inject
    EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepositoryForLease;

}
