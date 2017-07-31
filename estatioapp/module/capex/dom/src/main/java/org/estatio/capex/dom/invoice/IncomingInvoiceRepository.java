package org.estatio.capex.dom.invoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoice.class
)
public class IncomingInvoiceRepository {

    @Programmatic
    public java.util.List<IncomingInvoice> listAll() {
        return repositoryService.allInstances(IncomingInvoice.class);
    }


    @Programmatic
    public List<IncomingInvoice> findByInvoiceDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByInvoiceDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
    }


    @Programmatic
    public List<IncomingInvoice> findByDueDateBetween(final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByDueDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
    }


    @Programmatic
    public List<IncomingInvoice> findByPropertyAndDateReceivedBetween(final Property property, final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByPropertyAndDateReceivedBetween",
                        "property", property,
                        "fromDate", fromDate,
                        "toDate", toDate));
    }


    @Programmatic
    public List<IncomingInvoice> findByApprovalState(final IncomingInvoiceApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByApprovalState",
                        "approvalState", approvalState));
    }

    @Programmatic
    public List<IncomingInvoice> findByApprovalStateAndPaymentMethod(
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByApprovalStateAndPaymentMethod",
                        "approvalState", approvalState,
                        "paymentMethod", paymentMethod));
    }

    @Programmatic
    public IncomingInvoice findByInvoiceNumberAndSellerAndInvoiceDate(final String invoiceNumber, final Party seller, final LocalDate invoiceDate){
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByInvoiceNumberAndSellerAndInvoiceDate",
                        "invoiceNumber", invoiceNumber,
                        "seller", seller,
                        "invoiceDate", invoiceDate));
    }

    @Programmatic
    public List<IncomingInvoice> findByInvoiceNumberAndSeller(final String invoiceNumber, final Party seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByInvoiceNumberAndSeller",
                        "invoiceNumber", invoiceNumber,
                        "seller", seller));
    }


    @Programmatic
    public List<IncomingInvoice> findByBankAccount(final BankAccount bankAccount) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByBankAccount",
                        "bankAccount", bankAccount));
    }

    @Programmatic
    public List<IncomingInvoice> findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod(
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod",
                        "approvalState", approvalState,
                        "paymentMethod", paymentMethod));
    }

    @Programmatic
    public IncomingInvoice create(
            final IncomingInvoiceType type,
            final String invoiceNumber,
            final Property property,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final IncomingInvoiceApprovalState approvalStateIfAny) {
        final Currency currency = currencyRepository.findCurrency("EUR");
        final IncomingInvoice invoice =
                new IncomingInvoice(type, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                        paymentMethod, invoiceStatus, dateReceived, bankAccount, approvalStateIfAny);
        invoice.setCurrency(currency);
        serviceRegistry2.injectServicesInto(invoice);
        repositoryService.persistAndFlush(invoice);
        return invoice;
    }


    // Note: this method uses a first match on invoicenumber, seller and invoicedate which in practice can be assumed to be unique, but technically is not
    @Programmatic
    public IncomingInvoice upsert(
            final IncomingInvoiceType type,
            final String invoiceNumber,
            final Property property,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final IncomingInvoiceApprovalState approvalStateIfAny) {
        IncomingInvoice invoice = findByInvoiceNumberAndSellerAndInvoiceDate(invoiceNumber, seller, invoiceDate);
        if (invoice == null) {
            invoice = create(type, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, dateReceived, bankAccount,
                    approvalStateIfAny);
        } else {
            updateInvoice(invoice, property, atPath, buyer, dueDate, paymentMethod, invoiceStatus, dateReceived, bankAccount);
        }
        return invoice;
    }

    private void updateInvoice(
            final IncomingInvoice invoice,
            final Property property,
            final String atPath,
            final Party buyer,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount){
        invoice.setProperty(property);
        invoice.setApplicationTenancyPath(atPath);
        invoice.setBuyer(buyer);
        invoice.setDueDate(dueDate);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setStatus(invoiceStatus);
        invoice.setDateReceived(dateReceived);
        invoice.setBankAccount(bankAccount);
    }

    @Programmatic
    public void delete(final IncomingInvoice incomingInvoice) {
        repositoryService.removeAndFlush(incomingInvoice);
    }

    @Programmatic
    public List<IncomingInvoice> findIncomingInvoiceByDocumentName(final String name){
        List <IncomingInvoice> result = new ArrayList<>();
        for (Document doc : incomingDocumentRepository.matchAllIncomingDocumentsByName(name)){
            for (Paperclip paperclip : paperclipRepository.findByDocument(doc)){
                if (paperclip.getAttachedTo().getClass().isAssignableFrom(IncomingInvoice.class)){
                    final IncomingInvoice attachedTo = (IncomingInvoice) paperclip.getAttachedTo();
                    // check presence because there may be multiple scans attached to the same invoice
                    if (!result.contains(attachedTo)) {
                        result.add(attachedTo);
                    }
                }
            }
        }
        return result;
    }

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;
    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    CurrencyRepository currencyRepository;

}
