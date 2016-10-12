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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.appsettings.EstatioSettingsService;
import org.estatio.dom.asset.FixedAsset;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.estatio.dom.communications.AgreementCommunicationChannelLocator;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Invoice.class)
public class InvoiceRepository extends UdoDomainRepositoryAndFactory<Invoice> {

    public InvoiceRepository() {
        super(InvoiceRepository.class, Invoice.class);
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

    @Programmatic
    public List<Invoice> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    @Programmatic
    public List<Invoice> findByRunIdAndApplicationTenancyPath(final String runId, final String applicationTenancyPath) {
        return allMatches("findByRunIdAndApplicationTenancyPath",
                "runId", runId,
                "applicationTenancyPath", applicationTenancyPath);
    }

    @Programmatic
    public List<Invoice> findByStatus(
            final InvoiceStatus status) {
        return allMatches("findByStatus",
                "status", status);
    }

    @Programmatic
    public List<Invoice> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    @Programmatic
    public List<Invoice> findByBuyer(final Party party) {
        return allMatches("findByBuyer",
                "buyer", party);
    }

    @Programmatic
    public List<Invoice> findBySeller(final Party party) {
        return allMatches("findBySeller",
                "seller", party);
    }

    @Programmatic
    public List<Invoice> findByInvoiceNumber(final String invoiceNumber) {
        return allMatches("findByInvoiceNumber",
                "invoiceNumber", invoiceNumber);
    }

    @Programmatic
    public List<Invoice> findByFixedAssetAndStatus(
            final FixedAsset fixedAsset,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndStatus",
                "fixedAsset", fixedAsset,
                "status", status);
    }

    @Programmatic
    public List<Invoice> findByFixedAssetAndDueDate(
            final FixedAsset fixedAsset,
            final LocalDate dueDate) {
        return allMatches("findByFixedAssetAndDueDate",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate);
    }

    @Programmatic
    public List<Invoice> findByFixedAssetAndDueDateAndStatus(
            final FixedAsset fixedAsset,
            final LocalDate dueDate,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndDueDateAndStatus",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate,
                "status", status);
    }

    @Programmatic
    public List<Invoice> findByApplicationTenancyPathAndSellerAndDueDateAndStatus(
            final String applicationTenancyPath,
            final Party seller,
            final LocalDate dueDate,
            final InvoiceStatus status) {
        return allMatches("findByApplicationTenancyPathAndSellerAndDueDateAndStatus",
                "applicationTenancyPath", applicationTenancyPath,
                "seller", seller,
                "dueDate", dueDate,
                "status", status);
    }

    // //////////////////////////////////////

    @Programmatic
    public Invoice newInvoice(
            final ApplicationTenancy applicationTenancy,
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final LocalDate dueDate,
            final Lease lease,
            final String interactionId
    ) {
        Invoice invoice = newTransientInstance();
        invoice.setApplicationTenancyPath(applicationTenancy.getPath());
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

        // copy over the current invoice address (if any)
        final CommunicationChannel sendTo = firstCurrentTenantInvoiceAddress(lease);
        invoice.setSendTo(sendTo);

        persistIfNotAlready(invoice);
        getContainer().flush();
        return invoice;
    }

    CommunicationChannel firstCurrentTenantInvoiceAddress(final Agreement agreement) {
        final List<CommunicationChannel> channels = currentTenantInvoiceAddresses(agreement);
        return channels.size() > 0 ? channels.get(0): null;
    }

    List<CommunicationChannel> currentTenantInvoiceAddresses(final Agreement agreement) {
        return locator.current(agreement, LeaseConstants.ART_TENANT, LeaseConstants.ARCCT_INVOICE_ADDRESS);

    }



    @Programmatic
    public Invoice findOrCreateMatchingInvoice(
            final ApplicationTenancy applicationTenancy,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        Party buyer = lease.getSecondaryParty();
        Party seller = lease.getPrimaryParty();
        return findOrCreateMatchingInvoice(
                applicationTenancy, seller, buyer, paymentMethod, lease, invoiceStatus, dueDate, interactionId);
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
            final ApplicationTenancy applicationTenancy,
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
            return newInvoice(applicationTenancy, seller, buyer, paymentMethod, settings.systemCurrency(), dueDate, lease, interactionId);
        }
        return invoices.get(0);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    @Programmatic
    public void removeRuns(InvoiceCalculationParameters parameters) {
        List<Invoice> invoices = findByFixedAssetAndDueDateAndStatus(parameters.property(), parameters.invoiceDueDate(), InvoiceStatus.NEW);
        for (Invoice invoice : invoices) {
            invoice.remove();
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private EstatioSettingsService settings;

    @javax.inject.Inject
    AgreementCommunicationChannelLocator locator;

}
