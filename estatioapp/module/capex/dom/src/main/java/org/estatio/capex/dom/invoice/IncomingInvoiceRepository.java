package org.estatio.capex.dom.invoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

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

    public IncomingInvoice findByInvoiceNumber(final String invoiceNumber){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingInvoice.class,
                        "findByInvoiceNumber",
                        "invoiceNumber", invoiceNumber));
    }

    @Programmatic
    public IncomingInvoice create(
            final String invoiceNumber,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount) {
        final IncomingInvoice invoice =
                new IncomingInvoice(invoiceNumber, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, dateReceived, bankAccount);
        invoice.setCurrency(currencyRepository.findCurrency("EUR"));
        serviceRegistry2.injectServicesInto(invoice);
        repositoryService.persist(invoice);
        return invoice;
    }


    @Programmatic
    public IncomingInvoice findOrCreate(
            final String invoiceNumber,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount) {
        IncomingInvoice invoice = findByInvoiceNumber(invoiceNumber);
        if (invoice == null) {
            invoice = create(invoiceNumber, atPath, buyer, seller, invoiceDate, dueDate, paymentMethod, invoiceStatus, dateReceived, bankAccount);
        }
        return invoice;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    CurrencyRepository currencyRepository;

}
