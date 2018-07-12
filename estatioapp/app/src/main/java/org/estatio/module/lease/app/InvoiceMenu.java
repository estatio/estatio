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
package org.estatio.module.lease.app;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

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
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.coda.contributions.republish.InvoiceForLease_republish;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRun;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRunRepository;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatusRepository;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyInvoiceDateRepository;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.invoice.InvoiceMenu"
)
@DomainServiceLayout(
        named = "Invoices Out",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.1")
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
    public List<InvoiceForLease> findInvoicesByInvoiceNumber(
            final String invoiceNumber,
            @Nullable
            @ParameterLayout(describedAs = "Invoice numbers are reset each year")
            final Integer year) {
        return invoiceRepository.findMatchingInvoiceNumber(invoiceNumber).stream()
                .filter(InvoiceForLease.class::isInstance)
                .map(InvoiceForLease.class::cast)
                .filter(i -> {
                    final LocalDate codaValDate = i.getCodaValDate();
                    return year == null || codaValDate == null || codaValDate.getYear() == year;
                })
                .collect(Collectors.toList());
    }
    public Integer default1FindInvoicesByInvoiceNumber() {
        return clockService.now().getYear();
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

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<InvoiceForLease> republish(
            @ParameterLayout(describedAs = "invoice number prefix, eg 'CAR' (as in 'CAR-0144')")
            final String invoiceNumberPrefix,
            @ParameterLayout(describedAs = "invoice number suffices, eg '144,145,150' (for 'CAR-0144', 'CAR-0145', 'CAR-0150')")
            @Nullable
            final String invoiceNumberSuffices,
            @ParameterLayout(describedAs = "invoice number suffices from (inclusive), eg '144' (for 'CAR-0144', ...)")
            @Nullable
            final Integer invoiceNumberFrom,
            @ParameterLayout(describedAs = "invoice number suffices to (inclusive), eg '150' (for ..., 'CAR-0150')")
            @Nullable
            final Integer invoiceNumberTo,
            final int year
    ) {
        final List<Integer> invoiceNumberSuffixList;
        if(invoiceNumberSuffices != null) {
            invoiceNumberSuffixList = Lists.newArrayList(Splitter.on(',').split(invoiceNumberSuffices))
                                        .stream().map(Integer::parseInt).collect(Collectors.toList());
        } else {
            invoiceNumberSuffixList = ContiguousSet.create(
                            Range.closed(invoiceNumberFrom, invoiceNumberTo), DiscreteDomain.integers()).asList();
        }

        final List<InvoiceForLease> invoiceList = invoiceNumberSuffixList.stream()
                .map(num -> String.format("%s-%04d", invoiceNumberPrefix, num))
                .map(invoiceNumber -> {
                    final List<InvoiceForLease> invoices = findInvoicesByInvoiceNumber(invoiceNumber, year);
                    return invoices.size() == 1 ? invoices.get(0) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        invoiceList.forEach(invoice -> factoryService.mixin(InvoiceForLease_republish.class, invoice).act());

        return invoiceList;
    }


    public String default0Republish() {
        return "CAR";
    }
    public int default4Republish() {
        return clockService.now().getYear();
    }

    public String validateRepublish(
            final String invoiceNumberPrefix,
            final String invoiceNumberSuffices,
            final Integer invoiceNumberFrom,
            final Integer invoiceNumberTo,
            final int year) {
        if(invoiceNumberSuffices == null) {
            if (invoiceNumberFrom == null || invoiceNumberTo == null) {
                return "Specify invoice number suffices, or from/to range";
            } else if (invoiceNumberFrom != null && invoiceNumberTo == null) {
                return "Also specify invoice number to";

            } else if (invoiceNumberFrom == null && invoiceNumberTo != null) {
                return "Also specify invoice number from";

            } else /*(invoiceNumberFrom != null && invoiceNumberTo != null)*/ {
                return null;
            }
        } else {
            if (invoiceNumberFrom != null || invoiceNumberTo != null) {
                return "Specify invoice number suffices, or from/to range";
            } else {
                return null;
            }
        }
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

    @Inject
    FactoryService factoryService;

}
