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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.AgreementRoleCommunicationChannelTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.party.dom.Party;

@DomainService(repositoryFor = InvoiceForLease.class, nature = NatureOfService.DOMAIN)
public class InvoiceForLeaseRepository extends UdoDomainRepositoryAndFactory<InvoiceForLease> {

    public InvoiceForLeaseRepository() {
        super(InvoiceForLeaseRepository.class, InvoiceForLease.class);
    }

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

    public InvoiceForLease findOrCreateMatchingInvoice(
            final ApplicationTenancy applicationTenancy,
            final PaymentMethod paymentMethod,
            final Lease lease,
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate,
            final String interactionId) {

        Party buyer = lease.secondaryPartyAsOfElseCurrent(dueDate);
        Party seller = lease.primaryPartyAsOfElseCurrent(dueDate);
        return findOrCreateMatchingInvoice(
                applicationTenancy, seller, buyer, paymentMethod, lease, invoiceStatus, dueDate, interactionId);
    }

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
            return newInvoice(applicationTenancy, seller, buyer, paymentMethod, settingsService.systemCurrency(), dueDate, lease, interactionId);
        }
        return invoices.get(0);
    }

    public List<InvoiceForLease> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

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
        invoice.setRunId(interactionId);

        // copy down form the agreement, we require all invoice items to relate
        // back to this (root) fixed asset
        invoice.setPaidBy(lease.getPaidBy());
        invoice.setFixedAsset(lease.getProperty());

        // copy over the current invoice address (if any)
        final CommunicationChannel sendTo = firstCurrentTenantInvoiceAddress(lease);
        invoice.setSendTo(sendTo);

        invoice.updateDescriptions();

        persistIfNotAlready(invoice);
        getContainer().flush();
        return invoice;
    }


    CommunicationChannel firstCurrentTenantInvoiceAddress(final Agreement agreement) {
        final List<CommunicationChannel> channels = currentTenantInvoiceAddresses(agreement);
        return channels.size() > 0 ? channels.get(0): null;
    }

    List<CommunicationChannel> currentTenantInvoiceAddresses(final Agreement agreement) {
        return locator.current(agreement, LeaseAgreementRoleTypeEnum.TENANT.getTitle(), AgreementRoleCommunicationChannelTypeEnum.INVOICE_ADDRESS.getTitle());

    }



    public List<InvoiceForLease> findByFixedAssetAndStatus(
            final FixedAsset fixedAsset,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndStatus",
                "fixedAsset", fixedAsset,
                "status", status);
    }

    public List<InvoiceForLease> findByFixedAssetAndDueDate(
            final FixedAsset fixedAsset,
            final LocalDate dueDate) {
        return allMatches("findByFixedAssetAndDueDate",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate);
    }

    public List<InvoiceForLease> findByFixedAssetAndDueDateAndStatus(
            final FixedAsset fixedAsset,
            final LocalDate dueDate,
            final InvoiceStatus status) {
        return allMatches("findByFixedAssetAndDueDateAndStatus",
                "fixedAsset", fixedAsset,
                "dueDate", dueDate,
                "status", status);
    }



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
    public List<InvoiceForLease> findByApplicationTenancyPathAndSellerAndInvoiceDate(
            final String applicationTenancyPath,
            final Party seller,
            final LocalDate invoiceDate) {
        return allMatches("findByApplicationTenancyPathAndSellerAndInvoiceDate",
                "applicationTenancyPath", applicationTenancyPath,
                "seller", seller,
                "invoiceDate", invoiceDate);
    }




    public List<InvoiceForLease> findInvoicesByRunId(final String runId) {
        return allMatches("findByRunId",
                "runId", runId);
    }

    public List<InvoiceForLease> findByRunIdAndApplicationTenancyPath(final String runId, final String applicationTenancyPath) {
        return allMatches("findByRunIdAndApplicationTenancyPath",
                "runId", runId,
                "applicationTenancyPath", applicationTenancyPath);
    }



    public void removeRuns(InvoiceCalculationParameters parameters) {
        List<InvoiceForLease> invoices = findByFixedAssetAndDueDateAndStatus(parameters.property(), parameters.invoiceDueDate(), InvoiceStatus.NEW);
        for (Invoice invoice : invoices) {
            factoryService.mixin(Invoice._remove.class, invoice).exec();
        }
    }

    public List<InvoiceForLease> findInvoicesByInvoiceNumber(
            final String invoiceNumber,
            final Integer yearIfAny) {
        return invoiceRepository.findMatchingInvoiceNumber(invoiceNumber).stream()
                .filter(InvoiceForLease.class::isInstance)
                .map(InvoiceForLease.class::cast)
                .filter(i -> {
                    final LocalDate codaValDate = i.getCodaValDate();
                    return yearIfAny == null || codaValDate == null || codaValDate.getYear() == yearIfAny;
                })
                .collect(Collectors.toList());
    }

    public Optional<InvoiceForLease> findInvoiceByInvoiceNumber(
            final String invoiceNumber,
            final Integer year) {
        return invoiceRepository.findMatchingInvoiceNumber(invoiceNumber).stream()
                .filter(InvoiceForLease.class::isInstance)
                .map(InvoiceForLease.class::cast)
                .filter(i -> {
                    final LocalDate codaValDate = i.getCodaValDate();
                    return codaValDate != null && codaValDate.getYear() == year;
                })
                .findFirst();
    }

    @Inject
    FactoryService factoryService;

    @Inject
    AgreementCommunicationChannelLocator locator;


    @Inject
    LeaseInvoicingSettingsService settingsService;

    @Inject
    InvoiceRepository invoiceRepository;

}
