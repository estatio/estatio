package org.estatio.capex.dom.invoice;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

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
            final LocalDate dueDate) {
        final IncomingInvoice invoice = IncomingInvoice.builder()
                .invoiceNumber(invoiceNumber)
                .atPath(atPath)
                .seller(seller)
                .buyer(buyer)
                .invoiceDate(invoiceDate)
                .dueDate(dueDate)
                .build();
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
            final LocalDate dueDate) {
        IncomingInvoice invoice = findByInvoiceNumber(invoiceNumber);
        if (invoice == null) {
            invoice = create(invoiceNumber, atPath, buyer, seller, invoiceDate, dueDate);
        }
        return invoice;
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

}
