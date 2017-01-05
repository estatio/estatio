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
package org.estatio.dom.leaseinvoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.appsettings.EstatioSettingsService;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.communications.AgreementCommunicationChannelLocator;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Party;

@DomainService(repositoryFor = InvoiceForLease.class, nature = NatureOfService.DOMAIN)
public class InvoiceForLeaseRepository extends UdoDomainRepositoryAndFactory<InvoiceForLease> {

    public InvoiceForLeaseRepository() {
        super(InvoiceForLeaseRepository.class, InvoiceForLease.class);
    }

    @Programmatic
    public List<InvoiceForLease> findMatchingInvoices(
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
    public InvoiceForLease findOrCreateMatchingInvoice(
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
    public InvoiceForLease findMatchingInvoice(
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        final List<InvoiceForLease> invoices = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoices == null || invoices.size() == 0) {
            return null;
        }
        return invoices.get(0);
    }

    @Programmatic
    public InvoiceForLease findOrCreateMatchingInvoice(
            final ApplicationTenancy applicationTenancy,
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {
        final List<InvoiceForLease> invoices = findMatchingInvoices(
                seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);
        if (invoices == null || invoices.size() == 0) {
            return newInvoice(applicationTenancy, seller, buyer, paymentMethod, settings.systemCurrency(), dueDate, lease, interactionId);
        }
        return invoices.get(0);
    }





    @Programmatic
    public List<InvoiceForLease> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }



    @Programmatic
    public InvoiceForLease newInvoice(
            final ApplicationTenancy applicationTenancy,
            final Party seller,
            final Party buyer,
            final PaymentMethod paymentMethod,
            final Currency currency,
            final LocalDate dueDate,
            final Lease lease,
            final String interactionId
    ) {
        InvoiceForLease invoice = newTransientInstance();
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
    public List<InvoiceForLease> findByFixedAssetAndStatus(
            final FixedAsset fixedAsset,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndStatus",
                "fixedAsset", fixedAsset,
                "status", status);
    }

    @Programmatic
    public List<InvoiceForLease> findByFixedAssetAndDueDate(
            final FixedAsset fixedAsset,
            final LocalDate dueDate) {
        return allMatches("findByFixedAssetAndDueDate",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate);
    }

    @Programmatic
    public List<InvoiceForLease> findByFixedAssetAndDueDateAndStatus(
            final FixedAsset fixedAsset,
            final LocalDate dueDate,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndDueDateAndStatus",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate,
                "status", status);
    }



    @Programmatic
    public List<InvoiceForLease> findByApplicationTenancyPathAndSellerAndDueDateAndStatus(
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
    @Programmatic
    public List<InvoiceForLease> findByApplicationTenancyPathAndSellerAndInvoiceDate(
            final String applicationTenancyPath,
            final Party seller,
            final LocalDate invoiceDate) {
        return allMatches("findByApplicationTenancyPathAndSellerAndInvoiceDate",
                "applicationTenancyPath", applicationTenancyPath,
                "seller", seller,
                "invoiceDate", invoiceDate);
    }




    @Programmatic
    public List<InvoiceForLease> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    @Programmatic
    public List<InvoiceForLease> findByRunIdAndApplicationTenancyPath(final String runId, final String applicationTenancyPath) {
        return allMatches("findByRunIdAndApplicationTenancyPath",
                "runId", runId,
                "applicationTenancyPath", applicationTenancyPath);
    }



    @Programmatic
    public void removeRuns(InvoiceCalculationParameters parameters) {
        List<InvoiceForLease> invoices = findByFixedAssetAndDueDateAndStatus(parameters.property(), parameters.invoiceDueDate(), InvoiceStatus.NEW);
        for (Invoice invoice : invoices) {
            factoryService.mixin(Invoice._remove.class, invoice).$$();
        }
    }



    @javax.inject.Inject
    FactoryService factoryService;

    @javax.inject.Inject
    AgreementCommunicationChannelLocator locator;


    @javax.inject.Inject
    EstatioSettingsService settings;

}
