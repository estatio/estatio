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
package org.estatio.dom.invoice;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;
import org.estatio.services.settings.EstatioSettingsService;

@DomainService(repositoryFor = Invoice.class)
@DomainServiceLayout(
        named = "Invoices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "50.4")
public class Invoices extends EstatioDomainService<Invoice> {

    public Invoices() {
        super(Invoices.class, Invoice.class);
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Invoices")
    public List<Invoice> findInvoices(final Lease lease) {
        return allMatches("findByLease",
                "lease", lease);
    }

    @NotInServiceMenu
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "Invoices")
    public List<Invoice> findInvoices(final Party party) {
        return allMatches("findByBuyer",
                "buyer", party);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Invoice> findInvoicesByInvoiceNumber(
            final @ParameterLayout(named = "Invoice number") String invoiceNumber) {
        return allMatches("findByInvoiceNumber",
                "invoiceNumber", StringUtils.wildcardToCaseInsensitiveRegex(invoiceNumber));
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Invoice> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    @Programmatic
    public List<Invoice> findInvoices(
            final InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status);
    }

    @Programmatic
    public List<Invoice> findInvoices(
            final FixedAsset fixedAsset,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndStatus",
                "fixedAsset", fixedAsset,
                "status", status);
    }

    @Programmatic
    public List<Invoice> findInvoices(
            final FixedAsset fixedAsset,
            final LocalDate dueDate) {
        return allMatches("findByFixedAssetAndDueDate",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> findInvoices(
            final FixedAsset fixedAsset,
            final @ParameterLayout(named = "Due Date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate dueDate,
            final @Parameter(optionality = Optionality.OPTIONAL) InvoiceStatus status) {
        if (status == null) {
            return findInvoices(fixedAsset, dueDate);
        } else if (dueDate == null) {
            return findInvoices(fixedAsset, status);
        } else {
            return allMatches("findByFixedAssetAndDueDateAndStatus",
                    "fixedAsset", fixedAsset,
                    "dueDate", dueDate,
                    "status", status);
        }
    }

    // //////////////////////////////////////

    @NotContributed
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoiceForLease(
            final @ParameterLayout(named = "Lease") Lease lease,
            final @ParameterLayout(named = "Due date") LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency
            ) {
        return newInvoice(
                lease.getPrimaryParty(),
                lease.getSecondaryParty(),
                paymentMethod,
                currency,
                dueDate,
                lease,
                null);
    }

    // //////////////////////////////////////

    @Programmatic
    public Invoice newInvoice(
            final @ParameterLayout(named = "Seller") Party seller,
            final @ParameterLayout(named = "Buyer") Party buyer,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final @ParameterLayout(named = "Due date") LocalDate dueDate,
            final Lease lease,
            final String interactionId
            ) {
        Invoice invoice = newTransientInstance();
        invoice.setBuyer(buyer);
        invoice.setSeller(seller);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setStatus(InvoiceStatus.NEW);
        invoice.setCurrency(currency);
        invoice.setLease(lease);
        invoice.setDueDate(dueDate);
        invoice.setUuid(java.util.UUID.randomUUID().toString());
        invoice.setRunId(interactionId);

        // copy down form the agreement, we require all invoice items to relate
        // back to this (root) fixed asset
        invoice.setPaidBy(lease.getPaidBy());
        invoice.setFixedAsset(lease.getProperty());

        persistIfNotAlready(invoice);
        getContainer().flush();
        return invoice;
    }

    @Programmatic
    public Invoice findOrCreateMatchingInvoice(
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        Party buyer = lease.getSecondaryParty();
        Party seller = lease.getPrimaryParty();
        return findOrCreateMatchingInvoice(seller, buyer, paymentMethod, lease, invoiceStatus, dueDate, interactionId);
    }

    @Programmatic
    public Invoice findMatchingInvoice(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        final List<Invoice> invoices = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoices == null || invoices.size() == 0) {
            return null;
        }
        return invoices.get(0);
    }

    @Programmatic
    public Invoice findOrCreateMatchingInvoice(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        final List<Invoice> invoices = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoices == null || invoices.size() == 0) {
            return newInvoice(seller, buyer, paymentMethod, settings.systemCurrency(), dueDate, lease, interactionId);
        }
        return invoices.get(0);
    }

    @Programmatic
    public List<Invoice> findMatchingInvoices(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        return allMatches(
                "findMatchingInvoices",
                "seller", seller,
                "buyer", buyer,
                "paymentMethod", paymentMethod,
                "lease", lease,
                "status", invoiceStatus,
                "dueDate", dueDate);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public void removeRuns(InvoiceCalculationParameters parameters) {
        List<Invoice> invoices = findInvoices(parameters.property(), parameters.invoiceDueDate(), InvoiceStatus.NEW);
        for (Invoice invoice : invoices) {
            invoice.remove();
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private EstatioSettingsService settings;

}
