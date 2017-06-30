package org.estatio.capex.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.dom.asset.Property;
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
    public List<IncomingInvoice> findByDateReceivedBetween(final LocalDate fromDate, final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByDateReceivedBetween",
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
    public List<IncomingInvoice> findByBankAccount(final BankAccount bankAccount) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByBankAccount",
                        "bankAccount", bankAccount));
    }

    @Programmatic
    public List<IncomingInvoice> findNotInAnyPaymentBatchByApprovalState(final IncomingInvoiceApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findNotInAnyPaymentBatchByApprovalState",
                        "approvalState", approvalState));
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
        final IncomingInvoice invoice =
                new IncomingInvoice(type, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, dateReceived, bankAccount, approvalStateIfAny);
        invoice.setCurrency(currencyRepository.findCurrency("EUR"));
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

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    CurrencyRepository currencyRepository;

}
