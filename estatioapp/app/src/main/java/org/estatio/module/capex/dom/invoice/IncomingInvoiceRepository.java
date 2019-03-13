package org.estatio.module.capex.dom.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.datanucleus.query.typesafe.TypesafeQuery;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.util.CountryUtil;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoice.class
)
public class IncomingInvoiceRepository {

    public static final List<String> AT_PATHS_FRA_OFFICE = ImmutableList.of("/FRA","/BEL");
    public static final List<String> AT_PATHS_ITA_OFFICE = ImmutableList.of("/ITA");

    @Programmatic
    public java.util.List<IncomingInvoice> listAll() {
        return repositoryService.allInstances(IncomingInvoice.class);
    }

    @Programmatic
    public List<IncomingInvoice> findByInvoiceDateBetween(
                                    final LocalDate fromDate,
                                    final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByInvoiceDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
    }

    @Programmatic
    public List<IncomingInvoice> findByDueDateBetween(
                                    final LocalDate fromDate,
                                    final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByDueDateBetween",
                        "fromDate", fromDate,
                        "toDate", toDate));
    }

    @Programmatic
    public List<IncomingInvoice> findByPropertyAndDateReceivedBetween(
                                    final Property property,
                                    final LocalDate fromDate,
                                    final LocalDate toDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByPropertyAndDateReceivedBetween",
                        "property", property,
                        "fromDate", fromDate,
                        "toDate", toDate));
    }

    ////////////////////////////////////////////////////////////////////

    List<IncomingInvoice> findByApprovalState(final IncomingInvoiceApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByApprovalState",
                        "approvalState", approvalState));
    }

    @Programmatic
    public List<IncomingInvoice> findByAtPathPrefixesAndApprovalState(
            final List<String> atPathPrefixes,
            final IncomingInvoiceApprovalState approvalState) {
        final List<IncomingInvoice> incomingInvoices = Lists.newArrayList();
        for (final String atPathPrefix : atPathPrefixes) {
            incomingInvoices.addAll(repositoryService.allMatches(
                    new QueryDefault<>(
                            IncomingInvoice.class,
                            "findByAtPathPrefixAndApprovalState",
                            "atPathPrefix", atPathPrefix,
                            "approvalState", approvalState)));
        }
        return incomingInvoices;
    }

    @Programmatic
    List<IncomingInvoice> findByApprovalStateAndPaymentMethod(
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
    public List<IncomingInvoice> findByAtPathPrefixesAndApprovalStateAndPaymentMethods(
            final List<String> atPathPrefixes,
            final IncomingInvoiceApprovalState approvalState,
            final List<PaymentMethod> paymentMethods) {
        final List<IncomingInvoice> incomingInvoices = Lists.newArrayList();
        paymentMethods.forEach(paymentMethod ->
                appendByAtPathPrefixesAndApprovalStateAndPaymentMethod(atPathPrefixes, approvalState, paymentMethod, incomingInvoices));
        return incomingInvoices;
    }

    @Programmatic
    public List<IncomingInvoice> findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
            final List<String> atPathPrefixes,
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod) {
        final List<IncomingInvoice> incomingInvoices = Lists.newArrayList();
        appendByAtPathPrefixesAndApprovalStateAndPaymentMethod(atPathPrefixes, approvalState, paymentMethod, incomingInvoices);
        return incomingInvoices;
    }

    private void appendByAtPathPrefixesAndApprovalStateAndPaymentMethod(
            final List<String> atPathPrefixes,
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod,
            final List<IncomingInvoice> incomingInvoices) {
        for (final String atPathPrefix : atPathPrefixes) {
            incomingInvoices.addAll(repositoryService.allMatches(
                    new QueryDefault<>(
                            IncomingInvoice.class,
                            "findByAtPathPrefixAndApprovalStateAndPaymentMethod",
                            "atPathPrefix", atPathPrefix,
                            "approvalState", approvalState,
                            "paymentMethod", paymentMethod)));
        }
    }

    @Programmatic
    public IncomingInvoice findByInvoiceNumberAndSellerAndInvoiceDate(
            final String invoiceNumber, final Party seller, final LocalDate invoiceDate) {
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

    List<IncomingInvoice> findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod(
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
    public List<IncomingInvoice> findNotInAnyPaymentBatchByAtPathPrefixesAndApprovalStateAndPaymentMethod(
            final List<String> atPathPrefixes,
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod) {
        final List<IncomingInvoice> incomingInvoices = Lists.newArrayList();
        for (final String atPathPrefix : atPathPrefixes) {
            incomingInvoices.addAll(repositoryService.allMatches(
                    new QueryDefault<>(
                            IncomingInvoice.class,
                            "findNotInAnyPaymentBatchByAtPathPrefixAndApprovalStateAndPaymentMethod",
                            "atPathPrefix", atPathPrefix,
                            "approvalState", approvalState,
                            "paymentMethod", paymentMethod)));
        }
        return incomingInvoices;
    }

    @Programmatic
    public List<IncomingInvoice> findInvoicesPayableByBankTransferWithDifferentHistoricalPaymentMethods(
            final LocalDate fromDueDate,
            final LocalDate toDueDate,
            final String atPath) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findPayableByBankTransferAndDueDateBetween",
                        "fromDueDate", fromDueDate,
                        "toDueDate", toDueDate))
                .stream()
                .filter(incomingInvoice -> incomingInvoice.getAtPath().startsWith(atPath))
                .filter(incomingInvoice -> invoiceRepository.findBySeller(incomingInvoice.getSeller())
                        .stream()
                        .anyMatch(invoice -> invoice.getPaymentMethod() != PaymentMethod.BANK_TRANSFER && invoice.getPaymentMethod() != PaymentMethod.REFUND_BY_SUPPLIER && invoice.getPaymentMethod() != PaymentMethod.MANUAL_PROCESS))
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<IncomingInvoice> findCompletedOrLaterWithItemsByReportedDate(final LocalDate reportedDate) {

        // equivalent to:
        /*
        @Query(
                name = "findCompletedOrLaterWithItemsByReportedDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE items.contains(ii) "
                        + "   && (ii.reportedDate == :reportedDate) "
                        + "   && (approvalState != 'NEW' && approvalState != 'DISCARDED') "
                        + "VARIABLES org.estatio.module.capex.dom.invoice.IncomingInvoiceItem ii "
        ),

        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findCompletedOrLaterWithItemsByReportedDate",
                        "reportedDate", reportedDate));
         */

        final QIncomingInvoice ii = QIncomingInvoice.candidate();
        final QIncomingInvoiceItem iii = QIncomingInvoiceItem.variable("iii");

        final TypesafeQuery<IncomingInvoice> q = isisJdoSupport.newTypesafeQuery(IncomingInvoice.class);

        q.getFetchPlan().addGroup("seller_buyer_property_bankAccount");

        q.filter(
                ii.items.contains(iii)
                        .and(iii.reportedDate.eq(reportedDate))
                        .and(ii.approvalState.ne(IncomingInvoiceApprovalState.NEW))
                //            .and(ii.approvalState.ne(IncomingInvoiceApprovalState.DISCARDED))  EST-1731: brings this filtering up to consuming method IncomingInvoiceItemRepository#filterByCompletedOrLaterInvoices
        );
        final List<IncomingInvoice> incomingInvoices = Lists.newArrayList(q.executeList());
        q.closeAll();
        return incomingInvoices;

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
            final LocalDate vatRegistrationDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final IncomingInvoiceApprovalState approvalState,
            final boolean postedToCodaBooks,
            final LocalDate paidDate) {

        final Currency currency = currencyRepository.findCurrency("EUR");
        final IncomingInvoice invoice =
                new IncomingInvoice(type, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                        paymentMethod, invoiceStatus, dateReceived, bankAccount, approvalState);
        invoice.setPaidDate(paidDate);
        invoice.setCurrency(currency);
        invoice.setPostedToCodaBooks(postedToCodaBooks);
        invoice.setVatRegistrationDate(vatRegistrationDate);

        serviceRegistry2.injectServicesInto(invoice);
        repositoryService.persistAndFlush(invoice);


        // moved from ObjectPersistedEvent subscriber, because any changes made there on the invoice are not persisted.
        final IncomingInvoiceApprovalState approvalStateAfterPersisting = invoice.getApprovalState();
        if(approvalStateAfterPersisting == IncomingInvoiceApprovalStateTransitionType.INSTANTIATE.getToState()) {

            final boolean isPaid = invoice.getPaidDate() != null;
            final boolean noApprovalNeededForPaymentMethod =
                    invoice.getPaymentMethod() != null && invoice.getPaymentMethod().requiresNoApprovalInItaly();

            final IncomingInvoiceApprovalStateTransitionType transitionType;
            // italian invoices that do not require approval
            if (CountryUtil.isItalian(invoice) && noApprovalNeededForPaymentMethod) {
                transitionType = IncomingInvoiceApprovalStateTransitionType.INSTANTIATE_TO_PAYABLE;
            } else {
                // italian invoices that are paid already
                if (CountryUtil.isItalian(invoice) && isPaid) {
                    transitionType = IncomingInvoiceApprovalStateTransitionType.INSTANTIATE_BYPASSING_APPROVAL;
                } else {
                    // normal case
                    transitionType = IncomingInvoiceApprovalStateTransitionType.INSTANTIATE;
                }
            }
            stateTransitionService.trigger(invoice, transitionType, null, null);
        }

        return invoice;
    }

    // Note: this method uses a first match on invoicenumber, seller and invoicedate
    // which in practice can be assumed to be unique, though technically is not
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
            final LocalDate vatRegistrationDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final IncomingInvoiceApprovalState approvalState,
            final boolean postedToCodaBooks,
            final LocalDate paidDate) {
        IncomingInvoice invoice = findByInvoiceNumberAndSellerAndInvoiceDate(invoiceNumber, seller, invoiceDate);
        if (invoice == null) {
            invoice = create(type, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                    vatRegistrationDate,
                    paymentMethod, invoiceStatus, dateReceived, bankAccount,
                    approvalState,
                    postedToCodaBooks,
                    paidDate
            );
        } else {
            updateInvoice(invoice,
                    invoice.getType(),
                    invoice.getInvoiceNumber(),
                    property,
                    atPath,
                    buyer,
                    invoice.getSeller(),
                    invoice.getInvoiceDate(),
                    dueDate,
                    vatRegistrationDate,
                    paymentMethod,
                    invoiceStatus,
                    dateReceived,
                    bankAccount,
                    postedToCodaBooks,
                    paidDate);
        }
        return invoice;
    }

    @Programmatic
    public void updateInvoice(
            final IncomingInvoice invoice,
            final IncomingInvoiceType type,
            final String invoiceNumber,
            final Property property,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final LocalDate vatRegistrationDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final boolean postedToCodaBooks,
            final LocalDate paidDate) {

        invoice.setType(type);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setProperty(property);
        invoice.setApplicationTenancyPath(atPath);
        invoice.setBuyer(buyer);
        invoice.setSeller(seller);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setDueDate(dueDate);
        invoice.setVatRegistrationDate(vatRegistrationDate);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setStatus(invoiceStatus);
        invoice.setDateReceived(dateReceived);
        invoice.setBankAccount(bankAccount);
        invoice.setPostedToCodaBooks(postedToCodaBooks);
        if(invoice.getPaidDate() == null) {
            invoice.setPaidDate(paidDate);
        }

    }

    @Programmatic
    public void delete(final IncomingInvoice incomingInvoice) {
        repositoryService.removeAndFlush(incomingInvoice);
    }

    @Programmatic
    public List<IncomingInvoice> findIncomingInvoiceByDocumentName(final String name) {
        List<IncomingInvoice> result = new ArrayList<>();
        for (Document doc : incomingDocumentRepository.matchAllIncomingDocumentsByName(name)) {
            for (Paperclip paperclip : paperclipRepository.findByDocument(doc)) {
                if (paperclip.getAttachedTo().getClass().isAssignableFrom(IncomingInvoice.class)) {
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
    StateTransitionService stateTransitionService;
    @Inject
    IncomingDocumentRepository incomingDocumentRepository;
    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    RepositoryService repositoryService;
    @Inject
    IsisJdoSupport isisJdoSupport;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    CurrencyRepository currencyRepository;
    @Inject
    InvoiceRepository invoiceRepository;

}
