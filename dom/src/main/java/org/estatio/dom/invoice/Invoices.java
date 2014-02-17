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

import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Party;
import org.estatio.services.settings.EstatioSettingsService;

public class Invoices extends EstatioDomainService<Invoice> {

    public Invoices() {
        super(Invoices.class, Invoice.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Programmatic
    public List<Invoice> findInvoices(final Property property, final InvoiceStatus status) {
        return allMatches("findByPropertyAndStatus",
                "property", property,
                "status", status);
    }

    @ActionSemantics(Of.SAFE)
    @Programmatic
    public List<Invoice> findInvoices(
            final Property property, final LocalDate dueDate) {
        return allMatches("findByPropertyAndDueDate",
                "property", property, "dueDate", dueDate);
    }

    @ActionSemantics(Of.SAFE)
    @Programmatic
    public List<Invoice> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Invoice> findInvoices(
            final Property property,
            final @Named("Due Date") @Optional LocalDate dueDate,
            final @Optional InvoiceStatus status) {
        if (status == null) {
            return allMatches("findByPropertyAndDueDate",
                    "property", property, "dueDate", dueDate);
        } else if (dueDate == null) {
            return allMatches("findByPropertyAndStatus",
                    "property", property, "status", status);
        } else {
            return allMatches("findByPropertyAndDueDateAndStatus",
                    "property", property, "dueDate", dueDate, "status", status);
        }
    }

    @ActionSemantics(Of.SAFE)
    @DescribedAs("New invoices, to be approved")
    @MemberOrder(sequence = "10")
    public List<Invoice> findInvoicesToBeApproved() {
        return allMatches("findByStatus", "status", InvoiceStatus.NEW);
    }

    @ActionSemantics(Of.SAFE)
    @DescribedAs("Approved invoices, to be collected")
    @MemberOrder(sequence = "11")
    public List<Invoice> findInvoicesToBeCollected() {
        return allMatches("findByStatus", "status", InvoiceStatus.APPROVED);
    }

    @ActionSemantics(Of.SAFE)
    @DescribedAs("Collected invoices, to be invoiced")
    @MemberOrder(sequence = "12")
    public List<Invoice> findInvoicesToBeInvoiced() {
        return allMatches("findByStatus", "status", InvoiceStatus.COLLECTED);
    }

    @ActionSemantics(Of.SAFE)
    @DescribedAs("Already invoiced")
    @MemberOrder(sequence = "13")
    public List<Invoice> findInvoicesPreviouslyInvoiced() {
        return allMatches("findByStatus", "status", InvoiceStatus.INVOICED);
    }

    // //////////////////////////////////////

    @NotContributed
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoiceForLease(
            final @Named("Lease") Lease lease,
            final @Named("Due date") LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency
            ) {
        return newInvoice(lease.getPrimaryParty(), lease.getSecondaryParty(), paymentMethod, currency, dueDate, lease, null);
    }

    // //////////////////////////////////////

    @Programmatic
    public Invoice newInvoice(
            final @Named("Seller") Party seller,
            final @Named("Buyer") Party buyer,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Lease") Lease lease,
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

    @Hidden
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

    @Named("Invoices for lease")
    public List<Invoice> findInvoices(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name = "Administration", sequence = "numerators.invoices.1")
    public Numerator findCollectionNumberNumerator() {
        return numerators.findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name = "Administration", sequence = "numerators.invoices.2")
    @NotContributed
    public Numerator createCollectionNumberNumerator(
            final @Named("Format") String format,
            final @Named("Last value") BigInteger lastIncrement) {

        return numerators.createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement);
    }

    public String default0CreateCollectionNumberNumerator() {
        return "%09d";
    }

    public BigInteger default1CreateCollectionNumberNumerator() {
        return BigInteger.ZERO;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name = "Administration", sequence = "numerators.invoices.3")
    @NotContributed
    public Numerator findInvoiceNumberNumerator(
            final Property property) {
        return numerators.findScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, property);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name = "Administration", sequence = "numerators.invoices.4")
    @NotContributed
    public Numerator createInvoiceNumberNumerator(
            final Property property,
            final @Named("Format") String format,
            final @Named("Last value") BigInteger lastIncrement) {
        return numerators.createScopedNumerator(
                Constants.INVOICE_NUMBER_NUMERATOR_NAME, property, format, lastIncrement);
    }

    public String default1CreateInvoiceNumberNumerator() {
        return "XXX-%06d";
    }

    public BigInteger default2CreateInvoiceNumberNumerator() {
        return BigInteger.ZERO;
    }

    // //////////////////////////////////////

    private Numerators numerators;

    public void injectNumerators(final Numerators numerators) {
        this.numerators = numerators;
    }

    private EstatioSettingsService settings;

    public void injectSettings(final EstatioSettingsService settings) {
        this.settings = settings;
    }

}
