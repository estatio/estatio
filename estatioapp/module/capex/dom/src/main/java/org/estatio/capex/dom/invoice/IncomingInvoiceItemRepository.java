package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceItem.class
)
public class IncomingInvoiceItemRepository {

    @Programmatic
    public IncomingInvoiceItem findByInvoiceAndCharge(final IncomingInvoice invoice, final Charge charge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByInvoiceAndCharge",
                        "invoice", invoice,
                        "charge", charge
                ));
    }


    @Programmatic
    public IncomingInvoiceItem create(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project) {
        final IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(sequence, invoice, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate, startDate, endDate, property, project);
        serviceRegistry2.injectServicesInto(invoiceItem);
        repositoryService.persistAndFlush(invoiceItem);
        return invoiceItem;
    }

    @Programmatic
    public IncomingInvoiceItem findOrCreate(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project
    ) {
        IncomingInvoiceItem invoiceItem = findByInvoiceAndCharge(invoice, charge);
        if (invoiceItem == null) {
            invoiceItem = create(sequence, invoice, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate,
                    startDate, endDate,
                    property, project);
        }
        return invoiceItem;
    }


    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
